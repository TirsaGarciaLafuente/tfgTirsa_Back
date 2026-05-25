package com.breakroom.Models.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MensajeDto {
    private Long id;
    private Long idUsuario;
    private String avatar;
    private String texto;
    private LocalDateTime fechaEnvio;
    private String nombreAutor; 
}