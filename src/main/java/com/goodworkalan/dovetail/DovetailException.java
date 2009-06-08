package com.goodworkalan.dovetail;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A general purpose exception that indicates that an error occurred in one of
 * the classes in the pack package.
 * <p>
 * The exception is created with an error code. A client programmer can use the
 * error code in a switch statement to respond to specific error conditions.
 * 
 * @author Alan Gutierrez
 */
public final class DovetailException
extends RuntimeException
{
    /** The serial version id. */
    private static final long serialVersionUID = 20070821L;
    
    /** A list of arguments to the formatted error message. */
    private final List<Object> arguments = new ArrayList<Object>();
    
    /** The compiler has been given an empty pattern. */
    public final static int EMPTY_PATTERN = 301;
    
    /** A pattern must begin with a forward slash. */
    public final static int FIRST_FORWARD_SLASH_MISSING = 302;
    
    /** Expecting an open parenthesis after a multi-level match specifier. */
    public final static int EXPECTING_OPEN_PARENTESIS = 303;
    
    public final static int IDENTIFER_MISSING = 304;

    /** Unexpected question mark at a given position. */
    public final static int EXPECTING_JAVA_IDENTIFIER_START = 305;

    // TODO Document.
    public final static int EXPECTING_JAVA_IDENTIFIER_PART = 306;
    
    // TODO Document.
    public final static int UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION = 307;

    // TODO Document.
    public final static int CANNOT_PARSE_REGULAR_EXPESSION = 308;
    
    // TODO Document.
    public final static int INVALID_LIMIT_CHARACTER = 309;
    
    // TODO Document.
    public final static int UNEXPECTED_COMMA_IN_LIMIT = 310;

    // TODO Document.
    public final static int MINIMUM_LIMIT_REQUIRED = 311;
    
    // TODO Document.
    public final static int CANNOT_PARSE_LIMIT_VALUE = 312;
    
    // TODO Document.
    public final static int CANNOT_SPECIFY_LIMITS_ON_EXACTLY_ONE = 313;
    
    // TODO Document.
    public final static int CLOSING_BRACKED_EXPECTED = 314;
    
    // TODO Document.
    public final static int PATH_SEPARATOR_EXPECTED = 315;
    
    // TODO Document.
    public final static int MISMATCHED_IDENTIFIERS = 401;
    
    // TODO Document.
    public final static int MATCH_TEST_CONSTRUCTOR_THREW_EXCEPTION = 501;

    // TODO Document.
    public final static int MATCH_TEST_CONSTRUCTOR_NOT_VISIBLE = 502;
    
    // TODO Document.
    public final static int FORMAT_PARAMETER_IS_NULL = 601;
    
    /** Have not gotten around to creating a meaninful error message. */
    public final static int USELESS_ERROR_CODE = 0;
    
    /** The error code. */
    private final int code;

    /**
     * Create an exception with the given error code.
     * 
     * @param code
     *            The error code.
     */
    public DovetailException(int code)
    {
        super(Integer.toString(code));
        this.code = code;
    }

    /**
     * Create an exception with the given error code that wraps the given causal
     * exception.
     * 
     * @param code
     *            The error code.
     * @param cause
     *            The wrapped exception.
     */
    public DovetailException(int code, Throwable cause)
    {
        super(Integer.toString(code), cause);
        this.code = code;
    }

    /**
     * Get the error code.
     * 
     * @return The error code.
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Add an argument to the list of arguments to provide the formatted error
     * message associated with the error code.
     * 
     * @param argument
     *            The format argument.
     * @return This sheaf exception for chained invocation of add.
     */
    public DovetailException add(Object...args)
    {
        for (Object argument : args)
        {
            arguments.add(argument);
        }
        return this;
    }

    /**
     * Create an detail message from the error message format associated with
     * the error code and the format arguments.
     * 
     * @return The exception message.
     */
    @Override
    public String getMessage()
    {
        String key = Integer.toString(code);
        ResourceBundle exceptions = ResourceBundle.getBundle("com.goodworkalan.dovetail.exceptions");
        String format;
        try
        {
            format = exceptions.getString(key);
        }
        catch (MissingResourceException e)
        {
            return key;
        }
        try
        {
            return String.format(format, arguments.toArray());
        }
        catch (Throwable e)
        {
            throw new Error(key, e);
        }
    }
}