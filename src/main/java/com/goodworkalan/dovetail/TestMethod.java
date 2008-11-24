/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.regex.Matcher;

public interface TestMethod
{
    public String test(Matcher matcher) throws Exception;
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */