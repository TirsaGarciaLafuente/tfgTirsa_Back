package com.breakroom.controllers;

import com.breakroom.Models.DTO.ResultadoVotacionDto;
import com.breakroom.Models.DTO.VotoRequestDto;
import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.security.JwtUtil;
import com.breakroom.service.VotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/votaciones")
public class VotacionController {

    @Autowired
    private VotacionService votacionService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/pregunta-del-dia/{salaId}")
    public ResponseEntity<?> obtenerPreguntaDelDia(@PathVariable Long salaId) {
        Optional<Pregunta> pregunta = votacionService.obtenerPreguntaDelDia(salaId);
        if (pregunta.isPresent()) {
            return ResponseEntity.ok(pregunta.get());
        } else {
            return ResponseEntity.ok().body(Map.of("mensaje", "Aún no hay pregunta generada hoy"));
        }
    }

    @PostMapping("/votar")
    public ResponseEntity<?> votar(
            @RequestBody VotoRequestDto payload,
            @RequestHeader("Authorization") String authHeader) {
        
        String tokenLimpio = authHeader;

        try {
            Long votanteId = jwtUtil.extraerId(tokenLimpio);
            Long votadoId = payload.getVotadoId();
            Long preguntaId = payload.getPreguntaId();

            votacionService.votar(votanteId, votadoId, preguntaId);
            return ResponseEntity.ok().body(Map.of("mensaje", "Voto registrado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/verificar-voto/{preguntaId}")
    public ResponseEntity<?> verificarVoto(
            @PathVariable Long preguntaId,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            // Aplicamos la misma limpieza de seguridad
            String headerLimpio = authHeader.replaceAll("[^a-zA-Z0-9\\-_\\.\\s]", "");
            
            Long usuarioId = jwtUtil.extraerId(headerLimpio);
            boolean haVotado = votacionService.haVotadoHoy(usuarioId, preguntaId);
            
            return ResponseEntity.ok().body(Map.of("haVotado", haVotado));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido o corrupto"));
        }
    }

    // NUEVO ENDPOINT PARA LA GRÁFICA
    @GetMapping("/resultados/{preguntaId}")
    public ResponseEntity<List<ResultadoVotacionDto>> obtenerResultados(@PathVariable Long preguntaId) {
        List<ResultadoVotacionDto> resultados = votacionService.obtenerResultados(preguntaId);
        return ResponseEntity.ok(resultados);
    }
}