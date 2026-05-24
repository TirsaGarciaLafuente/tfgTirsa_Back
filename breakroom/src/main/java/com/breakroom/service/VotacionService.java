package com.breakroom.service;

import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.Models.DTO.ResultadoVotacionDto;
import java.util.List;
import java.util.Optional;

public interface VotacionService {
    Optional<Pregunta> obtenerPreguntaDelDia(Long salaId);
    void votar(Long votanteId, Long votadoId, Long preguntaId);
    boolean haVotadoHoy(Long usuarioId, Long preguntaId);
    
    // El método antiguo guardarPropuesta puedes borrarlo, ya no lo usamos
    
    // NUEVO: Método para obtener los resultados con porcentajes
    List<ResultadoVotacionDto> obtenerResultados(Long preguntaId);
}