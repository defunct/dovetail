package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.List;

// TODO Document.
public class GlobTree<T>
{
    /** The root node of the tree. */
    private final Node<T> root = new Node<T>(null);
    
    /** The factory used to create match tests. */
    private MatchTestFactory matchTestFactory = new SimpleMatchTestFactory();
    
    public void setMatchTestFactory(MatchTestFactory factory)
    {
        this.matchTestFactory = factory;
    }
    
    public MatchTestFactory getMatchTestFactory()
    {
        return matchTestFactory;
    }
    
    /**
     * Map the given glob to the given tree value. When a path given to the
     * tree matches the given glob, the given value will be returned  
     * 
     * @param glob
     * @param value
     */
    public void add(Glob glob, T value)
    {
        int[] matchesLeft = new int[glob.size()];
        matchesLeft[matchesLeft.length - 1] = 0;
        for (int i = matchesLeft.length - 2; i >= 0; i--)
        {
            matchesLeft[i] = matchesLeft[i + 1] + glob.get(i + 1).getMin();
        }
        List<Node<T>> path = new ArrayList<Node<T>>();
        Node<T> node = root;
        for (int i = 0; i < glob.size(); i++)
        {
            node = getChild(node, glob.get(i));
            path.add(node);
            if (node.getMatchesLeft() > matchesLeft[i])
            {
                node.setMatchesLeft(matchesLeft[i]);
            }
        }
        for (int i = matchesLeft.length - 1; i >= 0 && matchesLeft[i] == 0; i--)
        {
            path.get(i).setValue(value);
            path.get(i).setGlob(glob);
        }
    }

    // TODO Document.
    private Node<T> getChild(Node<T> parent, Test match)
    {
        Node<T> child = null;
        for (Node<T> node : parent)
        {
            if (node.getMatch().equals(match))
            {
                child = node;
                break;
            }
        }
        if (child == null)
        {
            child = new Node<T>(match);
            parent.addChild(child);
        }
        return child;
    }
    
    public Globber<T> newGlobber(MatchTestFactory factory)
    {
        return new Globber<T>(root.duplicate(), factory);
    }
    
    public Globber<T> newGlobber()
    {
        return new Globber<T>(root.duplicate(), new SimpleMatchTestFactory());
    }
    
    // TODO Document.
    public boolean match(String path)
    {
        return newGlobber().match(path);
    }
    
    // TODO Document.
    public List<Match<T>> map(String path)
    {
        return newGlobber().map(path);
    }
}
