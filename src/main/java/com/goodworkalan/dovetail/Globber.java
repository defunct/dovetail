package com.goodworkalan.dovetail;

import java.util.Collections;
import java.util.List;


public class Globber<T>
{
    /** The root node of the tree. */
    private final Node<T> root;
    
    private final MatchTestFactory factory;
    
    Globber(Node<T> root, MatchTestFactory factory)
    {
        this.root = root;
        this.factory = factory;
    }
    
    public boolean match(String path)
    {
        return ! map(path).isEmpty();
    }

    // TODO Document.
    public List<Match<T>> map(String path)
    {
        MatchBook<T> mapper = new MatchBook<T>();
        if (descend(mapper, root.getFirstChild(), path.split("/"), 0, path)) 
        {
            return mapper.matches();
        }
        return Collections.emptyList();
    }
    
    // TODO Document.
    private boolean descend(MatchBook<T> mapper, Node<T> node, String[] parts, int partIndex, String path)
    {
        int partsLeft = parts.length - partIndex;
        int matchesLeft = node.getMatch().getMin() + node.getMatchesLeft();
        int min = node.getMatch().getMin();
        int max = Math.min(partsLeft - matchesLeft + 1, node.getMatch().getMax());
        for (int i = min; i <= max; i++)
        {
            if (match(mapper.duplicate(), node, parts, partIndex, i, path))
            {
                return true;
            }
        }
        return false;
    }

    // TODO Document.
    private boolean match(MatchBook<T> mapper, Node<T> node, String[] parts, int partIndex, int length, String path)
    {
        if (length == 0 || node.getMatch().match(mapper.getParameters(), parts, partIndex, partIndex + length))
        {
            partIndex += length;

            if (partIndex == parts.length)
            {
                // TODO If there are matches left, why does this work?
                if (node.getGlob().matchTests(factory, path, mapper.getParameters()))
                {
                    mapper.map(0, node.getValue());
                }
                return node.getMatchesLeft() == 0;
            }
            if (!node.hasChildren())
            {
                return false;
            }
            boolean matched = false;
            for (Node<T> child : node)
            {
                matched = descend(mapper, child, parts, partIndex, path) || matched;
            }
            return matched;
        }
        return false;
    }
}
