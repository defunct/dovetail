package com.goodworkalan.dovetail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * An expression that matches a part or sub path in a path pattern. This 
 * class includes a constructor that will parse the pattern.
 * 
 * @author Alan Gutierrez
 */
final class Expression
implements Test
{
    // TODO Document.
    private final String parameter;
    
    // TODO Document.
    private final Pattern pattern;
    
    // TODO Document.
    private final TestMethod test;
    
    // TODO Document.
    private final Method manyTest;
    
    // TODO Document.
    private final int min;
    
    // TODO Document.
    private final int max;
    
    // TODO Document.
    public Expression(Class<?> target, String expression, int min, int max)
    {
        StringBuilder parameter = new StringBuilder();
        StringBuilder pattern = new StringBuilder();
        StringBuilder method = new StringBuilder();
        StringBuilder group = new StringBuilder();

        boolean eatWhite = true;
        short state = Glob.PROPERTY;

        char[] chars = expression.toCharArray();
        
        for (int i = 1; i < chars.length - 1; i++)
        {
            switch (state)
            {
                case Glob.PROPERTY:
                    if (chars[i] == ' ')
                    {
                        if (!eatWhite)
                        {
                            state = Glob.PATTERN;
                            eatWhite = true;
                        }
                    }
                    else if (chars[i] == '@' && parameter.length() == 0)
                    {
                        parameter.append(chars[i]);
                        eatWhite = false;
                    }
                    else if (Character.isJavaIdentifierPart(chars[i]))
                    {
                        parameter.append(chars[i]);
                        eatWhite = false;
                    }
                    else
                    {
                        throw new DovetailException();
                    }
                    break;
                case Glob.PATTERN:
                    if (chars[i] == ' ')
                    {
                        if (!eatWhite)
                        {
                            state = Glob.TEST;
                            eatWhite = true;
                        }
                    }
                    else
                    {
                        pattern.append(chars[i]);
                        eatWhite = false;
                    }
                    break;
                case Glob.TEST:
                    if (chars[i] == ' ')
                    {
                        if (!eatWhite)
                        {
                            state = Glob.GROUP;
                            eatWhite = true;
                        }
                    }
                    else if(chars[i] == '%' && method.length() == 0)
                    {
                        eatWhite = false;
                        state = Glob.GROUP;
                    }
                    else if(chars[i] == '#')
                    {
                         if (method.length() != 0)
                         {
                             throw new DovetailException();
                         }
                    }
                    else if(Character.isJavaIdentifierPart(chars[i]))
                    {
                        method.append(chars[i]);
                        eatWhite = false;
                    }
                    else
                    {
                        throw new DovetailException();
                    }
                    break;
                case Glob.GROUP:
                    if (chars[i] == ' ')
                    {
                        state = Glob.DONE;
                    }
                    else if(chars[i] == '%')
                    {
                         if (group.length() != 0)
                         {
                             throw new DovetailException();
                         }
                    }
                    else if (Character.isDigit(chars[i]))
                    {
                        group.append(chars[i]);
                    }
                    else
                    {
                        throw new DovetailException();
                    }
                    break;
                case Glob.DONE:
                    if (chars[i] != ' ')
                    {
                        throw new DovetailException();
                    }
                    break;
            }
        }
        this.min = min;
        this.max = max;
        if (parameter.length() == 0)
        {
            throw new DovetailException();
        }
        this.parameter = parameter.toString();
        if (pattern.length() == 0)
        {
            this.pattern = Pattern.compile(".*");
        }
        else
        {
            try
            {
                this.pattern = Pattern.compile(pattern.toString());
            }
            catch (PatternSyntaxException e)
            {
                throw new DovetailException(e);
            }
        }
        if (method.length() == 0)
        {
            this.test = new GroupTestMethod(toGroup(group));
        }
        else
        {
            this.test = getTestMethod(target, method.toString(), group);
        }
        if (method.length() == 0)
        {
            this.manyTest = getManyTestMethod(Glob.class, "manyTest");
        }
        else
        {
            this.manyTest = getManyTestMethod(Glob.class, method.toString());
        }
    }
    
    // TODO Document.
    public int toGroup(CharSequence group)
    {
        if (group.length() == 0)
        {
            return -1;
        }
        return Integer.parseInt(group.toString());
    }
    
    // TODO Document.
    private TestMethod getTestMethod(Class<?> target, String methodName, CharSequence group)
    {
        Method method;
        try
        {
            method = target.getMethod(methodName, Matcher.class);
            return new MatcherTestMethod(method);
        }
        catch (Exception e)
        {
        }
        try
        {
            method = target.getMethod(methodName, String.class);
            return new StringTestMethod(method, toGroup(group));
        }
        catch (Exception e)
        {
        }
        try
        {
            target.getMethod(methodName, String[].class);
            return new GroupTestMethod(toGroup(group));
        }
        catch (Exception e)
        {
            throw new DovetailException(e);
        }
    }
    
    // TODO Document.
    public static Method getManyTestMethod(Class<?> target, String methodName)
    {
        try
        {
            return target.getMethod(methodName, String[].class);
        }
        catch (Exception e)
        {
            return getManyTestMethod(Glob.class, "manyTest");
        }
    }

    // TODO Document.
    public boolean match(GlobMapper mapper, String[] parts, int start, int end)
    {
        List<String> path = new ArrayList<String>();
        for (int i = start; i < end; i++)
        {
            Matcher matcher = pattern.matcher(parts[i]);
            if (matcher.matches())
            {
                try
                {
                    String value = test.test(matcher);
                    if (value == null)
                    {
                        return false;
                    }
                    path.add(value);
                }
                catch (Exception e)
                {
                    throw new DovetailException(e);
                }
            }
            else
            {
                return false;
            }
        }
        try
        {
            String catenated = (String) manyTest.invoke(null, new Object[] { path.toArray(new String[path.size()]) });
            if (catenated != null)
            {
                mapper.addParameter(parameter, catenated);
                return true;
            }
        }
        catch (Exception e)
        {
            throw new DovetailException(e);
        }
        return false;
    }
    
    // TODO Document.
    public int getMin()
    {
        return min;
    }
    
    // TODO Document.
    public int getMax()
    {
        return max;
    }

    // TODO Document.
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof Expression)
        {
            Expression other = (Expression) obj;
            return manyTest.equals(other.manyTest)
                && min == other.min
                && max == other.max
                && parameter.equals(other.parameter)
                && pattern.pattern().equals(other.pattern.pattern())
                && test.equals(other.test);
        }
        return false;
    }

    // TODO Document.
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 37 + manyTest.hashCode();
        hash = hash * 37 + max;
        hash = hash * 37 + min;
        hash = hash * 37 + parameter.hashCode();
        hash = hash * 37 + pattern.hashCode();
        hash = hash * 37 + test.hashCode();
        return hash;
    }
}