package TQS.project.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtProvider {

  // Replace this with your own Base64-encoded 512-bit (64-byte) secret key!
  @Value("${jwt.secret}")
  private String secretBase64;

  // Decode the base64 secret into a Key instance (HMAC SHA-512 requires 512 bits
  // key)
  private final Key jwtSecret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretBase64));

  private final long jwtExpirationMs = 3600000; // 1 hour in milliseconds

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
    return (String) Jwts.parserBuilder()
        .setSigningKey(jwtSecret)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("role");
  }
}
