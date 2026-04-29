package com.breakroom.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.breakroom.Models.DTO.SalaDto;
import com.breakroom.service.SalaService;

@RestController 
@RequestMapping("/api/salas") 
public class SalaController {
	
	@Autowired
	private SalaService salaService;

    /**
     * Crea una nueva sala y asigna al usuario como creador/miembro.
     * @param nombre El nombre que se le quiera dar a la sala.
     * @param usuarioId El ID del usuario que la crea.
     */
	@PostMapping("/crear")
    public ResponseEntity<SalaDto> crear(@RequestParam String nombre, @RequestParam Long usuarioId) {	
        // Llamamos al servicio que gestiona la lógica de creación y código único
        return ResponseEntity.ok(salaService.crearSala(nombre, usuarioId));
    }

    /**
     * Permite a un usuario unirse a una sala existente mediante su código de invitación.
     * @param codSala Código de 6 caracteres (ej: AB1234).
     * @param usuarioId ID del usuario que se quiere unir.
     */
    @PostMapping("/unirse")
    public ResponseEntity<SalaDto> unirse(@RequestParam String codSala, @RequestParam Long usuarioId) {
        // El servicio validará si la sala existe y si hay menos de 5 miembros
        return ResponseEntity.ok(salaService.unirseConCodigo(codSala, usuarioId));
    }

    /**
     * Obtiene todas las salas a las que pertenece un usuario.
     * Endpoint: GET /api/salas/usuario/1
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SalaDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<SalaDto> salas = salaService.listarSalasPorUsuario(usuarioId);
        return ResponseEntity.ok(salas);
    }
    
    /**
     * Obtiene los detalles de una sala específica por su ID.
     * Endpoint: GET /api/salas/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalaDto> obtenerPorId(@PathVariable Long id) {
        SalaDto sala = salaService.obtenerPorId(id);
        return ResponseEntity.ok(sala);
    }
}