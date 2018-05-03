package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

public class LocacaoServiceTest {
	
	/**
	 * Atributos globais que devem ser utilizados em vários testes
	 * Dessa forma utilizamos @Before para instanciar/inicializar
	 */
	private LocacaoService service;
	
	// declarar static passa para escopo da classe e não reinicializa a cada teste
	private static int countTest = 0; 
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		// sempre reinicializa as variaveis da classe para cada teste
		service = new LocacaoService();
		countTest++;
		System.out.println("Teste: " + countTest);
	}
	
	@After
	public void tearDown() {
		//System.out.println("After");
	}
	
	/**
	 * Deve ser estático pois somente assim o JUnit vai ter acesso
	 * antes da classe ser criada (executa antes de tudo)
	 */
	@BeforeClass
	public static void setupClass() {
		//System.out.println("Before class");
	}
	
	/**
	 * Deve ser estático pois somente assim o JUnit vai ter acesso
	 * depois da classe ser criada (executa no final de tudo)
	 */
	@AfterClass
	public static void tearDownClass() {
		//System.out.println("After class");
	}
	
	/**
	 * Se mesmo cenario e ação usar mesmo teste no mesmo método mesmo com varias assertivas 
	 * @throws Exception 
	 */
	@Test
	public void deveAlugarFilme() throws Exception {
		// falha x erro: falha resultado inesperado, erro excessão por isso sempre lançar excessão pra camada superior que o JUnit trata
		
		// esse teste deve validar apenas fora no sábado utilizamos assumption
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
				
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		// (valor esperado, valor obtido, precisao)
		Assert.assertEquals(5.0, locacao.getValor(), 0.01);
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	
		// verifique que: valor da alocação seja 5.0
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
	
		// com error Rule conseguimos mapear todos os erros, caso contrario se existir erro em mais de um Assert vamos ver apenas o primeiro, ou seja, um a um
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(5.0)));
	}
	
	/**
	 * Tratamento de exceção enviada ELEGANTE utilizada apenas quando precisamos de uma determinada exceção
	 * Queremos que o teste retorne a Exception especificada
	 * @throws FilmeSemEstoqueException 
	 * @throws LocadoraException 
	 * @throws Exception
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
		//acao
		service.alugarFilme(usuario, filmes);
			
		// validacao
		// está no expected
	}
	
	/**
	 * Tratamento de exceção enviada ROBUSTA utilizamos quando precisamos uma determinada exceção e mensagem permite maior controle sobre o erro
	 * Nesse caso vai ocorrer a exceção e queremos validar a mensagem error
	 */
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque2() {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
		try {
			//acao
			service.alugarFilme(usuario, filmes);
		
			// para não gerar falso positivo, nesse caso chegou aqui não gerou exceção mais devria ter gerado (teste falhou)
			Assert.fail("Deveria ter lançado excessão FilmeSemEstoqueException!!!");
		} catch (FilmeSemEstoqueException e) {
			// validando o erro
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail("Deveria ter lançado excessão FilmeSemEstoqueException!!!");
		}
	}
	
	/**
	 * Tratamento de exceção enviada FORMA NOVA utilizamos quando precisamos uma determinada exceção e mensagem
	 * Nesse caso vai ocorrer a exceção e o teste vai falhar pois  queremos validar error
	 * @throws Exception 
	 */
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque3() throws Exception {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));
		
		// validacao faz parte do cenário, nesse caso passar sempre antes da ação
		// exceção esperada
		exception.expect(FilmeSemEstoqueException.class);
		// mensagem esperada na exceção
		//exception.expectMessage("Filme sem estoque");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	/**
	 * Tratamento apenas pro Usuario, se filme vier sem estoque não interessa deixa o JUnit tratar
	 * @throws FilmeSemEstoqueException
	 * @throws LocadoraException 
	 */
	@Test
	public void naoDeveAlugarFilmeSemFIlme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		// expectativa
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		service.alugarFilme(usuario, null);
	}
	
	/**
	 * Tratamento apenas pro Usuario, se filme vier sem estoque não interessa deixa o JUnit tratar
	 * @throws FilmeSemEstoqueException
	 */
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//cenario
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		try {
			//acao
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		} 
	}
	
	@Test
	public void deveDevolverFilmeNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		// esse teste deve validar apenas no sábado utilizamos assumption
		//Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 10.0));
	
		// acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		// validacao
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//Assert.assertTrue(ehSegunda);
		
		// criando proprios matchers
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
	
	}
}
