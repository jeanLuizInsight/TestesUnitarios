package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

/**
 * @RunWith(Parameterized.class)
 * JUnit sabe que os testes dessa classe devem ser tratados de uma forma diferente
 * testes parametrizaveis
 * Valores parametrizaveis (data driven test)
 * @author jean
 *
 */
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	private LocacaoService service;
	
	/**
	 * Link das variaveis utilizadas nos testes com a coleção criada abaixo
	 */
	@Parameter
	public List<Filme> filmes;  // primeiro registro do array
	@Parameter(value=1)
	public Double valorLocacao; // segundo registro do array
	@Parameter(value=2)
	public String descricaoTeste;
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}
	
	private static Filme fime1 = new Filme("Filme 1", 2, 4.0); 
	private static Filme fime2 = new Filme("Filme 2", 2, 4.0);
	private static Filme fime3 = new Filme("Filme 3", 2, 4.0);
	private static Filme fime4 = new Filme("Filme 4", 2, 4.0);
	private static Filme fime5 = new Filme("Filme 5", 2, 4.0);
	private static Filme fime6 = new Filme("Filme 6", 2, 4.0);
	
	/**
	 * @Parameters
	 * Definindo a coleção de dados de entrada/cenário que serão utilizados para os testes
	 * @return
	 */
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros() {
		// a quantidade de testes executados será exatamente igual a quantidade de linhas no array
		// por isso o método que define essa coleção deve ser estático
		return Arrays.asList(new Object[][] {
			{Arrays.asList(fime1,fime2,fime3), 11.0, "3 Filmes: 25%"},
			{Arrays.asList(fime1,fime2,fime3,fime4), 13.0, "4 Filmes: 50%"},
			{Arrays.asList(fime1,fime2,fime3,fime4,fime5), 14.0, "5 Filmes: 75%"},
			{Arrays.asList(fime1,fime2,fime3,fime4,fime5,fime6), 14.0, "6 Filmes: 100%"}
		});
	}
	
	/**
	 * Teste genérico utilizando variaveis externas
	 * @throws LocadoraException
	 * @throws FilmeSemEstoqueException
	 */
	@Test
	public void deveCalcularValorDaLocacaoComsiderandoDescontos() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
	
		// acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
	
		// validacao
		// 10+10+7.5+5+2.5+0
		Assert.assertThat(resultado.getValor(), is(valorLocacao));
	}
}