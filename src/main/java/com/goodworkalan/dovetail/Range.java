package com.goodworkalan.dovetail;

import java.util.Map;

/**
 * A test against a range of path parts in a glob.
 */
interface Range {
    /**
     * Return true if the glob matches the string parts from the ranges
     * specified by the start inclusive and end exclusive.
     * 
     * @param parameters
     *            A map of captured parameters.
     * @param parts
     *            The component parts of a URL path.
     * @param start
     *            The part at which to begin the match.
     * @param end
     *            The part at which to end the match.
     * @return True if this match matches.
     */
    public boolean match(Map<String, String> parameters, String[] parts, int start, int end);

    /**
     * The minimum number of parts that this match can match, either zero or
     * one.
     * 
     * @return The minimum number of parts that this match can match.
     */
    public int getMin();

    /**
     * The maximum number of parts that this match can match, either one or
     * {@link Integer#MAX_VALUE}.
     * 
     * @return The maximum number of parts that this match can match.
     */
    public int getMax();

    /**
     * Recreate this part of the path and append it to the given path buffer
     * using the given parameters map to replace the parameter captures in glob
     * pattern.
     * 
     * @param path
     *            The path buffer.
     * @param parameters
     *            The parameters used to replace parameter captures in the glob
     *            pattern.
     */
    public void append(StringBuilder path, Map<String, String> parameters);
}