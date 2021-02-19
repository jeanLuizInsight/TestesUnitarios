package daos;

import java.util.List;
import com.zanatta.entidades.Locacao;

public interface LocacaoDAO {

	void salvar(Locacao locacao);

	List<Locacao> obterLocacoesPendentes();
}
