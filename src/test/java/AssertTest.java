

import org.junit.Assert;
import org.junit.Test;
import com.zanatta.entidades.Usuario;

public class AssertTest {

	@Test
	public void teste() {
		// exemplos assertivas...
		// procurar utilizar o mínimo de negações...
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		// equals para varias tipagens (sempre comparar mesmo tipo)
		Assert.assertEquals(1l, 1l);
		// passar sempre delta (margem de erro de comparação)
		Assert.assertEquals(0.51234, 0.512, 0.001);
		
		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(i), i2);
		
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		
		// tendo implementado o equals
		Usuario usu = new Usuario("Jean");
		Usuario usu2 = new Usuario("Jean");
		Usuario usuNu = null;
		Assert.assertEquals(usu, usu2);
		
		// comparando o objeto a nivel de instancia
		Assert.assertSame(usu, usu);
		Assert.assertNotSame("Os objetos são de mesma intância", usu, usu2);
		
		Assert.assertNull(usuNu);
		
		
		
	}
}
