package br.com.clinica.odontologica.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HorarioDisponivelResponse(UUID profissionalId, String profissionalNome,
        UUID servicoId, String servicoNome, LocalDateTime inicio, LocalDateTime fim) {}
