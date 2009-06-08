package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.*;

public class SimpleMatchTestFactory implements MatchTestFactory
{
    public MatchTest getInstance(Class<? extends MatchTest> matchTestClass)
    {
        try
        {
            return matchTestClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new DovetailException(MATCH_TEST_CONSTRUCTOR_THREW_EXCEPTION, e);
        }
        catch (IllegalAccessException e)
        {
            throw new DovetailException(MATCH_TEST_CONSTRUCTOR_NOT_VISIBLE, e);
        }
    }
}