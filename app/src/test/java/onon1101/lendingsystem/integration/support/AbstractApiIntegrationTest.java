package onon1101.lendingsystem.integration.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@ApiIntegrationTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractApiIntegrationTest {

    private static final String DATABASE_NAME = TestSchema.name();

    @Autowired protected WebTestClient http;

    @Autowired private DatabaseCleaner databaseCleaner;

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.r2dbc.url",
                () -> "r2dbc:h2:mem:///" + DATABASE_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
    }

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.clean();
    }
}
