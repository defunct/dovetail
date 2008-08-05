/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail.context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ActionBeanContextFactory;
import net.sourceforge.stripes.controller.DefaultActionBeanContextFactory;

import com.agtrz.dovetail.GlobFactory;
import com.agtrz.dovetail.GlobMapping;
import com.agtrz.dovetail.StripesGlobRequestWrapper;

public class DovetailActionBeanContextFactory
implements ActionBeanContextFactory
{
    private final DefaultActionBeanContextFactory delegate = new DefaultActionBeanContextFactory();
    
    public void init(Configuration configuration) throws Exception
    {
        delegate.init(configuration);
    }

    public ActionBeanContext getContextInstance(HttpServletRequest request,
                                                HttpServletResponse response)
        throws ServletException
    {
        GlobMapping mapping = GlobFactory.getInstance().map(request);
        if (mapping == null)
        {
            return delegate.getContextInstance(request, response);
        }
        return delegate.getContextInstance(new StripesGlobRequestWrapper(request, mapping.getParameters()), response);
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */