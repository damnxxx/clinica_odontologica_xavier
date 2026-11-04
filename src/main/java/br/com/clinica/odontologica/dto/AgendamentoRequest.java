package br.com.clinica.odontologica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequest(
        @NotBlank @Size(max=120) String pacienteNome,
        @NotBlank @Email @Size(max=160) String pacienteEmail,
        @NotBlank @Size(max=20) String pacienteTelefone,
        @NotNull UUID profissionalId,
        @NotNull UUID servicoId,
        @NotNull LocalDateTime inicio,
        @Size(max=500) String observacao) {}
