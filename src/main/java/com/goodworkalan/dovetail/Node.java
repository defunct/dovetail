package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.List;

/**
 * A node in a tree of exploded path expressions used to implement an
 * association of path expressions to values. The tree allows a path to be
 * compared against a collection of path expressions without repeating a
 * comparison.
 * <p>
 * Path expressions are divided into their parts and assigned to the tree. The
 * root of the tree is a literal part with an empty string that matches the
 * empty string before root slash in an absolute path. Each node contains a list
 * of children. When a path expression is added to the tree, it is exploded into
 * a linked list of nodes. Each node can have multiple children, so that when a
 * part is added that starts with the same series of parts as another path
 * expression in the tree, the first part that differs is added to the list of
 * children of the last part that is the same.
 * <p>
 * Thus, when compared to a path, the failure of a node to match eliminates the
 * need to test any of its children.
 * 
 * @author Alan Gutierrez
 * 
 * @param <T>
 *            The type of object mapped to the path.
 */
class Node<T> {
    /** The path expression part. */
    public final Part part;

    /** The list of child nodes. */
    public final List<Node<T>> children;

    /** The associated value. */
    public T value;

    /** The path that terminates at this node. */
    public Path path;

    /** The maximum number of matches possible following this node. */
    public int matchesLeft;
    
    /**
     * The priority or -1 if this node is not the terminal part of a path
     * expression.
     */
    public int priority;

    /**
     * Create a tree node containing the given part.
     * 
     * @param part
     *            The part.
     */
    public Node(Part part) {
        this.part = part;
        this.children = new ArrayList<Node<T>>();
        this.matchesLeft = Integer.MAX_VALUE;
        this.priority = -1;
    }

    /**
     * Create a node that is a copy of the given node. This performs a deep copy
     * so that the new node will not reference any objects referenced by the
     * given node.
     * 
     * @param copy
     *            The node to copy.
     */
    public Node(Node<T> copy) {
        this(copy.part);
        this.value = copy.value;
        this.path = copy.path;
        this.matchesLeft = copy.matchesLeft;
        this.priority = copy.priority;
        for (Node<T> child : copy.children) {
            this.children.add(new Node<T>(child));
        }
    }
}