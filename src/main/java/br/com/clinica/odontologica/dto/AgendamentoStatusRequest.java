package br.com.clinica.odontologica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record AgendamentoStatusRequest(@NotBlank @Pattern(regexp="SOLICITADO|CONFIRMADO|CANCELADO|CONCLUIDO") String status,
                                       UUID pacienteId) {}
