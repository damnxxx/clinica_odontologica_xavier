package br.com.clinica.odontologica.dto;

import java.util.UUID;

public record ProfissionalResponse(UUID id, String nome, String especialidade, String formacao,
                                  int experienciaAnos, String biografia) {}
