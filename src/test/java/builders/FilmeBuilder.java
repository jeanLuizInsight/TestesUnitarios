package builders;

import com.zanatta.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;
	
	private FilmeBuilder() {}
	
	/**
	 * Padrão change method (cria um padrao e depois vai definindo o cenario)
	 * @return
	 */
	public static FilmeBuilder umFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme("Filme 1", 2, 10.0);
		return builder;
	}
	
	/**
	 * Padrão object method (cria um padrao com cenario especifico)
	 * OBS.: como bonus posso ter acesso a todos os outros
	 * métodos  
	 * @return
	 */
	public static FilmeBuilder umFilmeSemEstoque() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme("Filme 1", 0, 10.0);
		return builder;
	}
	
	public FilmeBuilder semEstoque() {
		filme.setEstoque(0);
		return this;
	}
	
	public FilmeBuilder comValor(Double valor) {
		filme.setPrecoLocacao(valor);
		return this;
	}
	
	public Filme agora() {
		return filme;
	}
}
