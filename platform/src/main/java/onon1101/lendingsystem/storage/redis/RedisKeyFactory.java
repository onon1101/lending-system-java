package onon1101.lendingsystem.storage.redis;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class RedisKeyFactory {

    private static final String APPLICATION = "lending-system";
    private static final String VERSION = "v1";

    public String create(String module, String resource, Object... identifiers) {
        requireSegment(module, "module");
        requireSegment(resource, "resource");

        String suffix =
                Arrays.stream(identifiers)
                        .map(identifier -> Objects.requireNonNull(identifier, "identifier"))
                        .map(Object::toString)
                        .peek(identifier -> requireSegment(identifier, "identifier"))
                        .collect(Collectors.joining(":"));

        String prefix = APPLICATION + ":" + VERSION + ":" + module + ":" + resource;

        return suffix.isEmpty() ? prefix : prefix + ":" + suffix;
    }

    private static void requireSegment(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }

        if (value.contains(":")) {
            throw new IllegalArgumentException(name + " must not contain ':'");
        }
    }
}
