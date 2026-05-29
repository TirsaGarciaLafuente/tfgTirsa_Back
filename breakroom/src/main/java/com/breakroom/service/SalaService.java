package com.breakroom.service;

import java.util.List;
import com.breakroom.Models.DTO.SalaDto;

public interface SalaService {

    public SalaDto crearSala(String nombre, Long usuarioId);
    public SalaDto unirseConCodigo(String codSala, Long usuarioId);
    public List<SalaDto> listarSalasPorUsuario(Long usuarioId);
    public SalaDto obtenerPorId(Long id);
    
    public void abandonarSala(Long salaId, Long usuarioId);
}