package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.CANNOT_PARSE_LIMIT_VALUE;
import static com.goodworkalan.dovetail.DovetailException.CANNOT_PARSE_REGULAR_EXPESSION;
import static com.goodworkalan.dovetail.DovetailException.EXPECTING_JAVA_IDENTIFIER_PART;
import static com.goodworkalan.dovetail.DovetailException.EXPECTING_JAVA_IDENTIFIER_START;
import static com.goodworkalan.dovetail.DovetailException.IDENTIFER_MISSING;
import static com.goodworkalan.dovetail.DovetailException.MINIMUM_LIMIT_REQUIRED;
import static com.goodworkalan.dovetail.DovetailException.UNEXPECTED_COMMA_IN_LIMIT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class Compilation
{
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
    private final List<Test> tests;
    
    /** The compiler state. */
    private CompilerState state;

    private boolean eatWhite;
    
    private boolean escape;
    
    private boolean deep;
    
    private int min;
    
    private int max;
    
    private int parenthesis;

    private int index;

    public Compilation(String glob)
    {
        this.glob = glob;
        this.capture = new StringBuilder();
        this.tests = new ArrayList<Test>();
        this.state = CompilerState.SEPARATOR;
        this.identifiers = new ArrayList<String>();
        this.index = 1;
        this.min = -1;
        this.max = -1;

        // First test is always an empty string literal.
        tests.add(new Literal(""));
    }
    
    public boolean hasMoreTokens()
    {
        return index < glob.length();
    }
    
    public char nextToken()
    {
        return glob.charAt(index++);
    }
    
    public DovetailException ex(DovetailException e)
    {
        return e.add(glob, index); 
    }
    
    public void setState(CompilerState state)
    {
        this.state = state;
    }
    
    public CompilerState getState()
    {
        return state;
    }
    
    public void setExactlyOne()
    {
        min = max = 1;
    }
    
    public boolean isExactlyOne()
    {
        return min == 1 && max == 1;
    }
    
    public boolean isEatWhite()
    {
        return eatWhite;
    }
    
    public void setEatWhite()
    {
        this.eatWhite = glob.charAt(index - 1) == ' ';
    }
    
    public boolean isEscape()
    {
        return escape;
    }

    public void setEscapeIf(char escaper)
    {
        escape = glob.charAt(index - 1) == escaper;
    }
    
    public void startParenthesisMatching()
    {
        eatWhite = true;
        parenthesis = 0;
    }
    
    public void openParenthesis()
    {
        if (!escape)
        {
            parenthesis++;
        }
    }
    
    public boolean closeParenthesis()
    {
        return escape || parenthesis-- != 0;
    }
    
    public void setRegex()
    {
        try
        {
            regex = Pattern.compile(capture.toString());
        }
        catch (PatternSyntaxException e)
        {
            throw ex(new DovetailException(CANNOT_PARSE_REGULAR_EXPESSION));
        }
        capture.setLength(0);
    }
    
    public void setSprintf()
    {
        sprintf = capture.toString();
        capture.setLength(0);
    }

    public void assertIdentifierCharacter(char token)
    {
        if (capture.length() == 0 && !Character.isJavaIdentifierStart(token))
        {
            throw ex(new DovetailException(EXPECTING_JAVA_IDENTIFIER_START));
        }
        else if (capture.length() > 0 && !Character.isJavaIdentifierPart(token))
        {
            throw ex(new DovetailException(EXPECTING_JAVA_IDENTIFIER_PART));
        }
    }

    public void setDeep(boolean deep)
    {
        this.deep = deep;
    }
    
    public void addLiteral()
    {
        tests.add(new Literal(capture.toString()));
        capture.setLength(0);
    }
    
    public void append(char token)
    {
        capture.append(token);
    }
    
    public void addIdentifier()
    {
        if (capture.length() == 0)
        {
            throw ex(new DovetailException(IDENTIFER_MISSING));
        }
        identifiers.add(capture.toString());
        capture.setLength(0);
    }

    public void addExpression()
    {
        if (regex == null)
        {
            regex = Pattern.compile(deep ? "(.*)/" : ".*");
        }
        if (sprintf == null)
        {
            sprintf = "%s";
        }
        if (min == -1)
        {
            min = 1;
        }
        if (max == -1)
        {
            max = Integer.MAX_VALUE;
        }
        Expression expression = new Expression(new ArrayList<String>(identifiers), regex, sprintf, min, max, deep);
        tests.add(expression);
        identifiers.clear();
        regex = null;
        sprintf = null;
        deep = false;
        min = -1;
        max = -1;
        parenthesis = 0;
    }
    
    public void setMinimum()
    {
        if (min != -1)
        {
            throw new DovetailException(UNEXPECTED_COMMA_IN_LIMIT);
        }
        if (capture.length() == 0)
        {
            throw new DovetailException(MINIMUM_LIMIT_REQUIRED);
        }
        try
        {
            min = Integer.parseInt(capture.toString());
        }
        catch (NumberFormatException e)
        {
            throw new DovetailException(CANNOT_PARSE_LIMIT_VALUE, e);
        }
        capture.setLength(0);
    }
    
    public void setLimit()
    {
        if (min == -1)
        {
            setMinimum();
            max = min;
        }
        else
        {
            if (capture.length() == 0)
            {
                max = Integer.MAX_VALUE;
            }
            else
            {
                try
                {
                    max = Integer.parseInt(capture.toString());
                }
                catch (NumberFormatException e)
                {
                    throw new DovetailException(CANNOT_PARSE_LIMIT_VALUE, e);
                }
            }
        }
        capture.setLength(0);
    }
    
    public Test[] getTests()
    {
        return tests.toArray(new Test[tests.size()]);
    }
}