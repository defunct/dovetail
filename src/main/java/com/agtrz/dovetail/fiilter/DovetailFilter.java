/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail.fiilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.agtrz.dovetail.GlobFactory;
import com.agtrz.dovetail.GlobMapping;
import com.agtrz.dovetail.StripesGlobRequestWrapper;

import net.sourceforge.stripes.controller.StripesFilter;

public class DovetailFilter
extends StripesFilter
{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, final FilterChain filterChain) throws IOException, ServletException
    {
        final GlobMapping mapping = GlobFactory.getInstance().map((HttpServletRequest) request);
        if (mapping == null)
        {
            super.doFilter(request, response, filterChain);
        }
        else
        {
            super.doFilter(request, response, new FilterChain()
            {
                public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
                {
                    filterChain.doFilter(new StripesGlobRequestWrapper((HttpServletRequest) request, mapping.getParameters()), response);
                }
            });
        }
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */