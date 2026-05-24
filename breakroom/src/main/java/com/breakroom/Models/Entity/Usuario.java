package com.breakroom.Models.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios") 
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementable
    private Long id;

    @Column(name="Username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name="Password", nullable = false, length = 250)
    private String password;

    @Column(name="Email", unique = true, nullable = false)
    private String email;
    
    @Column(name="Nombre")
    private String nombre;
    
    @Column(name = "avatar")
    private String avatar;
    
    public Usuario(Long id) {
        this.id = id;
    }
}