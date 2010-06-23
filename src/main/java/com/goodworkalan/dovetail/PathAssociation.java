package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maps paths to objects.
 * 
 * @author Alan Gutierrez
 * 
 * @param <T>
 *            The type of object mapped to the path.
 */
public class PathAssociation<T> {
    /** The root node of the tree. */
    private final Node<T> root;

    /** The priority which increases with each addition of a path expression. */
    private int priority;
    
    /** Create an empty path association. */
    public PathAssociation() {
        this.root = new Node<T>((Part) null);
    }

    /**
     * Create a path association that is a copy of the given path association.
     * The new path association will not reference any of the objects referenced
     * by the given path association.
     * 
     * @param copy
     *            The path association to copy.
     */
    public PathAssociation(PathAssociation<T> copy) {
        this.root = new Node<T>(copy.root);
        this.priority = copy.priority;
    }

    /**
     * Map the given glob to the given tree value. When a path given to the tree
     * matches the given glob, the given value will be returned
     * 
     * @param path
     *            The path to add.
     * @param value
     *            The value to map to the path.
     */
    public void put(Path path, T value) {
        int[] matchesLeft = new int[path.size()];
        matchesLeft[matchesLeft.length - 1] = 0;
        for (int i = matchesLeft.length - 2; i >= 0; i--) {
            matchesLeft[i] = matchesLeft[i + 1] + path.get(i + 1).getMin();
        }
        List<Node<T>> descent = new ArrayList<Node<T>>();
        Node<T> node = root;
        for (int i = 0; i < path.size(); i++) {
            node = getChild(node, path.get(i));
            descent.add(node);
            if (node.matchesLeft > matchesLeft[i]) {
                node.matchesLeft = matchesLeft[i];
            }
        }
        for (int i = matchesLeft.length - 1; i >= 0 && matchesLeft[i] == 0; i--) {
            descent.get(i).value = value;
            descent.get(i).path = path;
            descent.get(i).priority = priority;
        }
        priority++;
    }

    /**
     * Get the child node that contains the given part, or else add a new child
     * node to the given part returning the newly created child node.
     * 
     * @param parent
     *            The parent node.
     * @param part
     *            The part.
     * @return The child node that contains the part.
     */
    private Node<T> getChild(Node<T> parent, Part part) {
        Node<T> child = null;
        for (Node<T> node : parent.children) {
            if (node.part.equals(part)) {
                child = node;
                break;
            }
        }
        if (child == null) {
            child = new Node<T>(part);
            parent.children.add(child);
        }
        return child;
    }
    
    /**
     * Return true if a glob in the glob tree matches the given path.
     * 
     * @param path The path to test.
     * @return True of a path in the glob tree matches the given path.
     */
    public boolean matches(String path) {
        return ! match(path).isEmpty();
    }

    /**
     * Return a list of {@link Match} instances, one for each path expression
     * that matches the given path. The <code>Match</code> will contain both the
     * value and the captured match parameters.
     * <p>
     * The list will contain the values of all the path expressions that match
     * the given path. The matches values will be returned in the order in which
     * their path expressions were added to the path association.
     * <p>
     * For path expressions that can match multiple parts, the path expression
     * will be applied so that the multiple path expressions toward the
     * beginning of the path part will match as much as possible.
     * 
     * @param path
     *            The path to match.
     * @return A list of matches.
     */
    public List<Match<T>> match(String path) {
        if (!root.children.isEmpty()) {
            MatchBook<T> matches = new MatchBook<T>();
            descend(matches, root.children.get(0), path.split("/", -1), 0/*, path*/);
            return matches.matches();
        }
        return Collections.emptyList();
    }

    /**
     * Descend the path expression tree comparing the part expression in the
     * given node against the parts in the range of parts at the given index.
     * Apply the match so that it attempts to match the maximum number parts
     * first.
     * 
     * @param matches
     *            The collection of values with matched path expressions.
     * @param node
     *            The node to test.
     * @param parts
     *            The split path.
     * @param partIndex
     *            The part index into the split path.
     * @return True if the decent matched any nodes.
     */
    private void descend(MatchBook<T> matches, Node<T> node, String[] parts, int partIndex) {
        int partsLeft = parts.length - partIndex;
        int matchesLeft = node.part.getMin() + node.matchesLeft;
        int min = node.part.getMin();
//        int max = Math.min(partsLeft - matchesLeft + 1, node.part.getMax());
        int max = Math.min(partsLeft, Math.min(partsLeft - matchesLeft + 1, node.part.getMax()));
        for (int i = min; i <= max; i++) {
            match(matches.parameterCopy(), node, parts, partIndex, i);
        }
    }

    /**
     * Attempt to match the part expression in the given node against the parts
     * in the range of parts at the given index.
     * 
     * @param matches
     *            The collection of values with matched path expressions.
     * @param node
     *            The node to test.
     * @param parts
     *            The split path.
     * @param partIndex
     *            The part index into the split path.
     * @param length
     *            The count of parts in the range.
     * @return
     */
    private void match(MatchBook<T> matches, Node<T> node, String[] parts, int partIndex, int length) {
        if (length == 0 || node.part.match(matches.getParameters(), parts, partIndex, partIndex + length)) {
            partIndex += length;

            if (partIndex == parts.length) {
                if (node.priority != -1) {
                    matches.map(node.priority, node.value);
                }
            }

            for (Node<T> child : node.children) {
                descend(matches, child, parts, partIndex);
            }
        }
    }
}
