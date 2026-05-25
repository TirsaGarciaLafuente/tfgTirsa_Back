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

    // 1. Cuenta el total absoluto de votos emitidos en una pregunta concreta
    long countByPreguntaId(Long preguntaId);

    // 2. Agrupa y cuenta los votos recibidos por cada compañero (para la gráfica)
    @Query("SELECT v.votado.nombre, v.votado.avatar, COUNT(v) FROM Voto v WHERE v.pregunta.id = :preguntaId GROUP BY v.votado.nombre, v.votado.avatar")
    List<Object[]> obtenerRecuentoPorPregunta(@Param("preguntaId") Long preguntaId);
}