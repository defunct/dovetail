package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.Path.EMPTY_PATH_PART;
import static com.goodworkalan.dovetail.Path.FIRST_FORWARD_SLASH_MISSING;
import static com.goodworkalan.dovetail.Path.INVALID_LIMIT_CHARACTER;
import static com.goodworkalan.dovetail.Path.LIMIT_OR_SEPARATOR_EXPECTED;
import static com.goodworkalan.dovetail.Path.PATH_SEPARATOR_EXPECTED;
import static com.goodworkalan.dovetail.Path.UNESCAPED_FORWARD_SLASH_IN_FORMAT;
import static com.goodworkalan.dovetail.Path.UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION;
import static com.goodworkalan.dovetail.Path.UNEXPECTED_END_OF_PATH_EXPESSION;

import com.goodworkalan.danger.Danger;

/**
 * Creates a {@link Path} from a path pattern.
 * <p>
 * Additional tests can be specified using <code>test</code> methods.
 * 
 * @author Alan Gutierrez
 */
public final class PathCompiler {
    /** The relative root glob. */
    private final Path glob;

    /**
     * Create a glob compiler that will create globs relative to the given root
     * glob.
     * 
     * @param glob
     *            The relative root glob.
     */
    public PathCompiler(Path glob) {
        this.glob = glob;
    }
   
    /** Create a glob compiler without a relative root. */
    public PathCompiler() {
        this(new Path());
    }

    /**
     * Compile a glob from the given glob pattern that will apply the match
     * tests added to the glob compiler through the <code>test</code> methods.
     * 
     * @param pattern
     *            The glob pattern.
     * @return A glob object.
     */
    public Path compile(String pattern) {
        if (pattern == null) {
            throw new NullPointerException();
        }
        if (pattern.trim().length() == 0) {
            return glob.extend(new Path(new Part[] { new LiteralPart("") }, pattern));
        }
        if (pattern.charAt(0) != '/') {
            throw new Danger(Path.class, FIRST_FORWARD_SLASH_MISSING, pattern, 1);
        }
        Compilation compilation = new Compilation(pattern);
        while (compilation.hasMoreTokens())
 {
            char token = compilation.nextToken();
            switch (compilation.getState()) {
            case SEPARATOR:
                if (token == '/') {
                    compilation.ex(EMPTY_PATH_PART);
                } else if (token == '(') {
                    compilation.setState(CompilerState.IDENTIFIERS);
                    compilation.startParenthesisMatching();
                } else {
                    compilation.append(token);
                    compilation.setState(CompilerState.LITERAL);
                }
                break;
            case LITERAL:
                if (token == '/') {
                    compilation.addLiteral();
                    compilation.setState(CompilerState.SEPARATOR);
                } else {
                    compilation.append(token);
                }
                break;
            case IDENTIFIERS:
                if (token == ' ') {
                    if (!compilation.isEatWhite()) {
                        compilation.addIdentifier();
                        compilation.setState(CompilerState.REGEX);
                        compilation.startParenthesisMatching();
                    }
                } else if (token == ',') {
                    compilation.addIdentifier();
                    compilation.startParenthesisMatching();
                } else if (token == ')') {
                    compilation.addIdentifier();
                    compilation.setState(CompilerState.LIMITS_OPEN);
                    compilation.startParenthesisMatching();
                } else {
                    compilation.assertIdentifierCharacter(token);
                    compilation.append(token);
                }
                compilation.setEatWhite();
                break;
            case REGEX:
                if (token == '(') {
                    compilation.openParenthesis();
                    compilation.append(token);
                } else if (token == ')') {
                    if (compilation.closeParenthesis()) {
                        compilation.setRegex();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    } else {
                        compilation.append(token);
                    }
                } else if (token == '/') {
                    if (!compilation.isEatWhite() && !compilation.isEscape()) {
                        compilation.ex(UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION);
                    }
                    compilation.addExpression();
                    compilation.setState(CompilerState.SEPARATOR);
                } else if (token == ' ') {
                    if (!compilation.isEatWhite()) {
                        compilation.setRegex();
                        compilation.setState(CompilerState.SPRINTF);
                        compilation.startParenthesisMatching();
                    }
                } else {
                    compilation.append(token);
                }
                compilation.setEscapeIf('\\');
                compilation.setEatWhite();
                break;
            case SPRINTF:
                if (token == '(') {
                    compilation.openParenthesis();
                    if (compilation.isEscape()) {
                        compilation.backspace();
                    }
                    compilation.append(token);
                } else if (token == ')') {
                    if (compilation.closeParenthesis()) {
                        compilation.setFormat();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    } else {
                        if (compilation.isEscape()) {
                            compilation.backspace();
                        }
                        compilation.append(token);
                    }
                } else if (token == '/') {
                    if (compilation.isEscape()) {
                        compilation.backspace();
                        compilation.append(token);
                    } else {
                        compilation.ex(UNESCAPED_FORWARD_SLASH_IN_FORMAT);
                    }
                } else if (token == ' ') {
                    if (!compilation.isEatWhite()) {
                        compilation.setFormat();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    }
                } else {
                    compilation.append(token);
                }
                compilation.setEscapeIf('%');
                compilation.setEatWhite();
                break;
            case LIMITS_OPEN:
                if (token == '?') {
                    compilation.setLimits(0, 1);
                    compilation.setState(CompilerState.COMPLETE);
                } else if (token == '+') {
                    compilation.setLimits(1, Integer.MAX_VALUE);
                    compilation.setMultiple(true);
                    compilation.setState(CompilerState.COMPLETE);
                } else if (token == '*') {
                    compilation.setLimits(0, Integer.MAX_VALUE);
                    compilation.setMultiple(true);
                    compilation.setState(CompilerState.COMPLETE);
                } else if (token == '[') {
                    compilation.setMultiple(true);
                    compilation.setState(CompilerState.LIMITS);
                } else if (token == '/') {
                    compilation.setLimits(1, 1);
                    compilation.addExpression();
                    compilation.setState(CompilerState.SEPARATOR);
                } else {
                    compilation.ex(LIMIT_OR_SEPARATOR_EXPECTED);
                }
                break;
            case LIMITS:
                if (Character.isDigit(token)) {
                    compilation.append(token);
                } else if (token == ',') {
                    compilation.setMinimum();
                } else if (token == ']') {
                    compilation.setLimit();
                    compilation.setState(CompilerState.COMPLETE);
                } else {
                    compilation.ex(INVALID_LIMIT_CHARACTER);
                }
                break;
            case COMPLETE:
                if (token != '/') {
                    compilation.ex(PATH_SEPARATOR_EXPECTED);
                }
                compilation.addExpression();
                compilation.setState(CompilerState.SEPARATOR);
                break;
            }
        }
        switch (compilation.getState()) {
        case LITERAL:
            compilation.addLiteral();
            break;
        case LIMITS_OPEN:
            compilation.setLimits(1, 1);
            compilation.addExpression();
            break;
        case COMPLETE:
            compilation.addExpression();
            break;
        default:
            if (pattern.trim() == "/") {
                compilation.addLiteral();
            } else {
                compilation.ex(UNEXPECTED_END_OF_PATH_EXPESSION);
            }
        }
        return glob.extend(new Path(compilation.getTests(), pattern));
    }
}
