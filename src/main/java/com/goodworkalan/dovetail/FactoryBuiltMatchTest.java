package com.goodworkalan.dovetail;

import java.util.Map;

// TODO Document.
public class FactoryBuiltMatchTest implements MatchTest
{
    // TODO Document.
    private final MatchTestFactory factory;
    
    // TODO Document.
    private final Class<? extends MatchTest> matchTestClass;
    
    // TODO Document.
    public FactoryBuiltMatchTest(MatchTestFactory factory, Class<? extends MatchTest> matchTestClass)
    {
        this.factory = factory;
        this.matchTestClass = matchTestClass;
    }
    
    // TODO Document.
    public boolean test(String path, Map<String, String> parameters)
    {
        return factory.getInstance(matchTestClass).test(path, parameters);
    }
}
