package com.goodworkalan.dovetail;

final class Literal
implements Match
{
    private final String text;
    
    private final int min;
    
    private final int max;

    public Literal(String text, int min, int max)
    {
        this.text = text;
        this.min = min;
        this.max = max;
    }

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
        if (object instanceof Literal)
        {
            Literal literal = (Literal) object;
            return text.equals(literal.text)
                && min == literal.min
                && max == literal.max;
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 37 + text.hashCode();
        hash = hash * 37 + min;
        hash = hash * 37 + max;
        return hash;
    }
}