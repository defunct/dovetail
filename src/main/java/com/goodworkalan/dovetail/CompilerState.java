package com.goodworkalan.dovetail;

// TODO Document.
enum CompilerState
{
    SEPARATOR, LITERAL, CAPTURE, PATTERN, IDENTIFIERS, REGEX, SPRINTF, LIMITS_OPEN, LIMITS, LIMITS_CLOSE, COMPLETE 
}