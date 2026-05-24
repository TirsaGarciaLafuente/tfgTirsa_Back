package com.breakroom.repository;

import com.breakroom.Models.Entity.PreguntaPlantilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreguntaPlantillaRepository extends JpaRepository<PreguntaPlantilla, Long> {
    // Al heredar de JpaRepository, ya tenemos los métodos para contar, buscar y listar las plantillas.
}