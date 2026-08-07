package onon1101.lendingsystem.integration.user;

import static onon1101.lendingsystem.integration.support.ApiResponseAssertions.assertSuccessful;
import static org.assertj.core.api.Assertions.assertThat;

import onon1101.lendingsystem.integration.support.AbstractApiIntegrationTest;
import onon1101.lendingsystem.integration.support.TestIdentity;
import onon1101.lendingsystem.user.register.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;

class RegisterApiIntegrationTests extends AbstractApiIntegrationTest {

    @Test
    void registersPasswordAccount() {
        String username = TestIdentity.username("register");

        ResponseEntity<JsonNode> response =
                http.postForEntity(
                        "/api/v1/user/register",
                        new RegisterRequest(
                                username, "correct-password", username + "@example.com"),
                        JsonNode.class);

        JsonNode data = assertSuccessful(response, HttpStatus.OK.value());
        assertThat(data.path("userId").asString()).isNotBlank();
    }
}
