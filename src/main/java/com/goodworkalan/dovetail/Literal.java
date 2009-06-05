package com.goodworkalan.dovetail;

/**
 * Matches a part by matching a literal path.
 * 
 * @author Alan Gutierrez
 */
final class Literal
implements Test
{
    /** The literal text to compare against the part. */
    private final String text;
    
    /** The minimum number of parts that this match can match. */
    private final int min;

    /**
     * Create a match that will compare the given text against a part for
     * equality the given minimum and given maximum number of times in a URL
     * path. The only valid values for minimum are zero and one.
     * 
     * @param text
     *            The literal text to compare against the part.
     * @param min
     *            The minimum number of times to apply the match.
     */
    public Literal(String text, int min)
    {
        this.text = text;
        this.min = min;
    }

    /**
     * Return true if the literal matches the string parts from the ranges
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
    public boolean match(GlobMapper mapper, String[] parts, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            if (!text.equals(parts[i]))
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * The minimum number of parts that this match can match, either zero or
     * one.
     * 
     * @return The minimum number of parts that this match can match.
     */
    public int getMin()
    {
        return min;
    }

    /**
     * The maximum number of parts of a URL path that this literal can match,
     * which in the case of literal is always 1.
     * 
     * @return The maximum number of parts that this match can match.
     */
    public int getMax()
    {
        return 1;
    }

    /**
     * A literal is equal to another literal object with the same match text
     * property and the same minimum number of matches property.
     * 
     * @param object
     *            An object to which to compare this object.
     * @return True if the given object is equal to this literal.
     */
    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object instanceof Literal)
        {
            Literal literal = (Literal) object;
            return text.equals(literal.text)
                && min == literal.min;
        }
        return false;
    }

    /**
     * Return a hash code that combines the hash code of the match text property
     * and the minimum number of matches property.
     * 
     * @return The hash code.
     */
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 37 + text.hashCode();
        hash = hash * 37 + min;
        return hash;
    }
}