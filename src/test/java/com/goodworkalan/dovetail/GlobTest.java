/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.*;
import static com.goodworkalan.dovetail.DovetailException.FIRST_FORWARD_SLASH_MISSING;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.testng.annotations.Test;

public class GlobTest
{
    @Test public void startWithProperty()
    {
        Glob glob = new GlobCompiler().compile("/(account)/optout/(key)/(receipt)");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
    }
    
    @Test public void matchOneOrMore()
    {
        Glob glob = new GlobCompiler().compile("/(account)+/optout/(key)/(receipt)");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertTrue(glob.match("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(glob.match("/one/two/three/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
    }
    
    @Test public void matchOneOrMoreAny()
    {
        Glob glob = new GlobCompiler().compile("/(account)+/optout/(key)/(receipt)/(ignore)+");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(glob.match("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    @Test public void matchRegularExpression()
    {
        Glob glob = new GlobCompiler().compile("/(account [A-Za-z0-9-]+)/optout/(key)/(receipt)");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola.d/optout/4XGe1E/1"));
    }
        
    @Test public void regularExpressionGroup()
    {
        Glob glob = new GlobCompiler().compile("/(account an-([A-Za-z0-9-]+))/optout/(key)/(receipt)");
        assertTrue(glob.match("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(glob.match("/example/optout/4XGe1E/1"));
        assertTrue(glob.match("/an-example/optout/4XGe1E/1"));
    }
    
    @Test public void command()
    {
        Glob glob = new GlobCompiler().compile("/(page)+/optout/(key)/(receipt)/(event)");
        assertTrue(glob.match("/hello/optout/4XGe1E/1/view"));
        Map<String, String> parameters = glob._map("/hello/optout/4XGe1E/1/view");
        assertEquals("view", parameters.get("event"));
    }

    @Test public void zeroOrOne()
    {
        Glob glob = new GlobCompiler().compile("/(account)?/optout/(key)/(receipt)");
        Map<String, String> parameters = glob._map("/hello/optout/4XGe1E/1");
        assertNotNull(parameters);
        assertEquals("hello", parameters.get("account"));
        assertEquals("4XGe1E", parameters.get("key"));
        parameters = glob._map("/optout/4XGe1E/1");
        assertNotNull(parameters);
        assertNull(parameters.get("account"));
    }

    @Test public void optionalEvent()
    {
        Glob glob = new GlobCompiler().compile("/(account)/optout/(key)/(receipt)/(event)?");
        Map<String, String> parameters = glob._map("/hello/optout/4XGe1E/1/view");
        assertNotNull(parameters);
        assertEquals("view", parameters.get("event"));
        parameters = glob._map("/hello/optout/4XGe1E/1");
        assertNotNull(parameters);
        assertNull(parameters.get("event"));
    }    
    
    @Test(expectedExceptions=DovetailException.class) public void emptyString() 
    {
        try
        {
            new GlobCompiler().compile("");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), EMPTY_PATTERN);
            assertEquals(e.getMessage(), "Cannot create a glob from an empty string.");
            throw e;
        }
    }

    
    @Test(expectedExceptions=DovetailException.class) public void errorNotLeadingSlash() 
    {
        try
        {
            new GlobCompiler().compile("hello");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), FIRST_FORWARD_SLASH_MISSING);
            assertEquals(e.getMessage(), "A pattern must begin with a forward slash. Pattern hello at position 1.");
            throw e;
        }
    }
    
    @Test(expectedExceptions=NullPointerException.class) public void compileNullPointer() 
    {
        new GlobCompiler().compile(null);
    }
    
    @Test(expectedExceptions=DovetailException.class) public void emptyPathPart() 
    {
        try
        {
            new GlobCompiler().compile("//hello");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), EMPTY_PATH_PART);
            assertEquals(e.getMessage(), "Unexpected empty path part. Pattern //hello at position 2.");
            throw e;
        }
    }
    
    @Test public void eatWhiteAfterParenthesis()
    {
        Glob glob = new GlobCompiler().compile("/( account)/optout/(key)/(receipt)");
        assertTrue(glob.match("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.match("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(glob.match("/thinknola/snap/4XGe1E/1"));
    }

    @Test public void multipleIdentifiers()
    {
        Glob glob = new GlobCompiler().compile("/(bar,baz (.*)-(.*) %s-%s)/foo");
        
        assertTrue(glob.match("/a-b/foo"));
        assertFalse(glob.match("/a+b/foo"));
        assertFalse(glob.match("/a+b/bar"));
        
        Map<String, String> parameters = glob._map("/a-b/foo");
        assertEquals("a", parameters.get("bar"));
        assertEquals("b", parameters.get("baz"));
        
        assertEquals(glob.path(parameters), "/a-b/foo");
    }
    
    @Test public void escapedParenthesisInRegex()
    {
        Glob glob = new GlobCompiler().compile("/(bar \\(.*\\))/foo");
        
        assertTrue(glob.match("/(bar)/foo"));
        assertFalse(glob.match("/bar/foo"));
        
        Map<String, String> parameters = glob._map("/(bar)/foo");
        assertEquals("(bar)", parameters.get("bar"));
        
        assertEquals(glob.path(parameters), "/(bar)/foo");
    }
    
    @Test(expectedExceptions = DovetailException.class) public void limitOrSeparatorExpected()
    {
        try
        {
            new GlobCompiler().compile("/(bar (.*)) (%s)/foo");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), LIMIT_OR_SEPARATOR_EXPECTED);
            assertEquals(e.getMessage(), "A limit specifier or a path separator is expected following a match specification closing parenthesis. Pattern /(bar (.*)) (%s)/foo at position 12.");
            throw e;
        }
    }
    
    @Test(expectedExceptions = DovetailException.class) public void unescapedForwardSlash()
    {
        try
        {
            new GlobCompiler().compile("/(bar \\((.*)\\) (%s)/foo");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), UNESCAPED_FORWARD_SLASH_IN_FORMAT);
            assertEquals(e.getMessage(), "An unescaped forward slash was found in a match specification format. Pattern /(bar \\((.*)\\) (%s)/foo at position 20.");
            throw e;
        }
    }
    
    @Test public void parenthesisInFormat()
    {
        Glob glob = new GlobCompiler().compile("/(bar \\((.*)\\) (%s))/foo");
         
        assertTrue(glob.match("/(bar)/foo"));
        assertFalse(glob.match("/bar/foo"));
        
        Map<String, String> parameters = glob._map("/(bar)/foo");
        assertEquals("bar", parameters.get("bar"));
        
        assertEquals(glob.path(parameters), "/(bar)/foo");
    }
    
    @Test public void escapedParenthesisInFormat()
    {
        Glob glob = new GlobCompiler().compile("/(bar \\((.*)\\) %(%s%))/foo");
         
        assertTrue(glob.match("/(bar)/foo"));
        assertFalse(glob.match("/bar/foo"));
        
        Map<String, String> parameters = glob._map("/(bar)/foo");
        assertEquals("bar", parameters.get("bar"));
        
        assertEquals(glob.path(parameters), "/(bar)/foo");
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */