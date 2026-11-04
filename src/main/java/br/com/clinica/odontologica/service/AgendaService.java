package br.com.clinica.odontologica.service;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.exception.RecursoNaoEncontradoException;
import br.com.clinica.odontologica.exception.RegraNegocioException;
import br.com.clinica.odontologica.repository.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgendaService {
    private final AgendaRepository repository;
    public AgendaService(AgendaRepository repository) { this.repository=repository; }
    public List<ProfissionalResponse> profissionais() { return repository.profissionais(); }
    public List<ServicoResponse> servicos() { return repository.servicos(); }
    @Transactional public UUID cadastrarProfissional(ProfissionalRequest request) { return repository.inserirProfissional(request); }
    @Transactional public UUID cadastrarServico(ServicoRequest request) { return repository.inserirServico(request); }
    public List<AgendamentoResponse> agendamentos() { return repository.agendamentos(); }

    @Transactional
    public UUID solicitar(AgendamentoRequest request) { return criar(request,"SOLICITADO"); }
    @Transactional
    public UUID criarPelaEquipe(AgendamentoRequest request) { return criar(request,"CONFIRMADO"); }
    private UUID criar(AgendamentoRequest r,String status) {
        if (!r.inicio().isAfter(LocalDateTime.now())) throw new RegraNegocioException("Escolha uma data e horário futuros.");
        if (!repository.profissionalAtivo(r.profissionalId())) throw new RecursoNaoEncontradoException("Profissional não encontrado.");
        int duracao=repository.duracaoServico(r.servicoId()).orElseThrow(()->new RecursoNaoEncontradoException("Serviço não encontrado."));
        if (repository.conflito(r.profissionalId(),r.inicio(),r.inicio().plusMinutes(duracao),null)) throw new RegraNegocioException("Este horário não está mais disponível. Escolha outro.");
        UUID pacienteId=repository.pacientePorEmail(r.pacienteEmail());
        return repository.inserir(r,pacienteId,status);
    }
    @Transactional
    public void atualizar(UUID id,AgendamentoStatusRequest r) {
        if (!repository.atualizar(id,r.status(),r.pacienteId())) throw new RecursoNaoEncontradoException("Agendamento não encontrado.");
    }
}
