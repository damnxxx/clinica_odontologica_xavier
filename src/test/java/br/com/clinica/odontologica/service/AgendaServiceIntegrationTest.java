package br.com.clinica.odontologica.service;

import br.com.clinica.odontologica.dto.*;
import br.com.clinica.odontologica.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @ActiveProfiles("test")
class AgendaServiceIntegrationTest {
    private static final java.time.ZoneId FUSO=java.time.ZoneId.of("America/Sao_Paulo");
    @Autowired private AgendaService service;
    @Autowired private JdbcTemplate jdbc;
    private final UUID profissional=UUID.fromString("10000000-0000-0000-0000-000000000001");
    private final UUID servico=UUID.fromString("20000000-0000-0000-0000-000000000001");
    @BeforeEach void limpar(){jdbc.update("DELETE FROM agendamentos");jdbc.update("DELETE FROM bloqueios_agenda");jdbc.update("DELETE FROM disponibilidade_profissional");jdbc.update("DELETE FROM clientes");}
    @Test void listaEquipeEServicosPublicados(){assertEquals(5,service.profissionais().size());assertTrue(service.servicos().size()>=5);}

    @Test void novoProfissionalRecebeHorarioPadraoDaClinica(){
        UUID novo=service.cadastrarProfissional(new ProfissionalRequest("Dra. Teste","Clínica Geral","Formação demonstrativa",1,"Perfil de teste."));
        var disponibilidade=service.disponibilidades(novo);
        assertEquals(4,disponibilidade.size());
        assertEquals(java.util.List.of(3,4,5,6),disponibilidade.stream().map(DisponibilidadeResponse::diaSemana).toList());
        assertTrue(disponibilidade.stream().allMatch(d->d.inicio().equals(LocalTime.of(9,0))&&d.fim().equals(LocalTime.of(17,0))&&d.intervaloMinutos()==30));
    }

    @Test void registraPedidoPublicoEImpedeConflito(){
        LocalDate dia=LocalDate.now(FUSO).plusDays(2);
        service.cadastrarDisponibilidade(profissional,new DisponibilidadeRequest(dia.getDayOfWeek().getValue(),LocalTime.of(9,0),LocalTime.of(12,0),15));
        var inicio=dia.atTime(10,0);
        service.solicitar(new AgendamentoRequest("Pessoa Teste","teste@example.com","11999990000",profissional,servico,inicio,null));
        assertEquals(1,service.agendamentos().size());
        assertEquals("SOLICITADO",service.agendamentos().get(0).status());
        assertThrows(RegraNegocioException.class,()->service.solicitar(new AgendamentoRequest("Outra Pessoa","outra@example.com","11988880000",profissional,servico,inicio.plusMinutes(15),null)));
    }

    @Test void buscaSoHorariosLivresEConsideraBloqueios(){
        LocalDate dia=LocalDate.now(FUSO).plusDays(2);
        service.cadastrarDisponibilidade(profissional,new DisponibilidadeRequest(dia.getDayOfWeek().getValue(),LocalTime.of(9,0),LocalTime.of(11,0),30));
        assertEquals(3,service.horariosDisponiveis(profissional,servico,dia,dia).size());
        service.cadastrarBloqueio(profissional,new BloqueioAgendaRequest(dia.atTime(9,45),dia.atTime(10,15),"Compromisso"));
        var livres=service.horariosDisponiveis(profissional,servico,dia,dia);
        assertEquals(1,livres.size());
        assertEquals(dia.atTime(9,0),livres.get(0).inicio());
    }

    @Test void equipeConfirmaConsultaVinculadaAPacienteExistente(){
        UUID paciente=UUID.randomUUID();
        jdbc.update("INSERT INTO clientes(id,nome,cpf,email,telefone) VALUES (?,?,?,?,?)",paciente,"Paciente Existente","12345678901","paciente@example.com","11999990000");
        LocalDate dia=LocalDate.now(FUSO).plusDays(2);
        service.cadastrarDisponibilidade(profissional,new DisponibilidadeRequest(dia.getDayOfWeek().getValue(),LocalTime.of(9,0),LocalTime.of(12,0),15));
        service.criarPelaEquipe(new AgendamentoPacienteRequest(paciente,profissional,servico,dia.atTime(10,0),null));
        var criado=service.agendamentos().get(0);
        assertEquals(paciente,criado.pacienteId());
        assertEquals("Paciente Existente",criado.pacienteNome());
        assertEquals("CONFIRMADO",criado.status());
    }

    @Test void bloqueioNaoPodeSobreporConsultaSolicitada(){
        LocalDate dia=LocalDate.now(FUSO).plusDays(2);
        service.cadastrarDisponibilidade(profissional,new DisponibilidadeRequest(dia.getDayOfWeek().getValue(),LocalTime.of(9,0),LocalTime.of(12,0),15));
        service.solicitar(new AgendamentoRequest("Paciente Teste","teste@example.com","11999990000",profissional,servico,dia.atTime(10,0),null));
        assertThrows(RegraNegocioException.class,()->service.cadastrarBloqueio(profissional,new BloqueioAgendaRequest(dia.atTime(9,45),dia.atTime(10,30),"Bloqueio")));
    }
}
