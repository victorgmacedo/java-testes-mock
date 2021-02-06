package br.com.alura.leilao.acceptance.steps;

import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class PropondoLanceSteps {

    private Lance lance;
    private Leilao leilao;
    private List<Lance> lances;

    @Before
    public void setup(){
        this.lances = new ArrayList<>();
        leilao = new Leilao("Produto XX", new BigDecimal("1000"), LocalDate.now());
    }

    /**
     * 1 Cenário
     */
    @Given("Dado um lance valido")
    public void dado_um_lance_valido() {
        Usuario usuario = new Usuario("Victor");
        lance = new Lance(usuario, new BigDecimal("1000"));
        leilao = new Leilao("Produto XX", new BigDecimal("1000"), LocalDate.now());
    }
    @When("Quando propoe ao leilao")
    public void quando_propoe_lance() {
        leilao.propoe(lance);
    }
    @Then("Entao o lance eh aceito")
    public void entao_lance_eh_Aceito() {
        assertThat(leilao.getValorInicial(), equalTo(new BigDecimal("1000")));
    }


    /**
     * 2 Cenário
     */

    @Given("Dado um lance de {string} para o usuario {string}")
    public void varios_lances_validos(String valor, String usuarioName) {
        Usuario usuario = new Usuario(usuarioName);
        lances.add(new Lance(usuario, new BigDecimal(valor)));
    }

    @When("Quando propoe varios lances ao leilao")
    public void quando_propoe_varios_lances_ao_leilao() {
        lances.forEach(leilao::propoe);
    }
    @Then("os lances sao aceitos")
    public void os_lances_sao_aceitos() {
        assertThat(leilao.getValorInicial(), equalTo(new BigDecimal("1000")));
        assertThat(leilao.getLances(), hasItems(lances.get(0), lances.get(1)));
        assertThat(leilao.getLances().size(), equalTo(2));
    }

    /**
     * Cenario 3
     */

    @Given("Dado um lance de {int} para o usuario {string}")
    public void dado_um_lance_de_para_o_usuario(Integer valor, String usuarioName) {
        Usuario usuario = new Usuario(usuarioName);
        try {
            Lance lance = new Lance(usuario, new BigDecimal(valor));
            lances.add(lance);
        }catch (Exception e){}
    }

    @Then("os lances nao sao aceitos")
    public void os_lances_nao_sao_aceitos() {
        assertThat(leilao.getLances(), hasSize(0));
    }

}
