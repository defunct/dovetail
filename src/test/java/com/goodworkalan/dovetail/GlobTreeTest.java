package com.goodworkalan.dovetail;

import org.testng.annotations.Test;

import com.goodworkalan.dovetail.Glob;
import com.goodworkalan.dovetail.GlobTree;

public class GlobTreeTest
{
    @Test public void tree()
    {
        GlobTree tree = new GlobTree();
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        tree.add(glob);
    }
}
