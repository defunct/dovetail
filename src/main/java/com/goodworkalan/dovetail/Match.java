package com.goodworkalan.dovetail;

/**
 * A test against a range of path parts in a glob.
 * 
 * FIXME Rename Test.
 */
interface Match
{
    /**
     * Return true if the glob matches the string parts from the ranges
     * specified by the start inclusive and end exclusive.
     * 
     * @param mapper
     *            A state for captures.
     * @param parts
     *            The component parts of a URL path.
     * @param start
     *            The part at which to begin the match.
     * @param end
     *            The part at which to end the match.
     * @return True if this match matches.
     */
    public boolean match(GlobMapper mapper, String[] parts, int start, int end);

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