package com.breakroom.Models.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "votos")
@Data
@NoArgsConstructor
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El usuario de la sala que emite el voto
    @ManyToOne
    @JoinColumn(name = "votante_id")
    private Usuario votante;

    // El usuario de la sala que es votado/nominado
    @ManyToOne
    @JoinColumn(name = "votado_id")
    private Usuario votado;

    // La pregunta activa a la que pertenece este voto
    @ManyToOne
    @JoinColumn(name = "pregunta_id")
    private Pregunta pregunta;

    // Fecha y hora exacta de cuando se hizo clic en votar
    private LocalDateTime fechaVoto = LocalDateTime.now();
}