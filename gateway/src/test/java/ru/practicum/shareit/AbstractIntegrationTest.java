package ru.practicum.shareit;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {

    private static final DockerComposeContainer<?> ENVIRONMENT;

    static {
        ENVIRONMENT = new DockerComposeContainer<>(new File("docker-compose-test.yml"))
                .withExposedService("postgres", 5432,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(120)))
                .withExposedService("server", 9090,
                        Wait.forHttp("/actuator/health")
                                .forStatusCode(200)
                                .withStartupTimeout(Duration.ofSeconds(120)))
                .withStartupTimeout(Duration.ofSeconds(180))
                .withLogConsumer("postgres", new Slf4jLogConsumer(LoggerFactory.getLogger("POSTGRES")))
                .withLogConsumer("server", new Slf4jLogConsumer(LoggerFactory.getLogger("SERVER")));

        ENVIRONMENT.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("shareit-server.url", () -> "http://localhost:9090");
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/shareit");
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
    }
}