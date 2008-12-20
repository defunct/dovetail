package com.goodworkalan.dovetail;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

public class LiteralTest
{
    @Test
    public void equality()
    {
        Literal foo = new Literal("foo", 1, 1);
        assertTrue(foo.equals(foo));
        assertTrue(foo.equals(new Literal("foo", 1, 1)));
        assertFalse(foo.equals(new Literal("foo", 1, 2)));
        assertFalse(foo.equals(new Literal("foo", 2, 1)));
        assertFalse(foo.equals(new Literal("bar", 1, 1)));
        assertFalse(foo.equals(new Object()));
        assertEquals(foo.hashCode(), new Literal("foo", 1, 1).hashCode());
    }
}
