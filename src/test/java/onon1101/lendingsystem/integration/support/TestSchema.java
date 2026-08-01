package onon1101.lendingsystem.integration.support;

import java.util.UUID;

public final class TestSchema {
    private static final String NAME =
            "lending_system_test_" + UUID
                    .randomUUID()
                    .toString()
                    .replace(
                            "-",
                            "");

    private TestSchema() {
    }

    public static String name() {
        return NAME;
    }
}
