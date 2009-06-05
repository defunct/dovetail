/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.Set;

// TODO Document.
public final class Glob
{
    // TODO Document.
    final static short PROPERTY = 1;
    
    // TODO Document.
    final static short PATTERN = 2;

    // TODO Document.
    final static short TEST = 3;
    
    // TODO Document.
    final static short GROUP = 4;
    
    // TODO Document.
    final static short DONE = 5;
    
    // TODO Document.
    private final String pattern;

    // TODO Document.
    private final Match[] matches;
    
    // TODO Document.
    private final Class<?> conditionals;
    
    // TODO Document.
    public Class<?> getConditionalClass()
    {
        return conditionals;
    }
        
    // TODO Document.
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
    
    // TODO Document.
    public Glob(Match[] matches, String pattern, Class<?> conditionals)
    {
        this.matches = matches;
        this.pattern = pattern;
        this.conditionals = conditionals;
    }
    
    // TODO Document.
    public int size()
    {
        return matches.length;
    }
    
    // TODO Document.
    public Match get(int i)
    {
        return matches[i];
    }

    // TODO Document.
    public String getPattern()
    {
        return pattern;
    }

    // FIXME Sure, we can keep it, but let's just use the tree logic, create
    // a tree and match against that.
    public GlobMapping map(String path)
    {
        CoreGlobMapping globMapping = new CoreGlobMapping(this);
        if (match(globMapping, path))
        {
            return globMapping;
        }
        return null;
    }
    
    // TODO Document.
    public boolean match(String path)
    {
        return match(new NullGlobMapper(), path);
    }
    
    // TODO Document.
    public boolean match(GlobMapper mapper, String path)
    {
        return descend(mapper, matches, 0, path.split("/"), 0);
    }

    // TODO Document.
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

    // TODO Document.
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