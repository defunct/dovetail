/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

import org.testng.annotations.Test;

import com.agtrz.dovetail.Glob;
import com.agtrz.dovetail.GlobMapping;

public class GlobTestCase implements ActionBean
{
    public ActionBeanContext getContext()
    {
        return null;
    }
    
    public void setContext(ActionBeanContext context)
    {
    }

    @Test public void startWithProperty()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Glob glob = new Glob(GlobTestCase.class, "//{account}/optout/{key}/{receipt}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertTrue(glob.match("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(glob.match("/one/two/three/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Glob glob = new Glob(GlobTestCase.class, "//{account}/optout/{key}/{receipt}//*");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(glob.match("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account [A-Za-z0-9-]+}/optout/{key}/{receipt}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola.d/optout/4XGe1E/1"));
    }
        
    @Test public void matchTestMethod()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account [A-Za-z0-9-]+ #test}/optout/{key}/{receipt}");
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola.d/optout/4XGe1E/1"));
        assertTrue(glob.match("/example/optout/4XGe1E/1"));
    }
    
    @Test public void matchTestGroup()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account an-([A-Za-z0-9-]+) #test %1}/optout/{key}/{receipt}");
        assertFalse(glob.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(glob.match("/example/optout/4XGe1E/1"));
        assertTrue(glob.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void regularExpressionGroup()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account an-([A-Za-z0-9-]+) %1}/optout/{key}/{receipt}");
        assertTrue(glob.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(glob.match("/example/optout/4XGe1E/1"));
        assertTrue(glob.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Glob glob = new Glob(GlobTestCase.class, "//{page}/optout/{key}/{receipt}/{@event}");
        assertTrue(glob.match("/hello/optout/4XGe1E/1/view"));
        GlobMapping mapping = glob.map("/hello/optout/4XGe1E/1/view");
        assertEquals("view", mapping.getCommands().get("event"));
    }

    @Test public void zeroOrOne()
    {
        Glob glob = new Glob(GlobTestCase.class, "/?{account}/optout/{key}/{receipt}");
        GlobMapping mapping = glob.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertEquals("hello", mapping.getParameters().get("account")[0]);
        assertEquals("4XGe1E", mapping.getParameters().get("key")[0]);
        mapping = glob.map("/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getParameters().get("account"));
    }

    @Test public void optionalEvent()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}/?{@event}");
        GlobMapping mapping = glob.map("/hello/optout/4XGe1E/1/view");
        assertNotNull(mapping);
        assertEquals("view", mapping.getCommands().get("event"));
        mapping = glob.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getCommands().get("event"));
    }

    public static String test(String input)
    {
        if (input.equals("example"))
        {
            return input;
        }
        return null;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */