package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Used to collect the results of a match of a path against a tree of globs.
 * Because more than one glob in the tree of globs, a tree mapper will gather
 * a list of mappings.
 * 
 * @author Alan Gutierrez
 *
 * @param <T>
 */
public class TreeMapper<T> implements GlobMapper
{
    /** A working list of matched parameters. */
    private final Map<String, String> parameters;
    
    /** A map of glob priorities to mappings. */
    private final SortedMap<Integer, Mapping<T>> mappings;
    
    /**
     * Create an empty tree mapper.
     */
    public TreeMapper()
    {
        this.parameters = new HashMap<String, String>();
        this.mappings = new TreeMap<Integer, Mapping<T>>(Collections.reverseOrder());
    }
    
    /**
     * Create a copy of a tree mapper.
     * 
     * @param mappings The mapping list.
     * @param parameters The current state of the working parameter map.
     */
    private TreeMapper(SortedMap<Integer, Mapping<T>> mappings, Map<String, String> parameters)
    {
        this.mappings = mappings;
        this.parameters = new HashMap<String, String>(parameters);
    }

    /**
     * Add a parameter to the parameter map.
     * 
     * @param name
     *            The parameter name.
     * @param value
     *            The parameter value.
     */
    public void addParameter(String name, String value)
    {
        parameters.put(name, value);
    }
    
    /**
     * FIXME Why is this defunct?
     */
    public Set<String> mark()
    {
        return null;
    }
    
    /**
     * FIXME Why is this defunct? Did duplicate replace it?
     */
    public void revert(Set<String> mark)
    {
    }
    
    /**
     * Create a type-safe clone of this tree mapper with it's own copy of the
     * working parameter to capture the parameters matched so far. The duplicate
     * will share the list of mappings with this tree mapper, so that when
     * a match is discovered, it is added to the common list.
     *  
     * @return A duplicate of this tree mapper.
     */
    public TreeMapper<T> duplicate()
    {
        return new TreeMapper<T>(mappings, parameters);
    }
    
    // TODO Document.
    public void map(int priority, T object)
    {
        Mapping<T> mapping = new Mapping<T>(object, priority, parameters);
        mappings.put(priority, mapping);
    }
    
    // TODO Document.
    public List<Mapping<T>> mappings()
    {
        return new ArrayList<Mapping<T>>(mappings.values());
    }
}
