package TQS.project.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtProvider {

  // Replace this with your own Base64-encoded 512-bit (64-byte) secret key!
  private static final String SECRET_BASE64 =
      "wBv3slbZ4PQNZQkRjrUQv9UVUKbLn7b/JchYmVK55LWVfFCkN2o1C0k9qigXHZCU4grZ7lj04qXw9Sx56Zay4ZQio8huHSHO8hdkusSHUsuu8232";

  // Decode the base64 secret into a Key instance (HMAC SHA-512 requires 512 bits
  // key)
  private final Key jwtSecret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_BASE64));

  private final long jwtExpirationMs = 3600000; // 1 hour

  public String generateToken(String email, String role) {
    return Jwts.builder()
        .setSubject(email)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(jwtSecret, SignatureAlgorithm.HS512)
        .compact();
  }

  public String getEmailFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(jwtSecret)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String getRoleFromToken(String token) {
    return (String)
        Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("role");
  }
}
