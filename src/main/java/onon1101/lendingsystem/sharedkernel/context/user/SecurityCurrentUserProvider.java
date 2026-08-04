package onon1101.lendingsystem.sharedkernel.context.user;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public final class SecurityCurrentUserProvider implements CurrentUserProvider {

    private final UserCache userCache;
    private final CurrentUserReader userReader;

    public SecurityCurrentUserProvider(UserCache userCache, CurrentUserReader userReader) {
        this.userCache = userCache;
        this.userReader = userReader;
    }

    @Override
    public CurrentUserContext getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated JWT is required");
        }

        long privateUserId = requirePrivateUserId(jwtAuthentication.getToken());

        CurrentUserContext user =
                userCache.find(privateUserId).orElseGet(() -> loadFromDatabase(privateUserId));

        if (!user.active()) {
            throw new AccessDeniedException("User account is not active");
        }

        return user;
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

    private CurrentUserContext loadFromDatabase(long privateUserId) {
        CurrentUserContext user =
                userReader
                        .findByPrivateId(privateUserId)
                        .orElseThrow(
                                () -> new BadCredentialsException("Token user no longer exists"));

        userCache.save(user);
        return user;
    }
}
