package br.ce.wcaquino.matchers;

public class MatchersProprios {
	
	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		// apenas instanciando ele ja faz a validacao pois extende de TypeSafeMatcher com implementação matchesSafely
		return new DiaSemanaMatcher(diaSemana);
	}

}
