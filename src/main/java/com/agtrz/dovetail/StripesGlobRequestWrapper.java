/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sourceforge.stripes.exception.StripesServletException;

public class StripesGlobRequestWrapper
extends HttpServletRequestWrapper
{
    private final Map<String, String[]> parameters;
    
    @SuppressWarnings("unchecked")
    public StripesGlobRequestWrapper(HttpServletRequest request, Map<String, String[]> parameters)
    throws StripesServletException
    {
        super(request);
        
        Map<String, String[]> merged = new LinkedHashMap<String, String[]>();
        
        merge(merged, request.getParameterMap());
        merge(merged, parameters);
        
        this.parameters = merged;
    }
    
    private static void merge(Map<String, String[]> merged, Map<String, String[]> parameters)
    {
        for (String key : parameters.keySet())
        {
            String[] values = parameters.get(key);
            String[] existing = merged.get(key);
            if (existing == null)
            {
                merged.put(key, values);
            }
            else
            {
                String[] together = new String[existing.length + values.length];
                System.arraycopy(existing, 0, together, 0, existing.length);
                System.arraycopy(values, 0, together, existing.length, values.length);
                merged.put(key, together);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map getParameterMap()
    {
        return parameters;
    }
    
    @Override
    public String getParameter(String name)
    {
        String[] values = parameters.get(name);
        return values == null ? null : values.length == 0 ? null : values[0];
    }
    
    @Override
    public String[] getParameterValues(String name)
    {
        return parameters.get(name);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Enumeration getParameterNames()
    {
        return Collections.enumeration(parameters.keySet());
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */