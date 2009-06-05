package com.goodworkalan.dovetail;

import java.util.HashMap;
import java.util.Map;

/**
 * The results of an individual match of a path against a glob tree.  
 *  
 * @author Alan Gutierrez
 *
 * @param <T> The type to which globs are mapped.
 */
public class Match<T>
{
    /** The mapped object. */
    private final T object;
    
    /** The mapping priority. */
    private final int priority;
    
    /** The parameters extracted from the match. */
    private final Map<String, String> parameters; 
    
    /**
     * Create a match of the given 
     * @param object
     * @param priority
     * @param mapOfParameters
     */
    public Match(T object, int priority, Map<String, String> mapOfParameters)
    {
        this.object = object;
        this.priority = priority;
        this.parameters = new HashMap<String, String>(mapOfParameters);
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
        return parameters;
    }
}