package com.breakroom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.breakroom.Models.DTO.LoginDto;
import com.breakroom.Models.DTO.RegistroDto;
import com.breakroom.Models.DTO.UsuarioDto;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.UsuarioRepository;
import com.breakroom.service.UsuarioService;

/**
 * Implementación del servicio de usuarios que gestiona los procesos de autenticación,
 * registro, recuperación de credenciales y edición de la información del perfil.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Realiza el proceso de inicio de sesión validando el nombre de usuario y verificando
     * que la contraseña coincida tras desencriptarla.
     * * @param loginDto DTO con las credenciales de acceso del usuario.
     * @return El perfil básico del usuario autenticado en formato DTO.
     * @throws RuntimeException Si el usuario no existe o si la contraseña ingresada es incorrecta.
     */
    @Override
    public UsuarioDto login(LoginDto loginDto) {
        Usuario user = usuarioRepository.findByUsername(loginDto.getUsername());

        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        UsuarioDto response = new UsuarioDto();
        response.setEmail(user.getEmail());
        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setAvatar(user.getAvatar()); 

        return response;
    }

    /**
     * Da de alta a un nuevo usuario en el sistema, encripta su contraseña de seguridad
     * y le asigna una imagen de avatar por defecto.
     * * @param registroDto DTO con la información obligatoria para el registro.
     * @return Los datos del usuario recién creado en formato DTO.
     * @throws RuntimeException Si el correo electrónico o el nombre de usuario ya están registrados.
     */
    @Override
    public UsuarioDto registro(RegistroDto registroDto) {
        Usuario newUser = new Usuario();
        newUser.setUsername(registroDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registroDto.getPassword()));
        newUser.setEmail(registroDto.getEmail());
        newUser.setNombre(registroDto.getNombre());
        newUser.setAvatar("/assets/default-avatar.jpg"); 

        UsuarioDto response = new UsuarioDto();
        try {
            Usuario user = usuarioRepository.save(newUser);
            response.setEmail(user.getEmail());
            response.setId(user.getId());
            response.setNombre(user.getNombre());
            response.setAvatar(user.getAvatar()); 
        } catch (Exception e) {
            throw new RuntimeException("Email o nombre de usuario ya existentes");
        }

        return response;
    }

    /**
     * Comprueba de manera conjunta si existen registros que coincidan con el email y el alias indicados.
     * * @param email Correo electrónico a contrastar.
     * @param username Nombre de usuario a contrastar.
     * @return true si los datos coinciden con un registro activo, false en caso contrario.
     */
    @Override
    public boolean verificarUsuario(String email, String username) {
        return usuarioRepository.existsByEmailAndUsername(email, username);
    }

    /**
     * Reemplaza la contraseña actual de un usuario por una nueva debidamente encriptada.
     * * @param email Correo electrónico para validar la identidad.
     * @param username Nombre de usuario para validar la identidad.
     * @param password Nueva clave secreta sin encriptar.
     * @throws RuntimeException Si no se localiza ningún usuario con los datos de validación proporcionados.
     */
    @Override
    public void cambiarPassword(String email, String username, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndUsername(email, username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
    }

    /**
     * Busca y extrae el identificador numérico exclusivo de un usuario mediante su alias de acceso.
     * * @param username El nombre de usuario que se desea consultar.
     * @return El ID correspondiente al usuario.
     * @throws RuntimeException Si no se encuentra el alias indicado en el sistema.
     */
    @Override
    public Long obtenerIdPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con el username: " + username);
        }
        return usuario.getId();
    }

    /**
     * Recupera el perfil completo con todos los campos informativos de un usuario a partir de su ID.
     * * @param id Identificador único del usuario.
     * @return Un DTO estructurado con los detalles del perfil público y privado.
     * @throws RuntimeException Si el identificador especificado no existe en la base de datos.
     */
    @Override
    public UsuarioDto obtenerPerfil(Long id) {
        Usuario user = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioDto dto = new UsuarioDto();
        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setTitulo(user.getTitulo());
        dto.setDescripcion(user.getDescripcion());
        return dto;
    }

    /**
     * Modifica la ruta o identificador de la imagen de avatar configurada por el usuario.
     * * @param id Identificador único del usuario que cambia su imagen.
     * @param nuevoAvatar Nueva ruta o cadena identificativa de la imagen elegida.
     * @throws RuntimeException Si no se encuentra el usuario al intentar la actualización.
     */
    @Override
    public void actualizarAvatar(Long id, String nuevoAvatar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setAvatar(nuevoAvatar);
        usuarioRepository.save(usuario);
    }
    
    /**
     * Actualiza los datos de presentación personales del perfil de usuario como nombre, título profesional y descripción.
     * * @param id Identificador único del usuario que edita su información.
     * @param usuarioDto DTO que contiene los nuevos valores a persistir.
     * @throws RuntimeException Si el usuario solicitado no se encuentra en el sistema.
     */
    @Override
    public void actualizarPerfil(Long id, UsuarioDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setTitulo(usuarioDto.getTitulo());
        usuario.setDescripcion(usuarioDto.getDescripcion());
        
        usuarioRepository.save(usuario);
    }
}