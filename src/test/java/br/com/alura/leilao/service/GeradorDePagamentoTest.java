package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

class GeradorDePagamentoTest {

    private GeradorDePagamento geradorDePagamento;

    @Mock
    private PagamentoDao pagamento;

    @Mock
    private Clock clock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;


    @BeforeEach
    public void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.geradorDePagamento = new GeradorDePagamento(pagamento, clock);
    }

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilao(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2021, 1, 5);

        Instant instant  = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);
        Mockito.verify(pagamento).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertThat(pagamento.getVencimento(), equalTo(LocalDate.now(clock).plusDays(1)));
        assertThat(lanceVencedor.getValor(), equalTo(pagamento.getValor()));
        assertThat(lanceVencedor.getUsuario(), equalTo(pagamento.getUsuario()));
        assertThat(leilao, equalTo(pagamento.getLeilao()));

    }

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilaoParaSegundaFeiraQuandoSexta(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2021, 1, 1);

        Instant instant  = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);
        Mockito.verify(pagamento).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertThat(pagamento.getVencimento(), equalTo(LocalDate.now(clock).plusDays(3)));

    }

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilaoParaSegundaFeiraQuandoSabado(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2021, 1, 2);

        Instant instant  = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);
        Mockito.verify(pagamento).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertThat(pagamento.getVencimento(), equalTo(LocalDate.now(clock).plusDays(2)));

    }

    private Leilao leilao(){
        Leilao leilao = new Leilao("Celular", new BigDecimal("10.0"), new Usuario("Victor Gabriel de Macedo"));
        Lance primeiro = new Lance(new Usuario("Fulano"), new BigDecimal("100"));
        leilao.propoe(primeiro);
        leilao.setLanceVencedor(primeiro);
        return leilao;
    }


}