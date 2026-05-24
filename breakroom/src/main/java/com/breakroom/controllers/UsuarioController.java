package com.breakroom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Necesario para Principal
import java.util.Map;           // Necesario para Map

import com.breakroom.Models.DTO.UsuarioDto;
import com.breakroom.security.JwtUtil;
import com.breakroom.service.UsuarioService;

@RestController 
@RequestMapping("/api/usuarios") 
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;

    
	@Autowired
	private JwtUtil jwtUtil;
	
    // Endpoint para obtener el perfil del usuario actual
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDto> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
		Long usuarioId = jwtUtil.extraerId(authHeader);
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuarioId));
    }

    // Endpoint para actualizar el avatar
    @PutMapping("/avatar")
    public ResponseEntity<String> actualizarAvatar(@RequestBody Map<String, String> payload,@RequestHeader("Authorization") String authHeader) {
        String nuevoAvatar = payload.get("avatar");
		Long usuarioId = jwtUtil.extraerId(authHeader);

        usuarioService.actualizarAvatar(usuarioId, nuevoAvatar);
        return ResponseEntity.ok("Avatar actualizado con éxito");
    }
}