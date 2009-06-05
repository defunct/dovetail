package com.goodworkalan.dovetail;

import java.util.Map;

/**
 * A test against a range of path parts in a glob.
 * 
 * FIXME Rename Test.
 */
interface Test
{
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
}