package com.breakroom.controllers;

import com.breakroom.Models.DTO.MensajeDto;
import com.breakroom.security.JwtUtil;
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
    
	@Autowired
	private JwtUtil jwtUtil;

    @GetMapping("/historial/{salaId}")
    public ResponseEntity<List<MensajeDto>> obtenerHistorial(@PathVariable Long salaId) {
        return ResponseEntity.ok(mensajeService.obtenerHistorial(salaId));
    }

    @PostMapping("/enviar")
    public ResponseEntity<MensajeDto> enviarMensaje(
            @RequestParam Long salaId,
            @RequestParam String texto, @RequestHeader("Authorization") String authHeader) {
		Long usuarioId = jwtUtil.extraerId(authHeader);
        return ResponseEntity.ok(mensajeService.enviarMensaje(salaId, usuarioId, texto));
    }
}