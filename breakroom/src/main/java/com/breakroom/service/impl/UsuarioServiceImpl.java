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

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        response.setAvatar(user.getAvatar()); // Asegúrate de incluir el avatar

        return response;
    }

    @Override
    public UsuarioDto registro(RegistroDto registroDto) {
        // Creamos el objeto vacío y usamos setters
        Usuario newUser = new Usuario();
        newUser.setUsername(registroDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registroDto.getPassword()));
        newUser.setEmail(registroDto.getEmail());
        newUser.setNombre(registroDto.getNombre());
        // El avatar por defecto puede ser null o una ruta por defecto
        newUser.setAvatar("/assets/avatar-default.jpg"); 

        UsuarioDto response = new UsuarioDto();
        try {
            Usuario user = usuarioRepository.save(newUser);
            response.setEmail(user.getEmail());
            response.setId(user.getId());
            response.setNombre(user.getNombre());
            response.setAvatar(user.getAvatar()); // Incluimos el avatar en la respuesta
        } catch (Exception e) {
            throw new RuntimeException("Email o nombre de usuario ya existentes");
        }

        return response;
    }
    @Override
    public boolean verificarUsuario(String email, String username) {
        return usuarioRepository.existsByEmailAndUsername(email, username);
    }

    @Override
    public void cambiarPassword(String email, String username, String password) {
        Usuario usuario = usuarioRepository.findByEmailAndUsername(email, username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
    }

    @Override
    public Long obtenerIdPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con el username: " + username);
        }
        return usuario.getId();
    }

    // --- MÉTODOS NUEVOS PARA AVATAR ---

    @Override
    public UsuarioDto obtenerPerfil(Long id) {
        Usuario user = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioDto dto = new UsuarioDto();
        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

    @Override
    public void actualizarAvatar(Long id, String nuevoAvatar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setAvatar(nuevoAvatar);
        usuarioRepository.save(usuario);
    }
}