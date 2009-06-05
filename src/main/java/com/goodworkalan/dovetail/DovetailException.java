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
    
    /** Unexpected question mark at a given position. */
    public final static int UNEXPECTED_QUESION_MARK = 301;
    
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
    public DovetailException add(Object argument)
    {
        arguments.add(argument);
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
        ResourceBundle exceptions = ResourceBundle.getBundle("com.goodworkalan.sheaf.exceptions");
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