package br.com.clinica.odontologica.dto;

import br.com.clinica.odontologica.model.Cliente;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ClienteResponse(UUID id, String nome, String cpf, String email, String telefone,
                             LocalDate dataNascimento, String observacoesClinicas, List<EnderecoResponse> enderecos) {
    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getCpf(), c.getEmail(), c.getTelefone(),
                c.getDataNascimento(), c.getObservacoesClinicas(), c.getEnderecos().stream().map(EnderecoResponse::from).toList());
    }
}
