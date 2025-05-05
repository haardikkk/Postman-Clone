package com.devendpoint.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

	@Component
	public class JwtUtil {
	    private final String SECRET_KEY = "dfssklkdsslgdioghr.gf;gdfgdfkjhgdfdfvddsfdkhfldshfdslhdflhgldndkvd,smnlkdslkdsdfkldsnflkdfgn"; // Min 32 chars
	    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 5; // 5 hours

	    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

	    public String generateToken(String username) {
	        return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
	                .signWith(key)
	                .compact();
	    }

	    public String extractUsername(String token) {
	        return Jwts.parserBuilder().setSigningKey(key).build()
	                .parseClaimsJws(token).getBody().getSubject();
	    }

	    public boolean validateToken(String token) {
	        try {
	            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	            return true;
	        } catch (JwtException e) {
	            return false;
	        }
	    }
	
}
