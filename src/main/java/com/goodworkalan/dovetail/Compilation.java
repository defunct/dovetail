package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.CANNOT_PARSE_LIMIT_VALUE;
import static com.goodworkalan.dovetail.DovetailException.CANNOT_PARSE_REGULAR_EXPESSION;
import static com.goodworkalan.dovetail.DovetailException.JAVA_IDENTIFIER_PART_EXPECTED;
import static com.goodworkalan.dovetail.DovetailException.JAVA_IDENTIFIER_START_EXPECTED;
import static com.goodworkalan.dovetail.DovetailException.IDENTIFER_MISSING;
import static com.goodworkalan.dovetail.DovetailException.MINIMUM_LIMIT_REQUIRED;
import static com.goodworkalan.dovetail.DovetailException.UNEXPECTED_COMMA_IN_LIMIT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A structure to maintain the state of the compilation of a glob pattern.
 * 
 * @author Alan Guiterrez
 */
final class Compilation {
	/** The glob expression to parse. */
	private final String glob;

	/** The buffer for capturing a string. */
	private final StringBuilder capture;

	/** The list of parameter names. */
	private final List<String> identifiers;

	/** The regular expression. */
	private Pattern regex;

	/** The string format. */
	private String sprintf;

	/** The part tests. */
	private final List<Range> tests;

	/** The compiler state. */
	private CompilerState state;

	/** True if any white space encountered should be eaten. */
	private boolean eatWhite;

	/** True if the previous character encountered was an escape character. */
	private boolean escape;

	/** True if the match is supposed to match multiple path parts. */
	private boolean multiple;

	/** The minimum number of times a part expression can match. */
	private int minimum;

	/** The maximum number of times a part expression can match. */
	private int maximum;

	/** The count of open parenthesis. */
	private int parenthesis;

	/** The index of the next character in the glob to process. */
	private int index;

	/**
	 * Create a new compilation that will process the given glob pattern.
	 * 
	 * @param glob
	 *            The glob pattern.
	 */
	public Compilation(String glob) {
		this.glob = glob;
		this.capture = new StringBuilder();
		this.tests = new ArrayList<Range>();
		this.state = CompilerState.SEPARATOR;
		this.identifiers = new ArrayList<String>();
		this.index = 1;
		this.minimum = -1;
		this.maximum = -1;

		// First test is always an empty string literal.
		tests.add(new Literal(""));
	}

	/**
	 * Return true if the compilation has more characters to process.
	 * 
	 * @return True if there are more token.
	 */
	public boolean hasMoreTokens() {
		return index < glob.length();
	}

	/**
	 * Return the next character to process and advance the index.
	 * 
	 * @return The next token.
	 */
	public char nextToken() {
		return glob.charAt(index++);
	}

	/**
	 * Add the glob and the 1 based position of the current character to the
	 * format arguments of the {@link DovetailException}.
	 * 
	 * @param e
	 *            An exception.
	 * @return The given exception.
	 */
	public DovetailException ex(DovetailException e) {
		return e.add(glob, index);
	}

	/**
	 * Set the compiler state.
	 * 
	 * @param state
	 *            The compiler state.
	 */
	public void setState(CompilerState state) {
		this.state = state;
	}

	/**
	 * Get the compiler state.
	 * 
	 * @return The compiler state.
	 */
	public CompilerState getState() {
		return state;
	}

