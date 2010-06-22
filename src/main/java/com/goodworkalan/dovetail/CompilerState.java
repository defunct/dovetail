package com.goodworkalan.dovetail;

/**
 * Indicates the current compiler state while looping over the characters in a
 * path pattern.
 * 
 * @author Alan Gutierrez
 */
enum CompilerState {
    /** Expecting a path separator. */
    SEPARATOR,
    /** Reading a literal path part. */ 
    LITERAL,
    /** Reading a capturing part. */
    CAPTURE,
    /** Reading a capturing part identifier list. */
    IDENTIFIERS,
    /** Reading a capturing part regular expression. */
    REGEX,
    /** Reading a capturing part reassembly sprintf pattern. */
    SPRINTF,
    /** Expecting the opening of a capturing part limit definition. */
    LIMITS_OPEN,
    /** Reading the limits of a capturing part. */
    LIMITS,
    /** Expecting the close of a capturing part limit definition. */
    LIMITS_CLOSE,
    /** Expecting the end of a part. */
    COMPLETE 
}