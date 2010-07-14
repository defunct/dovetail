package com.goodworkalan.dovetail;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

// TODO Document.
public class LiteralTest
{
    // TODO Document.
    @Test
    public void equality()
    {
        LiteralPart foo = new LiteralPart("foo");
        assertTrue(foo.equals(foo));
        assertFalse(foo.equals(new Object()));
        assertEquals(foo, new LiteralPart("foo"));
        assertEquals(foo.hashCode(), new LiteralPart("foo").hashCode());
    }
}