	/**
	 * Set the minimum and maximum parts that can be matched.
	 */
	public void setLimits(int minimum, int maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * Return true of white space characters encountered in the pattern is to be
	 * be eaten.
	 * 
	 * @return True if white space is to be eaten.
	 */
	public boolean isEatWhite() {
		return eatWhite;
	}

	/**
	 * Set eat whitespace flag if the current character is a space.
	 */
	public void setEatWhite() {
		this.eatWhite = glob.charAt(index - 1) == ' ';
	}

	/**
	 * Get whether the current character is escaped.
	 * 
	 * @return True if the current character is escaped.
	 */
	public boolean isEscape() {
		return escape;
	}

	/**
	 * Set the escape flag if the character at the previous index matches
	 * the given character.
	 * @param escaper
	 */
	public void setEscapeIf(char escaper) {
		escape = glob.charAt(index - 1) == escaper;
	}

	// TODO Document.
	public void startParenthesisMatching() {
		eatWhite = true;
		parenthesis = 0;
	}

	// TODO Document.
	public void openParenthesis() {
		if (!escape) {
			parenthesis++;
		}
    }
    
    // TODO Document.
	public boolean closeParenthesis() {
		return escape || parenthesis-- != 0;
	}
    
	// TODO Document.
	public void setRegex() {
		try {
			regex = Pattern.compile(capture.toString());
		} catch (PatternSyntaxException e) {
			throw ex(new DovetailException(CANNOT_PARSE_REGULAR_EXPESSION));
		}
		capture.setLength(0);
	}

	// TODO Document.
	public void setFormat() {
		sprintf = capture.toString();
		capture.setLength(0);
	}

	// TODO Document.
	public void assertIdentifierCharacter(char token) {
		if (capture.length() == 0 && !Character.isJavaIdentifierStart(token)) {
			throw ex(new DovetailException(JAVA_IDENTIFIER_START_EXPECTED));
		} else if (capture.length() > 0 && !(Character.isJavaIdentifierPart(token) || "[]".indexOf(token) > -1)) {
            throw ex(new DovetailException(JAVA_IDENTIFIER_PART_EXPECTED)).add(token, glob, index - 1);
        }
    }

	// TODO Document.
	public void setMultiple(boolean deep) {
		this.multiple = deep;
	}

	// TODO Document.
	public void addLiteral() {
		tests.add(new Literal(capture.toString()));
		capture.setLength(0);
	}

	// TODO Document.
	public void backspace() {
		capture.setLength(capture.length() - 1);
	}
    
	// TODO Document.
	public void append(char token) {
		capture.append(token);
	}

	// TODO Document.
	public void addIdentifier() {
		if (capture.length() == 0) {
			throw ex(new DovetailException(IDENTIFER_MISSING));
		}
        identifiers.add(capture.toString());
        capture.setLength(0);
    }

	// TODO Document.
	public void addExpression() {
		if (regex == null) {
			regex = Pattern.compile(multiple ? "(.*)/" : ".*");
		}
		if (sprintf == null) {
			sprintf = "%s";
		}
		if (minimum == -1) {
			minimum = 1;
		}
		if (maximum == -1) {
			maximum = Integer.MAX_VALUE;
		}
       Expression expression = new Expression(new ArrayList<String>(identifiers), regex, sprintf, minimum, maximum, multiple);
        tests.add(expression);
        identifiers.clear();
        regex = null;
        sprintf = null;
        multiple = false;
        minimum = -1;
        maximum = -1;
        parenthesis = 0;
    }
    
	// TODO Document.
	public void setMinimum() {
		if (minimum != -1) {
			throw new DovetailException(UNEXPECTED_COMMA_IN_LIMIT);
		}
		if (capture.length() == 0) {
			throw new DovetailException(MINIMUM_LIMIT_REQUIRED);
		}
		try {
			minimum = Integer.parseInt(capture.toString());
		} catch (NumberFormatException e) {
			throw new DovetailException(CANNOT_PARSE_LIMIT_VALUE, e);
		}
		capture.setLength(0);
	}

	// TODO Document.
	public void setLimit() {
		if (minimum == -1) {
			setMinimum();
			maximum = minimum;
		} else {
			if (capture.length() == 0) {
				maximum = Integer.MAX_VALUE;
			} else {
				try {
					maximum = Integer.parseInt(capture.toString());
				} catch (NumberFormatException e) {
					throw new DovetailException(CANNOT_PARSE_LIMIT_VALUE, e);
				}
			}
		}
		capture.setLength(0);
	}

	/**
	 * Convert the list of tests to an array.
	 * 
	 * @return The list of tests as an array.
	 */
	public Range[] getTests() {
		return tests.toArray(new Range[tests.size()]);
	}
}