package com.breakroom.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 día

    public String generarToken(String username, Long usuarioId) {
        return Jwts.builder()
        		.claim("id", usuarioId) 
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extraerUsuario(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token, String username) {
        final String usuarioExtraido = extraerUsuario(token);
        return (usuarioExtraido.equals(username) && !tokenExpirado(token));
    }

    public Long extraerId(String authHeader) {
		String token = authHeader.substring(7);
        // Obtenemos el claim "id". Lo pedimos como Number porque JWT a veces
        // guarda los IDs pequeños como Integer en vez de Long
        Number id = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Number.class);
                
        return id != null ? id.longValue() : null;
    }
    
    private boolean tokenExpirado(String token) {
        Date expiracion = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiracion.before(new Date());
    }
}