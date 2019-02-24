package com.example.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -1297327947014192491L;

    static final String CLAM_KEY_USERNAME = "sub";
    static final String CLAM_KEY_AUDIENCE = "audience";
    static final String CLAM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getUsernameFromToken(String token) {
        String username = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        }catch (Exception e){
            username = null;
        }
        return username;
    }

    public Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (Exception e){
            claims = null;
        }
        return claims;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser)userDetails;
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        Date expiration = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                expiration = claims.getExpiration();
            }else {
                expiration = null;
            }
        }catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public String generateToken(JwtUser userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> clamis) {
        return Jwts.builder().setClaims(clamis).setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }
}
