package com.breakroom.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Componente de utilidad encargado de la generación, extracción de datos 
 * y validación de tokens de seguridad JWT.
 */
@Component
public class JwtUtil {
    

    private static final long EXPIRATION_TIME = 86400000; // 1 día
    
    @Value("${jwt.secret}")
    private String secretString;

    private Key secretKey;

    /**
     * Inicializa la clave de firma criptográfica convirtiendo el secreto de configuración 
     * en un objeto Key seguro justo después de que se inyecten las propiedades.
     */
    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Crea un token JWT que contiene el nombre de usuario, su ID y define un tiempo de expiración.
     * * @param username El nombre de usuario que será el sujeto del token.
     * @param usuarioId El identificador único del usuario para añadir como propiedad interna (claim).
     * @return Una cadena de texto que representa el token firmado.
     */
    public String generarToken(String username, Long usuarioId) {
        return Jwts.builder()
                .claim("id", usuarioId) 
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (sujeto) que se encuentra guardado en el token.
     * * @param token El token JWT del cual se quiere leer la información.
     * @return El nombre del usuario almacenado en el token.
     */
    public String extraerUsuario(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Comprueba si un token pertenece al usuario indicado y comprueba que no haya caducado todavía.
     * * @param token El token JWT que se va a verificar.
     * @param username El nombre de usuario con el que se va a contrastar.
     * @return true si el usuario coincide y el token sigue vigente, false en caso contrario.
     */
    public boolean validarToken(String token, String username) {
        final String usuarioExtraido = extraerUsuario(token);
        return (usuarioExtraido.equals(username) && !tokenExpirado(token));
    }

    /**
     * Extrae el ID del usuario directamente desde la cabecera "Authorization", 
     * descartando el prefijo para procesar el token.
     * * @param authHeader La cabecera completa que incluye el tipo de autenticación y el token.
     * @return El identificador numérico del usuario, o null si no se encuentra.
     */
    public Long extraerId(String authHeader) {
        String token = authHeader.substring(7);
        Number id = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Number.class);
                
        return id != null ? id.longValue() : null;
    }
    
    /**
     * Comprueba si la fecha de expiración del token es anterior al momento actual.
     * * @param token El token JWT que se va a comprobar.
     * @return true si el token ya ha caducado, false si todavía es válido.
     */
    private boolean tokenExpirado(String token) {
        Date expiracion = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiracion.before(new Date());
    }
}