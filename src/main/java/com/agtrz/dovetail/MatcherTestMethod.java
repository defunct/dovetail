/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.lang.reflect.Method;
import java.util.regex.Matcher;


public class MatcherTestMethod
implements TestMethod
{
    private final Method method;
    
    public MatcherTestMethod(Method method)
    {
        this.method = method;
    }

    public String test(Matcher matcher) throws Exception
    {
        return (String) method.invoke(null, matcher);
    }
    
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
    
    @Override
    public int hashCode()
    {
        return method.hashCode();
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */