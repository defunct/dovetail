/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.List;
import java.util.Map;

/**
 * A compiled Dovetail path pattern.
 *
 * @author Alan Gutierrez
 */
public final class Path {
    /** The pattern used to create the path. */
    private final String pattern;

    /** The array of tests to apply against the path. */
    private final Part[] tests;

    /**
     * Construct a slash separated relative path using the given list of path
     * parts.
     * 
     * @param parts
     *            The list of path parts.
     * @return The path parts catenated into a slash separated string.
     */
    public static String manyTest(String[] parts) {
        StringBuilder builder = new StringBuilder();
        String separator = "";
        for (String part : parts) {
            builder.append(separator);
            builder.append(part);
            separator = "/";
        }
        return builder.toString();
    }

    /**
     * Create an empty glob that matches the space before the root in a path,
     * the empty string literal test. This is used by the glob compiler as the
     * relative glob for a root compiler.
     */
    Path() {
        this(new Part[] { new LiteralPart("") }, "");
    }

    // TODO Document.
    Path(Part[] matches, String pattern) {
        this.tests = matches;
        this.pattern = pattern;
    }
    
    /**
     * Get the number of tests in the glob.
     * 
     * @return The number of tests in the glob.
     */
    int size() {
        return tests.length;
    }

    /**
     * Create a new glob by extending this glob by the given glob.
     * <p>
     * The glob provided will be made relative by removing the initial match
     * that matches the root of the a path, then the relativized glob will
     * be appended to a copy of this glob.
     * 
     * @param glob
     *            The glob to append to this glob.
     * @return The new, extended glob.
     */
    public Path extend(Path glob) {
        Part[] copyTests = new Part[tests.length + glob.tests.length - 1];
        System.arraycopy(tests, 0, copyTests, 0, tests.length);
        System.arraycopy(glob.tests, 1, copyTests, tests.length, glob.tests.length - 1);
        
        return new Path(copyTests, pattern + glob.pattern); 
    }

    /**
     * Get the test in the list of tests at the given index.
     * 
     * @param i
     *            The index.
     * @return The test at the given index.
     */
    Part get(int i) {
        return tests[i];
    }

    /**
     * Get the glob pattern.
     * 
     * @return The glob pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Test the glob against the given path returning a map of the captured
     * parameters if it matches, null if it does not match.
     * 
     * @param path
     *            The path to match.
     * @return A map of the cpatured parameters of null if it does not match.
     */
    public Map<String, String> match(String path) {
        PathTree<Object> tree = new PathTree<Object>();
        tree.put(this, new Object());
        List<Match<Object>> mapping = tree.match(path);
        if (mapping.isEmpty()) {
            return null;
        }
        return mapping.get(0).getParameters();
    }

    /**
     * Return true if the given path is matched by this glob.
     * 
     * @param path
     *            The path to match.
     * @return True if the path matches this glob.
     */
    public boolean matches(String path) {
        PathTree<Object> tree = new PathTree<Object>();
        tree.put(this, new Object());
        return tree.matches(path);
    }

    /**
     * Recreate a path from this glob using the parameters in the given
     * parameter map to replace the parameter captures in glob pattern.
     * 
     * @param path
     *            The path buffer.
     * @param parameters
     *            The parameters used to replace parameter captures in the glob
     *            pattern.
     */
    public String path(Map<String, String> parameters) {
        StringBuilder path = new StringBuilder();
        path.append("/");
        for (int i = 1, size = tests.length; i < size; i++) {
            if (path.charAt(path.length() - 1) != '/') {
                path.append('/');
            }
            tests[i].append(path, parameters);
        }
        return path.toString();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        String separator = "";
        for (Part test : tests) {
            string.append(separator).append(test.toString());
            separator = "/";
        }
        return string.toString();
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */