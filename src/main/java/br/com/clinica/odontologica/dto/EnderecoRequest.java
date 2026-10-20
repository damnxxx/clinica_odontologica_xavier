package br.com.clinica.odontologica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(@NotBlank @Size(max=120) String logradouro,
                              @NotBlank @Size(max=20) String numero,
                              @Size(max=80) String complemento,
                              @NotBlank @Size(max=80) String bairro,
                              @NotBlank @Size(max=80) String cidade,
                              @NotBlank @Size(min=2,max=2) String estado,
                              @NotBlank @Size(max=9) String cep) { }
