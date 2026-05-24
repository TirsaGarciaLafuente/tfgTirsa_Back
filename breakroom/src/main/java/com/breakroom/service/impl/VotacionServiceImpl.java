package com.breakroom.service.impl;

import com.breakroom.Models.DTO.ResultadoVotacionDto;
import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.Models.Entity.PreguntaPlantilla;
import com.breakroom.Models.Entity.Sala;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.Models.Entity.Voto;
import com.breakroom.repository.PreguntaPlantillaRepository;
import com.breakroom.repository.PreguntaRepository;
import com.breakroom.repository.SalaRepository;
import com.breakroom.repository.VotoRepository;
import com.breakroom.service.VotacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VotacionServiceImpl implements VotacionService {

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private PreguntaPlantillaRepository plantillaRepository;

    @Override
    public Optional<Pregunta> obtenerPreguntaDelDia(Long salaId) {
        LocalDate hoy = LocalDate.now();
        Optional<Pregunta> preguntaHoy = preguntaRepository.findBySalaIdAndFechaActiva(salaId, hoy);

        if (preguntaHoy.isPresent()) {
            return preguntaHoy;
        }

        // Si no hay pregunta hoy (ej: la sala se acaba de crear), le asignamos una al momento
        List<PreguntaPlantilla> plantillas = plantillaRepository.findAll();
        if (plantillas.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(plantillas); // Mezcla aleatoria
        PreguntaPlantilla elegida = plantillas.get(0);

        Sala sala = salaRepository.findById(salaId).orElseThrow();
        Pregunta nuevaPregunta = new Pregunta();
        nuevaPregunta.setTexto(elegida.getTexto());
        nuevaPregunta.setSala(sala);
        nuevaPregunta.setFechaActiva(hoy);
        // creador se queda en null automáticamente

        preguntaRepository.save(nuevaPregunta);
        return Optional.of(nuevaPregunta);
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

    @Override
    public List<ResultadoVotacionDto> obtenerResultados(Long preguntaId) {
        // 1. Obtenemos el total de votos para calcular el 100%
        long totalVotos = votoRepository.countByPreguntaId(preguntaId);
        
        // 2. Obtenemos la lista agrupada (Nombre compañero -> Cantidad de votos)
        List<Object[]> resultadosBrutos = votoRepository.obtenerRecuentoPorPregunta(preguntaId);

        // 3. Transformamos esos datos brutos en nuestro DTO limpio con el porcentaje exacto
        return resultadosBrutos.stream().map(obj -> {
            String nombre = (String) obj[0];
            Long votos = (Long) obj[1];
            
            // Regla de tres simple: (votos / total) * 100
            Double porcentaje = totalVotos > 0 ? (votos * 100.0) / totalVotos : 0.0;
            
            // Redondeamos a 1 decimal para que quede estético en Angular
            porcentaje = Math.round(porcentaje * 10.0) / 10.0;
            
            return new ResultadoVotacionDto(nombre, votos, porcentaje);
        }).collect(Collectors.toList());
    }
}