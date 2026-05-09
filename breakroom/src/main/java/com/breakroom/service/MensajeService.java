package com.breakroom.service;

import com.breakroom.Models.DTO.MensajeDto;
import java.util.List;

public interface MensajeService {
    List<MensajeDto> obtenerHistorial(Long salaId);
    MensajeDto enviarMensaje(Long salaId, Long usuarioId, String texto);
}