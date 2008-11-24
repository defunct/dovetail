/* Copyright Alan Gutierrez 2006 */
package com.agtrz.dovetail;

import java.util.regex.Matcher;


public class GroupTestMethod
implements TestMethod
{
    private final int group;
    
    public GroupTestMethod(int group)
    {
        this.group = group;
    }

    public String test(Matcher matcher)
    {
        if (group == -1)
        {
            if (matcher.groupCount() == 0)
            {
                return matcher.group(0);
            }
            return matcher.group(1);
        }
        return matcher.group(group);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object instanceof GroupTestMethod)
        {
            GroupTestMethod groupTestMethod = (GroupTestMethod) object;
            return group == groupTestMethod.group;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return group;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */