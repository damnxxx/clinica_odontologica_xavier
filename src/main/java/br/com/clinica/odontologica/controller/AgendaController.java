package br.com.clinica.odontologica.controller;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.service.AgendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class AgendaController {
    private final AgendaService service;
    public AgendaController(AgendaService service) { this.service=service; }
    @GetMapping("/api/publico/profissionais") public List<ProfissionalResponse> profissionaisPublicos(){ return service.profissionais(); }
    @GetMapping("/api/publico/servicos") public List<ServicoResponse> servicosPublicos(){ return service.servicos(); }
    @GetMapping("/api/publico/horarios")
    public List<HorarioDisponivelResponse> horariosPublicos(@RequestParam UUID profissionalId,@RequestParam UUID servicoId,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate ate){ return service.horariosDisponiveis(profissionalId,servicoId,de,ate); }
    @PostMapping("/api/profissionais") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> cadastrarProfissional(@Valid @RequestBody ProfissionalRequest request){ return Map.of("id",service.cadastrarProfissional(request).toString()); }
    @PostMapping("/api/servicos") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> cadastrarServico(@Valid @RequestBody ServicoRequest request){ return Map.of("id",service.cadastrarServico(request).toString()); }
    @GetMapping("/api/profissionais/{id}/disponibilidades") public List<DisponibilidadeResponse> disponibilidades(@PathVariable UUID id){return service.disponibilidades(id);}
    @PostMapping("/api/profissionais/{id}/disponibilidades") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> cadastrarDisponibilidade(@PathVariable UUID id,@Valid @RequestBody DisponibilidadeRequest request){return Map.of("id",service.cadastrarDisponibilidade(id,request).toString());}
    @DeleteMapping("/api/profissionais/{id}/disponibilidades/{disponibilidadeId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirDisponibilidade(@PathVariable UUID id,@PathVariable UUID disponibilidadeId){service.excluirDisponibilidade(id,disponibilidadeId);}
    @GetMapping("/api/profissionais/{id}/bloqueios")
    public List<BloqueioAgendaResponse> bloqueios(@PathVariable UUID id,@RequestParam LocalDateTime de,@RequestParam LocalDateTime ate){return service.bloqueios(id,de,ate);}
    @PostMapping("/api/profissionais/{id}/bloqueios") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> cadastrarBloqueio(@PathVariable UUID id,@Valid @RequestBody BloqueioAgendaRequest request){return Map.of("id",service.cadastrarBloqueio(id,request).toString());}
    @DeleteMapping("/api/profissionais/{id}/bloqueios/{bloqueioId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirBloqueio(@PathVariable UUID id,@PathVariable UUID bloqueioId){service.excluirBloqueio(id,bloqueioId);}
    @PostMapping("/api/publico/agendamentos") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> solicitar(@Valid @RequestBody AgendamentoRequest request){ return Map.of("mensagem","Solicitação recebida. A equipe entrará em contato para confirmar.","id",service.solicitar(request).toString()); }
    @GetMapping("/api/agendamentos") public List<AgendamentoResponse> listar(){ return service.agendamentos(); }
    @PostMapping("/api/agendamentos") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> criar(@Valid @RequestBody AgendamentoPacienteRequest request){ return Map.of("id",service.criarPelaEquipe(request).toString()); }
    @PatchMapping("/api/agendamentos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@PathVariable UUID id,@Valid @RequestBody AgendamentoStatusRequest request){ service.atualizar(id,request); }
}
