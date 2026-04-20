package com.breakroom.service;

import com.breakroom.Models.DTO.LoginDto;
import com.breakroom.Models.DTO.RegistroDto;
import com.breakroom.Models.DTO.UsuarioDto;

public interface UsuarioService {
	
	public UsuarioDto login (LoginDto loginDto);
	
	public UsuarioDto registro (RegistroDto registroDto);
	
	boolean verificarUsuario(String email, String username);
	
	void cambiarPassword(String email, String username, String password);

}
