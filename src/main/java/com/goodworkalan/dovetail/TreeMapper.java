package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

// TODO Document.
public class TreeMapper<T> implements GlobMapper
{
    // TODO Document.
    private final Map<String, String> mapOfCommands;
    
    // TODO Document.
    private final Map<String, String> mapOfParameters;
    
    // TODO Document.
    private final SortedMap<Integer, Mapping<T>> mapOfMappings;
    
    // TODO Document.
    public TreeMapper()
    {
        this.mapOfCommands = new HashMap<String, String>();
        this.mapOfParameters = new HashMap<String, String>();
        this.mapOfMappings = new TreeMap<Integer, Mapping<T>>(Collections.reverseOrder());
    }
    
    // TODO Document.
    private TreeMapper(SortedMap<Integer, Mapping<T>> mapOfMappings, Map<String, String> mapOfCommands, Map<String, String> mapOfParameters)
    {
        this.mapOfMappings = mapOfMappings;
        this.mapOfCommands = new HashMap<String, String>(mapOfCommands);
        this.mapOfParameters = new HashMap<String, String>(mapOfParameters);
    }

    // TODO Document.
    public void addCommand(String name, String value)
    {
        mapOfCommands.put(name, value);
    }
    
    // TODO Document.
    public void addParameter(String name, String value)
    {
        mapOfParameters.put(name, value);
    }
    
    // TODO Document.
    public Set<String> mark()
    {
        return null;
    }
    
    // TODO Document.
    public void revert(Set<String> mark)
    {
    }
    
    // TODO Document.
    public TreeMapper<T> duplicate()
    {
        return new TreeMapper<T>(mapOfMappings, mapOfCommands, mapOfParameters);
    }
    
    // TODO Document.
    public void map(int priority, T object)
    {
        Mapping<T> mapping = new Mapping<T>(object, priority, mapOfCommands, mapOfParameters);
        mapOfMappings.put(priority, mapping);
    }
    
    // TODO Document.
    public List<Mapping<T>> mappings()
    {
        return new ArrayList<Mapping<T>>(mapOfMappings.values());
    }
}
