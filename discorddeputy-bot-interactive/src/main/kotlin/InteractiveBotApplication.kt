import com.freberg.discorddeputy.api.ApiClient
import com.freberg.discorddeputy.api.DiscordNotification
import discord4j.common.JacksonResources
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import discord4j.rest.util.Color
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.Instant

val LOGGER: Logger = LoggerFactory.getLogger(object {}.javaClass)

fun main() {
    val client = DiscordClientBuilder.create(System.getenv("DISCORD_TOKEN"))
        .build()
        .login()
        .blockOptional()
        .orElseThrow()
    val applicationId = client.restClient.applicationId.block()!!

    val commands = createCommands(listOf("news.json", "offers.json"))
    LOGGER.info("Registering {} commands...", commands.size)
    registerCommands(applicationId, commands, client.restClient)

    client.on(ReadyEvent::class.java).subscribe { LOGGER.info("Logged in as {}", it.self.username) }

    client.on(ChatInputInteractionEvent::class.java) { handle(createApiClient(), it) }
        .then(client.onDisconnect())
        .block()
}

fun registerCommands(applicationId: Long, commands: List<ApplicationCommandRequest>, client: RestClient) {
    client.applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
        .doOnNext { LOGGER.info("Successfully registered command \"{}\"", it.name()) }
        .doOnError { LOGGER.error("Failed to register global commands", it) }
        .subscribe()
}

fun createCommands(commandFiles: List<String>): List<ApplicationCommandRequest> {
    val d4jMapper = JacksonResources.create()

    return commandFiles.map { object {}.javaClass.getResource("commands/$it") }
        .map { it!!.readText() }
        .map { d4jMapper.objectMapper.readValue(it, ApplicationCommandRequest::class.java) }
        .toList()
}

fun createApiClient(): ApiClient = ApiClient(System.getenv("API_URL"))

fun handle(apiClient: ApiClient, event: ChatInputInteractionEvent): Mono<Message> {
    return when (event.commandName) {
        "offers" -> {
            val offerType = event.getOption("type")
                .flatMap { it.value }
                .map { it.asString() }
                .orElse("current")

            if (offerType == "upcoming")
                responseUsingApi(event) { apiClient.upcomingOffers() } else
                responseUsingApi(event) { apiClient.currentOffers() }
        }

        "news" -> responseUsingApi(event) { apiClient.latestNews() }
        else -> {
            LOGGER.error("Unknown command \"{}\"", event.commandName)
            return Mono.empty()
        }
    }
}

fun responseUsingApi(
    event: ChatInputInteractionEvent,
    apiCall: suspend () -> List<DiscordNotification>
): Mono<Message> {
    return event.deferReply().then(
        mono {
            try {
                val notifications = apiCall()
                val followupSpec = toInteractionFollowupCreateSpec(notifications)
                event.createFollowup(followupSpec).awaitSingle()
            } catch (e: Exception) {
                LOGGER.error("API call failed", e)
                event.createFollowup(
                    InteractionFollowupCreateSpec.builder()
                        .content("An error occurred while fetching data.")
                        .build()
                ).awaitSingle()
            }
        })
}

fun toInteractionFollowupCreateSpec(notifications: List<DiscordNotification>): InteractionFollowupCreateSpec {
    val builder = InteractionFollowupCreateSpec.builder()
    when (notifications.size) {
        0 -> {
            builder.content("No results matching query")
        }

        1 -> {
            builder.addEmbed(toSingletonResponse(notifications[0]))
        }

        else -> {
            builder.addEmbed(toListResponse(notifications))
        }
    }
    return builder.build()
}

fun toSingletonResponse(notification: DiscordNotification): EmbedCreateSpec {
    val builder = EmbedCreateSpec.builder()
        .title(notification.title)
        .timestamp(Instant.now())
        .color(Color.BLACK)

    notification.descriptionHeader?.let {
        builder.addField(it, notification.description, false)
    } ?: run {
        builder.description(notification.description)
    }
    notification.url.let { builder.url(it) }
    notification.imageUrl?.let { builder.image(it) }

    return builder.build()
}

fun toListResponse(notifications: List<DiscordNotification>): EmbedCreateSpec {
    val builder = EmbedCreateSpec.builder()
        .title("${notifications.size} Matches!")
        .timestamp(Instant.now())
        .color(Color.BLACK)

    notifications.forEach { notification ->
        notification.descriptionHeader?.let {
            builder.addField(it, notification.description, false)
        } ?: run {
            builder.addField(notification.title, notification.description, false)
        }
    }
    return builder.build()
}


