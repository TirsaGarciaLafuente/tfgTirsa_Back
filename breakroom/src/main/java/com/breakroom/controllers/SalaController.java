package com.breakroom.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.breakroom.Models.DTO.SalaDto;
import com.breakroom.security.JwtUtil;
import com.breakroom.service.SalaService;

/**
 * Controlador que gestiona los espacios virtuales de reunión (salas),
 * controlando la creación, acceso y abandono de los usuarios en ellas.
 */
@RestController 
@RequestMapping("/api/salas") 
public class SalaController {
    
    @Autowired
    private SalaService salaService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Crea un nuevo espacio o sala y añade al creador directamente como su primer miembro.
     * * @param nombre El nombre asignado para identificar la sala.
     * @param authHeader Cabecera de autorización que contiene el token JWT para identificar al creador.
     * @return Respuesta con los datos de la sala recién creada en formato DTO.
     */
    @PostMapping("/crear")
    public ResponseEntity<SalaDto> crear(@RequestParam String nombre, @RequestHeader("Authorization") String authHeader) {  
        Long usuarioId = jwtUtil.extraerId(authHeader);
        return ResponseEntity.ok(salaService.crearSala(nombre, usuarioId));
    }

    /**
     * Permite a un usuario incorporarse a una sala utilizando su código único de invitación.
     * * @param codSala Código alfanumérico que identifica la sala a la que se desea ingresar.
     * @param authHeader Cabecera de autorización que contiene el token JWT para identificar al usuario que se une.
     * @return Respuesta con los datos de la sala actualizada en formato DTO.
     */
    @PostMapping("/unirse")
    public ResponseEntity<SalaDto> unirse(@RequestParam String codSala, @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUtil.extraerId(authHeader);
        return ResponseEntity.ok(salaService.unirseConCodigo(codSala, usuarioId));
    }

    /**
     * Devuelve el listado completo de salas en las que participa el usuario que hace la solicitud.
     * * @param authHeader Cabecera de autorización que contiene el token JWT para saber de qué usuario se trata.
     * @return Respuesta con la lista de salas asociadas en formato DTO.
     */
    @GetMapping("/usuario")
    public ResponseEntity<List<SalaDto>> listarPorUsuario(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUtil.extraerId(authHeader);
        List<SalaDto> salas = salaService.listarSalasPorUsuario(usuarioId);
        return ResponseEntity.ok(salas);
    }
    
    /**
     * Recupera la información detallada de una sala concreta utilizando su identificador.
     * * @param id Identificador único de la sala solicitada.
     * @return Respuesta con los datos detallados de la sala en formato DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalaDto> obtenerPorId(@PathVariable Long id) {
        SalaDto sala = salaService.obtenerPorId(id);
        return ResponseEntity.ok(sala);
    }
    
    /**
     * Tramita la salida de un usuario de una sala. Si la sala se queda vacía, se eliminará del sistema.
     * * @param salaId Identificador de la sala que el usuario desea dejar.
     * @param authHeader Cabecera de autorización que contiene el token JWT para identificar al usuario que se marcha.
     * @return Estado 200 OK si se procesa la salida, o un estado Bad Request con el mensaje de error correspondiente.
     */
    @DeleteMapping("/{salaId}/abandonar")
    public ResponseEntity<?> abandonarSala(@PathVariable Long salaId, @RequestHeader("Authorization") String authHeader) {
        try {
            Long usuarioId = jwtUtil.extraerId(authHeader);
            salaService.abandonarSala(salaId, usuarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al abandonar la sala: " + e.getMessage());
        }
    }
}