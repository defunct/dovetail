package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.List;

// TODO Document.
public class GlobCompiler
{
    // FIXME This becomes a factory specific to this package.
    private final Class<?> conditionals;
    
    // TODO Document.
    public GlobCompiler(Class<?> conditionals)
    {
        this.conditionals = conditionals;
    }
    
    // TODO Document.
    public Glob compile(String pattern)
    {
        int min = 1;
        int max = 1;
        String[] parts = pattern.split("/", -1);
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
                    matches.add(new Literal(parts[i], min));
                }
                else 
                {
                    max = Integer.MAX_VALUE;
                }
            }
            else if (part.charAt(0) == '{')
            {
                matches.add(new Expression(conditionals, part, min, max));
                min = max = 1;
            }
            else
            {
                matches.add(new Literal(part, min));
                min = max = 1;
            }
        }
        
        if (parts.length != matches.size() && max == Integer.MAX_VALUE)
        {
            matches.add(new Literal("", 1));
        }
        
        return new Glob(matches.toArray(new Match[matches.size()]), pattern, conditionals);
    }
}
