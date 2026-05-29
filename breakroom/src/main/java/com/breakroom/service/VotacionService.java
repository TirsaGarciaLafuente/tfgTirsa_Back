package com.breakroom.service;

import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.Models.DTO.ResultadoVotacionDto;
import java.util.List;
import java.util.Optional;

public interface VotacionService {
    Optional<Pregunta> obtenerPreguntaDelDia(Long salaId);
    void votar(Long votanteId, Long votadoId, Long preguntaId);
    boolean haVotadoHoy(Long usuarioId, Long preguntaId);
    List<ResultadoVotacionDto> obtenerResultados(Long preguntaId);
}