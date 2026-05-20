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
        
        // Recuperamos la entidad real del usuario para sacar su ID
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername());

        // Le pasamos tanto el username como el ID para generar el token
        final String jwt = jwtUtil.generarToken(userDetails.getUsername(), usuario.getId());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
	
	@PostMapping("/registro")
    public ResponseEntity<UsuarioDto> registro(@RequestBody RegistroDto registro) {	
        return ResponseEntity.ok(usuarioService.registro(registro));
    }
	
	@PostMapping("/verificar")
	public ResponseEntity<?> verificarUsuario(@RequestBody VerificarDto dto) {
	    boolean existe = usuarioService.verificarUsuario(dto.getEmail(), dto.getUsername());
	    if (existe) {
	        return ResponseEntity.ok().build();
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	}
	
	@PostMapping("/cambiar-password")
	public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordDto dto) {
	    usuarioService.cambiarPassword(dto.getEmail(), dto.getUsername(), dto.getPassword());
	    return ResponseEntity.ok().build();
	}
}