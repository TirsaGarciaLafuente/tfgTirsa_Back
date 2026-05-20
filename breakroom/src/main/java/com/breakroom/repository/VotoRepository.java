package com.breakroom.repository;


import com.breakroom.Models.Entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    // Comprueba si ya existe un voto en la base de datos con ese votante y esa pregunta
    boolean existsByVotanteIdAndPreguntaId(Long votanteId, Long preguntaId);
}