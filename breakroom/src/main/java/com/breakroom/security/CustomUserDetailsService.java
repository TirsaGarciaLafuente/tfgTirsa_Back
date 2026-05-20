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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Lógica real que debes descomentar cuando importes tu repositorio:
        
        Usuario usuario = usuarioRepository.findByUsername(username);
        
        return new User(usuario.getUsername(), usuario.getPassword(), new ArrayList<>());

    }
}