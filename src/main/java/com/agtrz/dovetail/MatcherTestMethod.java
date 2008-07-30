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
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */