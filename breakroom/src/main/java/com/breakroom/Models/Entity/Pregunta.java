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

    private String texto; // Ejemplo: "¿Quién se toma el café más largo?"

    // Guarda el día exacto en que se activa (aaaa-mm-dd). 
    // Si está en null, significa que es una pregunta en la "recámara" esperando a ser elegida.
    private LocalDate fechaActiva;

    // Relación: Muchas preguntas pueden haber sido creadas por el mismo Usuario
    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    // Relación: Muchas preguntas pertenecen a la misma Sala
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;
}