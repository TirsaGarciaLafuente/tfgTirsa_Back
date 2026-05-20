package com.breakroom.controllers;

import com.breakroom.Models.Entity.Galeria;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.GaleriaRepository;
import com.breakroom.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/galeria")
@CrossOrigin(origins = "http://localhost:4200")
public class GaleriaController {

    @Autowired
    private GaleriaRepository galeriaRepository;
    
    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/sala/{salaId}")
    public List<Galeria> obtenerGaleria(@PathVariable Long salaId) {
        return galeriaRepository.findBySalaIdOrderByFechaCreacionDesc(salaId);
    }

    @PostMapping("/subir")
    public Galeria guardarImagen(@RequestBody Galeria nuevaImagen, @RequestHeader("Authorization") String authHeader){

    	Long usuarioId = jwtUtil.extraerId(authHeader);
    	
    	nuevaImagen.setAutor(new Usuario(usuarioId));
        // Spring se encarga de rellenar el objeto Galeria con el JSON que enviemos
        return galeriaRepository.save(nuevaImagen);
    }
}