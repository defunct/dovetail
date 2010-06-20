package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.EMPTY_PATH_PART;
import static com.goodworkalan.dovetail.DovetailException.FIRST_FORWARD_SLASH_MISSING;
import static com.goodworkalan.dovetail.DovetailException.INVALID_LIMIT_CHARACTER;
import static com.goodworkalan.dovetail.DovetailException.LIMIT_OR_SEPARATOR_EXPECTED;
import static com.goodworkalan.dovetail.DovetailException.PATH_SEPARATOR_EXPECTED;
import static com.goodworkalan.dovetail.DovetailException.UNESCAPED_FORWARD_SLASH_IN_FORMAT;
import static com.goodworkalan.dovetail.DovetailException.UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION;
import static com.goodworkalan.dovetail.DovetailException.UNEXPECTED_END_OF_GLOB_EXPESSION;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a glob from a glob pattern.
 * <p>
 * Additional tests can be specified using <code>test</code> methods.
 * 
 * @author Alan Gutierrez
 */
public final class GlobCompiler
{
    /** The list of match tests to apply when a glob successfully matches. */
    private final List<MatchTestServer> matchTestServers;

    /** The relative root glob. */
    private final Glob glob;

	/**
	 * Create a glob compiler that will create globs relative to the given root
	 * glob.
	 * 
	 * @param glob
	 *            The relative root glob.
	 */
	public GlobCompiler(Glob glob) {
		this.glob = glob;
        this.matchTestServers = new ArrayList<MatchTestServer>();
    }
   
	/** Create a glob compiler without a relative root. */
    public GlobCompiler() {
		this(new Glob());
	}

	/**
	 * Add the type of a match test that will be applied to the path and the
	 * extracted parameters after a glob matches. If any of match tests
	 * associated with a glob returns false, the glob match will be considered a
	 * mismatch.
	 * <p>
	 * And instance of the given match test class will be created using the
	 * match test factory associated with this glob compiler. The factory
	 * pattern gives the client programmer the opportunity to construct a match
	 * test initialized with application resources, such as a database
	 * connection so that a user name parameter can be sought in a database, and
	 * the match rejected if no such user exists.
	 * 
	 * @param matchTestClass
	 *            The class of a match test to apply to otherwise successful
	 *            glob matches.
	 * @return This glob compiler as part of a chained builder.
	 */
	public GlobCompiler test(Class<? extends MatchTest> matchTestClass) {
		matchTestServers.add(new FactoryMatchTestServer(matchTestClass));
        return this;
    }

    /**
     * Add a match test that will be applied to the path and the extracted
     * parameters after a glob matches. If any of match tests associated with a
     * glob returns false, the glob match will be considered a mismatch.
     * 
     * @param matchTest
     *            The match test to apply to otherwise successful glob matches.
     * @return This glob compiler as part of a chained builder.
     */
	public GlobCompiler test(MatchTest matchTest) {
        matchTestServers.add(new InstanceMatchTestServer(matchTest));
        return this;
    }

