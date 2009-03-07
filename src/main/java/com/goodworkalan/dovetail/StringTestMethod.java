/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

// TODO Document.
public class StringTestMethod
implements TestMethod
{
    // TODO Document.
    private final TestMethod test;
    
    // TODO Document.
    private final Method method;
    
    // TODO Document.
    public StringTestMethod(Method method, int group)
    {
        this.method = method;
        this.test = new GroupTestMethod(group);
    }
    
    // TODO Document.
    public String test(Matcher matcher) throws Exception
    {
        return (String) method.invoke(null, test.test(matcher));
    }
    
    // TODO Document.
    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object instanceof StringTestMethod)
        {
            StringTestMethod stringTestMethod = (StringTestMethod) object;
            return test.equals(stringTestMethod.test)
                && method.equals(stringTestMethod.method);
        }
        return false;
    }
    
    // TODO Document.
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 37 + test.hashCode();
        hash = hash * 37 + method.hashCode();
        return hash;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */