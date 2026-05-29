package com.breakroom.repository;

import com.breakroom.Models.Entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByVotanteIdAndPreguntaId(Long votanteId, Long preguntaId);

    long countByPreguntaId(Long preguntaId);

    @Query("SELECT v.votado.nombre, v.votado.avatar, COUNT(v) FROM Voto v WHERE v.pregunta.id = :preguntaId GROUP BY v.votado.nombre, v.votado.avatar")
    List<Object[]> obtenerRecuentoPorPregunta(@Param("preguntaId") Long preguntaId);
}