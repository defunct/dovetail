/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.Set;

// TODO Document.
public class NullGlobMapper
implements GlobMapper
{
    // TODO Document.
    public NullGlobMapper()
    {
    }
    
    // TODO Document.
    public Set<String> mark()
    {
        return null;
    }
    
    // TODO Document.
    public void revert(Set<String> mark)
    {
    }

    // TODO Document.
    public void addCommand(String name, String value)
    {
    }

    // TODO Document.
    public void addParameter(String name, String value)
    {
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */