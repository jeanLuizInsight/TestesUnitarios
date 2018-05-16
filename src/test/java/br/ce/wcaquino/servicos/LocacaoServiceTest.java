package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;
import builders.FilmeBuilder;
import builders.LocacaoBuilder;
import builders.UsuarioBuilder;
import daos.LocacaoDAO;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

public class LocacaoServiceTest {
	
	/**
	 * Atributos globais que devem ser utilizados em vários testes
	 * Dessa forma utilizamos @Before para instanciar/inicializar
	 */
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO locacaoDao;
	@Mock
	private SpcService spcService;
	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		// (valor esperado, valor obtido, precisao)
		assertEquals(10.0, locacao.getValor(), 0.01);
		
		// exemplos default e com matcher proprio
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(1));
		
		// verifique que: valor da alocação seja 5.0
		assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(10.0)));
	
		// com error Rule conseguimos mapear todos os erros, caso contrario se existir erro em mais de um Assert vamos ver apenas o primeiro, ou seja, um a um
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(10.0)));
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());
		
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());
		
		try {
			//acao
			service.alugarFilme(usuario, filmes);
		
			// para não gerar falso positivo, nesse caso chegou aqui não gerou exceção mais devria ter gerado (teste falhou)
			fail("Deveria ter lançado excessão FilmeSemEstoqueException!!!");
		} catch (FilmeSemEstoqueException e) {
			// validando o erro
			assertTrue(true);
		} catch (Exception e) {
			fail("Deveria ter lançado excessão FilmeSemEstoqueException!!!");
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());
		
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		
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
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		try {
			//acao
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		} 
	}
	
	@Test
	public void deveDevolverFilmeNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		// esse teste deve validar apenas no sábado utilizamos assumption
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
	
		// acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		// validacao
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//Assert.assertTrue(ehSegunda);
		
		// criando proprios matchers
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
	
	}
	
	public static void main(String[] args) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSpc() throws Exception {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		// mock no cenario para simular que o usuario eh negativado
		when(spcService.possuiNegativacao(any(Usuario.class))).thenReturn(true);
		
		// acao
		try {
			service.alugarFilme(usuario, filmes);
			// validacao (não pode chegar aqui pois deve lançar exception)
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), CoreMatchers.is("Usuario negativado!"));	
		} 
		
		// vai verificar se o metodo possuiNegativacao foi chamado
		verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario 3").agora();
		
		List<Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora());
		// gravando a expectativa
		when(locacaoDao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		// acao
		service.notificarAtrasos();
		
		// validacao
		// verificacao passa o mock criado e o metodo utilizado nele para saber se o mesmo foi chamado
		verify(emailService).notificarAtrasos(usuario);
		// pro usuario 2 nunca deve ocorrer pois não está atrasado
		verify(emailService, Mockito.never()).notificarAtrasos(usuario2);
		verify(emailService).notificarAtrasos(usuario3);
	}
	
	/**
	 * Tratamento de erro/exception com mockito
	 * @throws Exception 
	 */
	@Test
	public void deveTratarErroNoSpc() throws Exception {
		// cenário
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		// fazendo o mock lançae exceção...
		when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Problemas com Spc, tente novamente!"));
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com Spc, tente novamente!");
		
		// acao
		service.alugarFilme(usuario, filmes);
		
		// verificacao
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		Locacao loc = LocacaoBuilder.umLocacao().agora();
		
		// acao
		// esse método salva nova locação sem retorna-la. pra conseguir testar utilizamos argumentor captor
		service.prorrograLocacao(loc, 3);
		
		// validacao
		// dessa forma conseguimos capturar o Locacao salvo dentro do metodo pelo DAO
		ArgumentCaptor<Locacao> argCap = ArgumentCaptor.forClass(Locacao.class);
		verify(locacaoDao).salvar(argCap.capture());
		Locacao locacaoRetornada = argCap.getValue();
		
		// o valor esperado eh 30 pois o valor da loc eh 10 multiplicado por 3 dias de prorrogacao
//		assertThat(locacaoRetornada.getValor(), is(30.0));
//		assertThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
//		assertThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(3));
	
		// vou usar o erro no lugar do assertThat pois quero capurar todos os problema em uma vez
		error.checkThat(locacaoRetornada.getValor(), is(30.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(3));
	}
	
}
