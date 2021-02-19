package com.zanatta.servicos;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import com.zanatta.entidades.Filme;
import com.zanatta.entidades.Locacao;
import com.zanatta.entidades.Usuario;
import com.zanatta.matchers.MatchersProprios;
import com.zanatta.servicos.EmailService;
import com.zanatta.servicos.LocacaoService;
import com.zanatta.servicos.SpcService;
import com.zanatta.utils.DataUtils;
import buildermaster.BuilderMaster;
import builders.FilmeBuilder;
import builders.LocacaoBuilder;
import builders.UsuarioBuilder;
import daos.LocacaoDAO;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTestPowerMock {
	
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
		// criando Spy do powerMock
		service = PowerMockito.spy(service);
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
		//Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
				
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		// exemplo utilizando construtor com Date
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
		
		// exemplo utilizando método estático com Calendar
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, 28);
//		cal.set(Calendar.MONTH, Calendar.APRIL);
//		cal.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(cal);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		// (valor esperado, valor obtido, precisao)
		assertEquals(10.0, locacao.getValor(), 0.01);
		
		// exemplos default e com matcher proprio
		//assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		//error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		
		//assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		//error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaoDias(1));
		// como com power mock estou repassando data especifica devo dizer mais 1 dia a partir da data informada
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
		
		// verifique que: valor da alocação seja 5.0
		assertThat(locacao.getValor(), is(CoreMatchers.equalTo(10.0)));
	
		// com error Rule conseguimos mapear todos os erros, caso contrario se existir erro em mais de um Assert vamos ver apenas o primeiro, ou seja, um a um
		error.checkThat(locacao.getValor(), is(CoreMatchers.equalTo(10.0)));
	}
	
	@Test
	public void deveDevolverFilmeNaSegundaAoAlugarNoSabado() throws Exception {
		// esse teste deve validar apenas no sábado utilizamos assumption
		//Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
	
		// exemplo utilizando construtor com Date
		// com powerMock não precisamos mais utilizar assumptions para dizer quando executar o teste
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017)); // 29/04 eh sabado
		
		// exemplo utilizando método estático com Calendar
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, 29);
//		cal.set(Calendar.MONTH, Calendar.APRIL);
//		cal.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(cal);
//				
		// acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		// validacao
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//Assert.assertTrue(ehSegunda);
		
		// criando proprios matchers
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		
		// verificando se o construtor Date foi realmente invocado (na ação(alugarFilme) ele deve ser invocado 2x)
		//PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		// verificando se o metodo estatico realmente foi invocado
		PowerMockito.verifyStatic(Mockito.times(2));
		Calendar.getInstance();
	}
	
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
	
		// mockando metodo privado
		// nesse caso vai assumir esse valor e não o do calculo no metodo
		PowerMockito.doReturn(1.0).when(service, "calculaValorLocacao", filmes);
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calculaValorLocacao", filmes);
	}
	
	/**
	 * Exemplo invocando métodos privados
	 * @throws Exception
	 */
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		// acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calculaValorLocacao", filmes);
		
		// verificacao
		Assert.assertThat(valor, is(10.0));
	}
}
