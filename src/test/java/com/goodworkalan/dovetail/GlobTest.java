/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.Path.EMPTY_PATH_PART;
import static com.goodworkalan.dovetail.Path.FIRST_FORWARD_SLASH_MISSING;
import static com.goodworkalan.dovetail.Path.LIMIT_OR_SEPARATOR_EXPECTED;
import static com.goodworkalan.dovetail.Path.UNESCAPED_FORWARD_SLASH_IN_FORMAT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.testng.annotations.Test;

import com.goodworkalan.danger.Danger;

// TODO Document.
public class GlobTest
{
    // TODO Document.
    @Test public void startWithProperty()
    {
        Path glob = new PathCompiler().compile("/(account)/optout/(key)/(receipt)");
        assertTrue(glob.matches("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.matches("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(glob.matches("/thinknola/snap/4XGe1E/1"));
    }
    
    // TODO Document.
    @Test public void matchOneOrMore()
    {
        Path glob = new PathCompiler().compile("/(account)+/optout/(key)/(receipt)");
        assertTrue(glob.matches("/thinknola/optout/4XGe1E/1"));
        assertTrue(glob.matches("/thinknola/path/optout/4XGe1E/1"));
        assertTrue(glob.matches("/one/two/three/optout/4XGe1E/1"));
        assertFalse(glob.matches("/thinknola/snap/4XGe1E/1"));
        assertFalse(glob.matches("/thinknola/optout/4XGe1E/1/2"));
    }
    
    // TODO Document.
    @Test public void matchOneOrMoreAny()
    {
        Path glob = new PathCompiler().compile("/(account)+/optout/(key)/(receipt)/(ignore)+");
        assertTrue(glob.matches("/thinknola/optout/4XGe1E/1/2"));
        assertTrue(glob.matches("/one/two/three/optout/4XGe1E/1/2"));
        assertFalse(glob.matches("/one/two/three/snap/4XGe1E/1/2"));
    }
    
    // TODO Document.
    @Test public void matchRegularExpression()
    {
        Path glob = new PathCompiler().compile("/(account [A-Za-z0-9-]+)/optout/(key)/(receipt)");
        assertTrue(glob.matches("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.matches("/thinknola.d/optout/4XGe1E/1"));
    }
        
    // TODO Document.
    @Test public void regularExpressionGroup()
    {
        Path glob = new PathCompiler().compile("/(account an-([A-Za-z0-9-]+))/optout/(key)/(receipt)");
        assertTrue(glob.matches("/an-thinknola/optout/4XGe1E/1"));
        assertFalse(glob.matches("/an-thinknola.d/optout/4XGe1E/1"));
        assertFalse(glob.matches("/example/optout/4XGe1E/1"));
        assertTrue(glob.matches("/an-example/optout/4XGe1E/1"));
    }
    
    // TODO Document.
    @Test public void command()
    {
        Path glob = new PathCompiler().compile("/(page)+/optout/(key)/(receipt)/(event)");
        assertTrue(glob.matches("/hello/optout/4XGe1E/1/view"));
        Map<String, String> parameters = glob.match("/hello/optout/4XGe1E/1/view");
        assertEquals("view", parameters.get("event"));
    }

    // TODO Document.
    @Test public void zeroOrOne()
    {
        Path glob = new PathCompiler().compile("/(account)?/optout/(key)/(receipt)");
        Map<String, String> parameters = glob.match("/hello/optout/4XGe1E/1");
        assertNotNull(parameters);
        assertEquals("hello", parameters.get("account"));
        assertEquals("4XGe1E", parameters.get("key"));
        parameters = glob.match("/optout/4XGe1E/1");
        assertNotNull(parameters);
        assertNull(parameters.get("account"));
    }

    // TODO Document.
    @Test
    public void optionalEvent() {
        Path glob = new PathCompiler().compile("/(account)/optout/(key)/(receipt)/(event)?");
        Map<String, String> parameters = glob.match("/hello/optout/4XGe1E/1/view");
        assertNotNull(parameters);
        assertEquals("view", parameters.get("event"));
        parameters = glob.match("/hello/optout/4XGe1E/1");
        assertNotNull(parameters);
        assertNull(parameters.get("event"));
    }    
    
    // TODO Document.
    @Test public void emptyString() 
    {
        Path glob = new PathCompiler().compile("");
        assertTrue(glob.matches(""));
    }
    
    // TODO Document.
    @Test
    public void slash()
    {
        Path glob = new PathCompiler().compile("/");
        assertTrue(glob.matches("/"));
    }
    
    // TODO Document.
    @Test(expectedExceptions = Danger.class)
    public void errorNotLeadingSlash() 
    {
        try
        {
            new PathCompiler().compile("hello");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), FIRST_FORWARD_SLASH_MISSING);
            assertEquals(e.getMessage(), "A pattern must begin with a forward slash. Pattern hello at position 1.");
            throw e;
        }
    }
    
    // TODO Document.
    @Test(expectedExceptions=NullPointerException.class) public void compileNullPointer() 
    {
        new PathCompiler().compile(null);
    }
    
    // TODO Document.
    @Test(expectedExceptions = Danger.class)
    public void emptyPathPart() 
    {
        try
        {
            new PathCompiler().compile("//hello");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), EMPTY_PATH_PART);
            assertEquals(e.getMessage(), "Unexpected empty path part. Pattern //hello at position 2.");
            throw e;
        }
    }
    
