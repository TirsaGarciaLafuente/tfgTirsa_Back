package com.breakroom.Models.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "preguntas")
@Data
@NoArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto; 

    private LocalDate fechaActiva;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;
}