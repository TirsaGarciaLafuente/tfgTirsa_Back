package com.breakroom.Models.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "preguntas_plantilla")
@Data
public class PreguntaPlantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String texto;
}