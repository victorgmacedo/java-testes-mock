package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService finalizarLeilaoService;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.finalizarLeilaoService = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void deveFinalizarLeilao(){
        List<Leilao> leiloes = leiloes();
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        this.finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        assertThat(leilao.isFechado(), is(true));
        assertThat(new BigDecimal("200.0"), equalTo(leilao.getLanceVencedor().getValor()));
        verify(leilaoDao).salvar(leilao);
    }

    @Test
    void deveEnviarEmailParaMaiorLance(){
        List<Leilao> leiloes = leiloes();
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        this.finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();
        verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    void naoDeveEnviarEmailParaMaiorLanceCasoOcorraExceptionNoSalvar(){
        List<Leilao> leiloes = leiloes();
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
        when(leilaoDao.salvar(any())).thenThrow(RuntimeException.class);

        try{
            this.finalizarLeilaoService.finalizarLeiloesExpirados();
            verifyNoInteractions(enviadorDeEmails);
        }catch (Exception e){}
    }

    private List<Leilao> leiloes(){
        Leilao leilao = new Leilao("Celular", new BigDecimal("10.0"), new Usuario("Victor Gabriel de Macedo"));
        Lance primeiro = new Lance(new Usuario("Fulano"), new BigDecimal("100"));
        Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("200.0"));
        leilao.propoe(primeiro);
        leilao.propoe(segundo);
        return Arrays.asList(leilao);
    }

}