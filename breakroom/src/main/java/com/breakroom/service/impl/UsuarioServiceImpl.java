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
public class UsuarioServiceImpl implements UsuarioService{

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

	    return response;
	}


	@Override
	public UsuarioDto registro(RegistroDto registroDto) {
		Usuario newUser = new Usuario(null, registroDto.getUsername(), passwordEncoder.encode(registroDto.getPassword()), registroDto.getEmail(), registroDto.getNombre());
		UsuarioDto response = new UsuarioDto();
		try {
			Usuario user = usuarioRepository.save(newUser);
			response.setEmail(user.getEmail());
			response.setId(user.getId());
			response.setNombre(user.getNombre());
		}catch(Exception e) {
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
}
