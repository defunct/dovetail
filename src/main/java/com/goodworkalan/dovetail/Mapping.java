package com.goodworkalan.dovetail;

import java.util.HashMap;
import java.util.Map;

public class Mapping<T>
{
    private final T object;
    
    private final int priority;
    
    private final Map<String, String> mapOfParameters; 
    
    public Mapping(T object, int priority, Map<String, String> mapOfCommands, Map<String, String> mapOfParameters)
    {
        this.object = object;
        this.priority = priority;
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
    
    public Map<String, String> getParameters()
    {
        return mapOfParameters;
    }
}