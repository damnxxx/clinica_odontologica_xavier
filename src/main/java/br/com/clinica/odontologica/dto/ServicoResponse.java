package br.com.clinica.odontologica.dto;

import java.util.UUID;

public record ServicoResponse(UUID id, String nome, String descricao, int duracaoMinutos) {}
