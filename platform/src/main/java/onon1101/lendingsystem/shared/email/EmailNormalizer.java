package onon1101.lendingsystem.shared.email;

import java.util.Locale;

public final class EmailNormalizer {

    private EmailNormalizer() {}

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
