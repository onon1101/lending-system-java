package onon1101.lendingsystem.integration.support;

import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;

import tools.jackson.databind.JsonNode;

/**
 * Assertions shared by all successful API contract tests.
 */
public final class ApiResponseAssertions {

    private ApiResponseAssertions() {
    }

    public static JsonNode assertSuccessful(
            ResponseEntity<JsonNode> response,
            int expectedStatus) {
        Assertions
                .assertThat(response
                        .getStatusCode()
                        .value())
                .isEqualTo(expectedStatus);
        Assertions
                .assertThat(response.getBody())
                .isNotNull();

        JsonNode body = response.getBody();
        Assertions
                .assertThat(body
                        .path("code")
                        .asInt())
                .isEqualTo(expectedStatus);
        Assertions
                .assertThat(body
                        .path("isSuccess")
                        .asBoolean())
                .isTrue();
        Assertions
                .assertThat(body
                        .path("errorCode")
                        .isNull())
                .isTrue();
        Assertions
                .assertThat(body
                        .path("data")
                        .isMissingNode())
                .isFalse();
        Assertions
                .assertThat(body
                        .path("data")
                        .isNull())
                .isFalse();
        return body.path("data");
    }
}
