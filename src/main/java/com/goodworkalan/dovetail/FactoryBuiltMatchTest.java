package com.goodworkalan.dovetail;

import java.util.Map;

public class FactoryBuiltMatchTest implements MatchTest
{
    private final MatchTestFactory factory;
    
    private final Class<? extends MatchTest> matchTestClass;
    
    public FactoryBuiltMatchTest(MatchTestFactory factory, Class<? extends MatchTest> matchTestClass)
    {
        this.factory = factory;
        this.matchTestClass = matchTestClass;
    }
    
    public boolean test(String path, Map<String, String> parameters)
    {
        return factory.getInstance(matchTestClass).test(path, parameters);
    }
}
