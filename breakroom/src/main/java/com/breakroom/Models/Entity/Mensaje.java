package com.breakroom.Models.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String texto;

    private LocalDateTime fechaEnvio;

    // Relación: Muchos mensajes pueden ser escritos por un mismo usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario autor;

    // Relación: Muchos mensajes pertenecen a una misma sala
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    // Este método asigna la fecha y hora exacta automáticamente justo antes de guardar en la base de datos
    @PrePersist
    protected void onCreate() {
        fechaEnvio = LocalDateTime.now();
    }
}