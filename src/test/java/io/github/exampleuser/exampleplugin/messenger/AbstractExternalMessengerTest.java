package io.github.exampleuser.exampleplugin.messenger;

import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import io.github.exampleuser.exampleplugin.utility.Messenger;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("externalmessaging")
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractExternalMessengerTest extends AbstractMessengerTest {
    @Container
    public static GenericContainer<?> container;

    AbstractExternalMessengerTest(GenericContainer<?> container, MessengerTestParams testConfig) {
        super(testConfig);
        AbstractExternalMessengerTest.container = container;
        container.start();
    }

    @BeforeAll
    @DisplayName("Initialize message broker")
    void beforeAllTests() {
        Assertions.assertTrue(container.isRunning());

        final String username = switch (getTestConfig().type()) {
            case "redis" -> "default";
            case "rabbitmq" -> "guest";
            default -> "";
        };

        final String password = switch (getTestConfig().type()) {
            case "redis" -> "default";
            case "rabbitmq" -> "guest";
            default -> "";
        };

        messengerConfig = MessengerConfig.builder()
            .withEnabled(true)
            .withBroker(getTestConfig().type())
            .withAddresses("%s:%s".formatted(container.getHost(), container.getFirstMappedPort()))
            .withUsername(username)
            .withPassword(password)
            .withSSL(false)
            .withVirtualHost("/")
            .build();

        Messenger.init(
            MessengerHandler.builder()
                .withConfig(messengerConfig)
                .withTesting(true)
                .withLogger(logger)
                .withName("Test")
                .withTaskAdapter(new MockTaskAdapter())
                .withReceiverAdapter(new MockReceiverAdapter())
                .build()
        );
        Messenger.getHandler().doStartup();
        Messenger.getHandler().scheduleTasks();
    }

    @AfterAll
    @Override
    void afterAllTests() {
        super.afterAllTests();
        container.stop();
    }
}
