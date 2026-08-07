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
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Fails when production exposes a new business API without adding it to the reviewed API inventory.
 * Every inventory entry must have at least one happy-path integration test.
 */
class ApiEndpointCoverageTests extends AbstractApiIntegrationTest {

    private static final Set<String> TESTED_API_INVENTORY =
            Set.of("POST /api/v1/auth/login", "POST /api/v1/user/register");

    @Autowired
    @Qualifier("requestMappingHandlerMapping") private RequestMappingHandlerMapping mappings;

    @Test
    void everyBusinessApiHasAHappyPathIntegrationTest() {
        Set<String> actual =
                mappings.getHandlerMethods().entrySet().stream()
                        .filter(entry -> isBusinessController(entry.getValue()))
                        .flatMap(entry -> endpoints(entry.getKey()).stream())
                        .collect(Collectors.toSet());

        assertThat(actual)
                .as("Add a happy-path API integration test, then register that METHOD /path here")
                .containsExactlyInAnyOrderElementsOf(TESTED_API_INVENTORY);
    }

    private static boolean isBusinessController(HandlerMethod handler) {
        return handler.getBeanType().getPackageName().startsWith("onon1101.lendingsystem")
                && handler.getBeanType().isAnnotationPresent(RestController.class);
    }

    private static Set<String> endpoints(RequestMappingInfo mapping) {
        Set<String> paths = mapping.getPatternValues();
        Set<RequestMethod> methods = mapping.getMethodsCondition().getMethods();
        return paths.stream()
                .flatMap(path -> methods.stream().map(method -> method.name() + " " + path))
                .collect(Collectors.toSet());
    }
}
