package br.com.clinica.odontologica.service;

import br.com.clinica.odontologica.dto.AgendamentoRequest;
import br.com.clinica.odontologica.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @ActiveProfiles("test")
class AgendaServiceIntegrationTest {
    @Autowired private AgendaService service;
    @Autowired private JdbcTemplate jdbc;
    private final UUID profissional=UUID.fromString("10000000-0000-0000-0000-000000000001");
    private final UUID servico=UUID.fromString("20000000-0000-0000-0000-000000000001");
    @BeforeEach void limpar(){jdbc.update("DELETE FROM agendamentos");jdbc.update("DELETE FROM clientes");}
    @Test void listaEquipeEServicosPublicados(){assertEquals(5,service.profissionais().size());assertTrue(service.servicos().size()>=5);}
    @Test void registraPedidoPublicoEImpedeConflito(){
        LocalDateTime inicio=LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        service.solicitar(new AgendamentoRequest("Pessoa Teste","teste@example.com","11999990000",profissional,servico,inicio,null));
        assertEquals(1,service.agendamentos().size());
        assertEquals("SOLICITADO",service.agendamentos().get(0).status());
        assertThrows(RegraNegocioException.class,()->service.solicitar(new AgendamentoRequest("Outra Pessoa","outra@example.com","11988880000",profissional,servico,inicio.plusMinutes(15),null)));
    }
}
