package onon1101.lendingsystem.security;

import onon1101.lendingsystem.auth.login.token.AccessTokenProperties;
import onon1101.lendingsystem.sharedkernel.api.RequestContextFilter;
import onon1101.lendingsystem.sharedkernel.token.JwtDecoderProvider;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoderProvider decoderProvider)
            throws Exception {
        JwtDecoder accessTokenDecoder = decoderProvider.getDecoder(AccessTokenProperties.PURPOSE);
        return http.csrf(csrf -> csrf.disable())
                .addFilterBefore(new RequestContextFilter(), SecurityContextHolderFilter.class)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(
                                                "/api/v1/auth/login",
                                                "/api/v1/user/register",
                                                "/api/v1/auth/forgot-password",
                                                "/api/v1/auth/register",
                                                "/api/v1/user/validate-email")
                                        .permitAll()
                                        .requestMatchers(EndpointRequest.to("health"))
                                        .permitAll()
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v3/api-docs/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(
                        resourceServer ->
                                resourceServer.jwt(jwt -> jwt.decoder(accessTokenDecoder)))
                .build();
    }
}
