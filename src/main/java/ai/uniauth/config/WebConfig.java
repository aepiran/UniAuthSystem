package ai.uniauth.config;

import ai.uniauth.model.entity.UniSystem;
import ai.uniauth.rep.SystemRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebConfig {

    private final SystemRepository systemRepository;

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://eduflex.com",
            "https://www.eduflex.com"
    );

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            PermissionEvaluator permissionEvaluator
    ) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain integrationFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/integration/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/integration/**",
                                "/api/v1/mock-data/**",
                                "/api/auth/**",
                                "/oauth2/**",
                                "/oauth2d/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
    public ApiKeyAuthenticationFilter apiKeyFilter() {
        return new ApiKeyAuthenticationFilter(systemRepository);
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/mock-data/**",
                                "/api/auth/**",
                                "/oauth2/**",
                                "/oauth2d/**"
                        ).permitAll()

                        // Cho phép tất cả API → tuỳ bạn bật/tắt
                        .requestMatchers("/api/**").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Cache-Control",
                "X-API-Key"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

@RequiredArgsConstructor
class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final SystemRepository systemRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String systemCode = request.getHeader("X-System-Code");
        String apiKey = request.getHeader("X-API-Key");

        if (systemCode == null || apiKey == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing system code or API key");
            return;
        }

        Optional<UniSystem> systemOpt = systemRepository.findBySystemCode(systemCode);
        if (systemOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid system code");
            return;
        }

        UniSystem system = systemOpt.get();
        if (!system.getApiKey().equals(apiKey) || !Boolean.TRUE.equals(system.getIsActive())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API key or system inactive");
            return;
        }

        // Set system in request attribute for later use
        request.setAttribute("system", system);

        filterChain.doFilter(request, response);
    }
}
