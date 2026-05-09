package com.breakroom.service.impl;

import com.breakroom.Models.DTO.MensajeDto;
import com.breakroom.Models.Entity.Mensaje;
import com.breakroom.Models.Entity.Sala;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.MensajeRepository;
import com.breakroom.repository.SalaRepository;
import com.breakroom.repository.UsuarioRepository;
import com.breakroom.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MensajeServiceImpl implements MensajeService {

    @Autowired private MensajeRepository mensajeRepository;
    @Autowired private SalaRepository salaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Override
    public List<MensajeDto> obtenerHistorial(Long salaId) {
        List<Mensaje> mensajes = mensajeRepository.findBySalaIdOrderByFechaEnvioAsc(salaId);
        return mensajes.stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    public MensajeDto enviarMensaje(Long salaId, Long usuarioId, String texto) {
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mensaje nuevoMensaje = Mensaje.builder()
                .texto(texto)
                .sala(sala)
                .autor(autor)
                .build();

        Mensaje mensajeGuardado = mensajeRepository.save(nuevoMensaje);
        return mapearADto(mensajeGuardado);
    }

    private MensajeDto mapearADto(Mensaje mensaje) {
        MensajeDto dto = new MensajeDto();
        dto.setId(mensaje.getId());
        dto.setTexto(mensaje.getTexto());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setNombreAutor(mensaje.getAutor().getNombre()); 
        return dto;
    }
}