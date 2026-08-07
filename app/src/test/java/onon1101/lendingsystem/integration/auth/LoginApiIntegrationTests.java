package onon1101.lendingsystem.integration.auth;

import static onon1101.lendingsystem.integration.support.ApiResponseAssertions.assertSuccessful;
import static org.assertj.core.api.Assertions.assertThat;

import onon1101.lendingsystem.auth.login.LoginRequest;
import onon1101.lendingsystem.integration.support.AbstractApiIntegrationTest;
import onon1101.lendingsystem.integration.support.TestIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;

class LoginApiIntegrationTests extends AbstractApiIntegrationTest {

    @Autowired private LoginApiTestData testData;

    @Test
    void returnsAccessTokenForValidCredentials() {
        String username = TestIdentity.username("login");
        testData.activePasswordUser(username, "correct-password");

        ResponseEntity<JsonNode> response = login(username, "correct-password");

        JsonNode data = assertSuccessful(response, HttpStatus.OK.value());
        assertThat(data.path("accessToken").asString()).isNotBlank();
        assertThat(data.path("tokenType").asString()).isEqualTo("Bearer");
        assertThat(data.path("accessTokenExpiresIn").asLong()).isPositive();
    }

    @Test
    void normalizesUsernameBeforeAuthentication() {
        String username = TestIdentity.username("normalize");
        testData.activePasswordUser(username, "correct-password");

        ResponseEntity<JsonNode> response =
                login("  " + username.toUpperCase() + "  ", "correct-password");

        assertSuccessful(response, HttpStatus.OK.value());
    }

    @Test
    void returnsDomainFailureForInvalidCredentials() {
        String username = TestIdentity.username("invalid");
        testData.activePasswordUser(username, "correct-password");

        ResponseEntity<JsonNode> response = login(username, "wrong-password");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path("isSuccess").asBoolean()).isFalse();
        assertThat(response.getBody().path("errorCode").asString())
                .isEqualTo("Auth.InvalidCredentials");
        assertThat(response.getBody().path("data").isNull()).isTrue();
    }

    @Test
    void returnsBadRequestForBlankUsername() {
        ResponseEntity<JsonNode> response = login("", "some-password");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path("code").asInt()).isEqualTo(400);
        assertThat(response.getBody().path("isSuccess").asBoolean()).isFalse();
        assertThat(response.getBody().path("errorCode").asString())
                .isEqualTo("Validation.InvalidRequest");
    }

    private ResponseEntity<JsonNode> login(String username, String password) {
        return http.postForEntity(
                "/api/v1/auth/login", new LoginRequest(username, password), JsonNode.class);
    }
}
