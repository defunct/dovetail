/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.controller.StripesConstants;

public class GlobFactory
{
    private static GlobFactory INSTANCE = new GlobFactory();
    
    private Map<Integer, List<Glob>> priorities = new TreeMap<Integer, List<Glob>>();
    
    public static GlobFactory getInstance()
    {
        return INSTANCE;
    }
    
    public void add(int priority, Glob glob)
    {
        List<Glob> globs = priorities.get(- priority);
        if (globs == null)
        {
            globs = new ArrayList<Glob>();
            priorities.put(- priority, globs);
        }
        globs.add(glob);
    }
    
    private String asQueryString(GlobMapping mapping)
    {
        if (mapping == null)
        {
            return null;
        }

        String separator = "";
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String[]> entry : mapping.getParameters().entrySet())
        {
            for (String value : entry.getValue())
            {
                query.append(separator);
                query.append(entry.getKey());
                query.append("=");
                query.append(value);
                separator = "&";
            }
        }
        return query.toString();
    }
    
    public String getQueryString(HttpServletRequest request)
    {
        return asQueryString(map(request));
    }
    
    public String getQueryString(String path)
    {
        return asQueryString(map(path));
    }

    protected String getRequestedPath(HttpServletRequest request)
    {
        String servletPath = null, pathInfo = null;

        // Check to see if the request is processing an include, and pull the
        // path
        // information from the appropriate source.
        if (request.getAttribute(StripesConstants.REQ_ATTR_INCLUDE_PATH) != null)
        {
            servletPath = (String) request.getAttribute(StripesConstants.REQ_ATTR_INCLUDE_PATH);
            pathInfo = (String) request.getAttribute(StripesConstants.REQ_ATTR_INCLUDE_PATH_INFO);
        }
        else
        {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
        }

        return (servletPath == null ? "" : servletPath) + (pathInfo == null ? "" : pathInfo);
    }
    
    protected GlobMapping getGlobMappingFromRequest(HttpServletRequest request, String path)
    {
        Map<String, GlobMapping> mappings = getGlobMappings(request);
        if (mappings == null)
        {
            mappings = new HashMap<String, GlobMapping>();
            request.setAttribute(Glob.class.getName(), mappings);
        }
        if (!mappings.containsKey(path))
        {
            mappings.put(path, GlobFactory.getInstance().map(path));
        }
        return mappings.get(path);
    }
    
    @SuppressWarnings("unchecked")
    protected Map<String, GlobMapping> getGlobMappings(HttpServletRequest request)
    {
        return (Map<String, GlobMapping>) request.getAttribute(Glob.class.getName());
    }

    public GlobMapping map(HttpServletRequest request)
    {
        return getGlobMappingFromRequest(request, getRequestedPath(request));
    }

    public GlobMapping map(HttpServletRequest request, String path)
    {
        return getGlobMappingFromRequest(request, path);
    }

    public Glob getGlob(String path)
    {
        for (List<Glob> globs : priorities.values())
        {
            for (Glob glob : globs)
            {
                if (glob.match(path))
                {
                    return glob;
                }
            }        
        }
        return null;
    }
    
    public GlobMapping map(String path)
    {
        for (List<Glob> globs : priorities.values())
        {
            for (Glob glob : globs)
            {
                GlobMapping mapping = glob.map(path);
                if (mapping != null)
                {
                    return mapping;
                }
            }
        }
        return null;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */