package com.breakroom.service.impl;

import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.Models.Entity.Voto;
import com.breakroom.repository.PreguntaRepository;
import com.breakroom.repository.VotoRepository;
import com.breakroom.service.VotacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VotacionServiceImpl implements VotacionService {

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Override
    public Optional<Pregunta> obtenerPreguntaDelDia(Long salaId) {
        LocalDate hoy = LocalDate.now();
        Optional<Pregunta> preguntaHoy = preguntaRepository.findBySalaIdAndFechaActiva(salaId, hoy);

        if (preguntaHoy.isPresent()) {
            return preguntaHoy;
        }

        List<Pregunta> pendientes = preguntaRepository.findBySalaIdAndFechaActivaIsNull(salaId);

        if (pendientes.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(pendientes);
        Pregunta preguntaElegida = pendientes.get(0);

        preguntaElegida.setFechaActiva(hoy);
        preguntaRepository.save(preguntaElegida);

        return Optional.of(preguntaElegida);
    }

    @Override
    public void votar(Long votanteId, Long votadoId, Long preguntaId) {
        if (haVotadoHoy(votanteId, preguntaId)) {
            throw new RuntimeException("Ya has emitido tu voto para esta pregunta hoy");
        }

        Voto nuevoVoto = new Voto();
        
        nuevoVoto.setVotante(new Usuario(votanteId));
        nuevoVoto.setVotado(new Usuario(votadoId));
        
        Pregunta preguntaRef = new Pregunta();
        preguntaRef.setId(preguntaId);
        nuevoVoto.setPregunta(preguntaRef);
        
        nuevoVoto.setFechaVoto(LocalDateTime.now());

        votoRepository.save(nuevoVoto);
    }

    @Override
    public boolean haVotadoHoy(Long usuarioId, Long preguntaId) {
        return votoRepository.existsByVotanteIdAndPreguntaId(usuarioId, preguntaId);
    }
}