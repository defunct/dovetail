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
public class PathTree<T> {
    /** The root node of the tree. */
    private final Node<T> root = new Node<T>(null);

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
            if (node.getMatchesLeft() > matchesLeft[i]) {
                node.setMatchesLeft(matchesLeft[i]);
            }
        }
        for (int i = matchesLeft.length - 1; i >= 0 && matchesLeft[i] == 0; i--) {
            descent.get(i).setValue(value);
            descent.get(i).setGlob(path);
        }
    }

    // TODO Document.
    private Node<T> getChild(Node<T> parent, Part match) {
        Node<T> child = null;
        for (Node<T> node : parent) {
            if (node.getMatch().equals(match)) {
                child = node;
                break;
            }
        }
        if (child == null) {
            child = new Node<T>(match);
            parent.addChild(child);
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

    // TODO Document.
    public List<Match<T>> match(String path) {
        if (root.hasChildren()) {
            MatchBook<T> mapper = new MatchBook<T>();
            if (descend(mapper, root.getFirstChild(), path.split("/", -1), 0, path)) {
                return mapper.matches();
            }
        }
        return Collections.emptyList();
    }
    
    // TODO Document.
    private boolean descend(MatchBook<T> mapper, Node<T> node, String[] parts, int partIndex, String path) {
        int partsLeft = parts.length - partIndex;
        int matchesLeft = node.getMatch().getMin() + node.getMatchesLeft();
        int min = node.getMatch().getMin();
        int max = Math.min(partsLeft - matchesLeft + 1, node.getMatch().getMax());
        for (int i = min; i <= max; i++) {
            if (match(mapper.duplicate(), node, parts, partIndex, i, path)) {
                return true;
            }
        }
        return false;
    }

    // TODO Document.
    private boolean match(MatchBook<T> mapper, Node<T> node, String[] parts, int partIndex, int length, String path) {
        if (length == 0 || node.getMatch().match(mapper.getParameters(), parts, partIndex, partIndex + length)) {
            partIndex += length;

            if (partIndex == parts.length) {
                // TODO If there are matches left, why does this work?
                mapper.map(0, node.getValue());
                return node.getMatchesLeft() == 0;
            }
            if (!node.hasChildren()) {
                return false;
            }
            boolean matched = false;
            for (Node<T> child : node) {
                matched = descend(mapper, child, parts, partIndex, path) || matched;
            }
            return matched;
        }
        return false;
    }
}
