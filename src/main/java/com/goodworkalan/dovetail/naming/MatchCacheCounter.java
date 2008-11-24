package com.goodworkalan.dovetail.naming;

import com.goodworkalan.dovetail.CoreMatchCache;
import com.goodworkalan.dovetail.MatchCache;

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
