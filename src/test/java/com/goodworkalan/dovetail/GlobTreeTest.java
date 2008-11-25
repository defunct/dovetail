package com.goodworkalan.dovetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import net.sourceforge.stripes.action.ActionBean;

import org.testng.annotations.Test;

import com.goodworkalan.dovetail.Glob;
import com.goodworkalan.dovetail.GlobTree;

public class GlobTreeTest
{
    @Test public void tree()
    {
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        tree.add(glob);
    }
    
    @Test public void shortMatch()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/thinknola/optout"));
        assertFalse(tree.match("/thinknola/optout/snert"));
        assertFalse(tree.match("/thinknola/snert"));
        assertFalse(tree.match("/thinknola"));
    }
 
    @Test public void startWithProperty()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(tree.match("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Glob glob = new Glob(GlobTestCase.class, "//{account}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertTrue(tree.match("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(tree.match("/one/two/three/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/snap/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Glob glob = new Glob(GlobTestCase.class, "//{account}/optout/{key}/{receipt}//*");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(tree.match("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(tree.match("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account [A-Za-z0-9-]+}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola.d/optout/4XGe1E/1"));
    }
        
    @Test public void matchTestMethod()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account [A-Za-z0-9-]+ #test}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola.d/optout/4XGe1E/1"));
        assertTrue(tree.match("/example/optout/4XGe1E/1"));
    }
    
    @Test public void matchTestGroup()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account an-([A-Za-z0-9-]+) #test %1}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertFalse(tree.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(tree.match("/example/optout/4XGe1E/1"));
        assertTrue(tree.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void regularExpressionGroup()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account an-([A-Za-z0-9-]+) %1}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(tree.match("/example/optout/4XGe1E/1"));
        assertTrue(tree.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Glob glob = new Glob(GlobTestCase.class, "//{page}/optout/{key}/{receipt}/{@event}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        assertTrue(tree.match("/hello/optout/4XGe1E/1/view"));
        Mapping<Class<? extends ActionBean>> mapping = tree.map("/hello/optout/4XGe1E/1/view");
        assertEquals("view", mapping.getCommands().get("event"));
    }

    @Test public void zeroOrOne()
    {
        Glob glob = new Glob(GlobTestCase.class, "/?{account}/optout/{key}/{receipt}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        Mapping<Class<? extends ActionBean>> mapping = tree.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertEquals("hello", mapping.getParameters().get("account"));
        assertEquals("4XGe1E", mapping.getParameters().get("key"));
        mapping = tree.map("/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getParameters().get("account"));
    }

    @Test public void optionalEvent()
    {
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}/?{@event}");
        GlobTree<Class<? extends ActionBean>> tree = new GlobTree<Class<? extends ActionBean>>();
        tree.add(glob);
        Mapping<Class<? extends ActionBean>> mapping = tree.map("/hello/optout/4XGe1E/1/view");
        assertNotNull(mapping);
        assertEquals("view", mapping.getCommands().get("event"));
        mapping = tree.map("/hello/optout/4XGe1E/1");
        assertNotNull(mapping);
        assertNull(mapping.getCommands().get("event"));
    }   
}