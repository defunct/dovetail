package com.goodworkalan.dovetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.testng.annotations.Test;

public class GlobTreeTest
{
    private Glob newGlob(Class<?> conditionals, String pattern)
    {
        return new GlobCompiler(conditionals).compile(pattern);
    }

    @Test public void tree()
    {
        GlobTree<Object> tree = new GlobTree<Object>();
        Glob glob = newGlob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        tree.add(glob, new Object());
    }
    
    @Test public void shortMatch()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account}/optout");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout"));
        assertFalse(tree.match("/thinknola/optout/snert"));
        assertFalse(tree.match("/thinknola/snert"));
        assertFalse(tree.match("/thinknola"));
    }
 
    @Test public void startWithProperty()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(tree.match("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Glob glob = newGlob(GlobTestCase.class, "//{account}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertTrue(tree.match("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(tree.match("/one/two/three/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/snap/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Glob glob = newGlob(GlobTestCase.class, "//{account}/optout/{key}/{receipt}//*");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(tree.match("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(tree.match("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account [A-Za-z0-9-]+}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola.d/optout/4XGe1E/1"));
    }
        
    @Test public void matchTestMethod()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account [A-Za-z0-9-]+ #test}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola.d/optout/4XGe1E/1"));
        assertTrue(tree.match("/example/optout/4XGe1E/1"));
    }
    
    @Test public void matchTestGroup()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account an-([A-Za-z0-9-]+) #test %1}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertFalse(tree.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(tree.match("/example/optout/4XGe1E/1"));
        assertTrue(tree.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void regularExpressionGroup()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account an-([A-Za-z0-9-]+) %1}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(tree.match("/example/optout/4XGe1E/1"));
        assertTrue(tree.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Glob glob = newGlob(GlobTestCase.class, "//{page}/optout/{key}/{receipt}/{@event}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/hello/optout/4XGe1E/1/view"));
        Mapping<Object> mapping = tree.map("/hello/optout/4XGe1E/1/view");
        assertEquals("view", mapping.getCommands().get("event"));
    }

    @Test public void zeroOrOne()
    {
        Glob glob = newGlob(GlobTestCase.class, "/?{account}/optout/{key}/{receipt}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        Mapping<Object> mapping = tree.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertEquals("hello", mapping.getParameters().get("account"));
        assertEquals("4XGe1E", mapping.getParameters().get("key"));
        mapping = tree.map("/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getParameters().get("account"));
    }

    @Test public void optionalEvent()
    {
        Glob glob = newGlob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}/?{@event}");
        GlobTree<Object> tree = new GlobTree<Object>();
        tree.add(glob, new Object());
        Mapping<Object> mapping = tree.map("/hello/optout/4XGe1E/1/view");
        assertNotNull(mapping);
        assertEquals("view", mapping.getCommands().get("event"));
        mapping = tree.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getCommands().get("event"));
    }   
}