package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// TODO Document.
public class GlobTree<T>
{
    // TODO Document.
    private final Node<T> root = new Node<T>(null);
    
    // TODO Document.
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
    private Node<T> getChild(Node<T> parent, Match match)
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
        return match(new TreeMapper<T>(), path);
    }
    
    // TODO Document.
    public Mapping<T> map(String path)
    {
        TreeMapper<T> mapper = new TreeMapper<T>();
        if (match(mapper, path)) 
        {
            return mapper.mappings().get(0);
        }
        return null;
    }
    
    // TODO Document.
    public boolean match(TreeMapper<T> mapper, String path)
    {
        return descend(mapper, root.listOfNodes.get(0), path.split("/"), 0);
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
            Set<String> mark = mapper.mark();
            if (match(mapper.duplicate(), node, parts, partIndex, i))
            {
                return true;
            }
            mapper.revert(mark);
        }
        return false;
    }

    // TODO Document.
    private boolean match(TreeMapper<T> mapper, Node<T> node, String[] parts, int partIndex, int length)
    {
        if (length == 0 || node.match.match(mapper, parts, partIndex, partIndex + length))
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
        public final Match match;
        
        public final List<Node<T>> listOfNodes;
        
        public T value;
        
        public int matchesLeft;
        
        public Node(Match match)
        {
            this.match = match;
            this.listOfNodes = new ArrayList<Node<T>>();
            this.matchesLeft = Integer.MAX_VALUE;
        }
    }
}
