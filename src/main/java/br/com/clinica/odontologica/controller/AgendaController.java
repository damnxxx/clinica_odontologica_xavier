package br.com.clinica.odontologica.controller;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.service.AgendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class AgendaController {
    private final AgendaService service;
    public AgendaController(AgendaService service) { this.service=service; }
    @GetMapping("/api/publico/profissionais") public List<ProfissionalResponse> profissionaisPublicos(){ return service.profissionais(); }
    @GetMapping("/api/publico/servicos") public List<ServicoResponse> servicosPublicos(){ return service.servicos(); }
    @PostMapping("/api/profissionais") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> cadastrarProfissional(@Valid @RequestBody ProfissionalRequest request){ return Map.of("id",service.cadastrarProfissional(request).toString()); }
    @PostMapping("/api/servicos") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> cadastrarServico(@Valid @RequestBody ServicoRequest request){ return Map.of("id",service.cadastrarServico(request).toString()); }
    @PostMapping("/api/publico/agendamentos") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> solicitar(@Valid @RequestBody AgendamentoRequest request){ return Map.of("mensagem","Solicitação recebida. A equipe entrará em contato para confirmar.","id",service.solicitar(request).toString()); }
    @GetMapping("/api/agendamentos") public List<AgendamentoResponse> listar(){ return service.agendamentos(); }
    @PostMapping("/api/agendamentos") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> criar(@Valid @RequestBody AgendamentoRequest request){ return Map.of("id",service.criarPelaEquipe(request).toString()); }
    @PatchMapping("/api/agendamentos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@PathVariable UUID id,@Valid @RequestBody AgendamentoStatusRequest request){ service.atualizar(id,request); }
}
