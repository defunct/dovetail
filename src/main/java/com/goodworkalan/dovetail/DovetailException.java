/* Copyright Alan Gutierrez 2006 */
package com.goodworkalan.dovetail;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class DovetailException
extends RuntimeException
{
    private static final long serialVersionUID = 20080620L;

    private final int code;
    
    public DovetailException()
    {
        super(null, null);
        code = 0;
    }
    
    public DovetailException(Throwable cause)
    {
        super(null, cause);
        code = 0;
    }

    public DovetailException(int code, Object... arguments)
    {
        super(message(code, arguments));
        this.code = code;
    }
    
    public DovetailException(int code, Throwable cause, Object... arguments)
    {
        super(message(code, arguments), cause);
        this.code = code;
    }
    
    private static String message(Integer code, Object[] arguments)
    {
        ResourceBundle bundle = ResourceBundle.getBundle("exceptions");
        return MessageFormat.format(bundle.getString(code.toString()), arguments);
    }
    
    public int getCode()
    {
        return code;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */