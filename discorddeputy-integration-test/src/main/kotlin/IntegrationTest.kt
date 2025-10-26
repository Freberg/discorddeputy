import com.rabbitmq.client.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

private const val CONNECTION_NAME = "CanaryAnalysisJob"
private val LOGGER: Logger = LoggerFactory.getLogger(object {}.javaClass)

private val RABBITMQ_HOST: String = System.getenv("RABBITMQ_HOST") ?: "localhost"
private val RABBITMQ_USERNAME: String = System.getenv("RABBITMQ_USERNAME") ?: "rabbitmq"
private val RABBITMQ_PASSWORD: String = System.getenv("RABBITMQ_PASSWORD") ?: "rabbitmq"
private val INPUT_EXCHANGE: String = System.getenv("INPUT_EXCHANGE") ?: "source-notifications"
private val OUTPUT_EXCHANGE: String = System.getenv("OUTPUT_EXCHANGE") ?: "bot-notifications"
private val SHUTDOWN_URL = System.getenv("SERVICE_SHUTDOWN_URL") ?: "http://localhost:7085/actuator/shutdown"
private val TEST_MODE: String = System.getenv("TEST_MODE") ?: ""
private val VERIFY_TIMEOUT_MS: Long = System.getenv("VERIFY_TIMEOUT_SECONDS")?.toLongOrNull()?.times(1000) ?: 10000L
private val TEST_MESSAGE_BODY: String = System.getenv("TEST_MESSAGE_BODY") ?: """
        {"id": "canary-message-${System.currentTimeMillis()}", "source": "OFFER", "testKey": "testValue"}
        """.trimIndent()

fun main() {
    LOGGER.info("Starting RabbitMQ Test Runner in mode: {}", TEST_MODE)

    val testFunction = when (TEST_MODE) {
        "PRODUCER_TEST" -> { channel: Channel -> runProducerTest(channel) }
        "PROCESSOR_TEST" -> { channel: Channel -> runProcessorTest(channel) }
        else -> {
            LOGGER.error("Unknown TEST_MODE {}", TEST_MODE)
            exitProcess(1)
        }
    }

    try {
        val factory = ConnectionFactory().apply {
            host = RABBITMQ_HOST
            username = RABBITMQ_USERNAME
            password = RABBITMQ_PASSWORD
            requestedHeartbeat = 30
            connectionTimeout = 5000
        }
        factory.newConnection(CONNECTION_NAME).use { connection ->
            connection.createChannel().use { channel ->
                val success = testFunction(channel)
                shutdownService()
                if (success) {
                    LOGGER.info("✨ TEST COMPLETED SUCCESSFULLY!")
                    exitProcess(0)
                } else {
                    LOGGER.info("❌ TEST FAILED.")
                    exitProcess(1)
                }
            }
        }
    } catch (e: Exception) {
        LOGGER.error("Failed to execute test", e)
        exitProcess(1)
    }
}


fun runProducerTest(channel: Channel): Boolean {
    LOGGER.info("--- Running Producer Test ---")

    TestConsumer(channel).use { testConsumer ->
        return testConsumer.verifyMinMessageCount(1)
    }
}

fun runProcessorTest(channel: Channel): Boolean {
    LOGGER.info("--- Running Processor Deduplication Test ---")

    TestConsumer(channel).use { testConsumer ->
        LOGGER.info("Step 1: Sending unique message to processor (Input: {})", INPUT_EXCHANGE)
        produceMessage(channel, INPUT_EXCHANGE, TEST_MESSAGE_BODY)
        Thread.sleep(500)

        LOGGER.info("Step 2: Sending duplicate message (Input: {})", INPUT_EXCHANGE)
        produceMessage(channel, INPUT_EXCHANGE, TEST_MESSAGE_BODY)

        return testConsumer.verifyMessageCount(1)
    }
}

fun produceMessage(channel: Channel, exchange: String, message: String) {
    channel.basicPublish(
        exchange,
        "#",
        AMQP.BasicProperties.Builder().contentType("application/json").build(),
        message.toByteArray(StandardCharsets.UTF_8)
    )
    LOGGER.info("✅ Produced message to {}", exchange)
}

fun shutdownService() {
    try {
        LOGGER.info("Sending shutdown request to service: {}", SHUTDOWN_URL)
        val url = URI.create(SHUTDOWN_URL).toURL();
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        connection.doOutput = true
        val responseCode = connection.responseCode
        LOGGER.info("Shutdown request completed with response code {}", responseCode)
    } catch (e: Exception) {
        LOGGER.error("Failed to send shutdown request", e)
    }
}

class TestConsumer(private val channel: Channel) : DefaultConsumer(channel), AutoCloseable {

    val receivedMessages = ConcurrentLinkedQueue<String>()
    val latch = CountDownLatch(1)
    private val consumerTag: String

    init {
        channel.exchangeDeclare(OUTPUT_EXCHANGE, "topic", true)
        val queueName = channel.queueDeclare().queue
        channel.queueBind(queueName, OUTPUT_EXCHANGE, "#")
        LOGGER.info("Topology setup: Test Queue '{}' bound to Exchange '{}' with key '#'", queueName, OUTPUT_EXCHANGE)

        consumerTag = channel.basicConsume(queueName, true, this)
        LOGGER.info("Consumer registered with tag: {}", consumerTag)
    }

    override fun handleDelivery(
        consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray
    ) {
        val message = String(body, StandardCharsets.UTF_8)
        receivedMessages.add(message)
        LOGGER.info("-> Consumed message (Total: {}): {}", receivedMessages.size, message)

        if (receivedMessages.size == 1) {
            latch.countDown()
        }
    }

    override fun close() {
        channel.basicCancel(consumerTag)
        LOGGER.info("Consumer {} cancelled and resources released.", consumerTag)
    }

    fun verifyMessageCount(expectedCount: Int): Boolean {
        LOGGER.info("Verifying message count, waiting up to {}ms for delivery...", VERIFY_TIMEOUT_MS)
        latch.await(VERIFY_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        Thread.sleep(2000)

        val finalCount = receivedMessages.size
        if (finalCount == expectedCount) {
            LOGGER.info("✅ SUCCESS: Asserted message count of {} successfully.", finalCount)
            return true
        } else {
            LOGGER.error("❌ FAILURE: Expected {} message(s), but received {} message(s).", expectedCount, finalCount)
            return false
        }
    }

    fun verifyMinMessageCount(minCount: Int): Boolean {
        LOGGER.info("Verifying minimum of {} message(s), waiting up to {}ms for delivery...", minCount, VERIFY_TIMEOUT_MS)
        latch.await(VERIFY_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        Thread.sleep(2000)

        val finalCount = receivedMessages.size
        if (finalCount >= minCount) {
            LOGGER.info("✅ SUCCESS: Asserted minimum message count of {} successfully (received {}).", minCount, finalCount)
            return true
        } else {
            LOGGER.error("❌ FAILURE: Expected at least {} message(s), but received {} message(s).", minCount, finalCount)
            return false
        }
    }
}