package com.agtrz.dovetail;

/**
 * Match any part of a path.
 */
final class Any implements Match
{
    /** The minimum number of parts this match can match. */
    private final int min;

    /** The maximum number of parts this match can match. */
    private final int max;

    /**
     * Create a match that will match any part zero, one or more times. The
     * count of matches, zero, one or more is based on the values of min and
     * max. Currently, the values of minimum 1 and a maximum of 1 are used to
     * match <code>/*</code> in a glob pattern. The values of minimum of 1 and a
     * maximum Integer.INT_MAX are used to implement <code>//*</code> in a glob
     * pattern.
     * 
     * @param min
     *            The minimum number of times this pattern can match.
     * @param max
     *            The maximum number of times this pattern can match.
     */
    public Any(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public boolean match(GlobMapper mapper, String[] parts, int start, int end)
    {
        return true;
    }

    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }
    
    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object instanceof Any)
        {
            Any any = (Any) object;
            return min == any.min && max == any.max;
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 37 + min;
        hash = hash * 37 + max;
        return hash;
    }
}