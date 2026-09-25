package onon1101.lendingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import onon1101.lendingsystem.integration.support.AbstractApiIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

/**
 * Fails when the reviewed business API surface changes unexpectedly. Endpoint behavior belongs in
 * the focused integration test for that feature.
 */
class ApiEndpointCoverageTests extends AbstractApiIntegrationTest {

    private static final Set<String> BUSINESS_API_INVENTORY =
            Set.of(
                    "POST /api/v1/auth/email-verification/confirm",
                    "POST /api/v1/auth/email-verification/resend",
                    "POST /api/v1/auth/forgot-password",
                    "POST /api/v1/auth/login",
                    "POST /api/v1/auth/logout",
                    "POST /api/v1/auth/refresh",
                    "POST /api/v1/auth/reset-password",
                    "POST /api/v1/item/delete",
                    "GET /api/v1/item/retrieve/{itemId}",
                    "POST /api/v1/items/create",
                    "POST /api/v1/items/update",
                    "POST /api/v1/user/register",
                    "GET /api/v1/user/whoami");

    @Autowired
    @Qualifier("requestMappingHandlerMapping") private RequestMappingHandlerMapping mappings;

    @Test
    void businessApiSurfaceMatchesReviewedInventory() {
        Set<String> actual =
                mappings.getHandlerMethods().entrySet().stream()
                        .filter(entry -> isBusinessController(entry.getValue()))
                        .flatMap(entry -> endpoints(entry.getKey()).stream())
                        .collect(Collectors.toSet());

        assertThat(actual)
                .as("Review the API change and update the business endpoint inventory")
                .containsExactlyInAnyOrderElementsOf(BUSINESS_API_INVENTORY);
    }

    private static boolean isBusinessController(HandlerMethod handler) {
        return handler.getBeanType().getPackageName().startsWith("onon1101.lendingsystem")
                && handler.getBeanType().isAnnotationPresent(RestController.class);
    }

    private static Set<String> endpoints(RequestMappingInfo mapping) {
        Set<String> paths =
                mapping.getPatternsCondition().getPatterns().stream()
                        .map(pattern -> pattern.getPatternString())
                        .collect(Collectors.toSet());
        Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
        return paths.stream()
                .flatMap(path -> methods.stream().map(method -> method.name() + " " + path))
                .collect(Collectors.toSet());
    }
}
