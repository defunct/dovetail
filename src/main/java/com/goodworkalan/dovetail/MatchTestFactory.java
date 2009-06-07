package com.goodworkalan.dovetail;

public interface MatchTestFactory
{
    public MatchTest getInstance(Class<? extends MatchTest> matchTestClass);
}
