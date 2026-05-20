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

	
	
	


}