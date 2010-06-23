package com.goodworkalan.dovetail;

import java.util.HashMap;
import java.util.Map;

/**
 * The value and captured parameters of an individual match of a path against a
 * path association.
 * 
 * @author Alan Gutierrez
 * 
 * @param <T>
 *            The type of object mapped to the path.
 */
public class Match<T> {
    /** The mapped object. */
    private final T object;

    /** The parameters extracted from the match. */
    private final Map<String, String> parameters;

    /**
     * Create a match of the given
     * 
     * @param object
     *            The mapped object.
     * @param mapOfParameters
     *            The captured parameters.
     */
    public Match(T object,Map<String, String> mapOfParameters) {
        this.object = object;
        this.parameters = new HashMap<String, String>(mapOfParameters);
    }

    /**
     * Get the mapped object.
     * 
     * @return The mapped object.
     */
    public T getObject() {
        return object;
    }

    /**
     * Get the captured parameters.
     * 
     * @return The parameters captured by the match.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }
}