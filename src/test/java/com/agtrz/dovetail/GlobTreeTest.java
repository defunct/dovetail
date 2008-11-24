package com.agtrz.dovetail;

import org.testng.annotations.Test;

public class GlobTreeTest
{
    @Test public void tree()
    {
        GlobTree tree = new GlobTree();
        Glob glob = new Glob(GlobTestCase.class, "/{account}/optout/{key}/{receipt}");
        tree.add(glob);
    }
}
