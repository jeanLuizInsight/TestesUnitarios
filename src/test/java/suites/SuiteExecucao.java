package suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.zanatta.servicos.CalculadoraTest;
import com.zanatta.servicos.CalculoValorLocacaoTest;
import com.zanatta.servicos.LocacaoServiceTest;

/**
 * Exemplo utilizando uma Suite para executar todos os testes
 * NÃO eh recomendado pois em uma ferramenta de integração contínua
 * serão executados todos os testes do pacote de testes
 * mais todos os testes que encontrar na suite.
 * @author jean
 *
 */
//@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {
	// Remova se puder!
	
	@BeforeClass
	public static void init() {
		System.out.println("Before");
	}
	
	@AfterClass
	public static void finish() {
		System.out.println("Finish");
	}
}
