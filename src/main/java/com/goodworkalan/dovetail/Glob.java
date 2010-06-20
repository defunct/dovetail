/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.util.List;
import java.util.Map;

/**
 * A compiled dovetail pattern.
 *
 * @author Alan Gutierrez
 */
public final class Glob {
    /** The pattern used to create the glob. */
    private final String pattern;

    /** The array of tests to apply against the path. */
    private final Range[] tests;
    
    // TODO Document.
    private final MatchTestServer[] matchTestServers;
    
	// TODO Document.
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
	 * Create an empty glob that matches the space before the root in a path.
	 * This is used by the glob compiler as the relative glob for a root
	 * compiler.
	 */
	Glob() {
		this(new Range[] { new Literal("") }, "", new MatchTestServer[0]);
	}

    // TODO Document.
	Glob(Range[] matches, String pattern, MatchTestServer[] matchTestServers) {
        this.tests = matches;
        this.pattern = pattern;
        this.matchTestServers = matchTestServers;
    }
    
	/**
	 * Get the number of parts in the glob.
	 * 
	 * @return The number of parts in the glob.
	 */
	public int size() {
		return tests.length;
	}
    
    // TODO Document.
	public Glob extend(Glob glob) {
		Range[] copyTests = new Range[tests.length + glob.tests.length - 1];
        System.arraycopy(tests, 0, copyTests, 0, tests.length);
        System.arraycopy(glob.tests, 1, copyTests, tests.length, glob.tests.length - 1);
        
        MatchTestServer[] copyMatchTests = new MatchTestServer[matchTestServers.length + glob.matchTestServers.length];
        System.arraycopy(matchTestServers, 0, copyMatchTests, 0, matchTestServers.length);
        System.arraycopy(glob.matchTestServers, 0, copyMatchTests, matchTestServers.length, glob.matchTestServers.length);
        
        return new Glob(copyTests, pattern + glob.pattern, copyMatchTests); 
    }

	// TODO Document.
	public Range get(int i) {
		return tests[i];
	}

	// TODO Document.
	public String getPattern() {
		return pattern;
	}

    /**
     * Apply the match tests in this glob to the given path and map of
     * parameters.
     * <p>
     * FIXME Not quite right. Rethink creation pattern. Tests should be
     * applied by the tree, they should not be in two places. Glob might become
     * internal, or only a limited interface, a GlobTree returns a compiler, the
     * compiler builds the Glob and adds it to the tree.
     * 
     * @param path
     *            The path to text.
     * @param parameters
     *            The parameters to test.
     * @return True if all the match tests in this glob pass.
     */
    public boolean matchTests(MatchTestFactory factory, String path, Map<String, String> parameters) {
		for (MatchTestServer matchTestServer : matchTestServers) {
			if (!matchTestServer.getInstance(factory).test(path, parameters)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test the glob against the given path returning a map of the captured
     * parameters if it matches, null if it does not match.
     * 
     * FIXME RENAME.
     * 
     * @param path
     *            The path to match.
     * @return A map of the cpatured parameters of null if it does not match.
     */
	public Map<String, String> map(String path) {
		GlobTree<Object> tree = new GlobTree<Object>();
		tree.add(this, new Object());
		List<Match<Object>> mapping = tree.map(path);
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
	public boolean match(String path) {
		GlobTree<Object> tree = new GlobTree<Object>();
		tree.add(this, new Object());
		return tree.match(path);
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
		for (Range test : tests) {
			string.append(separator).append(test.toString());
			separator = "/";
		}
		return string.toString();
	}
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */