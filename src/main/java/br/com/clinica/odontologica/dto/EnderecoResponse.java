package br.com.clinica.odontologica.dto;

import br.com.clinica.odontologica.model.Endereco;

public record EnderecoResponse(Long id, String logradouro, String numero, String complemento,
                               String bairro, String cidade, String estado, String cep) {
    public static EnderecoResponse from(Endereco e) {
        return new EnderecoResponse(e.getId(), e.getLogradouro(), e.getNumero(), e.getComplemento(),
                e.getBairro(), e.getCidade(), e.getEstado(), e.getCep());
    }
}
