package br.com.clinica.odontologica.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BloqueioAgendaResponse(UUID id, UUID profissionalId, LocalDateTime inicio,
        LocalDateTime fim, String motivo) {}
