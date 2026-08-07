package onon1101.lendingsystem.sharedkernel.token;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
final class JwtDecoderRegistry implements JwtDecoderProvider {

    private final Map<String, JwtDecoder> decoders;

    JwtDecoderRegistry(
            JwtDecoderFactory factory,
            List<TokenProperties> tokenProperties
    ) {
        Map<String, JwtDecoder> result =
                new HashMap<>();

        for (TokenProperties properties : tokenProperties) {
            JwtDecoder previous =
                    result.putIfAbsent(
                            properties.purpose(),
                            factory.create(properties));

            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate JWT token purpose: "
                                + properties.purpose());
            }
        }

        this.decoders = Map.copyOf(result);
    }

    @Override
    public JwtDecoder getDecoder(String purpose) {
        JwtDecoder decoder = decoders.get(purpose);

        if (decoder == null) {
            throw new IllegalArgumentException(
                    "No JWT decoder registered for purpose: "
                            + purpose);
        }

        return decoder;
    }
}
