package onon1101.lendingsystem.shared.email;

import org.apache.commons.validator.routines.EmailValidator;

public final class EmailUtil {
    private EmailUtil() {}

    public static boolean validateEmail(String email) {
        return email == null || !EmailValidator.getInstance().isValid(email);
    }
}
