/**
 * 
 */
package com.agtrz.dovetail;

final class Literal
implements Match
{
    private final String literal;
    
    private final int min;
    
    private final int max;

    public Literal(String literal, int min, int max)
    {
        this.literal = literal;
        this.min = min;
        this.max = max;
    }

    public boolean match(GlobMapper mapper, String[] parts, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            if (!literal.equals(parts[i]))
            {
                return false;
            }
        }
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
}