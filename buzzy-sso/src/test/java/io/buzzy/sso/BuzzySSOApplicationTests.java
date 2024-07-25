package io.buzzy.sso;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Supplier;

@Testcontainers
@SpringBootTest
class BuzzySSOApplicationTests {

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("buzzy_db")
            .withUsername("postgres")
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
            .withExposedPorts(5432)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))
            ))
            .waitingFor(Wait.defaultWaitStrategy());

    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:6.2.6"))
            .withExposedPorts(6379)
            .waitingFor(Wait.defaultWaitStrategy());


    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {
        Supplier<String> jdbcUrlSupplier = database::getJdbcUrl;
        String jdbcUrl = jdbcUrlSupplier.get() + "?stringtype=unspecified&serverTimezone=UTC";

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeAll
    public static void beforeAll() {
        database.start();
        redis.start();
    }

    @AfterAll
    public static void afterAll() {
        database.stop();
        redis.stop();
    }

    @Test
    void contextLoads() {
    }

}
