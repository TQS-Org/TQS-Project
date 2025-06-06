package TQS.project.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf()
        .disable()
        .authorizeHttpRequests(
            auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/validate",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/api/auth/register")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/stations")
                    .permitAll() // GET /api/stations is public

                    // EV_DRIVER endpoints
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/stations/search**",
                        "/api/booking/**",
                        "/api/charger/**",
                        "/api/client/**")
                    .hasRole("EV_DRIVER")
                    .requestMatchers(HttpMethod.POST, "/api/charger/*/session", "/api/booking/**")
                    .hasRole("EV_DRIVER")
                    .requestMatchers(HttpMethod.GET, "/api/stations/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/stations/*/chargers")
                    .permitAll()

                    // ADMIN endpoints
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/staff/operator",
                        "/api/staff/operator/assign-station",
                        "/api/stations",
                        "/api/charger/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/staff/operators")
                    .hasRole("ADMIN")

                    // OPERATOR endpoints
                    .requestMatchers(HttpMethod.PUT, "/api/stations/**", "/api/charger/**")
                    .hasAnyRole("EV_DRIVER", "OPERATOR")
                    .requestMatchers(HttpMethod.GET, "/api/staff/station")
                    .hasRole("OPERATOR")

                    // fallback
                    .anyRequest()
                    .authenticated())
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000", "http://deti-tqs-23.ua.pt:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", ""));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
