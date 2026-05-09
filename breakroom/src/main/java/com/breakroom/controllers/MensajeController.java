package com.breakroom.controllers;

import com.breakroom.Models.DTO.MensajeDto;
import com.breakroom.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @GetMapping("/historial/{salaId}")
    public ResponseEntity<List<MensajeDto>> obtenerHistorial(@PathVariable Long salaId) {
        return ResponseEntity.ok(mensajeService.obtenerHistorial(salaId));
    }

    @PostMapping("/enviar")
    public ResponseEntity<MensajeDto> enviarMensaje(
            @RequestParam Long salaId, 
            @RequestParam Long usuarioId, 
            @RequestParam String texto) {
        return ResponseEntity.ok(mensajeService.enviarMensaje(salaId, usuarioId, texto));
    }
}