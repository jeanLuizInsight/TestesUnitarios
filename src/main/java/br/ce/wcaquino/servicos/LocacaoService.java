package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import daos.LocacaoDAO;
import exceptions.FilmeSemEstoqueException;
import exceptions.LocadoraException;

public class LocacaoService {
	
	private LocacaoDAO locacaoDao;
	private SpcService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException {
		if (usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}

		if (CollectionUtils.isEmpty(filmes)) {
			throw new LocadoraException("Filme vazio");
		}

		this.validaEstoqueFilmes(filmes);
		
		boolean negativado = false;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com Spc, tente novamente!");
		}
		
		if (negativado) {
			throw new LocadoraException("Usuario negativado!");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(Calendar.getInstance().getTime());
		locacao.setValor(this.calculaValorLocacao(filmes));

		// Entrega no dia seguinte
		Calendar calendar = Calendar.getInstance();
		Date dataEntrega = adicionarDias(calendar, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(calendar, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		locacaoDao.salvar(locacao);

		return locacao;
	}

	private void validaEstoqueFilmes(List<Filme> filmes) throws FilmeSemEstoqueException {
		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("Filme " + filme.getNome() + " sem estoque!");
			}
		}
	}

	private Double calculaValorLocacao(List<Filme> filmes) {
		Double valor = Double.valueOf(0);
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			// calculando descontos possiveis
			switch (i) {
			case 2:
				valorFilme = valorFilme * 0.75;
				break;
			case 3:
				valorFilme = valorFilme * 0.5;
				break;
			case 4:
				valorFilme = valorFilme * 0.25;
				break;
			case 5:
				valorFilme = 0.0;
				break;

			default:
				break;
			}
			valor += valorFilme;
		}

		return valor;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = locacaoDao.obterLocacoesPendentes();
		for(Locacao loc : locacoes) {
			if (loc.getDataRetorno().before(Calendar.getInstance().getTime())) {
				emailService.notificarAtrasos(loc.getUsuario());
			}
		}
	}
	
	public void prorrograLocacao(Locacao locacao, int dias) {
		Locacao loc = new Locacao();
		loc.setUsuario(locacao.getUsuario());
		loc.setFilmes(locacao.getFilmes());
		loc.setDataLocacao(Calendar.getInstance().getTime());
		loc.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		loc.setValor(locacao.getValor() * dias);
		locacaoDao.salvar(loc);
	}
}