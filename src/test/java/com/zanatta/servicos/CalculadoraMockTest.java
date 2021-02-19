package com.zanatta.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.zanatta.servicos.Calculadora;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deveMostrarDiferencaEntreMockESpy() {
		// comportamento do mock:
		// preciso gravar expectativa pra ele retornar o valor esperado
		// caso não gravar espectativa vai retornar o valor padrão to tipo (no caso 0)
		// se retorno void não faz nada
		Mockito.when(calcMock.somar(1, 2)).thenReturn(8);
		System.out.println("[Mock] Valor com expectativa: " + calcMock.somar(1, 2));
		System.out.println("[Mock] Valor sem expectativa: " + calcMock.somar(1, 5));
		
		// comportamento spy:
		// se eu não gravar a expectativa ele vai retornar o valor de execução real do método
		// portanto não funciona com interface
		//Mockito.when(calcSpy.somar(1, 2)).thenReturn(8);  // assim o método somar é executado na expectativa (o que não eh legal ocorrer)
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);    // assim não é executado
		// se for retorno void faz o que tiver, para não fazer usa assim
		Mockito.doNothing().when(calcSpy).imprime();
		System.out.println("[Spy] Valor com expectativa: " + calcSpy.somar(1, 2));
		System.out.println("[Spy] Valor sem expectativa: " + calcSpy.somar(1, 5));
		
		System.out.println("[Mock] imprime: ");
		calcMock.imprime();
		System.out.println("[Spy] imprime: ");
		calcSpy.imprime();
	}

	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCap = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCap.capture(), argCap.capture())).thenReturn(5);
		
		Assert.assertEquals(5,  calc.somar(2343423, -435435));
		// lembrando que a captura dos valores deve ser utilizada sempre depois da acao, 
		// que no caso eh efetuada na linha acima
		System.out.println(argCap.getAllValues());
	}
}
