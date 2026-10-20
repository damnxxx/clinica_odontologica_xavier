package br.com.clinica.odontologica.controller;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody ClienteRequest request) { return service.cadastrar(request); }
    @GetMapping public List<ClienteResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public ClienteResponse consultar(@PathVariable UUID id) { return service.consultar(id); }
    @PutMapping("/{id}") public ClienteResponse atualizar(@PathVariable UUID id, @Valid @RequestBody ClienteRequest request) { return service.atualizar(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluir(@PathVariable UUID id) { service.excluir(id); }

    @PostMapping("/{clienteId}/enderecos") @ResponseStatus(HttpStatus.CREATED)
    public EnderecoResponse adicionarEndereco(@PathVariable UUID clienteId, @Valid @RequestBody EnderecoRequest request) { return service.adicionarEndereco(clienteId, request); }
    @PutMapping("/{clienteId}/enderecos/{enderecoId}")
    public EnderecoResponse atualizarEndereco(@PathVariable UUID clienteId, @PathVariable Long enderecoId, @Valid @RequestBody EnderecoRequest request) { return service.atualizarEndereco(clienteId, enderecoId, request); }
    @DeleteMapping("/{clienteId}/enderecos/{enderecoId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirEndereco(@PathVariable UUID clienteId, @PathVariable Long enderecoId) { service.excluirEndereco(clienteId, enderecoId); }
}
