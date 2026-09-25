package onon1101.lendingsystem.configurations.context.user;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    private final UserCache userCache;
    private final CurrentUserReader userReader;

    public SecurityCurrentUserProvider(UserCache userCache, CurrentUserReader userReader) {
        this.userCache = userCache;
        this.userReader = userReader;
    }

    @Override
    public Mono<CurrentUserContext> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .switchIfEmpty(
                        Mono.error(
                                new AuthenticationCredentialsNotFoundException(
                                        "Authenticated JWT is required")))
                .flatMap(this::resolveUser)
                .filter(CurrentUserContext::active)
                .switchIfEmpty(Mono.error(new AccessDeniedException("User account is not active")));
    }

    private Mono<CurrentUserContext> resolveUser(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return Mono.error(
                    new AuthenticationCredentialsNotFoundException(
                            "Authenticated JWT is required"));
        }

        long privateUserId = requirePrivateUserId(jwtAuthentication.getToken());
        return userCache.find(privateUserId).switchIfEmpty(loadFromDatabase(privateUserId));
    }

    private long requirePrivateUserId(Jwt jwt) {
        Object claim = jwt.getClaim(AccessTokenClaim.USER_PRIVATE_ID);

        if (!(claim instanceof Number number)) {
            throw new BadCredentialsException("Access token does not contain user_private_id");
        }

        long privateUserId = number.longValue();
        if (privateUserId <= 0) {
            throw new BadCredentialsException("Access token contains an invalid user_private_id");
        }

        return privateUserId;
    }

    private Mono<CurrentUserContext> loadFromDatabase(long privateUserId) {
        return userReader
                .findByPrivateId(privateUserId)
                .switchIfEmpty(
                        Mono.error(new BadCredentialsException("Token user no longer exists")))
                .flatMap(user -> userCache.save(user).thenReturn(user));
    }
}