    // TODO Document.
    @Test public void eatWhiteAfterParenthesis()
    {
        Path glob = new PathCompiler().compile("/( account)/optout/(key)/(receipt)");
        assertTrue(glob.matches("/thinknola/optout/4XGe1E/1"));
        assertFalse(glob.matches("/thinknola/optout/4XGe1E/1/2"));
        assertFalse(glob.matches("/thinknola/snap/4XGe1E/1"));
    }

    // TODO Document.
    @Test public void multipleIdentifiers()
    {
        Path glob = new PathCompiler().compile("/(bar,baz (.*)-(.*) %s-%s)/foo");
        
        assertTrue(glob.matches("/a-b/foo"));
        assertFalse(glob.matches("/a+b/foo"));
        assertFalse(glob.matches("/a+b/bar"));
        
        Map<String, String> parameters = glob.match("/a-b/foo");
        assertEquals("a", parameters.get("bar"));
        assertEquals("b", parameters.get("baz"));
        
        assertEquals(glob.path(parameters), "/a-b/foo");
    }
    
    // TODO Document.
    @Test public void escapedParenthesisInRegex()
    {
        Path glob = new PathCompiler().compile("/(bar \\(.*\\))/foo");
        
        assertTrue(glob.matches("/(bar)/foo"));
        assertFalse(glob.matches("/bar/foo"));
        
        Map<String, String> parameters = glob.match("/(bar)/foo");
        assertEquals("(bar)", parameters.get("bar"));
        
        assertEquals(glob.path(parameters), "/(bar)/foo");
    }
    
    // TODO Document.
    @Test(expectedExceptions = Danger.class)
    public void limitOrSeparatorExpected()
    {
        try
        {
            new PathCompiler().compile("/(bar (.*)) (%s)/foo");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), LIMIT_OR_SEPARATOR_EXPECTED);
            assertEquals(e.getMessage(), "A limit specifier or a path separator is expected following a match specification closing parenthesis. Pattern /(bar (.*)) (%s)/foo at position 12.");
            throw e;
        }
    }
    
    // TODO Document.
    @Test(expectedExceptions = Danger.class) 
    public void unescapedForwardSlash()
    {
        try
        {
            new PathCompiler().compile("/(bar \\((.*)\\) (%s)/foo");
        }
        catch (DovetailException e)
        {
            assertEquals(e.getCode(), UNESCAPED_FORWARD_SLASH_IN_FORMAT);
            assertEquals(e.getMessage(), "An unescaped forward slash was found in a match specification format. Pattern /(bar \\((.*)\\) (%s)/foo at position 20.");
            throw e;
        }
    }
    
    // TODO Document.
    @Test public void parenthesisInFormat()
    {
        Path glob = new PathCompiler().compile("/(bar \\((.*)\\) (%s))/foo");
         
        assertTrue(glob.matches("/(bar)/foo"));
        assertFalse(glob.matches("/bar/foo"));
        
        Map<String, String> parameters = glob.match("/(bar)/foo");
        assertEquals("bar", parameters.get("bar"));
        
        assertEquals(glob.path(parameters), "/(bar)/foo");
    }
    
    // TODO Document.
    @Test public void escapedParenthesisInFormat()
    {
        Path glob = new PathCompiler().compile("/(bar \\((.*)\\) %(%s%))/foo");
         
        assertTrue(glob.matches("/(bar)/foo"));
        assertFalse(glob.matches("/bar/foo"));
        
        Map<String, String> parameters = glob.match("/(bar)/foo");
        assertEquals("bar", parameters.get("bar"));
        
        assertEquals(glob.path(parameters), "/(bar)/foo");
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */