package onon1101.lendingsystem.security;

import onon1101.lendingsystem.auth.login.token.AccessTokenProperties;
import onon1101.lendingsystem.configurations.controller.RequestContextFilter;
import onon1101.lendingsystem.configurations.token.JwtDecoderProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain securityFilterChain(
            ServerHttpSecurity http, JwtDecoderProvider decoderProvider) {
        JwtDecoder accessTokenDecoder = decoderProvider.getDecoder(AccessTokenProperties.PURPOSE);
        ReactiveJwtDecoder reactiveAccessTokenDecoder =
                token -> Mono.fromCallable(() -> accessTokenDecoder.decode(token));

        return http.csrf(csrf -> csrf.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterBefore(
                        new RequestContextFilter(), SecurityWebFiltersOrder.REACTOR_CONTEXT)
                .authorizeExchange(
                        authorize ->
                                authorize
                                        .pathMatchers(
                                                "/api/v1/auth/login",
                                                "/api/v1/auth/logout",
                                                "/api/v1/auth/refresh",
                                                "/api/v1/user/register",
                                                "/api/v1/auth/forgot-password",
                                                "/api/v1/auth/register",
                                                "/api/v1/auth/email-verification/confirm",
                                                "/api/v1/auth/email-verification/resend")
                                        .permitAll()
                                        .pathMatchers("/actuator/health", "/actuator/health/**")
                                        .permitAll()
                                        .pathMatchers(
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v3/api-docs/**")
                                        .permitAll()
                                        .anyExchange()
                                        .authenticated())
                .oauth2ResourceServer(
                        resourceServer ->
                                resourceServer.jwt(
                                        jwt -> jwt.jwtDecoder(reactiveAccessTokenDecoder)))
                .build();
    }
}
