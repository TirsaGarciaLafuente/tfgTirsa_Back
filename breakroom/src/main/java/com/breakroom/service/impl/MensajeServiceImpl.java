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

/**
 * Implementación del servicio de mensajería que gestiona el almacenamiento, 
 * envío y recuperación del historial de chats en las salas.
 */
@Service
public class MensajeServiceImpl implements MensajeService {

    @Autowired private MensajeRepository mensajeRepository;
    @Autowired private SalaRepository salaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    /**
     * Recupera todos los mensajes de una sala específica ordenados cronológicamente
     * y los transforma a formato DTO.
     * * @param salaId Identificador de la sala de chat.
     * @return Lista de mensajes optimizados para su visualización.
     */
    @Override
    public List<MensajeDto> obtenerHistorial(Long salaId) {
        List<Mensaje> mensajes = mensajeRepository.findBySalaIdOrderByFechaEnvioAsc(salaId);
        return mensajes.stream().map(this::mapearADto).collect(Collectors.toList());
    }

    /**
     * Registra un nuevo mensaje en el sistema asociándolo a una sala y a un usuario autor.
     * * @param salaId Identificador de la sala donde se envía el mensaje.
     * @param usuarioId Identificador del usuario que redacta el mensaje.
     * @param texto Contenido textual del mensaje.
     * @return El mensaje guardado convertido a formato DTO.
     * @throws RuntimeException Si la sala o el usuario indicados no existen en la base de datos.
     */
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

    /**
     * Pasa los datos de una entidad interna Mensaje a un objeto de transferencia de datos MensajeDto.
     * * @param mensaje Entidad original con los datos de la base de datos.
     * @return Objeto DTO con la información lista para el frontend.
     */
    private MensajeDto mapearADto(Mensaje mensaje) {
        MensajeDto dto = new MensajeDto();
        dto.setId(mensaje.getId());
        dto.setIdUsuario(mensaje.getAutor().getId());
        dto.setAvatar(mensaje.getAutor().getAvatar());
        dto.setTexto(mensaje.getTexto());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setNombreAutor(mensaje.getAutor().getNombre()); 
        return dto;
    }
}