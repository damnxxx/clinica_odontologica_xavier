package br.com.clinica.odontologica.dto;
import jakarta.validation.constraints.*;
public record ServicoRequest(@NotBlank @Size(max=120) String nome,@NotBlank @Size(max=500) String descricao,@Min(15) @Max(240) int duracaoMinutos) {}
