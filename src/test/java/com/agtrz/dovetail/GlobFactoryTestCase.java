/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.testng.annotations.Test;

public class GlobFactoryTestCase
{
    @Test public void parameters()
    {
        GlobFactory.getInstance().add(1, new Glob(Glob.class, "/{account [0-9a-zA-Z]+}/index.a"));
        GlobFactory.getInstance().add(0, new Glob(Glob.class, "//{page}/index.a"));
        assertEquals("account=thinknola", GlobFactory.getInstance().getQueryString("/thinknola/index.a"));
        assertEquals("page=my/page", GlobFactory.getInstance().getQueryString("/my/page/index.a"));
        assertNull(GlobFactory.getInstance().getQueryString("/thinknola/snert.a"));
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */