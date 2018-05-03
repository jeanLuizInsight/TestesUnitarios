

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Garantindo ordem dos testes pelo nome do metodo
 * @author jean
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemTest {
	
	// static pos quero que fica no escopo da classe
	public static int contador = 0;

	@Test
	public void t1_verifica() {
		contador = 1;
	}
	
	@Test
	public void t2_verifica() {
		Assert.assertEquals(1, contador);
	}
}
