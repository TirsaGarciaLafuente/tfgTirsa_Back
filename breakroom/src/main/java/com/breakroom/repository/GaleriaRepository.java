package com.breakroom.repository;

import com.breakroom.Models.Entity.Galeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface GaleriaRepository extends JpaRepository<Galeria, Long> {
    // Busca todos los dibujos de una sala específica ordenados por los más recientes
    List<Galeria> findBySalaIdOrderByFechaCreacionDesc(Long salaId);
}