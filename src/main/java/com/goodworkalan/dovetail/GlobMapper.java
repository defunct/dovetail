/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.Set;

public interface GlobMapper
{
    public Set<String> mark();
    
    public void revert(Set<String> mark);
    
    public void addParameter(String name, String value);
    
    public void addCommand(String name, String value);
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */