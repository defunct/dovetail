/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

// TODO Document.
public class MatcherTestMethod
implements TestMethod
{
    // TODO Document.
    private final Method method;
    
    // TODO Document.
    public MatcherTestMethod(Method method)
    {
        this.method = method;
    }

    // TODO Document.
    public String test(Matcher matcher) throws Exception
    {
        return (String) method.invoke(null, matcher);
    }
    
    // TODO Document.
    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object instanceof MatcherTestMethod)
        {
            MatcherTestMethod matcherTestMethod = (MatcherTestMethod) object;
            return method.equals(matcherTestMethod.method);
        }
        return false;
    }
    
    // TODO Document.
    @Override
    public int hashCode()
    {
        return method.hashCode();
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */