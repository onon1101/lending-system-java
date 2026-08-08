package onon1101.lendingsystem.configurations.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import onon1101.lendingsystem.configurations.domain.DomainError;
import onon1101.lendingsystem.configurations.domain.Result;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

class ResultRollbackAspectTests {

    @Test
    void rollsBackDatabaseChangesWhenTransactionalMethodReturnsFailure() {
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TestConfiguration.class)) {
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
            TestService service = context.getBean(TestService.class);
            jdbcTemplate.execute("create table rollback_probe (id integer primary key)");

            Result<Void> result = service.insertAndFail(1);

            assertThat(result.isFailure()).isTrue();
            assertThat(jdbcTemplate.queryForObject("select count(*) from rollback_probe", Integer.class))
                    .isZero();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAspectJAutoProxy
    @Import({ResultRollbackAspect.class, TransactionConfiguration.class})
    static class TestConfiguration {

        @Bean
        DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .generateUniqueName(true)
                    .build();
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        TestService testService(JdbcTemplate jdbcTemplate) {
            return new TestService(jdbcTemplate);
        }
    }

    static class TestService {

        private final JdbcTemplate jdbcTemplate;

        TestService(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Transactional
        public Result<Void> insertAndFail(int id) {
            jdbcTemplate.update("insert into rollback_probe (id) values (?)", id);
            return Result.failure(new TestError());
        }
    }

    static final class TestError extends DomainError {

        TestError() {
            super("test_failure", "Test failure");
        }
    }
}
