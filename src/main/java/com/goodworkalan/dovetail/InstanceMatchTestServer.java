package com.goodworkalan.dovetail;

public class InstanceMatchTestServer implements MatchTestServer {
	private final MatchTest matchTest;

	public InstanceMatchTestServer(MatchTest matchTest) {
		this.matchTest = matchTest;
	}

	public MatchTest getInstance(MatchTestFactory factory) {
		return matchTest;
	}
}
