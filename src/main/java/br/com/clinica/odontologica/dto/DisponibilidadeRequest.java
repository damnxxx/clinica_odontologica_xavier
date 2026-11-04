package br.com.clinica.odontologica.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record DisponibilidadeRequest(@Min(1) @Max(7) int diaSemana,
        @NotNull LocalTime inicio, @NotNull LocalTime fim,
        @Min(5) @Max(240) int intervaloMinutos) {}
