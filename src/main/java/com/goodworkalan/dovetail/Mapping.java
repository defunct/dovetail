package com.goodworkalan.dovetail;

import java.util.HashMap;
import java.util.Map;

// TODO Document.
public class Mapping<T>
{
    // TODO Document.
    private final T object;
    
    // TODO Document.
    private final int priority;
    
    // TODO Document.
    private final Map<String, String> mapOfParameters; 
    
    // TODO Document.
    public Mapping(T object, int priority, Map<String, String> mapOfCommands, Map<String, String> mapOfParameters)
    {
        this.object = object;
        this.priority = priority;
        this.mapOfParameters = new HashMap<String, String>(mapOfParameters);
    }
    
    // TODO Document.
    public T getObject()
    {
        return object;
    }
    
    // TODO Document.
    public int getPriority()
    {
        return priority;
    }
    
    // TODO Document.
    public Map<String, String> getParameters()
    {
        return mapOfParameters;
    }
}