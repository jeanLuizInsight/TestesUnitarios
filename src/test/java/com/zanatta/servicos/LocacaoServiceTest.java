package com.zanatta.servicos;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.zanatta.entidades.Filme;
import com.zanatta.entidades.Locacao;
import com.zanatta.entidades.Usuario;
import com.zanatta.matchers.MatchersProprios;
import com.zanatta.utils.DataUtils;
import builders.FilmeBuilder;
import builders.LocacaoBuilder;
import builders.UsuarioBuilder;
import daos.LocacaoDAO;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

// @RunWith(ParallelRunners.class)
public class LocacaoServiceTest {

	/**
	 * Atributos globais que devem ser utilizados em vários testes
	 * Dessa forma utilizamos @Before para instanciar/inicializar
	 */
	@InjectMocks @Spy private LocacaoService service;

	@Mock private LocacaoDAO locacaoDao;
	@Mock private SpcService spcService;
	@Mock private EmailService emailService;

	@Rule public ErrorCollector error = new ErrorCollector();

	@Rule public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() {
		// System.out.println("After");
	}

	/**
	 * Deve ser estático pois somente assim o JUnit vai ter acesso
	 * antes da classe ser criada (executa antes de tudo)
	 */
	@BeforeClass
	public static void setupClass() {
		// System.out.println("Before class");
	}

	/**
	 * Deve ser estático pois somente assim o JUnit vai ter acesso
	 * depois da classe ser criada (executa no final de tudo)
	 */
	@AfterClass
	public static void tearDownClass() {
		// System.out.println("After class");
	}

