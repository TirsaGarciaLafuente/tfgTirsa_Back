package com.breakroom.service;

import com.breakroom.Models.DTO.LoginDto;
import com.breakroom.Models.DTO.RegistroDto;
import com.breakroom.Models.DTO.UsuarioDto;

public interface UsuarioService {
	
    UsuarioDto login (LoginDto loginDto);
	
    UsuarioDto registro (RegistroDto registroDto);
	
    boolean verificarUsuario(String email, String username);
	
    void cambiarPassword(String email, String username, String password);
	
    Long obtenerIdPorUsername(String username);
    
    UsuarioDto obtenerPerfil(Long id);
    void actualizarAvatar(Long id, String nuevoAvatar);
}