package br.com.clinica.odontologica.repository;

import br.com.clinica.odontologica.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AgendaRepository {
    private final JdbcTemplate jdbc;
    public AgendaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<ProfissionalResponse> profissionais() {
        return jdbc.query("SELECT id,nome,especialidade,formacao,experiencia_anos,biografia FROM profissionais WHERE ativo=TRUE ORDER BY nome",
            (rs,n) -> new ProfissionalResponse(rs.getObject("id",UUID.class),rs.getString("nome"),rs.getString("especialidade"),rs.getString("formacao"),rs.getInt("experiencia_anos"),rs.getString("biografia")));
    }
    public List<ServicoResponse> servicos() {
        return jdbc.query("SELECT id,nome,descricao,duracao_minutos FROM servicos WHERE ativo=TRUE ORDER BY nome",
            (rs,n) -> new ServicoResponse(rs.getObject("id",UUID.class),rs.getString("nome"),rs.getString("descricao"),rs.getInt("duracao_minutos")));
    }
    public UUID inserirProfissional(ProfissionalRequest r) { UUID id=UUID.randomUUID(); jdbc.update("INSERT INTO profissionais(id,nome,especialidade,formacao,experiencia_anos,biografia) VALUES (?,?,?,?,?,?)",id,r.nome().trim(),r.especialidade().trim(),r.formacao().trim(),r.experienciaAnos(),r.biografia().trim()); return id; }
    public UUID inserirServico(ServicoRequest r) { UUID id=UUID.randomUUID(); jdbc.update("INSERT INTO servicos(id,nome,descricao,duracao_minutos) VALUES (?,?,?,?)",id,r.nome().trim(),r.descricao().trim(),r.duracaoMinutos()); return id; }
    public List<DisponibilidadeResponse> disponibilidades(UUID profissionalId) {
        return jdbc.query("SELECT id,profissional_id,dia_semana,inicio,fim,intervalo_minutos FROM disponibilidade_profissional WHERE profissional_id=? AND ativo=TRUE ORDER BY dia_semana,inicio",
                (rs,n)->new DisponibilidadeResponse(rs.getObject("id",UUID.class),rs.getObject("profissional_id",UUID.class),rs.getInt("dia_semana"),rs.getTime("inicio").toLocalTime(),rs.getTime("fim").toLocalTime(),rs.getInt("intervalo_minutos")),profissionalId);
    }
    public boolean sobrepoeDisponibilidade(UUID profissionalId, DisponibilidadeRequest r) {
        return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM disponibilidade_profissional WHERE profissional_id=? AND dia_semana=? AND ativo=TRUE AND inicio < ? AND fim > ?)",Boolean.class,profissionalId, r.diaSemana(),Time.valueOf(r.fim()),Time.valueOf(r.inicio())));
    }
    public UUID inserirDisponibilidade(UUID profissionalId, DisponibilidadeRequest r) {
        UUID id=UUID.randomUUID(); jdbc.update("INSERT INTO disponibilidade_profissional(id,profissional_id,dia_semana,inicio,fim,intervalo_minutos) VALUES (?,?,?,?,?,?)",id,profissionalId,r.diaSemana(),Time.valueOf(r.inicio()),Time.valueOf(r.fim()),r.intervaloMinutos()); return id;
    }
    public boolean excluirDisponibilidade(UUID profissionalId,UUID disponibilidadeId) { return jdbc.update("DELETE FROM disponibilidade_profissional WHERE profissional_id=? AND id=?",profissionalId,disponibilidadeId)>0; }
    public List<BloqueioAgendaResponse> bloqueios(UUID profissionalId,LocalDateTime de,LocalDateTime ate) {
        return jdbc.query("SELECT id,profissional_id,inicio,fim,motivo FROM bloqueios_agenda WHERE profissional_id=? AND inicio<? AND fim>? ORDER BY inicio",
                (rs,n)->new BloqueioAgendaResponse(rs.getObject("id",UUID.class),rs.getObject("profissional_id",UUID.class),rs.getTimestamp("inicio").toLocalDateTime(),rs.getTimestamp("fim").toLocalDateTime(),rs.getString("motivo")),profissionalId,Timestamp.valueOf(ate),Timestamp.valueOf(de));
    }
    public UUID inserirBloqueio(UUID profissionalId,BloqueioAgendaRequest r) {
        UUID id=UUID.randomUUID(); jdbc.update("INSERT INTO bloqueios_agenda(id,profissional_id,inicio,fim,motivo) VALUES (?,?,?,?,?)",id,profissionalId,Timestamp.valueOf(r.inicio()),Timestamp.valueOf(r.fim()),r.motivo()); return id;
    }
    public boolean excluirBloqueio(UUID profissionalId,UUID bloqueioId) { return jdbc.update("DELETE FROM bloqueios_agenda WHERE profissional_id=? AND id=?",profissionalId,bloqueioId)>0; }
    public List<IntervaloAgenda> intervalosOcupados(UUID profissionalId,LocalDateTime de,LocalDateTime ate) {
        return jdbc.query("SELECT inicio,fim FROM agendamentos WHERE profissional_id=? AND status IN ('SOLICITADO','CONFIRMADO') AND inicio>=? AND inicio<?",
                (rs,n)->new IntervaloAgenda(rs.getTimestamp("inicio").toLocalDateTime(),rs.getTimestamp("fim").toLocalDateTime()),profissionalId,Timestamp.valueOf(de.minusMinutes(240)),Timestamp.valueOf(ate));
    }
    public String nomeProfissional(UUID id) { return jdbc.query("SELECT nome FROM profissionais WHERE id=? AND ativo=TRUE",rs->rs.next()?rs.getString(1):null,id); }
    public String nomeServico(UUID id) { return jdbc.query("SELECT nome FROM servicos WHERE id=? AND ativo=TRUE",rs->rs.next()?rs.getString(1):null,id); }
    public Optional<Integer> duracaoServico(UUID id) {
        return jdbc.query("SELECT duracao_minutos FROM servicos WHERE id=? AND ativo=TRUE", rs -> rs.next()?Optional.of(rs.getInt(1)):Optional.empty(), id);
    }
    public boolean profissionalAtivo(UUID id) { return Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM profissionais WHERE id=? AND ativo=TRUE)",Boolean.class,id)); }
    public UUID pacientePorEmail(String email) {
        return jdbc.query("SELECT id FROM clientes WHERE LOWER(email)=LOWER(?) ORDER BY id FETCH FIRST 1 ROW ONLY", rs -> rs.next()?rs.getObject(1,UUID.class):null,email);
    }
    public boolean conflito(UUID profissional, java.time.LocalDateTime inicio, java.time.LocalDateTime fim, UUID ignorar) {
        return jdbc.query("SELECT id,inicio,fim FROM agendamentos WHERE profissional_id=? AND status IN ('SOLICITADO','CONFIRMADO') AND inicio >= ? AND inicio < ?",
                rs -> { while (rs.next()) { UUID id=rs.getObject("id",UUID.class); if (ignorar!=null && ignorar.equals(id)) continue;
                    java.time.LocalDateTime existente=rs.getTimestamp("inicio").toLocalDateTime();
                    java.time.LocalDateTime fimExistente=rs.getTimestamp("fim").toLocalDateTime();
                    if (existente.isBefore(fim) && fimExistente.isAfter(inicio)) return true;
                } return false; }, profissional,Timestamp.valueOf(inicio.minusMinutes(240)),Timestamp.valueOf(fim));
    }
    public UUID inserir(AgendamentoRequest r, UUID pacienteId, String status,int duracaoMinutos) {
        UUID id=UUID.randomUUID();
        jdbc.update("INSERT INTO agendamentos(id,paciente_id,profissional_id,servico_id,paciente_nome,paciente_email,paciente_telefone,inicio,fim,status,observacao) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
            id,pacienteId,r.profissionalId(),r.servicoId(),r.pacienteNome().trim(),r.pacienteEmail().trim(),r.pacienteTelefone().trim(),Timestamp.valueOf(r.inicio()),Timestamp.valueOf(r.inicio().plusMinutes(duracaoMinutos)),status,r.observacao());
        return id;
    }
    public List<AgendamentoResponse> agendamentos() {
        return jdbc.query("SELECT a.id,a.paciente_id,a.paciente_nome,a.paciente_email,a.paciente_telefone,a.profissional_id,p.nome profissional_nome,a.servico_id,s.nome servico_nome,a.inicio,a.status,a.observacao FROM agendamentos a JOIN profissionais p ON p.id=a.profissional_id JOIN servicos s ON s.id=a.servico_id ORDER BY a.inicio DESC",
            (rs,n)->new AgendamentoResponse(rs.getObject("id",UUID.class),rs.getObject("paciente_id",UUID.class),rs.getString("paciente_nome"),rs.getString("paciente_email"),rs.getString("paciente_telefone"),rs.getObject("profissional_id",UUID.class),rs.getString("profissional_nome"),rs.getObject("servico_id",UUID.class),rs.getString("servico_nome"),rs.getTimestamp("inicio").toLocalDateTime(),rs.getString("status"),rs.getString("observacao")));
    }
    public boolean atualizar(UUID id,String status,UUID pacienteId) {
        return jdbc.update("UPDATE agendamentos SET status=?,paciente_id=COALESCE(?,paciente_id) WHERE id=?",status,pacienteId,id)>0;
    }
}
