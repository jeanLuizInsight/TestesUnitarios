package br.ce.wcaquino.servicos;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Se mesmo cenario e ação usar mesmo teste no mesmo método mesmo com varias assertivas 
	 * @throws Exception 
	 */
	@Test
	public void testeLocacao() throws Exception {
		// falha x erro: falha resultado inesperado, erro excessão por isso sempre lançar excessão pra camada superior que o JUnit trata
		
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
			
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
	public void testeLocacaoFilmeSemEstoque() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		//acao
		service.alugarFilme(usuario, filme);
			
		// validacao
		// está no expected
	}
	
	/**
	 * Tratamento de exceção enviada ROBUSTA utilizamos quando precisamos uma determinada exceção e mensagem permite maior controle sobre o erro
	 * Nesse caso vai ocorrer a exceção e queremos validar a mensagem error
	 */
	@Test
	public void testeLocacaoFilmeSemEstoque2() {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		try {
			//acao
			service.alugarFilme(usuario, filme);
		
			// para não gerar falso positivo, nesse caso chegou aqui não gerou exceção mais devria ter gerado (teste falhou)
			Assert.fail("Deveria ter lançado excessão!!!");
		} catch (Exception e) {
			// validando o erro
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
		}
	}
	
	/**
	 * Tratamento de exceção enviada FORMA NOVA utilizamos quando precisamos uma determinada exceção e mensagem
	 * Nesse caso vai ocorrer a exceção e o teste vai falhar pois  queremos validar error
	 * @throws Exception 
	 */
	@Test
	public void testeLocacaoFilmeSemEstoque3() throws Exception {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		// validacao faz parte do cenário, nesse caso passar sempre antes da ação
		// exceção esperada
		exception.expect(FilmeSemEstoqueException.class);
		// mensagem esperada na exceção
		//exception.expectMessage("Filme sem estoque");
		
		//acao
		service.alugarFilme(usuario, filme);
	}
	
	/**
	 * Tratamento apenas pro Usuario, se filme vier sem estoque não interessa deixa o JUnit tratar
	 * @throws FilmeSemEstoqueException
	 * @throws LocadoraException 
	 */
	@Test
	public void testeLocacaoFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		LocacaoService service = new LocacaoService();
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
	public void testeLocacaoUsuarioVazio() throws FilmeSemEstoqueException {
		//cenario
		LocacaoService service = new LocacaoService();
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		try {
			//acao
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio"));
		} 
	}
}
