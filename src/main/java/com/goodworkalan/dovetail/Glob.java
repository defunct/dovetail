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

    /** The array of tests to apply against a path. */
    private final Test[] tests;
    
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
        this.tests = matches;
        this.pattern = pattern;
        this.matchTests = matchTests;
    }
    
    // TODO Document.
    public int size()
    {
        return tests.length;
    }
    
    // TODO Document.
    public Test get(int i)
    {
        return tests[i];
    }

    // TODO Document.
    public String getPattern()
    {
        return pattern;
    }

    /**
     * Apply the match tests in this glob to the given path and map of
     * parameters.
     * <p>
     * FIXME Not quite right. Rethink creation pattern. Tests should be
     * applied by the tree, they should not be in two places. Glob might become
     * internal, or only a limited interface, a GlobTree returns a compiler, the
     * compiler builds the Glob and adds it to the tree.
     * 
     * @param path
     *            The path to text.
     * @param parameters
     *            The parameters to test.
     * @return True if all the match tests in this glob pass.
     */
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

    /**
     * Test the glob against the given path returning a map of the captured
     * parameters if it matches, null if it does not match.
     * 
     * @param path
     *            The path to match.
     * @return A map of the cpatured parameters of null if it does not match.
     */
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

    /**
     * Return true if the given path is matched by this glob.
     * 
     * @param path
     *            The path to match.
     * @return True if the path matches this glob.
     */
    public boolean match(String path)
    {
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(this, new Object());
        return tree.match(path);
    }

    /**
     * Recreate a path from this glob using the parameters in the given
     * parameter map to replace the parameter captures in glob pattern.
     * 
     * @param path
     *            The path buffer.
     * @param parameters
     *            The parameters used to replace parameter captures in the glob
     *            pattern.
     */
    public String path(Map<String, String> parameters)
    {
        StringBuilder path = new StringBuilder();
        path.append("/");
        for (int i = 1, size = tests.length; i < size; i++)
        {
            if (path.charAt(path.length() - 1) != '/')
            {
                path.append('/');
            }
            tests[i].append(path, parameters);
        }
        return path.toString();
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */