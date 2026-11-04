package br.com.clinica.odontologica.dto;
import jakarta.validation.constraints.*;
public record ProfissionalRequest(@NotBlank @Size(max=120) String nome,@NotBlank @Size(max=120) String especialidade,
 @NotBlank @Size(max=240) String formacao,@Min(0) @Max(60) int experienciaAnos,@NotBlank @Size(max=1000) String biografia) {}
