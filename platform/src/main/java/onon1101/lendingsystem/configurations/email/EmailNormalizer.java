package onon1101.lendingsystem.configurations.email;


import java.util.Locale;

public final class EmailNormalizer {

    private EmailNormalizer() {}

    public static String normalize(String email) {
        if(email == null) {
            throw new IllegalStateException(
                    "email must not be null."
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
