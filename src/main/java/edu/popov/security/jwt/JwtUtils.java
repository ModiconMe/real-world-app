package edu.popov.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${jwt.valid-time}")
    private Long validTime;

    private final Key key;
    private final UserDetailsService userDetailsService;

    /**
     * Take secret key from app properties -> encrypt
     * @param signKey secret key from application.yml
     * @param userDetailsService user credentials (login, password, etc...)
     * @throws Exception if secret key too short
     */
    public JwtUtils(
            @Value("${jwt.secret-key}")String signKey,
            UserDetailsService userDetailsService
    ) throws Exception {
        if (signKey.length() < 32) {
            throw new Exception("signKey must have length at least 32");
        }
        key = Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8));
        this.userDetailsService = userDetailsService;
    }

    /**
     * Generate jwt token after authentication
     * @param userDetails user credentials (login, password, etc...)
     * @return jwt token as string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer("real-world-app")
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(validTime)))
                .signWith(key).compact();
    }

    /**
     * Check that token is not expired (application.yml set 24 hours) and token user is equals to authentication user
     * @param token jwt token
     */
    public boolean isTokenValid(String token) {
        boolean expired = isTokenExpired(token);
        Optional<UserDetails> userDetails = Optional.ofNullable(
                userDetailsService.loadUserByUsername(extractUsername(token)));
        return (userDetails.isPresent() && !expired);
    }

    /**
     * Extract token owner username from jwt token
     * @param token jwt token
     * @return token owner username
     */
    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    /**
     * Check that token is not expired (application.yml set 24 hours)
     * @param token jwt token
     */
    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        Instant now = Instant.now();
        Date exp = claims.getExpiration();
        return exp.before(Date.from(now));
    }

    /**
     * Extract encrypted information about token (owner, who issue, when issued and etc.)
     * @param token jwt token
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}