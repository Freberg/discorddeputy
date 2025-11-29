package com.freberg.discorddeputy;

import com.freberg.discorddeputy.repository.DiscordNotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@SuppressWarnings("unused")
class ProcessorApplicationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));

    @Autowired
    private InputDestination input;
    @Autowired
    private OutputDestination output;
    @Autowired
    private DiscordNotificationRepository repository;
    @Value("${spring.cloud.stream.bindings.processor-out-0.destination}")
    private String bindingName;

    @Test
    public void deduplicate() {
        // send new message
        assertIdNotInDatabase("1");
        var input1 = createPayload("1");
        send(input1);
        var output1 = receive();
        assertIdInDatabase("1");
        assertInputAndOutputEquals(input1, output1);

        // send new message
        assertIdNotInDatabase("2");
        var input2 = createPayload("2");
        send(input2);
        var output2 = receive();
        assertIdInDatabase("2");
        assertInputAndOutputEquals(input2, output2);

        // send duplicate
        var input3 = createPayload("1");
        send(input3);
        var output3 = receive();
        assertNull(output3);
    }

    @Test
    public void serde() {
        var message = createPayload("unique");
        send(message);
        var output = receive();
        StepVerifier.create(repository.findById("unique"))
                .expectNextMatches(message::equals)
                .verifyComplete();
    }

    private void send(DiscordNotification discordNotification) {
        input.send(createMessage(discordNotification));
    }

    private Message<byte[]> receive() {
        return output.receive(100, bindingName);
    }

    private void assertIdNotInDatabase(String id) {
        StepVerifier.create(repository.existsById(id))
                .expectNextMatches(Boolean.FALSE::equals)
                .verifyComplete();
    }
    private void assertIdInDatabase(String id) {
        StepVerifier.create(repository.existsById(id))
                .expectNextMatches(Boolean.TRUE::equals)
                .verifyComplete();
    }

    private void assertInputAndOutputEquals(DiscordNotification input, Message<byte[]> output) {
        assertNotNull(output);
        assertEquals(input, OBJECT_MAPPER.readValue(output.getPayload(), DiscordNotification.class));
    }

    private Message<?> createMessage(DiscordNotification payload) {
        return new GenericMessage<>(payload);
    }

    private DiscordNotification createPayload(String id) {
        return new DiscordNotification(Map.of(
                "id", id,
                "source", "some_source",
                "other_key", "other_value"
        ));
    }

    @SpringBootApplication
    @Import(TestChannelBinderConfiguration.class)
    public static class TestProcessorApplication {
    }
}