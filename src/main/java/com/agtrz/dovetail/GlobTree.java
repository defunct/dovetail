package com.agtrz.dovetail;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.ActionBean;

public class GlobTree
{
    private final Node root = new Node(new Literal("", 1, 1));
    
    public void add(Glob glob)
    {
        Node parent = root;
        for (int i = 0; i < glob.size() - 1; i++)
        {
            parent = getChild(parent, glob.get(i));
        }
        Node child = getChild(parent, glob.get(glob.size() - 1));
        child.value = glob.getTargetClass();
    }

    private Node getChild(Node parent, Match match)
    {
        Node child = null;
        for (Node node : parent.listOfNodes)
        {
            if (node.match.equals(match))
            {
                child = node;
                break;
            }
        }
        if (child == null)
        {
            child = new Node(match);
            parent.listOfNodes.add(child);
        }
        return child;
    }
    
    final static class Node
    {
        public final Match match;
        
        public final List<Node> listOfNodes;
        
        public Class<? extends ActionBean> value;
        
        public Node(Match match)
        {
            this.match = match;
            this.listOfNodes = new ArrayList<Node>();
        }
    }
}
