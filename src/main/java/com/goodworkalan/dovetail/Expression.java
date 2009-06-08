package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.*;
import static com.goodworkalan.dovetail.DovetailException.MISMATCHED_IDENTIFIERS;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final List<String> identifiers;
    
    // TODO Document.
    private final Pattern regex;
    
    private final String sprintf;
    
    // TODO Document.
    private final int min;
    
    // TODO Document.
    private final int max;
    
    private final boolean deep;
    
    // TODO Document.
    public int toGroup(CharSequence group)
    {
        if (group.length() == 0)
        {
            return -1;
        }
        return Integer.parseInt(group.toString());
    }
    
    public Expression(List<String> identifiers, Pattern regex, String sprintf, int min, int max, boolean deep)
    {
        this.identifiers = identifiers;
        this.regex = regex;
        this.sprintf = sprintf;
        this.min = min;
        this.max = max;
        this.deep = deep;
    }

    // TODO Document.
    public boolean match(Map<String, String> parameters, String[] parts, int start, int end)
    {
        if (min == 0 && end - start == 0)
        {
            return true;
        }
        else
        {
            if (deep)
            {
                StringBuilder path = new StringBuilder();
                for (int i = start; i < end; i++)
                {
                    path.append(parts[i]).append("/");
                }
                Matcher matcher = regex.matcher(path);
                if (matcher.matches())
                {
                    parameters(matcher, parameters);
                    return true;
                }
                
            }
            else if (end - start != 1)
            {
                throw new IllegalStateException();
            }
            else
            {
                Matcher matcher = regex.matcher(parts[start]);
                if (matcher.matches())
                {
                    parameters(matcher, parameters);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean parameters(Matcher matcher, Map<String, String> parameters)
    {
        if (matcher.matches())
        {
            if (matcher.groupCount() == 0)
            {
                if (identifiers.size() != 1)
                {
                    throw new DovetailException(MISMATCHED_IDENTIFIERS).add(1, identifiers.size());
                }
                parameters.put(identifiers.get(0), matcher.group());
            }
            else
            {
                if (identifiers.size() != matcher.groupCount())
                {
                    throw new DovetailException(MISMATCHED_IDENTIFIERS).add(matcher.groupCount(), identifiers.size());
                }
                for (int i = 0; i < matcher.groupCount(); i++)
                {
                    parameters.put(identifiers.get(i), matcher.group(i + 1));
                }
            }
            return true;
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
    
    public void append(StringBuilder path, Map<String, String> parameters)
    {
        Object[] args = new Object[identifiers.size()];
        for (int i = 0; i < identifiers.size(); i++)
        {
            args[i] = parameters.get(identifiers.get(i));
            if (args[i] == null)
            {
                throw new DovetailException(FORMAT_PARAMETER_IS_NULL).add(identifiers.get(i));
            }
        }
        path.append(String.format(sprintf, args));
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
            return min == other.min
                && max == other.max
                && identifiers.equals(other.identifiers)
                && regex.pattern().equals(other.regex.pattern())
                && sprintf.equals(other.sprintf)
                && deep == deep;
        }
        return false;
    }

    // TODO Document.
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 37 + max;
        hash = hash * 37 + min;
        hash = hash * 37 + identifiers.hashCode();
        hash = hash * 37 + regex.pattern().hashCode();
        hash = hash * 37 + sprintf.hashCode();
        hash = hash * 37 + (deep ? 15485867 : 32452843);
        return hash;
    }
}