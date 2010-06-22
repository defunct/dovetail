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
public final class DovetailException extends RuntimeException {
    /** The serial version id. */
    private static final long serialVersionUID = 20070821L;

    /** A list of arguments to the formatted error message. */
    private final List<Object> arguments = new ArrayList<Object>();

    /** TODO Outgoing! The compiler has been given an empty pattern. */
    public final static int EMPTY_PATTERN = 301;

    /** A pattern must begin with a forward slash. */
    public final static int FIRST_FORWARD_SLASH_MISSING = 302;

    /** Unexpected empty path part. */
    public final static int EMPTY_PATH_PART = 303;

    public final static int IDENTIFER_MISSING = 304;

    /** Unexpected character while expecting a Java identifier start character. */
    public final static int JAVA_IDENTIFIER_START_EXPECTED = 305;

    /** Unexpected character while expecting a Java identifier part character. */
    public final static int JAVA_IDENTIFIER_PART_EXPECTED = 306;

    /** Unescaped forward slash encountered in regular expression of capturing part. */
    public final static int UNESCAPED_FORWARD_SLASH_IN_REGULAR_EXPEESSION = 307;

    /** Unable to parse the regular expression of a capturing part. */
    public final static int CANNOT_PARSE_REGULAR_EXPESSION = 308;

    /** Unexpected character while reading a capturing part limit definition. */
    public final static int INVALID_LIMIT_CHARACTER = 309;

    /** Unexpected comma while reading a capturing part limit definition. */
    public final static int UNEXPECTED_COMMA_IN_LIMIT = 310;

    /** The required minimum limit is unspecified. */
    public final static int MINIMUM_LIMIT_REQUIRED = 311;

    /** Unable to parse a limit value. */
    public final static int CANNOT_PARSE_LIMIT_VALUE = 312;

    /** Unexpected character while expecting a capturing part limit definition or path separator. */
    public final static int LIMIT_OR_SEPARATOR_EXPECTED = 313;

    /** Unexpected character while expecting a path separator. */
    public final static int PATH_SEPARATOR_EXPECTED = 316;

    /** Unescaped forward slash encountered in reassembly sprintf pattern of capturing part. */
    public final static int UNESCAPED_FORWARD_SLASH_IN_FORMAT = 317;

    /** Unexpected end of a path expression. */
    public final static int UNEXPECTED_END_OF_PATH_EXPESSION = 318;

    /** Reassembly parameter is null. */
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
    public DovetailException(int code) {
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
    public DovetailException(int code, Throwable cause) {
        super(Integer.toString(code), cause);
        this.code = code;
    }

    /**
     * Get the error code.
     * 
     * @return The error code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Add an argument to the list of arguments to provide the formatted error
     * message associated with the error code.
     * 
     * @param arguments
     *            The format arguments.
     * @return This sheaf exception for chained invocation of add.
     */
    public DovetailException add(Object... arguments) {
        for (Object argument : arguments) {
            this.arguments.add(argument);
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
    public String getMessage() {
       String key = Integer.toString(code);
        ResourceBundle exceptions = ResourceBundle.getBundle("com.goodworkalan.dovetail.exceptions");
        String format;
        try {
            format = exceptions.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
        try {
            return String.format(format, arguments.toArray());
        } catch (Throwable e) {
            throw new Error(key, e);
        }
    }
}
