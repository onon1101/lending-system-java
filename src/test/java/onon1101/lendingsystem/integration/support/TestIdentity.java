package onon1101.lendingsystem.integration.support;

import java.util.UUID;

public class TestIdentity {
    private TestIdentity() {
    }

    public static String username(String prefix) {
        return prefix + "_" + UUID
                .randomUUID()
                .toString()
                .replace(
                        "-",
                        "")
                .substring(
                        0,
                        12);
    }

    public static String email(String prefix) {
        return username(prefix) + "@test.invalid";
    }
}
