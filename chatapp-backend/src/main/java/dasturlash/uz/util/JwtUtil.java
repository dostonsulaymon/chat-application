package dasturlash.uz.util;

import dasturlash.uz.dto.JwtDTO;
import dasturlash.uz.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.token.live-time}")
    private long tokenLiveTime;

    @Value("${jwt.refresh-token.live-time}")
    private long refreshTokenLiveTime;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String encode(String login, UserRole role) {
        return getString(login, role, tokenLiveTime);
    }

    public String refreshToken(String login, UserRole role) {
        return getString(login, role, refreshTokenLiveTime);
    }

    private String getString(String login, UserRole role, long tokenLiveTime) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role.name()); // Store role as a string
        extraClaims.put("login", login);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            Date now = new Date();

            if (expiration.before(now)) {
                return new TokenValidationResult(false, "Token has expired");
            }

            return new TokenValidationResult(true, "Token is valid");

        } catch (ExpiredJwtException e) {
            return new TokenValidationResult(false, "Token has expired");
        } catch (MalformedJwtException e) {
            return new TokenValidationResult(false, "Invalid token format");
        } catch (Exception e) {
            return new TokenValidationResult(false, "Invalid token");
        }
    }

    public JwtDTO decode(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String login = (String) claims.get("login");
        UserRole role = UserRole.valueOf((String) claims.get("role"));

        return new JwtDTO(login, role);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static class TokenValidationResult {
        private boolean valid;
        private String message;

        public TokenValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
