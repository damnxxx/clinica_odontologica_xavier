package br.com.clinica.odontologica.dto;

import java.time.LocalTime;
import java.util.UUID;

public record DisponibilidadeResponse(UUID id, UUID profissionalId, int diaSemana,
        LocalTime inicio, LocalTime fim, int intervaloMinutos) {}
