/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.List;
import java.util.Map;

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
    private final Test[] matches;
    
    // TODO Document.
    private final MatchTest[] matchTests;
    
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
    public Glob(Test[] matches, String pattern, MatchTest[] matchTests)
    {
        this.matches = matches;
        this.pattern = pattern;
        this.matchTests = matchTests;
    }
    
    // TODO Document.
    public int size()
    {
        return matches.length;
    }
    
    // TODO Document.
    public Test get(int i)
    {
        return matches[i];
    }

    // TODO Document.
    public String getPattern()
    {
        return pattern;
    }
    
    public boolean matchTests(String path, Map<String, String> parameters)
    {
        for (MatchTest matchTest : matchTests)
        {
            if (!matchTest.test(path, parameters))
            {
                return false;
            }
        }
        return true;
    }

    // FIXME Sure, we can keep it, but let's just use the tree logic, create
    // a tree and match against that.
    public Map<String, String> _map(String path)
    {
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(this, new Object());
        List<Match<Object>> mapping = tree.map(path);
        if (mapping.isEmpty())
        {
            return null;
        }
        return mapping.get(0).getParameters();
    }
    
    // TODO Document.
    public boolean match(String path)
    {
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(this, new Object());
        return tree.match(path);
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */