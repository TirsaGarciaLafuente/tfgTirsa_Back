package com.breakroom.controllers;

import com.breakroom.Models.DTO.MensajeDto;
import com.breakroom.security.JwtUtil;
import com.breakroom.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar el envío y la recuperación de mensajes dentro
 * de las salas de chat.
 */
@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Recupera la lista de mensajes anteriores de una sala de chat concreta.
     * * @param salaId Identificador de la sala de la que se quiere consultar el historial.
     * @return Respuesta con la lista de mensajes en formato DTO.
     */
    @GetMapping("/historial/{salaId}")
    public ResponseEntity<List<MensajeDto>> obtenerHistorial(@PathVariable Long salaId) {
        return ResponseEntity.ok(mensajeService.obtenerHistorial(salaId));
    }

    /**
     * Publica un nuevo mensaje en una sala específica identificando al autor mediante su sesión.
     * * @param salaId Identificador de la sala a la que va destinado el mensaje.
     * @param texto El contenido del mensaje que se va a enviar.
     * @param authHeader Cabecera de autorización con el token JWT para identificar al emisor.
     * @return El mensaje recién guardado y procesado como DTO.
     */
    @PostMapping("/enviar")
    public ResponseEntity<MensajeDto> enviarMensaje(
            @RequestParam Long salaId,
            @RequestParam String texto, @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = jwtUtil.extraerId(authHeader);
        return ResponseEntity.ok(mensajeService.enviarMensaje(salaId, usuarioId, texto));
    }
}