/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class Glob
{
    private final static short PROPERTY = 1;
    
    private final static short PATTERN = 2;

    private final static short TEST = 3;
    
    private final static short GROUP = 4;
    
    private final static short DONE = 5;
    
    private final String pattern;

    private final Match[] matches;
        
    public static String manyTest(String[] parts)
    {
        StringBuilder builder = new StringBuilder();
        String separator = "";
        for (String part : parts)
        {
            builder.append(separator);
            builder.append(part);
            separator = "/";
        }
        return builder.toString();
    }
    
    public Glob(Class<?> target, String pattern)
    {
        int min = 1;
        int max = 1;
        String[] parts = pattern.split("/");
        List<Match> matches = new ArrayList<Match>();
        for (int i = 0; i < parts.length; i++)
        {
            String part = parts[i];
            if (part.length() > 0 && part.charAt(0) == '?')
            {
                if (max == Integer.MAX_VALUE)
                {
                    throw new DovetailException();
                }
                part = part.substring(1);
                min = 0;
            }
            if (part.length() == 0)
            {
                if (i == 0)
                {
                    matches.add(new Literal(parts[i], min, max));
                }
                else 
                {
                    max = Integer.MAX_VALUE;
                }
            }
            else if (part.charAt(0) == '{')
            {
                matches.add(new Expression(target, part, min, max));
                min = max = 1;
            }
            else if (part.equals("*"))
            {
                matches.add(new Any(min, max));
                min = max = 1;
            }
            else
            {
                matches.add(new Literal(part, min, max));
                min = max = 1;
            }
        }
        this.matches = matches.toArray(new Match[matches.size()]);
        this.pattern = pattern;
    }

    private interface Match
    {
        public boolean match(GlobMapper mapper, String[] parts, int start, int end);

        public int getMin();
        
        public int getMax();
    }

    private final static class Expression
    implements Match
    {
        private final String parameter;
        
        private final Pattern pattern;
        
        private final TestMethod test;
        
        private final Method manyTest;
        
        private final int min;
        
        private final int max;
        
        private final boolean command;
        
        public Expression(Class<?> target, String expression, int min, int max)
        {
            StringBuilder parameter = new StringBuilder();
            StringBuilder pattern = new StringBuilder();
            StringBuilder method = new StringBuilder();
            StringBuilder group = new StringBuilder();

            boolean eatWhite = true;
            short state = PROPERTY;

            char[] chars = expression.toCharArray();
            
            for (int i = 1; i < chars.length - 1; i++)
            {
                switch (state)
                {
                    case PROPERTY:
                        if (chars[i] == ' ')
                        {
                            if (!eatWhite)
                            {
                                state = PATTERN;
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
                    case PATTERN:
                        if (chars[i] == ' ')
                        {
                            if (!eatWhite)
                            {
                                state = TEST;
                                eatWhite = true;
                            }
                        }
                        else
                        {
                            pattern.append(chars[i]);
                            eatWhite = false;
                        }
                        break;
                    case TEST:
                        if (chars[i] == ' ')
                        {
                            if (!eatWhite)
                            {
                                state = GROUP;
                                eatWhite = true;
                            }
                        }
                        else if(chars[i] == '%' && method.length() == 0)
                        {
                            eatWhite = false;
                            state = GROUP;
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
                    case GROUP:
                        if (chars[i] == ' ')
                        {
                            state = DONE;
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
                    case DONE:
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
            if (parameter.charAt(0) == '@')
            {
                this.parameter = parameter.substring(1);
                this.command = true;
            }
            else
            {
                this.parameter = parameter.toString();
                this.command = false;
            }
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
        
        public int toGroup(CharSequence group)
        {
            if (group.length() == 0)
            {
                return -1;
            }
            return Integer.parseInt(group.toString());
        }
        
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
                    if (command)
                    {
                        mapper.addCommand(parameter, catenated);
                    }
                    else
                    {
                        mapper.addParameter(parameter, catenated);
                    }
                    return true;
                }
            }
            catch (Exception e)
            {
                throw new DovetailException(e);
            }
            return false;
        }
        
        public int getMin()
        {
            return min;
        }
        
        public int getMax()
        {
            return max;
        }
    }

    private final static class Literal
    implements Match
    {
        private final String literal;
        
        private final int min;
        
        private final int max;

        public Literal(String literal, int min, int max)
        {
            this.literal = literal;
            this.min = min;
            this.max = max;
        }

        public boolean match(GlobMapper mapper, String[] parts, int start, int end)
        {
            for (int i = start; i < end; i++)
            {
                if (!literal.equals(parts[i]))
                {
                    return false;
                }
            }
            return true;
        }
        
        public int getMin()
        {
            return min;
        }
        
        public int getMax()
        {
            return max;
        }
    }
    
    private final static class Any
    implements Match
    {
        private final int min;
        
        private final int max;
        
        public Any(int min, int max)
        {
            this.min = min;
            this.max = max;
        }
        
        public boolean match(GlobMapper mapper, String[] parts, int start, int end)
        {
            return true;
        }
        
        public int getMin()
        {
            return min;
        }
        
        public int getMax()
        {
            return max;
        }
    }
    
    public String getPattern()
    {
        return pattern;
    }

    public GlobMapping map(String path)
    {
        CoreGlobMapping globMapping = new CoreGlobMapping(this);
        if (match(globMapping, path))
        {
            return globMapping;
        }
        return null;
    }
    
    public boolean match(String path)
    {
        return match(new NullGlobMapper(), path);
    }
    
    public boolean match(GlobMapper mapper, String path)
    {
        return descend(mapper, matches, 0, path.split("/"), 0);
    }

    private static boolean descend(GlobMapper mapper, Match[] matches, int matchIndex, String[] parts, int partIndex)
    {
        int partsLeft = parts.length - partIndex;
        int matchesLeft = matches.length - matchIndex;
        for (int i = matchIndex; i < matches.length; i++)
        {
            if (matches[i].getMin() == 0)
            {
                matchesLeft--;
            }
        }
        int min = matches[matchIndex].getMin();
        int max = Math.min(partsLeft - matchesLeft + 1, matches[matchIndex].getMax());
        for (int i = min; i <= max; i++)
        {
            Set<String> mark = mapper.mark();
            if (match(mapper, matches, matchIndex, parts, partIndex, i))
            {
                return true;
            }
            mapper.revert(mark);
        }
        return false;
    }

    private static boolean match(GlobMapper mapper, Match[] matches, int matchIndex, String[] parts, int partIndex, int length)
    {
        if (length == 0 || matches[matchIndex].match(mapper, parts, partIndex, partIndex + length))
        {
            matchIndex++;
            
            partIndex += length;

            if (partIndex == parts.length)
            {
                int matchesLeft = matches.length - matchIndex;
                for (int i = matchIndex; i < matches.length; i++)
                {
                    if (matches[matchIndex].getMin() == 0)
                    {
                        matchesLeft--;
                    }
                }
                return matchesLeft == 0;
            }
            if (matchIndex == matches.length)
            {
                return false;
            }
            return descend(mapper, matches, matchIndex, parts, partIndex);
        }
        return false;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */