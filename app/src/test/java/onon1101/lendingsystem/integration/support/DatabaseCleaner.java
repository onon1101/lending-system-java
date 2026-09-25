package onon1101.lendingsystem.integration.support;

import org.springframework.core.env.Environment;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {
    private final DatabaseClient databaseClient;
    private final Environment environment;

    public DatabaseCleaner(DatabaseClient databaseClient, Environment environment) {
        this.databaseClient = databaseClient;
        this.environment = environment;
    }

    public void clean() {
        String url = environment.getRequiredProperty("spring.r2dbc.url");
        if (!url.startsWith("r2dbc:h2:mem:///lending_system_test_")) {
            throw new IllegalStateException("Refusing to clean a non-test database: " + url);
        }

        // H2 does not support PostgreSQL's multi-table TRUNCATE syntax. Deleting the aggregate
        // root is fast for test-sized data, and the foreign keys cascade to authentication rows.
        databaseClient.sql("DELETE FROM users").fetch().rowsUpdated().block();
    }
}
