package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class MatchBook<T> {
    /** A working list of matched parameters. */
    private final Map<String, String> parameters;

    /** A map of glob priorities to mappings. */
    private final SortedMap<Integer, Match<T>> matches;

    /**
     * Create an empty tree mapper.
     */
    public MatchBook() {
        this.parameters = new HashMap<String, String>();
        this.matches = new TreeMap<Integer, Match<T>>(Collections
                .reverseOrder());
    }
    
    /**
     * Create a copy of a tree mapper.
     * 
     * @param mappings The mapping list.
     * @param parameters The current state of the working parameter map.
     */
    private MatchBook(SortedMap<Integer, Match<T>> mappings, Map<String, String> parameters) {
        this.matches = mappings;
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
    public void addParameter(String name, String value) {
        parameters.put(name, value);
    }

    /**
     * Get the current working parameter map.
     * 
     * @return The working parameter map.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Create a type-safe clone of this tree mapper with it's own copy of the
     * working parameter to capture the parameters matched so far. The duplicate
     * will share the list of mappings with this tree mapper, so that when a
     * match is discovered, it is added to the common list.
     * 
     * @return A duplicate of this tree mapper.
     */
    public MatchBook<T> duplicate() {
        return new MatchBook<T>(matches, parameters);
    }

    /**
     * Record a successful match against a path.
     * 
     * @param priority
     *            The glob priority.
     * @param object
     *            The object mapped to the glob.
     */
    public void map(int priority, T object) {
        Match<T> match = new Match<T>(object, priority, parameters);
        matches.put(priority, match);
    }

    // TODO Document.
    public List<Match<T>> matches() {
        return new ArrayList<Match<T>>(matches.values());
    }
}
