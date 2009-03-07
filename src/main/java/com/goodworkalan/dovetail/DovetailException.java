/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.text.MessageFormat;
import java.util.ResourceBundle;

// TODO Document.
public class DovetailException
extends RuntimeException
{
    // TODO Document.
    private static final long serialVersionUID = 20080620L;

    // TODO Document.
    private final int code;
    
    // TODO Document.
    public DovetailException()
    {
        super(null, null);
        code = 0;
    }
    
    // TODO Document.
    public DovetailException(Throwable cause)
    {
        super(null, cause);
        code = 0;
    }

    // TODO Document.
    public DovetailException(int code, Object... arguments)
    {
        super(message(code, arguments));
        this.code = code;
    }
    
    // TODO Document.
    public DovetailException(int code, Throwable cause, Object... arguments)
    {
        super(message(code, arguments), cause);
        this.code = code;
    }
    
    // TODO Document.
    private static String message(Integer code, Object[] arguments)
    {
        ResourceBundle bundle = ResourceBundle.getBundle("exceptions");
        return MessageFormat.format(bundle.getString(code.toString()), arguments);
    }
    
    // TODO Document.
    public int getCode()
    {
        return code;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */