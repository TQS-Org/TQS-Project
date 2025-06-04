package TQS.project.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        .authorizeHttpRequests(auth -> auth
          .requestMatchers("/actuator/**").permitAll()
          .requestMatchers(
              "/api/auth/login",
              "/api/auth/validate",
              "/swagger-ui/**",
              "/swagger-ui.html",
              "/v3/api-docs/**",
              "/api-docs/**",
              "/api/auth/register",
              "/api/stations"
          ).permitAll()
          .requestMatchers(
            "/api/stations/search**",
            "/api/booking",
            "/api/booking/",
            "/api/booking/**",
            "/api/booking/charger/**",
            "/api/charger/**")
          .hasRole("EV_DRIVER")
          .requestMatchers("/api/stations/search**")
          .hasRole("EV_DRIVER")
          .requestMatchers("/api/staff/operator", "/api/staff/operators")
          .hasRole("ADMIN")
          .anyRequest()
          .authenticated()
        )
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

/*
 * .requestMatchers("/api/driver/**").hasRole("EV_DRIVER")
 * .requestMatchers("/api/operator/**").hasRole("OPERATOR")
 * .requestMatchers("/api/admin/**").hasRole("ADMIN")
 */
