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

/**
 * Implementación del servicio de votaciones que gestiona las preguntas del día,
 * el registro de los votos de los usuarios y el cálculo estadístico de los resultados.
 */
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

    /**
     * Busca la pregunta activa asignada para la fecha actual en una sala. Si no existe ninguna,
     * selecciona de forma aleatoria una de las plantillas base del sistema y la activa para el día de hoy.
     * * @param salaId Identificador de la sala de la cual se solicita la pregunta.
     * @return Un Optional que contiene la pregunta del día si está disponible o si se ha podido generar.
     */
    @Override
    public Optional<Pregunta> obtenerPreguntaDelDia(Long salaId) {
        LocalDate hoy = LocalDate.now();
        Optional<Pregunta> preguntaHoy = preguntaRepository.findBySalaIdAndFechaActiva(salaId, hoy);

        if (preguntaHoy.isPresent()) {
            return preguntaHoy;
        }

        List<PreguntaPlantilla> plantillas = plantillaRepository.findAll();
        if (plantillas.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(plantillas); 
        PreguntaPlantilla elegida = plantillas.get(0);

        Sala sala = salaRepository.findById(salaId).orElseThrow();
        Pregunta nuevaPregunta = new Pregunta();
        nuevaPregunta.setTexto(elegida.getTexto());
        nuevaPregunta.setSala(sala);
        nuevaPregunta.setFechaActiva(hoy);

        preguntaRepository.save(nuevaPregunta);
        return Optional.of(nuevaPregunta);
    }

    /**
     * Registra el voto emitido por un usuario hacia un compañero en una pregunta concreta,
     * controlando que no se pueda votar más de una vez en la misma pregunta.
     * * @param votanteId Identificador del usuario que emite el voto.
     * @param votadoId Identificador del usuario elegido o votado.
     * @param preguntaId Identificador de la pregunta a la que pertenece la votación.
     * @throws RuntimeException Si el usuario ya había votado previamente en esta misma pregunta.
     */
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

    /**
     * Comprueba si existe constancia de un voto previo por parte de un usuario para una pregunta concreta.
     * * @param usuarioId Identificador del usuario votante.
     * @param preguntaId Identificador de la pregunta que se está consultando.
     * @return true si el usuario ya ha votado, false si todavía tiene el voto disponible.
     */
    @Override
    public boolean haVotadoHoy(Long usuarioId, Long preguntaId) {
        return votoRepository.existsByVotanteIdAndPreguntaId(usuarioId, preguntaId);
    }

    /**
     * Recupera el recuento total de votos de una pregunta y procesa las estadísticas por participante,
     * calculando los porcentajes correspondientes redondeados a un decimal.
     * * @param preguntaId Identificador de la pregunta de la que se quieren extraer las métricas.
     * @return Lista de los resultados procesados en formato DTO para su representación visual.
     */
    @Override
    public List<ResultadoVotacionDto> obtenerResultados(Long preguntaId) {
        long totalVotos = votoRepository.countByPreguntaId(preguntaId);
        
        List<Object[]> resultadosBrutos = votoRepository.obtenerRecuentoPorPregunta(preguntaId);

        return resultadosBrutos.stream().map(obj -> {
            String nombre = (String) obj[0];
            String avatar = (String) obj[1];
            Long votos = (Long) obj[2];
            
            Double porcentaje = totalVotos > 0 ? (votos * 100.0) / totalVotos : 0.0;
            
            porcentaje = Math.round(porcentaje * 10.0) / 10.0;
            
            return new ResultadoVotacionDto(nombre, avatar, votos, porcentaje);
        }).collect(Collectors.toList());
    }
}