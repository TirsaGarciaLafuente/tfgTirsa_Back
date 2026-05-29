package com.breakroom.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.UsuarioRepository;

/**
 * Servicio personalizado que conecta el sistema de autenticación de Spring Security 
 * con la base de datos para validar las credenciales de los usuarios.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario en la base de datos a partir de su nombre de usuario 
     * y devuelve un objeto compatible con Spring Security.
     * * @param username El nombre de usuario que intenta iniciar sesión.
     * @return El objeto UserDetails con las credenciales del usuario.
     * @throws UsernameNotFoundException Si no se encuentra al usuario en el sistema.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                
        Usuario usuario = usuarioRepository.findByUsername(username);
        
        return new User(usuario.getUsername(), usuario.getPassword(), new ArrayList<>());

    }
}