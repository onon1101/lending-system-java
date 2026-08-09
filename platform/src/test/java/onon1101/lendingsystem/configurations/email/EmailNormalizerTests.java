package onon1101.lendingsystem.configurations.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EmailNormalizerTests {

    @Test
    void trimsAndConvertsEmailToLowercase() {
        assertEquals(
                "member001@example.com",
                EmailNormalizer.normalize(" Member001@Example.COM "));
    }

    @Test
    void keepsNullForBeanValidation() {
        assertNull(EmailNormalizer.normalize(null));
    }
}
