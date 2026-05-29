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

    @ManyToOne
    @JoinColumn(name = "votante_id")
    private Usuario votante;

    @ManyToOne
    @JoinColumn(name = "votado_id")
    private Usuario votado;

    @ManyToOne
    @JoinColumn(name = "pregunta_id")
    private Pregunta pregunta;

    private LocalDateTime fechaVoto = LocalDateTime.now();
}