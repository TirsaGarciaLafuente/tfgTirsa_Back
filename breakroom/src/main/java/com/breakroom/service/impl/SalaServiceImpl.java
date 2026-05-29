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

/**
 * Implementación del servicio de salas que gestiona el ciclo de vida de los espacios virtuales,
 * incluyendo la creación, el acceso mediante invitaciones y la salida de los participantes.
 */
@Service
public class SalaServiceImpl implements SalaService{

    @Autowired
    private SalaRepository salaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Registra una nueva sala en el sistema, genera su código de invitación alfanumérico
     * y añade al usuario creador como primer miembro participante.
     * * @param nombre El nombre descriptivo asignado a la sala.
     * @param usuarioId Identificador del usuario que crea el espacio.
     * @return Los datos de la nueva sala estructurados en formato DTO.
     * @throws RuntimeException Si el identificador del usuario no corresponde a ninguno registrado.
     */
    @Transactional
    @Override
    public SalaDto crearSala(String nombre, Long usuarioId) {
        Usuario creador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        Sala nuevaSala = Sala.builder()
                .nombre(nombre)
                .codSala(generarCodigoUnico()) 
                .build();

        nuevaSala.getMiembros().add(creador);

        Sala salaGuardada = salaRepository.save(nuevaSala);

        return mapearADto(salaGuardada);
    }

    /**
     * Incorpora a un usuario dentro de una sala validando su código de acceso
     * y comprobando que no se supere el límite de aforo permitido.
     * * @param codSala Código único de 6 caracteres asignado a la sala.
     * @param usuarioId Identificador del usuario que solicita el ingreso.
     * @return Los datos actualizados de la sala con el nuevo miembro incorporado.
     * @throws RuntimeException Si el código es erróneo, la sala está al límite de capacidad o el usuario no existe.
     */
    @Transactional
    @Override
    public SalaDto unirseConCodigo(String codSala, Long usuarioId) {
        Sala sala = salaRepository.findByCodSala(codSala)
                .orElseThrow(() -> new RuntimeException("El código de sala no existe"));

        if (sala.getMiembros().size() >= 8) {
            throw new RuntimeException("La sala ya está llena");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        sala.getMiembros().add(usuario);

        return mapearADto(salaRepository.save(sala));
    }
    
    /**
     * Localiza y devuelve todas las salas activas en las que participa un usuario concreto.
     * * @param usuarioId Identificador único del usuario a consultar.
     * @return Una lista con las salas asociadas transformadas a formato DTO.
     */
     @Override
    public List<SalaDto> listarSalasPorUsuario(Long usuarioId) {
        List<Sala> salas = salaRepository.findByMiembros_Id(usuarioId);
        
        return salas.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    /**
     * Busca y obtiene la información de una sala específica mediante su identificador único.
     * * @param id Identificador de la sala requerida.
     * @return Los datos de la sala mapeados a formato DTO.
     * @throws RuntimeException Si no existe ninguna sala asociada a ese identificador.
     */
    @Override
    public SalaDto obtenerPorId(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada con ID: " + id));

        return mapearADto(sala);
    }

    /**
     * Gestiona la baja de un miembro dentro de una sala. En caso de que sea el último
     * participante restante, se procede a la eliminación definitiva de la sala.
     * * @param salaId Identificador de la sala que se abandona.
     * @param usuarioId Identificador del usuario que solicita la salida.
     * @throws RuntimeException Si la sala o el usuario no existen en la base de datos.
     */
    @Transactional
    @Override
    public void abandonarSala(Long salaId, Long usuarioId) {
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada con ID: " + salaId));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        sala.getMiembros().remove(usuario);

        if (sala.getMiembros().isEmpty()) {
            salaRepository.delete(sala);
        } else {
            salaRepository.save(sala);
        }
    }

    /**
     * Genera un código alfanumérico aleatorio y único de 6 caracteres en mayúsculas.
     * * @return Una cadena de texto corta y aleatoria.
     */
    private String generarCodigoUnico() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * Transfiere las propiedades de una entidad Sala a un objeto de transferencia SalaDto,
     * adaptando también de forma interna la lista de miembros adjuntos.
     * * @param sala Objeto de la entidad origen que se va a procesar.
     * @return El objeto DTO equivalente preparado para su envío externo.
     */
    private SalaDto mapearADto(Sala sala) {
        SalaDto dto = new SalaDto();
        dto.setId(sala.getId());
        dto.setNombre(sala.getNombre());
        dto.setCodSala(sala.getCodSala());
        dto.setFechaCreacion(sala.getFechaCreacion());
        
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