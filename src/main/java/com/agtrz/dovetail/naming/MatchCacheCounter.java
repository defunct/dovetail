package com.agtrz.dovetail.naming;

import com.agtrz.dovetail.CoreMatchCache;
import com.agtrz.dovetail.MatchCache;

public class MatchCacheCounter
{
    public final MatchCache cache;

    public int count;

    public MatchCacheCounter(int count)
    {
        this.cache = new CoreMatchCache();
        this.count = count;
    }
}
