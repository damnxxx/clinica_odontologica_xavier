package br.com.clinica.odontologica.service;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.exception.RecursoNaoEncontradoException;
import br.com.clinica.odontologica.exception.RegraNegocioException;
import br.com.clinica.odontologica.repository.AgendaRepository;
import br.com.clinica.odontologica.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class AgendaService {
    private static final ZoneId FUSO_CLINICA = ZoneId.of("America/Sao_Paulo");
    private final AgendaRepository repository;
    private final ClienteRepository clientes;
    public AgendaService(AgendaRepository repository, ClienteRepository clientes) { this.repository=repository; this.clientes=clientes; }
    public List<ProfissionalResponse> profissionais() { return repository.profissionais(); }
    public List<ServicoResponse> servicos() { return repository.servicos(); }
    @Transactional public UUID cadastrarProfissional(ProfissionalRequest request) { return repository.inserirProfissional(request); }
    @Transactional public UUID cadastrarServico(ServicoRequest request) { return repository.inserirServico(request); }
    public List<AgendamentoResponse> agendamentos() { return repository.agendamentos(); }

    public List<DisponibilidadeResponse> disponibilidades(UUID profissionalId) {
        exigirProfissionalAtivo(profissionalId);
        return repository.disponibilidades(profissionalId);
    }
    @Transactional public UUID cadastrarDisponibilidade(UUID profissionalId, DisponibilidadeRequest request) {
        exigirProfissionalAtivo(profissionalId);
        if (!request.fim().isAfter(request.inicio())) throw new RegraNegocioException("O fim do expediente deve ser posterior ao início.");
        if (repository.sobrepoeDisponibilidade(profissionalId,request)) throw new RegraNegocioException("Esse período sobrepõe outra disponibilidade do profissional.");
        return repository.inserirDisponibilidade(profissionalId,request);
    }
    @Transactional public void excluirDisponibilidade(UUID profissionalId,UUID disponibilidadeId) {
        if(!repository.excluirDisponibilidade(profissionalId,disponibilidadeId)) throw new RecursoNaoEncontradoException("Disponibilidade não encontrada.");
    }
    public List<BloqueioAgendaResponse> bloqueios(UUID profissionalId,LocalDateTime de,LocalDateTime ate) {
        exigirProfissionalAtivo(profissionalId);
        if(!ate.isAfter(de) || Duration.between(de,ate).toDays()>93) throw new RegraNegocioException("Intervalo de consulta de bloqueios inválido.");
        return repository.bloqueios(profissionalId,de,ate);
    }
    @Transactional public UUID cadastrarBloqueio(UUID profissionalId,BloqueioAgendaRequest request) {
        exigirProfissionalAtivo(profissionalId);
        if(!request.fim().isAfter(request.inicio())) throw new RegraNegocioException("O fim do bloqueio deve ser posterior ao início.");
        if(request.inicio().isBefore(LocalDateTime.now(FUSO_CLINICA))) throw new RegraNegocioException("O bloqueio deve começar no presente ou no futuro.");
        if(repository.conflito(profissionalId,request.inicio(),request.fim(),null)) throw new RegraNegocioException("O período contém consulta solicitada ou confirmada.");
        return repository.inserirBloqueio(profissionalId,request);
    }
    @Transactional public void excluirBloqueio(UUID profissionalId,UUID bloqueioId) {
        if(!repository.excluirBloqueio(profissionalId,bloqueioId)) throw new RecursoNaoEncontradoException("Bloqueio não encontrado.");
    }

    public List<HorarioDisponivelResponse> horariosDisponiveis(UUID profissionalId,UUID servicoId,LocalDate de,LocalDate ate) {
        exigirProfissionalAtivo(profissionalId);
        int duracao=repository.duracaoServico(servicoId).orElseThrow(()->new RecursoNaoEncontradoException("Serviço não encontrado."));
        if(ate.isBefore(de) || Duration.between(de.atStartOfDay(),ate.plusDays(1).atStartOfDay()).toDays()>31)
            throw new RegraNegocioException("Pesquise um período de até 31 dias.");
        String profissional=repository.nomeProfissional(profissionalId), servico=repository.nomeServico(servicoId);
        List<DisponibilidadeResponse> jornadas=repository.disponibilidades(profissionalId);
        LocalDateTime inicioPeriodo=de.atStartOfDay(), fimPeriodo=ate.plusDays(1).atStartOfDay();
        List<IntervaloAgenda> consultas=repository.intervalosOcupados(profissionalId,inicioPeriodo,fimPeriodo);
        List<BloqueioAgendaResponse> bloqueios=repository.bloqueios(profissionalId,inicioPeriodo,fimPeriodo);
        LocalDateTime agora=LocalDateTime.now(FUSO_CLINICA);
        var resultado=new java.util.ArrayList<HorarioDisponivelResponse>();
        for(LocalDate dia=de; !dia.isAfter(ate); dia=dia.plusDays(1)) {
            int diaSemana=dia.getDayOfWeek().getValue();
            for(DisponibilidadeResponse jornada:jornadas) {
                if(jornada.diaSemana()!=diaSemana) continue;
                LocalDateTime limite=dia.atTime(jornada.fim());
                for(LocalDateTime inicio=dia.atTime(jornada.inicio()); !inicio.plusMinutes(duracao).isAfter(limite); inicio=inicio.plusMinutes(jornada.intervaloMinutos())) {
                    LocalDateTime candidatoInicio=inicio;
                    LocalDateTime candidatoFim=candidatoInicio.plusMinutes(duracao);
                    if(!candidatoInicio.isAfter(agora)) continue;
                    boolean conflitoConsulta=consultas.stream().anyMatch(i->i.inicio().isBefore(candidatoFim)&&i.fim().isAfter(candidatoInicio));
                    boolean conflitoBloqueio=bloqueios.stream().anyMatch(b->b.inicio().isBefore(candidatoFim)&&b.fim().isAfter(candidatoInicio));
                    if(!conflitoConsulta&&!conflitoBloqueio) resultado.add(new HorarioDisponivelResponse(profissionalId,profissional,servicoId,servico,candidatoInicio,candidatoFim));
                }
            }
        }
        return resultado;
    }

    @Transactional public UUID solicitar(AgendamentoRequest request) {
        int duracao=validarHorario(request.profissionalId(),request.servicoId(),request.inicio());
        if(repository.conflito(request.profissionalId(),request.inicio(),request.inicio().plusMinutes(duracao),null))
            throw new RegraNegocioException("Este horário não está mais disponível. Escolha outro.");
        UUID pacienteId=repository.pacientePorEmail(request.pacienteEmail());
        return repository.inserir(request,pacienteId,"SOLICITADO",duracao);
    }
    @Transactional public UUID criarPelaEquipe(AgendamentoPacienteRequest request) {
        var cliente=clientes.findById(request.pacienteId()).orElseThrow(()->new RecursoNaoEncontradoException("Paciente não encontrado."));
        int duracao=validarHorario(request.profissionalId(),request.servicoId(),request.inicio());
        if(repository.conflito(request.profissionalId(),request.inicio(),request.inicio().plusMinutes(duracao),null))
            throw new RegraNegocioException("Este horário não está mais disponível. Escolha outro.");
        var r=new AgendamentoRequest(cliente.getNome(),cliente.getEmail(),cliente.getTelefone(),request.profissionalId(),request.servicoId(),request.inicio(),request.observacao());
        return repository.inserir(r,cliente.getId(),"CONFIRMADO",duracao);
    }
    private int validarHorario(UUID profissionalId,UUID servicoId,LocalDateTime inicio) {
        if(!inicio.isAfter(LocalDateTime.now(FUSO_CLINICA))) throw new RegraNegocioException("Escolha uma data e horário futuros.");
        exigirProfissionalAtivo(profissionalId);
        int duracao=repository.duracaoServico(servicoId).orElseThrow(()->new RecursoNaoEncontradoException("Serviço não encontrado."));
        LocalDateTime fim=inicio.plusMinutes(duracao);
        boolean dentroDisponibilidade=repository.disponibilidades(profissionalId).stream()
                .filter(d->d.diaSemana()==inicio.getDayOfWeek().getValue())
                .anyMatch(d->{ LocalDateTime inicioExpediente=inicio.toLocalDate().atTime(d.inicio()); LocalDateTime fimExpediente=inicio.toLocalDate().atTime(d.fim());
                    return !inicio.isBefore(inicioExpediente)&&!fim.isAfter(fimExpediente)&&inicio.getSecond()==0&&inicio.getNano()==0
                            && Duration.between(inicioExpediente,inicio).toMinutes()%d.intervaloMinutos()==0; });
        if(!dentroDisponibilidade) throw new RegraNegocioException("O horário está fora da disponibilidade configurada do profissional.");
        if(repository.bloqueios(profissionalId,inicio,fim).stream().anyMatch(b->b.inicio().isBefore(fim)&&b.fim().isAfter(inicio)))
            throw new RegraNegocioException("Este horário está bloqueado na agenda do profissional.");
        return duracao;
    }
    private void exigirProfissionalAtivo(UUID id) {
        if(!repository.profissionalAtivo(id)) throw new RecursoNaoEncontradoException("Profissional não encontrado.");
    }
    @Transactional public void atualizar(UUID id,AgendamentoStatusRequest r) {
        if (!repository.atualizar(id,r.status(),r.pacienteId())) throw new RecursoNaoEncontradoException("Agendamento não encontrado.");
    }
}
