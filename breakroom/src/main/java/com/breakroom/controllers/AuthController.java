package com.breakroom.controllers;

import com.breakroom.Models.DTO.AuthResponse;
import com.breakroom.Models.DTO.CambiarPasswordDto;
import com.breakroom.Models.DTO.LoginRequest;
import com.breakroom.Models.DTO.RegistroDto;
import com.breakroom.Models.DTO.UsuarioDto;
import com.breakroom.Models.DTO.VerificarDto;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.UsuarioRepository;
import com.breakroom.security.JwtUtil;
import com.breakroom.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador encargado de gestionar los procesos de autenticación, 
 * registro y recuperación de credenciales de los usuarios.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Autentica a un usuario con sus credenciales y genera un token JWT si son correctas.
     * * @param loginRequest Objeto con el nombre de usuario y contraseña.
     * @return Respuesta con el token JWT si tiene éxito, o un mensaje de error si falla.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la autenticación");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername());

        final String jwt = jwtUtil.generarToken(userDetails.getUsername(), usuario.getId());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
    /**
     * Registra un nuevo usuario en el sistema.
     * * @param registro Objeto con los datos del nuevo usuario.
     * @return El usuario creado en formato DTO.
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDto> registro(@RequestBody RegistroDto registro) { 
        return ResponseEntity.ok(usuarioService.registro(registro));
    }
    
    /**
     * Comprueba si existe un usuario registrado con el email y nombre de usuario indicados.
     * * @param dto Objeto que contiene el email y el username a comprobar.
     * @return Estado 200 OK si el usuario existe, o 404 NOT FOUND si no coincide.
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarUsuario(@RequestBody VerificarDto dto) {
        boolean existe = usuarioService.verificarUsuario(dto.getEmail(), dto.getUsername());
        if (existe) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Modifica la contraseña de un usuario tras validar su identidad.
     * * @param dto Objeto con las credenciales de validación y la nueva contraseña.
     * @return Estado 200 OK tras actualizar la contraseña de forma correcta.
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordDto dto) {
        usuarioService.cambiarPassword(dto.getEmail(), dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok().build();
    }
}