    /**
     * Compile a glob from the given glob pattern that will apply the match
     * tests added to the glob compiler through the <code>test</code> methods.
     * 
     * @param pattern
     *            The glob pattern.
     * @return A glob object.
     */
    public Glob compile(String pattern)
    {
        if (pattern == null)
        {
            throw new NullPointerException();
        }
        if (pattern.trim().length() == 0)
        {
            return glob.extend(new Glob(new Test[] { new Literal("") }, pattern, getMatchTests()));
        }
        if (pattern.charAt(0) != '/')
        {
            throw new DovetailException(FIRST_FORWARD_SLASH_MISSING).add(pattern, 1);
        }
        Compilation compilation = new Compilation(pattern);
        while (compilation.hasMoreTokens())
        {
            char token = compilation.nextToken();
            switch (compilation.getState())
            {
            case SEPARATOR:
                if (token == '/')
                {
                    throw compilation.ex(new DovetailException(EMPTY_PATH_PART));
                }
                else if (token == '(')
                {
                    compilation.setState(CompilerState.IDENTIFIERS);
                    compilation.startParenthesisMatching();
                }
                else
                {
                    compilation.append(token);
                    compilation.setState(CompilerState.LITERAL);
                }
                break;
            case LITERAL:
                if (token == '/')
                {
                    compilation.addLiteral();
                    compilation.setState(CompilerState.SEPARATOR);
                }
                else
                {
                    compilation.append(token);
                }
                break;
            case IDENTIFIERS:
                if (token == ' ')
                {
                    if (!compilation.isEatWhite())
                    {
                        compilation.addIdentifier();
                        compilation.setState(CompilerState.REGEX);
                        compilation.startParenthesisMatching();
                    }
                }
                else if (token == ',')
                {
                    compilation.addIdentifier();
                    compilation.startParenthesisMatching();
                }
                else if (token == ')')
                {
                    compilation.addIdentifier();
                    compilation.setState(CompilerState.LIMITS_OPEN);
                    compilation.startParenthesisMatching();
                }
                else
                {
                    compilation.assertIdentifierCharacter(token);
                    compilation.append(token);
                }
                compilation.setEatWhite();
                break;
            case REGEX:
                if (token == '(')
                {
                    compilation.openParenthesis();
                    compilation.append(token);
                }
                else if (token == ')')
                {
                    if (compilation.closeParenthesis())
                    {
                        compilation.append(token);
                    }
                    else
                    {
                        compilation.setRegex();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    }
                }
                else if (token == '/')
                {
                    if (!compilation.isEatWhite() && !compilation.isEscape())
                    {
                        throw compilation.ex(new DovetailException(UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION));
                    }
                    compilation.addExpression();
                    compilation.setState(CompilerState.SEPARATOR);
                }
                else if (token == ' ')
                {
                    if (!compilation.isEatWhite())
                    {
                        compilation.setRegex();
                        compilation.setState(CompilerState.SPRINTF);
                        compilation.startParenthesisMatching();
                    }
                }
                else
                {
                    compilation.append(token);
                }
                compilation.setEscapeIf('\\');
                compilation.setEatWhite();
                break;
            case SPRINTF:
                if (token == '(')
                {
                    compilation.openParenthesis();
                    if (compilation.isEscape())
                    {
                        compilation.backspace();
                    }
                    compilation.append(token);
                }
                else if (token == ')')
                {
                    if (compilation.closeParenthesis())
                    {
                        if (compilation.isEscape())
                        {
                            compilation.backspace();
                        }
                        compilation.append(token);
                    }
                    else
                    {
                        compilation.setFormat();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    }
                }
                else if (token == '/')
                {
                    if (compilation.isEscape())
                    {
                        compilation.backspace();
                        compilation.append(token);
                    }
                    else
                    {
                        throw compilation.ex(new DovetailException(UNESCAPED_FORWARD_SLASH_IN_FORMAT));
                    }
                }
                else if (token == ' ')
                {
                    if (!compilation.isEatWhite())
                    {
                        compilation.setFormat();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    }
                }
                else
                {
                    compilation.append(token);
                }
                compilation.setEscapeIf('%');
                compilation.setEatWhite();
                break;
            case LIMITS_OPEN:
                if (token == '?')
                {
                    compilation.setLimits(0, 1);
                    compilation.setState(CompilerState.COMPLETE);
                }
                else if (token == '+')
                {
                    compilation.setLimits(1, Integer.MAX_VALUE);
                    compilation.setMultiple(true);
                    compilation.setState(CompilerState.COMPLETE);
                }
                else if (token == '*')
                {
                    compilation.setLimits(0, Integer.MAX_VALUE);
                    compilation.setMultiple(true);
                    compilation.setState(CompilerState.COMPLETE);
                }
                else if (token == '[')
                {
                    compilation.setMultiple(true);
                    compilation.setState(CompilerState.LIMITS);
                }
                else if (token == '/')
                {
                    compilation.setLimits(1, 1);
                    compilation.addExpression();
                    compilation.setState(CompilerState.SEPARATOR);
                }
                else
                {
                    throw compilation.ex(new DovetailException(LIMIT_OR_SEPARATOR_EXPECTED));
                }
                     
                break;
            case LIMITS:
                if (Character.isDigit(token))
                {
                    compilation.append(token);
                }
                else if (token == ',')
                {
                    compilation.setMinimum();
                }
                else if (token == ']')
                {
                    compilation.setLimit();
                    compilation.setState(CompilerState.COMPLETE);
                }
                else
                {
                    throw compilation.ex(new DovetailException(INVALID_LIMIT_CHARACTER));
                }
                break;
            case COMPLETE:
                if (token != '/')
                {
                    throw compilation.ex(new DovetailException(PATH_SEPARATOR_EXPECTED));
                }
                compilation.addExpression();
                compilation.setState(CompilerState.SEPARATOR);
                break;
            }
        }
        switch (compilation.getState())
        {
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
            if (pattern.trim() == "/")
            {
                compilation.addLiteral();
            }
            else
            {
                throw compilation.ex(new DovetailException(UNEXPECTED_END_OF_GLOB_EXPESSION));
            }
        }
        return glob.extend(new Glob(compilation.getTests(), pattern, getMatchTests()));
    }

    /**
     * Convert the list of match tests to an array and clear the match test
     * list.
     * 
     * @return The list of match tests as an array.
     */
    private MatchTestServer[] getMatchTests()
    {
        MatchTestServer[] array = matchTestServers.toArray(new MatchTestServer[matchTestServers.size()]);
        matchTestServers.clear();
        return array;
    }
}
