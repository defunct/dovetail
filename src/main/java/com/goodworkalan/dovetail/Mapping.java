package com.goodworkalan.dovetail;

import java.util.HashMap;
import java.util.Map;

public class Mapping<T>
{
    private final T object;
    
    private final int priority;
    
    private final Map<String, String> mapOfCommands;
    
    private final Map<String, String> mapOfParameters; 
    
    public Mapping(T object, int priority, Map<String, String> mapOfCommands, Map<String, String> mapOfParameters)
    {
        this.object = object;
        this.priority = priority;
        this.mapOfCommands = new HashMap<String, String>(mapOfCommands);
        this.mapOfParameters = new HashMap<String, String>(mapOfParameters);
    }
    
    public T getObject()
    {
        return object;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public Map<String, String> getCommands()
    {
        return mapOfCommands;
    }
    
    public Map<String, String> getParameters()
    {
        return mapOfParameters;
    }
}