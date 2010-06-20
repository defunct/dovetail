package com.goodworkalan.dovetail;

public class FactoryMatchTestServer implements MatchTestServer {
	private final Class<? extends MatchTest> matchTestClass;

	public FactoryMatchTestServer(Class<? extends MatchTest> matchTestClass) {
		this.matchTestClass = matchTestClass;
	}

	public MatchTest getInstance(MatchTestFactory factory) {
		return factory.getInstance(matchTestClass);
	}
}
