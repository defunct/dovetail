/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// TODO Document.
public class CoreGlobMapping
implements GlobMapping, GlobMapper
{
    // TODO Document.
    private final Glob glob;

    // TODO Document.
    private final Map<String, String[]> parameters;
    
    // TODO Document.
    public CoreGlobMapping(Glob glob)
    {
        this.glob = glob;
        this.parameters = new LinkedHashMap<String, String[]>();
    }
    
    // TODO Document.
    public Glob getGlob()
    {
        return glob;
    }
    
    // TODO Document.
    public Set<String> mark()
    {
        Set<String> set = new HashSet<String>();
        set.addAll(parameters.keySet());
        return set;
    }
    
    // TODO Document.
    public void revert(Set<String> mark)
    {
        parameters.keySet().retainAll(mark);
    }
    
    // TODO Document.
    public void addParameter(String name, String value)
    {
        parameters.put(name, new String[] { value });
    }
    
    // TODO Document.
    public Map<String, String[]> getParameters()
    {
        return parameters;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */