package com.breakroom.service.impl;

import com.breakroom.Models.DTO.SalaDto;
import com.breakroom.Models.DTO.UsuarioDto;
import com.breakroom.Models.Entity.Sala;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.SalaRepository;
import com.breakroom.repository.UsuarioRepository;
import com.breakroom.service.SalaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SalaServiceImpl implements SalaService{

	@Autowired
    private SalaRepository salaRepository;
	
	@Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    @Override
    public SalaDto crearSala(String nombre, Long usuarioId) {
        // 1. Buscamos al usuario que crea la sala
        Usuario creador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // 2. Creamos la nueva entidad Sala
        Sala nuevaSala = Sala.builder()
                .nombre(nombre)
                .codSala(generarCodigoUnico()) // Generamos el código
                .build();

        // 3. Añadimos al creador a la lista de miembros
        nuevaSala.getMiembros().add(creador);

        // 4. Guardamos en la base de datos
        Sala salaGuardada = salaRepository.save(nuevaSala);

        // 5. Devolvemos el DTO
        return mapearADto(salaGuardada);
    }

    @Transactional
    @Override
    public SalaDto unirseConCodigo(String codSala, Long usuarioId) {
        // 1. Buscamos la sala por su código
        Sala sala = salaRepository.findByCodSala(codSala)
                .orElseThrow(() -> new RuntimeException("El código de sala no existe"));

        // --> RESTRICCIÓN DE AFORO <--
        if (sala.getMiembros().size() >= 8) {
            throw new RuntimeException("La sala ya está llena");
        }

        // 2. Buscamos al usuario que quiere entrar
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Añadimos el usuario a la sala
        sala.getMiembros().add(usuario);

        // 4. Guardamos los cambios y devolvemos DTO
        return mapearADto(salaRepository.save(sala));
    }
    
     @Override
    public List<SalaDto> listarSalasPorUsuario(Long usuarioId) {
        // 1. Buscamos todas las salas vinculadas a ese ID de usuario
        List<Sala> salas = salaRepository.findByMiembros_Id(usuarioId);
        
        // 2. Convertimos la lista de entidades a DTOs usando Stream
        return salas.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Override
    public SalaDto obtenerPorId(Long id) {
        // 1. Buscamos la sala por ID
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada con ID: " + id));

        // 2. Reutilizamos tu método mapearADto para devolver el DTO
        return mapearADto(sala);
    }

    // NUEVO MÉTODO: Lógica para eliminar la relación entre el usuario y la sala
    @Transactional
    @Override
    public void abandonarSala(Long salaId, Long usuarioId) {
        // 1. Buscar la sala
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada con ID: " + salaId));

        // 2. Buscar al usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // 3. Quitar al usuario de la lista de miembros
        sala.getMiembros().remove(usuario);

        // 4. Si la sala se queda vacía, se elimina. Si no, se guardan los cambios.
        if (sala.getMiembros().isEmpty()) {
            salaRepository.delete(sala);
        } else {
            salaRepository.save(sala);
        }
    }

    // Método auxiliar para generar códigos tipo "XJ92L1"
    private String generarCodigoUnico() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // Convertimos la Entidad en DTO para el Frontend
    private SalaDto mapearADto(Sala sala) {
        SalaDto dto = new SalaDto();
        dto.setId(sala.getId());
        dto.setNombre(sala.getNombre());
        dto.setCodSala(sala.getCodSala());
        dto.setFechaCreacion(sala.getFechaCreacion());
        
        // NUEVA LÓGICA: Mapeamos la lista de miembros de la entidad a UsuarioDto en el DTO
        if (sala.getMiembros() != null) {
            List<UsuarioDto> listaUsuariosDto = sala.getMiembros().stream()
                .map(usuario -> {
                    UsuarioDto uDto = new UsuarioDto();
                    uDto.setId(usuario.getId());
                    uDto.setNombre(usuario.getNombre());
                    uDto.setEmail(usuario.getEmail());
                    return uDto;
                })
                .collect(Collectors.toList());
            
            dto.setUsuarios(listaUsuariosDto);
        }
        
        return dto;
    }
}