/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.Map;


public interface GlobMapping
{
    public Glob getGlob();

    public Map<String, String[]> getParameters();
    
    public Map<String, String> getCommands();
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */