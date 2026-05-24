package com.breakroom.Models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoVotacionDto {
    private String nombre;
    private Long votos;
    private Double porcentaje;
}