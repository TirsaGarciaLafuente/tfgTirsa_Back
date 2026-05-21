package com.breakroom.controllers;

import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.service.VotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/votaciones")
@CrossOrigin(origins = "*") // Permite que Angular se conecte sin problemas de CORS
public class VotacionController {

    @Autowired
    private VotacionService votacionService;

    /**
     * Endpoint para obtener la pregunta del día de una sala.
     * URL: GET http://localhost:8080/api/votaciones/sala/{salaId}
     */
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<?> obtenerPreguntaDelDia(@PathVariable Long salaId) {
        
        // Llamamos al servicio que programamos en el paso anterior
        Optional<Pregunta> pregunta = votacionService.obtenerPreguntaDelDia(salaId);
        
        // Si hay una pregunta activa para hoy, la devolvemos con un estado 200 OK
        if (pregunta.isPresent()) {
            return ResponseEntity.ok(pregunta.get());
        }
        
        // Si no hay preguntas en la recámara, devolvemos un mensaje avisando
        return ResponseEntity.ok("{\"mensaje\": \"No hay preguntas disponibles para esta sala. ¡Propón una!\"}");
    }
}