	/**
	 * Se mesmo cenario e ação usar mesmo teste no mesmo método mesmo com varias assertivas
	 * @throws Exception
	 */
	@Test
	public void deveAlugarFilme() throws Exception {
		// falha x erro: falha resultado inesperado, erro excessão por isso sempre lançar excessão pra camada superior que o JUnit trata

		// esse teste deve validar apenas fora no sábado utilizamos assumption
		// Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(this.service).obterData();
		// acao
		final Locacao locacao = this.service.alugarFilme(usuario, filmes);

		// verificacao
		// (valor esperado, valor obtido, precisao)
		Assert.assertEquals(10.0, locacao.getValor(), 0.01);

		// exemplos default e com matcher proprio
		// assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		// error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		this.error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), CoreMatchers.is(true));

		// assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		// error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(1));
		this.error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), CoreMatchers.is(true));

		// verifique que: valor da alocação seja 5.0
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(10.0)));

		// com error Rule conseguimos mapear todos os erros, caso contrario se existir erro em mais de um Assert vamos ver apenas o primeiro, ou seja,
		// um a um
		this.error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(10.0)));
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
		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());

		// acao
		this.service.alugarFilme(usuario, filmes);

		// validacao
		// está no expected
	}

	/**
	 * Tratamento de exceção enviada ROBUSTA utilizamos quando precisamos uma determinada exceção e mensagem permite maior controle sobre o erro
	 * Nesse caso vai ocorrer a exceção e queremos validar a mensagem error
	 */
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque2() {
		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());

		try {
			// acao
			this.service.alugarFilme(usuario, filmes);

			// para não gerar falso positivo, nesse caso chegou aqui não gerou exceção mais devria ter gerado (teste falhou)
			Assert.fail("Deveria ter lançado excessão FilmeSemEstoqueException!!!");
		} catch (final FilmeSemEstoqueException e) {
			// validando o erro
			Assert.assertTrue(true);
		} catch (final Exception e) {
			Assert.fail("Deveria ter lançado excessão FilmeSemEstoqueException!!!");
		}
	}

	/**
	 * Tratamento de exceção enviada FORMA NOVA utilizamos quando precisamos uma determinada exceção e mensagem
	 * Nesse caso vai ocorrer a exceção e o teste vai falhar pois queremos validar error
	 * @throws Exception
	 */
	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque3() throws Exception {
		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());

		// validacao faz parte do cenário, nesse caso passar sempre antes da ação
		// exceção esperada
		this.exception.expect(FilmeSemEstoqueException.class);
		// mensagem esperada na exceção
		// exception.expectMessage("Filme sem estoque");

		// acao
		this.service.alugarFilme(usuario, filmes);
	}

	/**
	 * Tratamento apenas pro Usuario, se filme vier sem estoque não interessa deixa o JUnit tratar
	 * @throws FilmeSemEstoqueException
	 * @throws LocadoraException
	 */
	@Test
	public void naoDeveAlugarFilmeSemFIlme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();

		// expectativa
		this.exception.expect(LocadoraException.class);
		this.exception.expectMessage("Filme vazio");

		// acao
		this.service.alugarFilme(usuario, null);
	}

	/**
	 * Tratamento apenas pro Usuario, se filme vier sem estoque não interessa deixa o JUnit tratar
	 * @throws FilmeSemEstoqueException
	 */
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		try {
			// acao
			this.service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (final LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		}
	}

	@Test
	public void deveDevolverFilmeNaSegundaAoAlugarNoSabado() throws Exception {
		// esse teste deve validar apenas no sábado utilizamos assumption
		// Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(this.service).obterData();

		// acao
		final Locacao retorno = this.service.alugarFilme(usuario, filmes);

		// validacao
		// boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		// Assert.assertTrue(ehSegunda);

		// criando proprios matchers
		Assert.assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
	}

	@Test
	public void naoDeveAlugarFilmeParaNegativadoSpc() throws Exception {
		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		// mock no cenario para simular que o usuario eh negativado
		Mockito.when(this.spcService.possuiNegativacao(Matchers.any(Usuario.class))).thenReturn(true);

		// acao
		try {
			this.service.alugarFilme(usuario, filmes);
			// validacao (não pode chegar aqui pois deve lançar exception)
			Assert.fail();
		} catch (final LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario negativado!"));
		}

		// vai verificar se o metodo possuiNegativacao foi chamado
		Mockito.verify(this.spcService).possuiNegativacao(usuario);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		final Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario 3").agora();

		final List<Locacao> locacoes = Arrays.asList(
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasado().comUsuario(usuario3).agora());
		// gravando a expectativa
		Mockito.when(this.locacaoDao.obterLocacoesPendentes()).thenReturn(locacoes);

		// acao
		this.service.notificarAtrasos();

		// validacao
		// verificacao passa o mock criado e o metodo utilizado nele para saber se o mesmo foi chamado
		Mockito.verify(this.emailService).notificarAtrasos(usuario);
		// pro usuario 2 nunca deve ocorrer pois não está atrasado
		Mockito.verify(this.emailService, Mockito.never()).notificarAtrasos(usuario2);
		Mockito.verify(this.emailService).notificarAtrasos(usuario3);
	}

	/**
	 * Tratamento de erro/exception com mockito
	 * @throws Exception
	 */
	@Test
	public void deveTratarErroNoSpc() throws Exception {
		// cenário
		final Usuario usuario = UsuarioBuilder.umUsuario().agora();
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		// fazendo o mock lançae exceção...
		Mockito.when(this.spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Problemas com Spc, tente novamente!"));

		this.exception.expect(LocadoraException.class);
		this.exception.expectMessage("Problemas com Spc, tente novamente!");

		// acao
		this.service.alugarFilme(usuario, filmes);

		// verificacao
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		final Locacao loc = LocacaoBuilder.umLocacao().agora();

		// acao
		// esse método salva nova locação sem retorna-la. pra conseguir testar utilizamos argumentor captor
		this.service.prorrograLocacao(loc, 3);

		// validacao
		// dessa forma conseguimos capturar o Locacao salvo dentro do metodo pelo DAO
		final ArgumentCaptor<Locacao> argCap = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(this.locacaoDao).salvar(argCap.capture());
		final Locacao locacaoRetornada = argCap.getValue();

		// o valor esperado eh 30 pois o valor da loc eh 10 multiplicado por 3 dias de prorrogacao
		// assertThat(locacaoRetornada.getValor(), is(30.0));
		// assertThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		// assertThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(3));

		// vou usar o erro no lugar do assertThat pois quero capurar todos os problema em uma vez
		this.error.checkThat(locacaoRetornada.getValor(), CoreMatchers.is(30.0));
		this.error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHoje());
		this.error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(3));
	}

	/**
	 * Exemplo invocando métodos privados
	 * @throws Exception
	 */
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		final List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		// acao
		final Class<LocacaoService> clazz = LocacaoService.class;
		final Method metodo = clazz.getDeclaredMethod("calculaValorLocacao", List.class);
		metodo.setAccessible(true);
		final Double valor = (Double) metodo.invoke(this.service, filmes);

		// verificacao
		Assert.assertThat(valor, CoreMatchers.is(10.0));
	}
}
