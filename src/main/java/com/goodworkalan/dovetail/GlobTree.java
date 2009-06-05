package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO Document.
public class GlobTree<T>
{
    /** The root node of the tree. */
    private final Node<T> root = new Node<T>(null);
    
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
        Node<T> node = root;
        for (int i = 0; i < glob.size(); i++)
        {
            node = getChild(node, glob.get(i));
            if (node.matchesLeft > matchesLeft[i])
            {
                node.matchesLeft = matchesLeft[i];
            }
        }
        node.value = value;
    }

    // TODO Document.
    private Node<T> getChild(Node<T> parent, Test match)
    {
        Node<T> child = null;
        for (Node<T> node : parent.listOfNodes)
        {
            if (node.match.equals(match))
            {
                child = node;
                break;
            }
        }
        if (child == null)
        {
            child = new Node<T>(match);
            parent.listOfNodes.add(child);
        }
        return child;
    }
    
    // TODO Document.
    public boolean match(String path)
    {
        return ! map(path).isEmpty();
    }
    
    // TODO Document.
    public List<Match<T>> map(String path)
    {
        TreeMapper<T> mapper = new TreeMapper<T>();
        if (descend(mapper, root.listOfNodes.get(0), path.split("/"), 0)) 
        {
            return mapper.mappings();
        }
        return Collections.emptyList();
    }
    
    // TODO Document.
    private boolean descend(TreeMapper<T> mapper, Node<T> node, String[] parts, int partIndex)
    {
        int partsLeft = parts.length - partIndex;
        int matchesLeft = node.match.getMin() + node.matchesLeft;
        int min = node.match.getMin();
        int max = Math.min(partsLeft - matchesLeft + 1, node.match.getMax());
        for (int i = min; i <= max; i++)
        {
            if (match(mapper.duplicate(), node, parts, partIndex, i))
            {
                return true;
            }
        }
        return false;
    }

    // TODO Document.
    private boolean match(TreeMapper<T> mapper, Node<T> node, String[] parts, int partIndex, int length)
    {
        if (length == 0 || node.match.match(mapper.getParameters(), parts, partIndex, partIndex + length))
        {
            partIndex += length;

            if (partIndex == parts.length)
            {
                mapper.map(0, node.value);
                return node.matchesLeft == 0;
            }
            if (node.listOfNodes.isEmpty())
            {
                return false;
            }
            boolean matched = false;
            for (Node<T> child : node.listOfNodes)
            {
                matched = descend(mapper, child, parts, partIndex) || matched;
            }
            return matched;
        }
        return false;
    }

    // TODO Document.
    final static class Node<T>
    {
        public final Test match;
        
        public final List<Node<T>> listOfNodes;
        
        public T value;
        
        public int matchesLeft;
        
        public Node(Test match)
        {
            this.match = match;
            this.listOfNodes = new ArrayList<Node<T>>();
            this.matchesLeft = Integer.MAX_VALUE;
        }
    }
}
