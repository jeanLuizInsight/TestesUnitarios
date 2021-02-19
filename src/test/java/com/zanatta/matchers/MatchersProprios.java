package com.zanatta.matchers;

public class MatchersProprios {
	
	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		// apenas instanciando ele ja faz a validacao pois extende de TypeSafeMatcher com implementação matchesSafely
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DataDiferencaDiasMatcher ehHojeComDiferencaoDias(Integer qtdDias) {
		return new DataDiferencaDiasMatcher(qtdDias);
	}
	
	public static DataDiferencaDiasMatcher ehHoje() {
		return new DataDiferencaDiasMatcher(0);
	}

}
