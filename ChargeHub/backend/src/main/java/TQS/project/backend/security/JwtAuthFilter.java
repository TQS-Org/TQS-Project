package TQS.project.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  @Autowired private JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getServletPath();

    // Skip filtering for public actuator and auth endpoints
    if (path.startsWith("/actuator/")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/api-docs")
        || path.startsWith("/swagger-ui.html")
        || path.startsWith("/swagger-ui-custom.html")
        || path.startsWith("/api-docs/swagger-config")
        || path.startsWith("/api/auth/")) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        String email = jwtProvider.getEmailFromToken(token);
        String role = jwtProvider.getRoleFromToken(token); // e.g., EV_DRIVER

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)) // must be prefixed
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception e) {
        // Log or ignore
      }
    }
    filterChain.doFilter(request, response);
  }
}
