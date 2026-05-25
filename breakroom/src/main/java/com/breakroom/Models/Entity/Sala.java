package com.breakroom.Models.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList; // Añade este import
import java.util.HashSet;
import java.util.List;    // Añade este import
import java.util.Set;

@Entity
@Table(name = "sala")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(name = "cod_sala", nullable = false, unique = true, length = 50)
    private String codSala;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sala_usuarios",
        joinColumns = @JoinColumn(name = "sala_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @Builder.Default
    private Set<Usuario> miembros = new HashSet<>();

    // RELACIÓN BIEN MAPEADA: Usamos List para evitar problemas con @Data de Pregunta
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pregunta> preguntas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.miembros == null) {
            this.miembros = new HashSet<>();
        }
        if (this.preguntas == null) {
            this.preguntas = new ArrayList<>();
        }
    }
}