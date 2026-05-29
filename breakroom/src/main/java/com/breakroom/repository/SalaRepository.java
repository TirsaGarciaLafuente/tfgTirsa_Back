package com.breakroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.breakroom.Models.Entity.Sala;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
    Optional<Sala> findByCodSala(String codSala);
    
    List<Sala> findByMiembros_Id(Long usuarioId);
}