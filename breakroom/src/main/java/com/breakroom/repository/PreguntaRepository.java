package com.breakroom.repository;

import com.breakroom.Models.Entity.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    // 1. Busca la pregunta que tenga una fecha activa concreta en una sala específica
    Optional<Pregunta> findBySalaIdAndFechaActiva(Long salaId, LocalDate fecha);

    // 2. Trae todas las preguntas de una sala que aún no han sido activadas (fecha_activa es NULL)
    List<Pregunta> findBySalaIdAndFechaActivaIsNull(Long salaId);

    // 3. Comprueba si un usuario ya ha propuesto una pregunta hoy para una sala específica
    boolean existsBySalaIdAndCreadorIdAndFechaActiva(Long salaId, Long creadorId, LocalDate fecha);
}