package com.zanatta.servicos;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.zanatta.servicos.Calculadora;
import exceptions.NaoPodeDividirPorZeroException;
import runners.ParallelRunners;

//@RunWith(ParallelRunners.class)
public class CalculadoraTest {

	@Test
	public void deveSomarDoisValores() {
		// cenário
		int x = 5;
		int y = 3;
		Calculadora calc = new Calculadora();
		
		// ação
		int resultado = calc.somar(x, y);
		
		// validação
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		// cenário
		int x = 5;
		int y = 3;
		Calculadora calc = new Calculadora();
		
		// ação
		int resultado = calc.subtrair(x, y);
		
		// validação
		Assert.assertEquals(2, resultado);
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		// cenário
		int x = 8;
		int y = 2;
		Calculadora calc = new Calculadora();
		
		// ação
		int resultado = calc.dividir(x, y);
		
		// validação
		Assert.assertEquals(4, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		// cenário
		int x = 5;
		int y = 0;
		Calculadora calc = new Calculadora();
		
		// ação
		int resultado = calc.dividir(x, y);
		
		// validação
		// expected exception
	}
}
