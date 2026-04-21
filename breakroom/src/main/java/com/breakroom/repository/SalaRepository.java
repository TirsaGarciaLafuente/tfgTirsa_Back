package com.breakroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.breakroom.Models.Entity.Sala;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
    // Este método es clave para que los usuarios se unan con el código
    Optional<Sala> findByCodSala(String codSala);
    
    // Buscamos las salas que contienen a un usuario específico en su Set de miembros
    List<Sala> findByMiembros_Id(Long usuarioId);
}