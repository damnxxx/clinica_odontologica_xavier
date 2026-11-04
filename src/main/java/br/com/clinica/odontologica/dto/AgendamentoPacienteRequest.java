package br.com.clinica.odontologica.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoPacienteRequest(@NotNull UUID pacienteId,
        @NotNull UUID profissionalId, @NotNull UUID servicoId,
        @NotNull LocalDateTime inicio, @Size(max=500) String observacao) {}
