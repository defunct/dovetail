package com.goodworkalan.dovetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

public class GlobTreeTest
{
    private Path newGlob(String pattern)
    {
        return new PathCompiler().compile(pattern);
    }

    @Test public void tree()
    {
        PathTree<Object> tree = new PathTree<Object>();
        Path glob = newGlob("/(account)/optout/(key)/(receipt)");
        tree.add(glob, new Object());
    }
    
    @Test public void shortMatch()
    {
        Path glob = newGlob("/(account)/optout");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout"));
        assertFalse(tree.match("/thinknola/optout/snert"));
        assertFalse(tree.match("/thinknola/snert"));
        assertFalse(tree.match("/thinknola"));
    }
 
    @Test public void startWithProperty()
    {
        Path glob = newGlob("/(account)/optout/(key)/(receipt)");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(tree.match("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Path glob = newGlob("/(account)+/optout/(key)/(receipt)");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertTrue(tree.match("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(tree.match("/one/two/three/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/snap/4XGe1E/1"));
        assertFalse(tree.match("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Path glob = newGlob("/(account)+/optout/(key)/(receipt)/(ignore)+");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(tree.match("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(tree.match("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Path glob = newGlob("/(account [A-Za-z0-9-]+)/optout/(key)/(receipt)");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/thinknola.d/optout/4XGe1E/1"));
    }
    
    @Test public void regularExpressionGroup()
    {
        Path glob = newGlob("/(account an-([A-Za-z0-9-]+))/optout/(key)/(receipt)");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(tree.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(tree.match("/example/optout/4XGe1E/1"));
        assertTrue(tree.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Path glob = newGlob("/(page)+/optout/(key)/(receipt)/(event)");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        assertTrue(tree.match("/hello/optout/4XGe1E/1/view"));
        List<Match<Object>> mappings = tree.map("/hello/optout/4XGe1E/1/view");
        assertEquals("view", mappings.get(0).getParameters().get("event"));
    }

    @Test public void zeroOrOne()
    {
        Path glob = newGlob("/(account)?/optout/(key)/(receipt)");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        List<Match<Object>> mappings = tree.map("/hello/optout/4XGe1E/1");
        assertFalse(mappings.isEmpty());
        assertEquals("hello", mappings.get(0).getParameters().get("account"));
        assertEquals("4XGe1E", mappings.get(0).getParameters().get("key"));
        mappings = tree.map("/optout/4XGe1E/1");
        assertFalse(mappings.isEmpty());
        assertNull(mappings.get(0).getParameters().get("account"));
    }

    @Test public void optionalEvent()
    {
        Path glob = newGlob("/(account)/optout/(key)/(receipt)/(event)?");
        PathTree<Object> tree = new PathTree<Object>();
        tree.add(glob, new Object());
        List<Match<Object>> mappings = tree.map("/hello/optout/4XGe1E/1/view");
        assertFalse(mappings.isEmpty());
        assertEquals("view", mappings.get(0).getParameters().get("event"));
        mappings = tree.map("/hello/optout/4XGe1E/1");
        assertFalse(mappings.isEmpty());
        assertNull(mappings.get(0).getParameters().get("event"));
    }   
}