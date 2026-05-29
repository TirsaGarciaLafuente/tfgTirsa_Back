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

/**
 * Controlador que gestiona el sistema de votaciones diarias dentro de las salas,
 * permitiendo consultar preguntas, emitir votos y ver los resultados.
 */
@RestController
@RequestMapping("/api/votaciones")
public class VotacionController {

    @Autowired
    private VotacionService votacionService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Recupera la pregunta asignada para el día de hoy en una sala específica.
     * * @param salaId Identificador de la sala de la cual se quiere obtener la pregunta.
     * @return Respuesta con los datos de la pregunta si existe, o un mensaje indicando que no se ha generado ninguna.
     */
    @GetMapping("/pregunta-del-dia/{salaId}")
    public ResponseEntity<?> obtenerPreguntaDelDia(@PathVariable Long salaId) {
        Optional<Pregunta> pregunta = votacionService.obtenerPreguntaDelDia(salaId);
        if (pregunta.isPresent()) {
            return ResponseEntity.ok(pregunta.get());
        } else {
            return ResponseEntity.ok().body(Map.of("mensaje", "Aún no hay pregunta generada hoy"));
        }
    }

    /**
     * Registra el voto de un usuario hacia otro para una pregunta determinada.
     * * @param payload Objeto DTO que contiene el ID de la pregunta y el ID del usuario votado.
     * @param authHeader Cabecera de autorización con el token JWT del usuario que vota.
     * @return Respuesta confirmando el registro del voto, o un estado de error si no se pudo procesar.
     */
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
    
    /**
     * Comprueba si el usuario conectado ya ha participado en la votación de una pregunta concreta.
     * * @param preguntaId Identificador de la pregunta que se desea verificar.
     * @param authHeader Cabecera de autorización que contiene el token JWT del usuario.
     * @return Respuesta con un valor booleano en la clave "haVotado", o un estado 401 si el token falla.
     */
    @GetMapping("/verificar-voto/{preguntaId}")
    public ResponseEntity<?> verificarVoto(
            @PathVariable Long preguntaId,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String headerLimpio = authHeader.replaceAll("[^a-zA-Z0-9\\-_\\.\\s]", "");
            
            Long usuarioId = jwtUtil.extraerId(headerLimpio);
            boolean haVotado = votacionService.haVotadoHoy(usuarioId, preguntaId);
            
            return ResponseEntity.ok().body(Map.of("haVotado", haVotado));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido o corrupto"));
        }
    }

    /**
     * Obtiene el recuento y las estadísticas de votos de una pregunta para poder mostrarlos en una gráfica.
     * * @param preguntaId Identificador de la pregunta de la que se quieren los resultados.
     * @return Respuesta con la lista de los resultados detallados en formato DTO.
     */
    @GetMapping("/resultados/{preguntaId}")
    public ResponseEntity<List<ResultadoVotacionDto>> obtenerResultados(@PathVariable Long preguntaId) {
        List<ResultadoVotacionDto> resultados = votacionService.obtenerResultados(preguntaId);
        return ResponseEntity.ok(resultados);
    }
}