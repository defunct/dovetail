package com.goodworkalan.dovetail;

// TODO Document.
enum CompilerState {
    SEPARATOR, LITERAL, CAPTURE, IDENTIFIERS, REGEX, SPRINTF, LIMITS_OPEN, LIMITS, LIMITS_CLOSE, COMPLETE 
}