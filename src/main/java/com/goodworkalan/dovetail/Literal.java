package com.goodworkalan.dovetail;

// TODO Document.
final class Literal
implements Match
{
    // TODO Document.
    private final String text;
    
    // TODO Document.
    private final int min;
    
    // TODO Document.
    private final int max;

    // TODO Document.
    public Literal(String text, int min, int max)
    {
        this.text = text;
        this.min = min;
        this.max = max;
    }

    // TODO Document.
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
    
    // TODO Document.
    public int getMin()
    {
        return min;
    }
    
    // TODO Document.
    public int getMax()
    {
        return max;
    }
    
    // TODO Document.
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
    
    // TODO Document.
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