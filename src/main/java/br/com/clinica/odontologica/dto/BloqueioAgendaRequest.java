package br.com.clinica.odontologica.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record BloqueioAgendaRequest(@NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim, @Size(max=250) String motivo) {}
