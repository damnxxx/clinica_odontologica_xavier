package br.com.clinica.odontologica.service;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.exception.RecursoNaoEncontradoException;
import br.com.clinica.odontologica.exception.RegraNegocioException;
import br.com.clinica.odontologica.model.Cliente;
import br.com.clinica.odontologica.model.Endereco;
import br.com.clinica.odontologica.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request) {
        String cpf = normalizarCpf(request.cpf());
        if (repository.existsByCpf(cpf)) throw new RegraNegocioException("Já existe cliente cadastrado com este CPF.");
        Cliente cliente = new Cliente(request.nome().trim(), cpf, request.email().trim(), request.telefone().trim(),
                request.dataNascimento(), request.observacoesClinicas());
        request.enderecos().forEach(e -> cliente.adicionarEndereco(novoEndereco(e)));
        return ClienteResponse.from(repository.inserir(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(ClienteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse consultar(UUID id) {
        return ClienteResponse.from(buscar(id));
    }

    @Transactional
    public ClienteResponse atualizar(UUID id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        String cpf = normalizarCpf(request.cpf());
        if (repository.existsByCpfAndIdNot(cpf, id)) {
            throw new RegraNegocioException("Já existe outro cliente cadastrado com este CPF.");
        }
        cliente.setNome(request.nome().trim());
        cliente.setCpf(cpf);
        cliente.setEmail(request.email().trim());
        cliente.setTelefone(request.telefone().trim());
        cliente.setDataNascimento(request.dataNascimento());
        cliente.setObservacoesClinicas(request.observacoesClinicas());
        cliente.getEnderecos().clear();
        request.enderecos().forEach(e -> cliente.adicionarEndereco(novoEndereco(e)));
        return ClienteResponse.from(repository.atualizar(cliente));
    }

    @Transactional
    public void excluir(UUID id) {
        buscar(id);
        repository.excluir(id);
    }

    @Transactional
    public EnderecoResponse adicionarEndereco(UUID clienteId, EnderecoRequest request) {
        buscar(clienteId);
        Endereco endereco = novoEndereco(request);
        repository.inserirEndereco(clienteId, endereco);
        return EnderecoResponse.from(endereco);
    }

    @Transactional
    public EnderecoResponse atualizarEndereco(UUID clienteId, Long enderecoId, EnderecoRequest request) {
        Endereco endereco = buscarEndereco(clienteId, enderecoId);
        preencher(endereco, request);
        if (!repository.atualizarEndereco(clienteId, endereco)) {
            throw new RecursoNaoEncontradoException("Endereço não encontrado para este cliente.");
        }
        return EnderecoResponse.from(endereco);
    }

    @Transactional
    public void excluirEndereco(UUID clienteId, Long enderecoId) {
        Cliente cliente = buscar(clienteId);
        boolean pertenceAoCliente = cliente.getEnderecos().stream().anyMatch(e -> e.getId().equals(enderecoId));
        if (!pertenceAoCliente) {
            throw new RecursoNaoEncontradoException("Endereço não encontrado para este cliente.");
        }
        if (cliente.getEnderecos().size() <= 1) {
            throw new RegraNegocioException("O cliente deve possuir ao menos um endereço.");
        }
        repository.excluirEndereco(clienteId, enderecoId);
    }

    private Cliente buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " não encontrado."));
    }

    private Endereco buscarEndereco(UUID clienteId, Long enderecoId) {
        return buscar(clienteId).getEnderecos().stream()
                .filter(e -> e.getId().equals(enderecoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado para este cliente."));
    }

    private Endereco novoEndereco(EnderecoRequest r) {
        return new Endereco(r.logradouro().trim(), r.numero().trim(), r.complemento(), r.bairro().trim(),
                r.cidade().trim(), r.estado().trim().toUpperCase(), r.cep().trim());
    }

    private void preencher(Endereco e, EnderecoRequest r) {
        e.setLogradouro(r.logradouro().trim());
        e.setNumero(r.numero().trim());
        e.setComplemento(r.complemento());
        e.setBairro(r.bairro().trim());
        e.setCidade(r.cidade().trim());
        e.setEstado(r.estado().trim().toUpperCase());
        e.setCep(r.cep().trim());
    }

    private String normalizarCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }
}
