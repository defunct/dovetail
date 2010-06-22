package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Node<T> implements Iterable<Node<T>> {
    private final Part match;

    private final List<Node<T>> children;

    private T value;

    private Path glob;

    private int matchesLeft;

    public Node(Part match) {
        this.match = match;
        this.children = new ArrayList<Node<T>>();
        this.matchesLeft = Integer.MAX_VALUE;
    }

    public Node<T> getFirstChild() {
        return children.get(0);
    }

    public Iterator<Node<T>> iterator() {
        return children.iterator();
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void addChild(Node<T> child) {
        children.add(child);
    }

    public Part getMatch() {
        return match;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Path getGlob() {
        return glob;
    }

    public void setGlob(Path glob) {
        this.glob = glob;
    }

    public int getMatchesLeft() {
        return matchesLeft;
    }

    public void setMatchesLeft(int matchesLeft) {
        this.matchesLeft = matchesLeft;
    }

    public Node<T> duplicate() {
        Node<T> copy = new Node<T>(match);
        copy.value = value;
        copy.glob = glob;
        copy.matchesLeft = matchesLeft;
        for (Node<T> child : children) {
            copy.children.add(child.duplicate());
        }
        return copy;
    }
}