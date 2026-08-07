package onon1101.lendingsystem.integration.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/** Marks a test that crosses the HTTP, application, and database boundaries. */
@Inherited
@Tag("integration")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIntegrationTest {}
