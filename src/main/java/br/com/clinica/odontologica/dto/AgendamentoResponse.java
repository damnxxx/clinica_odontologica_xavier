package br.com.clinica.odontologica.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(UUID id, UUID pacienteId, String pacienteNome, String pacienteEmail,
        String pacienteTelefone, UUID profissionalId, String profissionalNome, UUID servicoId,
        String servicoNome, LocalDateTime inicio, String status, String observacao) {}
