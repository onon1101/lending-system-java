package onon1101.lendingsystem.integration.support;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {
    private final JdbcClient jdbc;
    private final Environment environment;

    public DatabaseCleaner(
            JdbcClient jdbc,
            Environment environment) {
        this.jdbc = jdbc;
        this.environment = environment;
    }

    public void clean() {
        String url = environment.getRequiredProperty("spring.datasource.url");
        if (!url.startsWith("jdbc:h2:mem:lending_system_test_")) {
            throw new IllegalStateException("Refusing to clean a non-test database: " + url);
        }

        // H2 does not support PostgreSQL's multi-table TRUNCATE syntax. Deleting the aggregate
        // root is fast for test-sized data, and the foreign keys cascade to authentication rows.
        jdbc
                .sql("DELETE FROM users")
                .update();
    }
}
