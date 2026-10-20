package br.com.clinica.odontologica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record ClienteRequest(@NotBlank @Size(max=120) String nome,
                             @NotBlank @Size(min=11,max=14) String cpf,
                             @NotBlank @Email @Size(max=160) String email,
                             @NotBlank @Size(max=20) String telefone,
                             LocalDate dataNascimento,
                             @Size(max=1000) String observacoesClinicas,
                             @NotEmpty List<@Valid EnderecoRequest> enderecos) { }
