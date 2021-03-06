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

    /** A pattern must begin with a forward slash. */
    public final static String FIRST_FORWARD_SLASH_MISSING = "302";

    /** Unexpected empty path part. */
    public final static String EMPTY_PATH_PART = "303";

    /** Identifier is missing. */
    public final static String IDENTIFER_MISSING = "304";

    /** Unexpected character while expecting a Java identifier start character. */
    public final static String JAVA_IDENTIFIER_START_EXPECTED = "305";

    /** Unexpected character while expecting a Java identifier part character. */
    public final static String JAVA_IDENTIFIER_PART_EXPECTED = "306";

    /** Unescaped forward slash encountered in regular expression of capturing part. */
    public final static String UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION = "307";

    /** Unable to parse the regular expression of a capturing part. */
    public final static String CANNOT_PARSE_REGULAR_EXPESSION = "308";

    /** Unexpected character while reading a capturing part limit definition. */
    public final static String INVALID_LIMIT_CHARACTER = "309";

    /** Unexpected comma while reading a capturing part limit definition. */
    public final static String UNEXPECTED_COMMA_IN_LIMIT = "310";

    /** The required minimum limit is unspecified. */
    public final static String MINIMUM_LIMIT_REQUIRED = "311";

    /** Unable to parse a limit value. */
    public final static String CANNOT_PARSE_LIMIT_VALUE = "312";

    /** Unexpected character while expecting a capturing part limit definition or path separator. */
    public final static String LIMIT_OR_SEPARATOR_EXPECTED = "313";

    /** Unexpected character while expecting a path separator. */
    public final static String PATH_SEPARATOR_EXPECTED = "316";

    /** Unescaped forward slash encountered in reassembly sprintf pattern of capturing part. */
    public final static String UNESCAPED_FORWARD_SLASH_IN_FORMAT = "317";

    /** Unexpected end of a path expression. */
    public final static String UNEXPECTED_END_OF_PATH_EXPESSION = "318";

    /** Reassembly parameter is null. */
    public final static String FORMAT_PARAMETER_IS_NULL = "601";

    /** Have not gotten around to creating a meaninful error message. */
    public final static String USELESS_ERROR_CODE = "0";

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

    /**
     * Create a new path expression with the given array of part expressions
     * compiled from the given path pattern.
     * 
     * @param parts
     *            The part expressions.
     * @param pattern
     *            The path pattern.
     */
    Path(Part[] parts, String pattern) {
        this.tests = parts;
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
        PathAssociation<Object> tree = new PathAssociation<Object>();
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
        PathAssociation<Object> tree = new PathAssociation<Object>();
        tree.put(this, new Object());
        return tree.matches(path);
    }

    /**
     * Recreate a path from this glob using the parameters in the given
     * parameter map to replace the parameter captures in glob pattern.
     * 
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
