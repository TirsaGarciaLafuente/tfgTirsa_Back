package com.breakroom.service;

import com.breakroom.Models.Entity.Pregunta;
import com.breakroom.Models.Entity.PreguntaPlantilla;
import com.breakroom.Models.Entity.Sala;
import com.breakroom.repository.PreguntaPlantillaRepository;
import com.breakroom.repository.PreguntaRepository;
import com.breakroom.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class ProgramadorDiario {

    @Autowired
    private PreguntaPlantillaRepository plantillaRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    // Se ejecuta automáticamente a las 00:00 (medianoche) todos los días
    @Scheduled(cron = "0 0 0 * * ?")
    public void generarPreguntaDelDia() {
        List<PreguntaPlantilla> plantillas = plantillaRepository.findAll();
        
        // Si el banco de preguntas está vacío, no hace nada
        if (plantillas.isEmpty()) {
            return;
        }

        // Elegir una plantilla al azar
        Random random = new Random();
        PreguntaPlantilla elegida = plantillas.get(random.nextInt(plantillas.size()));

        List<Sala> salas = salaRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Sala sala : salas) {
            // Comprueba si la sala ya tiene una pregunta asignada hoy
            boolean existe = preguntaRepository.findBySalaIdAndFechaActiva(sala.getId(), hoy).isPresent();
            
            if (!existe) {
                Pregunta nuevaPregunta = new Pregunta();
                nuevaPregunta.setTexto(elegida.getTexto());
                nuevaPregunta.setSala(sala);
                nuevaPregunta.setFechaActiva(hoy);
                nuevaPregunta.setCreador(null); // La pregunta es automática, no tiene creador

                preguntaRepository.save(nuevaPregunta);
            }
        }
        
        System.out.println("Pregunta automática asignada a las salas: " + elegida.getTexto());
    }
}