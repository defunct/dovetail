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
        PathAssociation<Object> tree = new PathAssociation<Object>();
        Path glob = newGlob("/(account)/optout/(key)/(receipt)");
        tree.put(glob, new Object());
    }
    
    @Test public void shortMatch()
    {
        Path glob = newGlob("/(account)/optout");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/thinknola/optout"));
        assertFalse(tree.matches("/thinknola/optout/snert"));
        assertFalse(tree.matches("/thinknola/snert"));
        assertFalse(tree.matches("/thinknola"));
    }
 
    @Test public void startWithProperty()
    {
        Path glob = newGlob("/(account)/optout/(key)/(receipt)");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.matches("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(tree.matches("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Path glob = newGlob("/(account)+/optout/(key)/(receipt)");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/thinknola/optout/4XGe1E/1"));
        assertTrue(tree.matches("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(tree.matches("/one/two/three/optout/4XGe1E/1"));
        assertFalse(tree.matches("/thinknola/snap/4XGe1E/1"));
        assertFalse(tree.matches("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Path glob = newGlob("/(account)+/optout/(key)/(receipt)/(ignore)+");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(tree.matches("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(tree.matches("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Path glob = newGlob("/(account [A-Za-z0-9-]+)/optout/(key)/(receipt)");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/thinknola/optout/4XGe1E/1"));
        assertFalse(tree.matches("/thinknola.d/optout/4XGe1E/1"));
    }
    
    @Test public void regularExpressionGroup()
    {
        Path glob = newGlob("/(account an-([A-Za-z0-9-]+))/optout/(key)/(receipt)");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(tree.matches("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(tree.matches("/example/optout/4XGe1E/1"));
        assertTrue(tree.matches("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Path glob = newGlob("/(page)+/optout/(key)/(receipt)/(event)");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        assertTrue(tree.matches("/hello/optout/4XGe1E/1/view"));
        List<Match<Object>> mappings = tree.match("/hello/optout/4XGe1E/1/view");
        assertEquals("view", mappings.get(0).getParameters().get("event"));
    }

    @Test public void zeroOrOne()
    {
        Path glob = newGlob("/(account)?/optout/(key)/(receipt)");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        List<Match<Object>> mappings = tree.match("/hello/optout/4XGe1E/1");
        assertFalse(mappings.isEmpty());
        assertEquals("hello", mappings.get(0).getParameters().get("account"));
        assertEquals("4XGe1E", mappings.get(0).getParameters().get("key"));
        mappings = tree.match("/optout/4XGe1E/1");
        assertFalse(mappings.isEmpty());
        assertNull(mappings.get(0).getParameters().get("account"));
    }

    @Test public void optionalEvent()
    {
        Path glob = newGlob("/(account)/optout/(key)/(receipt)/(event)?");
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(glob, new Object());
        List<Match<Object>> mappings = tree.match("/hello/optout/4XGe1E/1/view");
        assertFalse(mappings.isEmpty());
        assertEquals("view", mappings.get(0).getParameters().get("event"));
        mappings = tree.match("/hello/optout/4XGe1E/1");
        assertFalse(mappings.isEmpty());
        assertNull(mappings.get(0).getParameters().get("event"));
    }   
}