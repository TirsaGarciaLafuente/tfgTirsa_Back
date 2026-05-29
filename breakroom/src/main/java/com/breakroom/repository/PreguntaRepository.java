package com.breakroom.repository;

import com.breakroom.Models.Entity.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    Optional<Pregunta> findBySalaIdAndFechaActiva(Long salaId, LocalDate fecha);

    List<Pregunta> findBySalaIdAndFechaActivaIsNull(Long salaId);

    boolean existsBySalaIdAndCreadorIdAndFechaActiva(Long salaId, Long creadorId, LocalDate fecha);
}