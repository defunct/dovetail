/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class CoreGlobMapping
implements GlobMapping, GlobMapper
{
    private final Glob glob;

    private final Map<String, String[]> parameters;
    
    private final Map<String, String> commands;
    
    public CoreGlobMapping(Glob glob)
    {
        this.glob = glob;
        this.parameters = new LinkedHashMap<String, String[]>();
        this.commands = new LinkedHashMap<String, String>();
    }
    
    public Glob getGlob()
    {
        return glob;
    }
    
    public Set<String> mark()
    {
        Set<String> set = new HashSet<String>();
        set.addAll(parameters.keySet());
        set.addAll(commands.keySet());
        return set;
    }
    
    public void revert(Set<String> mark)
    {
        parameters.keySet().retainAll(mark);
        commands.keySet().retainAll(mark);
    }
    
    public void addParameter(String name, String value)
    {
        parameters.put(name, new String[] { value });
    }
    
    public Map<String, String[]> getParameters()
    {
        return parameters;
    }
    
    public void addCommand(String name, String value)
    {
        commands.put(name, value);
    }
    
    public Map<String, String> getCommands()
    {
        return commands;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */