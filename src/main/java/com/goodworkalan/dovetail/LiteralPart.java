package com.goodworkalan.dovetail;

import java.util.Map;

/**
 * Matches a part by matching a literal path.
 * 
 * @author Alan Gutierrez
 */
final class LiteralPart implements Part {
    /** The literal text to compare against the part. */
    private final String text;

    /**
     * Create a match that will compare the given text against a part for
     * equality the given minimum and given maximum number of times in a URL
     * path. The only valid values for minimum are zero and one.
     * 
     * @param text
     *            The literal text to compare against the part.
     */
    public LiteralPart(String text) {
        this.text = text;
    }

    /**
     * Return true if the literal matches the string parts from the ranges
     * specified by the start inclusive and end exclusive.
     * 
     * @param mapper
     *            A state for captures.
     * @param parts
     *            The component parts of a URL path.
     * @param start
     *            The part at which to begin the match.
     * @param end
     *            The part at which to end the match.
     * @return True if this match matches.
     */
    public boolean match(Map<String, String> parameters, String[] parts, int start, int end) {
        for (int i = start; i < end; i++) {
            if (!text.equals(parts[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * The minimum number of parts that this match can match, either zero or
     * one.
     * 
     * @return The minimum number of parts that this match can match.
     */
    public int getMin() {
        return 1;
    }

    /**
     * The maximum number of parts of a URL path that this literal can match,
     * which in the case of literal is always 1.
     * 
     * @return The maximum number of parts that this match can match.
     */
    public int getMax() {
        return 1;
    }

    /**
     * A literal is equal to another literal object with the same match text
     * property and the same minimum number of matches property.
     * 
     * @param object
     *            An object to which to compare this object.
     * @return True if the given object is equal to this literal.
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LiteralPart) {
            LiteralPart literal = (LiteralPart) object;
            return text.equals(literal.text);
        }
        return false;
    }

    /**
     * Return a hash code that combines the hash code of the match text property
     * and the minimum number of matches property.
     * 
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return text.hashCode();
    }

    /**
     * Assemble a path by appending the literal part text to the given string
     * builder.
     * 
     * @param path
     *            The path under construction.
     * @param parameters
     *            The parameters used to populate captures.
     */
    public void append(StringBuilder path, Map<String, String> parameters) {
        path.append(text);
    }

    /**
     * Return the literal text.
     * 
     * @return The literal text.
     */
    @Override
    public String toString() {
        return text;
    }
}