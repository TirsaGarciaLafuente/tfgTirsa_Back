package com.breakroom.service;


import com.breakroom.Models.Entity.Pregunta;
import java.util.Optional;

public interface VotacionService {
    
    // Método que ya teníamos
    Optional<Pregunta> obtenerPreguntaDelDia(Long salaId);

    // NUEVO: Registra el voto de un usuario hacia otro para una pregunta concreta
    void votar(Long votanteId, Long votadoId, Long preguntaId);

    // NUEVO: Devuelve true si el usuario ya ha votado en la pregunta de hoy
    boolean haVotadoHoy(Long usuarioId, Long preguntaId);
}