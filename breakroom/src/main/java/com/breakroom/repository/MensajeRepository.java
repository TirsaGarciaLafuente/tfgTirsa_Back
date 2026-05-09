package com.breakroom.repository;

import com.breakroom.Models.Entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    // Busca los mensajes de una sala y los ordena por fecha (el más antiguo primero)
    List<Mensaje> findBySalaIdOrderByFechaEnvioAsc(Long salaId);
}