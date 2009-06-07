package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.*;
import static com.goodworkalan.dovetail.DovetailException.EXPECTING_OPEN_PARENTESIS;
import static com.goodworkalan.dovetail.DovetailException.FIRST_FORWARD_SLASH_MISSING;
import static com.goodworkalan.dovetail.DovetailException.INVALID_LIMIT_CHARACTER;
import static com.goodworkalan.dovetail.DovetailException.UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION;

import java.util.ArrayList;
import java.util.List;

// TODO Document.
public final class GlobCompiler
{
    // FIXME This becomes a factory specific to this package.
    private final MatchTestFactory factory;
    
    private final List<MatchTest> matchTests;
    
    // TODO Document.
    public GlobCompiler(MatchTestFactory factory)
    {
        this.factory = factory;
        this.matchTests = new ArrayList<MatchTest>();
    }
    
    public GlobCompiler()
    {
        this(new SimpleMatchTestFactory());
    }
    
    public GlobCompiler test(Class<? extends MatchTest> matchTestClass)
    {
        matchTests.add(new FactoryBuiltMatchTest(factory, matchTestClass));
        return this;
    }
    
    public GlobCompiler test(MatchTest matchTest)
    {
        matchTests.add(matchTest);
        return this;
    }
    
    public Glob compile(String pattern)
    {
        if (pattern == null)
        {
            throw new NullPointerException();
        }
        if (pattern.length() == 0)
        {
            throw new DovetailException(EMPTY_PATTERN);
        }
        if (pattern.charAt(0) != '/')
        {
            throw new DovetailException(FIRST_FORWARD_SLASH_MISSING);
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
                    compilation.setState(CompilerState.PATTERN);
                    compilation.setDeep(true);
                }
                else if (token == '(')
                {
                    compilation.setState(CompilerState.IDENTIFIERS);
                    compilation.setExactlyOne();
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
            case PATTERN:
                if (token != '(')
                {
                    throw compilation.ex(new DovetailException(EXPECTING_OPEN_PARENTESIS));
                }
                else
                {
                    compilation.setState(CompilerState.IDENTIFIERS);
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
                    compilation.setState(CompilerState.REGEX);
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
                        compilation.setState(CompilerState.LIMITS_OPEN);
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
                        compilation.setSprintf();
                        compilation.setState(CompilerState.LIMITS_OPEN);
                        compilation.startParenthesisMatching();
                    }
                }
                else if (token == '/')
                {
                    compilation.addExpression();
                    compilation.setState(CompilerState.SEPARATOR);
                }
                else if (token == ' ')
                {
                    if (!compilation.isEatWhite())
                    {
                        compilation.setSprintf();
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
                if (token == '[')
                {
                    if (compilation.isExactlyOne())
                    {
                        throw compilation.ex(new DovetailException(CANNOT_SPECIFY_LIMITS_ON_EXACTLY_ONE));
                    }
                    compilation.setState(CompilerState.LIMITS);
                }
                else if (token == '/')
                {
                    compilation.addExpression();
                    compilation.setState(CompilerState.SEPARATOR);
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
            compilation.addExpression();
            break;
        case COMPLETE:
            compilation.addExpression();
            break;
        }
        return new Glob(compilation.getTests(), pattern, getMatchTests());
    }
    
    private MatchTest[] getMatchTests()
    {
        MatchTest[] array = matchTests.toArray(new MatchTest[matchTests.size()]);
        matchTests.clear();
        return array;
    }
}
