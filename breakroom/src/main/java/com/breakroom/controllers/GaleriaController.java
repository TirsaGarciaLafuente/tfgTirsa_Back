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

/**
 * Controlador que gestiona las imágenes y elementos multimedia de la galería
 * asociados a las diferentes salas.
 */
@RestController
@RequestMapping("/api/galeria")
@CrossOrigin(origins = "http://localhost:4200")
public class GaleriaController {

    @Autowired
    private GaleriaRepository galeriaRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Obtiene el listado de imágenes de una sala específica, ordenadas de más reciente a más antigua.
     * * @param salaId Identificador de la sala de la que se quieren recuperar las imágenes.
     * @return Lista con los elementos de la galería de esa sala.
     */
    @GetMapping("/sala/{salaId}")
    public List<Galeria> obtenerGaleria(@PathVariable Long salaId) {
        return galeriaRepository.findBySalaIdOrderByFechaCreacionDesc(salaId);
    }

    /**
     * Guarda una nueva imagen en la galería, asignando automáticamente al usuario que realiza la petición como autor.
     * * @param nuevaImagen Objeto que contiene los datos de la imagen a guardar.
     * @param authHeader Cabecera de autorización que incluye el token JWT del usuario.
     * @return El objeto de la galería guardado con sus datos actualizados.
     */
    @PostMapping("/subir")
    public Galeria guardarImagen(@RequestBody Galeria nuevaImagen, @RequestHeader("Authorization") String authHeader){

        Long usuarioId = jwtUtil.extraerId(authHeader);
        
        nuevaImagen.setAutor(new Usuario(usuarioId));
        return galeriaRepository.save(nuevaImagen);
    }
    
    /**
     * Elimina una imagen de la galería tras comprobar que el usuario que lo solicita es el dueño de la misma.
     * * @param id Identificador único de la imagen que se quiere borrar.
     * @param authHeader Cabecera de autorización que contiene el token JWT del usuario para comprobar permisos.
     * @return Respuesta con estado 200 OK si se borra, 403 FORBIDDEN si el usuario no es el autor, o 404 NOT FOUND si la imagen no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarImagen(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        
        Long usuarioId = jwtUtil.extraerId(authHeader);
        
        java.util.Optional<Galeria> imagenOpt = galeriaRepository.findById(id);
        
        if (imagenOpt.isPresent()) {
            Galeria imagen = imagenOpt.get();
            
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