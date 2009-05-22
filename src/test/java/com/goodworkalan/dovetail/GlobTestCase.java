/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.testng.annotations.Test;

public class GlobTestCase
{
    @Test public void startWithProperty()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/{account}/optout/{key}/{receipt}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("//{account}/optout/{key}/{receipt}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertTrue(glob.match("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(glob.match("/one/two/three/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("//{account}/optout/{key}/{receipt}//{ignore}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(glob.match("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/{account [A-Za-z0-9-]+}/optout/{key}/{receipt}");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola.d/optout/4XGe1E/1"));
    }
        
    @Test public void matchTestMethod()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/{account [A-Za-z0-9-]+ #test}/optout/{key}/{receipt}");
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola.d/optout/4XGe1E/1"));
        assertTrue(glob.match("/example/optout/4XGe1E/1"));
    }
    
    @Test public void matchTestGroup()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/{account an-([A-Za-z0-9-]+) #test %1}/optout/{key}/{receipt}");
        assertFalse(glob.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(glob.match("/example/optout/4XGe1E/1"));
        assertTrue(glob.match("/an-example/optout/4XGe1E/1"));
    }
    
    public void thinkingOutLoud() 
    {
        new GlobCompiler(Object.class).compile("//{path}/{page,extension (.+)\\.([^.]+) %1$s.%2$s}");
    }
    
    @Test public void regularExpressionGroup()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/{account an-([A-Za-z0-9-]+) %1}/optout/{key}/{receipt}");
        assertTrue(glob.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(glob.match("/example/optout/4XGe1E/1"));
        assertTrue(glob.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("//{page}/optout/{key}/{receipt}/{event}");
        assertTrue(glob.match("/hello/optout/4XGe1E/1/view"));
        GlobMapping mapping = glob.map("/hello/optout/4XGe1E/1/view");
        assertEquals("view", mapping.getParameters().get("event")[0]);
    }

    @Test public void zeroOrOne()
    {
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/?{account}/optout/{key}/{receipt}");
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
        Glob glob = new GlobCompiler(GlobTestCase.class).compile("/{account}/optout/{key}/{receipt}/?{event}");
        GlobMapping mapping = glob.map("/hello/optout/4XGe1E/1/view");
        assertNotNull(mapping);
        assertEquals("view", mapping.getParameters().get("event")[0]);
        mapping = glob.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getParameters().get("event"));
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