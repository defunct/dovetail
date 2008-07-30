/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.lang.reflect.Method;
import java.util.regex.Matcher;


public class StringTestMethod
implements TestMethod
{
    private final TestMethod test;
    
    private final Method method;
    
    public StringTestMethod(Method method, int group)
    {
        this.method = method;
        this.test = new GroupTestMethod(group);
    }
    
    public String test(Matcher matcher) throws Exception
    {
        return (String) method.invoke(null, test.test(matcher));
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */