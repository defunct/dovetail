/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.Set;


public class NullGlobMapper
implements GlobMapper
{
    public NullGlobMapper()
    {
    }
    
    public Set<String> mark()
    {
        return null;
    }
    
    public void revert(Set<String> mark)
    {
    }

    public void addCommand(String name, String value)
    {
    }

    public void addParameter(String name, String value)
    {
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */