package com.breakroom.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.breakroom.Models.Entity.Galeria;
import com.breakroom.Models.Entity.Usuario;
import com.breakroom.repository.GaleriaRepository;
import com.breakroom.security.JwtUtil;

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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarImagen(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        
        // Extraemos el ID del usuario logueado desde el token
        Long usuarioId = jwtUtil.extraerId(authHeader);
        
        // Buscamos la imagen en la base de datos
        java.util.Optional<Galeria> imagenOpt = galeriaRepository.findById(id);
        
        if (imagenOpt.isPresent()) {
            Galeria imagen = imagenOpt.get();
            
            // Comprobamos si el usuario logueado es el mismo que el autor de la imagen
            if (imagen.getAutor().getId().equals(usuarioId)) {
                galeriaRepository.delete(imagen);
                return org.springframework.http.ResponseEntity.ok().build();
            } else {
                return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body("No tienes permisos");
            }
        } else {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body("Imagen no encontrada");
        }
    }
}