package builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {

	private Usuario usuario;
	
	/**
	 * Nimguem pode criar instancias do Builder externamente ao proprio builder
	 */
	private UsuarioBuilder() {}
	
	/**
	 * Acessado externamente sem necessidade de uma instancia
	 * @return
	 */
	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario("Usuario 1");
		return builder;
	}
	
	public UsuarioBuilder comNome(String nome) {
		usuario.setNome(nome);
		return this;
	}
	
	public Usuario agora() {
		return usuario;
	}
}
