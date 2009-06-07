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
        Literal foo = new Literal("foo");
        assertTrue(foo.equals(foo));
        assertFalse(foo.equals(new Object()));
        assertEquals(foo, new Literal("foo"));
        assertEquals(foo.hashCode(), new Literal("foo").hashCode());
    }
}
