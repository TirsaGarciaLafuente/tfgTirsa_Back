package com.breakroom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

import com.breakroom.Models.DTO.UsuarioDto;
import com.breakroom.security.JwtUtil;
import com.breakroom.service.UsuarioService;

/**
 * Controlador que expone los endpoints necesarios para consultar y actualizar
 * la información de perfil y configuración de los usuarios.
 */
@RestController 
@RequestMapping("/api/usuarios") 
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Devuelve los datos del perfil del usuario que realiza la consulta, identificándolo por su token.
     * * @param authHeader Cabecera de autorización con el token JWT de la sesión activa.
     * @return Respuesta con los detalles del perfil del usuario en un DTO.
     */
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDto> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUtil.extraerId(authHeader);
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuarioId));
    }
    
    /**
     * Devuelve la información pública del perfil de cualquier usuario buscando por su ID.
     * * @param id Identificador único del usuario a consultar.
     * @return Respuesta con los datos de perfil correspondientes.
     */
    @GetMapping("/perfil/{id}")
    public ResponseEntity<UsuarioDto> obtenerPerfilPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(id));
    }

    /**
     * Actualiza la imagen o identificador del avatar del usuario en sesión.
     * * @param payload Mapa que contiene la clave "avatar" con su nuevo valor.
     * @param authHeader Cabecera de autorización utilizada para validar la identidad del usuario.
     * @return Respuesta con texto plano indicando que el cambio se realizó correctamente.
     */
    @PutMapping("/avatar")
    public ResponseEntity<String> actualizarAvatar(@RequestBody Map<String, String> payload,@RequestHeader("Authorization") String authHeader) {
        String nuevoAvatar = payload.get("avatar");
        Long usuarioId = jwtUtil.extraerId(authHeader);

        usuarioService.actualizarAvatar(usuarioId, nuevoAvatar);
        return ResponseEntity.ok("Avatar actualizado con éxito");
    }
    
    /**
     * Permite modificar los datos generales de presentación del usuario, como nombre, título o descripción.
     * * @param usuarioDto DTO que contiene los nuevos datos que se quieren guardar.
     * @param authHeader Cabecera de autorización necesaria para saber qué usuario solicita el cambio.
     * @return Respuesta con texto plano indicando el éxito de la operación.
     */
    @PutMapping("/perfil")
    public ResponseEntity<String> actualizarPerfil(@RequestBody UsuarioDto usuarioDto, @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUtil.extraerId(authHeader);
        
        usuarioService.actualizarPerfil(usuarioId, usuarioDto);
        
        return ResponseEntity.ok("Perfil actualizado con éxito");
    }
}