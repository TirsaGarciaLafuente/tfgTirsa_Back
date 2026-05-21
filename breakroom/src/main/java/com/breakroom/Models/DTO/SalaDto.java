package com.breakroom.Models.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SalaDto {
    private Long id;
    private String nombre;
    private String codSala;
    private LocalDateTime fechaCreacion;
    private List<UsuarioDto> usuarios; 
}