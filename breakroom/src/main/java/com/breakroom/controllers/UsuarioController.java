package com.breakroom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.breakroom.Models.DTO.CambiarPasswordDto;
import com.breakroom.Models.DTO.LoginDto;
import com.breakroom.Models.DTO.RegistroDto;
import com.breakroom.Models.DTO.UsuarioDto;
import com.breakroom.Models.DTO.VerificarDto;
import com.breakroom.service.UsuarioService;


@RestController 
@RequestMapping("/api/usuarios") 
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginDto login) {	
        return ResponseEntity.ok(usuarioService.login(login));
    }
	
	@PostMapping("/registro")
    public ResponseEntity<UsuarioDto> registro(@RequestBody RegistroDto registro) {	
        return ResponseEntity.ok(usuarioService.registro(registro));
    }
	
	@PostMapping("/verificar")
	public ResponseEntity<?> verificarUsuario(@RequestBody VerificarDto dto) {
	    boolean existe = usuarioService.verificarUsuario(dto.getEmail(), dto.getUsername());
	    if (existe) {
	        return ResponseEntity.ok().build();
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	}
	
	@PostMapping("/cambiar-password")
	public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordDto dto) {
	    usuarioService.cambiarPassword(dto.getEmail(), dto.getUsername(), dto.getPassword());
	    return ResponseEntity.ok().build();
	}
	
	


}