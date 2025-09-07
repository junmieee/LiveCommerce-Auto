package com.example.config;

import com.example.security.JwtAuthenticationFilter;
import com.example.security.JwtTokenProvider;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider tokenProvider)
      throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        "/api/users/login",
                        "/api/users/refresh",
                        "/api/users/register",
                        "/api/users/check-email",
                        "/actuator/**",
                        "/",
                        "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(Customizer.withDefaults());

    http.addFilterBefore(
        new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
          String allowedOriginsCsv) {
    List<String> allowedOrigins =
        Arrays.stream(allowedOriginsCsv.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(
        Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()));
    config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
    config.setExposedHeaders(Arrays.asList("Authorization"));
    config.setAllowCredentials(false); // 토큰은 헤더로 전달하므로 credentials 불필요
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
