package onon1101.lendingsystem.integration.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ApiIntegrationTest
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractApiIntegrationTest {

    private static final String DATABASE_NAME = TestSchema.name();

    @Autowired protected TestRestTemplate http;

    @Autowired private DatabaseCleaner databaseCleaner;

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:h2:mem:" + DATABASE_NAME + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
    }

    @BeforeEach
    void cleanDatabase() {
        databaseCleaner.clean();
    }
}
