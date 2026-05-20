package com.breakroom.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Nueva sintaxis para activar CORS usando los valores por defecto
            .cors(Customizer.withDefaults()) 
            
            // 2. Nueva sintaxis para desactivar CSRF mediante función lambda
            .csrf(csrf -> csrf.disable()) 
            
            // 3. Reglas de autorización de rutas
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/error").permitAll() // Permite que Spring devuelva el error real
            	    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // Asegura que el preflight pase
            	    .anyRequest().authenticated()
            	)
            // 4. Configuración de sesiones sin estado (Stateless)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            );

        // Coloca tu filtro JWT antes del filtro estándar de Spring
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite las peticiones desde tu frontend de Angular
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); 
        // Permite todos los métodos necesarios, especialmente OPTIONS para el preflight
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); 
        // Permite enviar y recibir el token y el tipo de contenido
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); 
        configuration.setAllowCredentials(true); // Necesario si manejas credenciales o tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas las rutas
        return source;
    }
}