package com.solartis.test.exception;


public class PDFException extends Exception 
{
    private static final long serialVersionUID = 1L;
    
    public PDFException(String message)
	{
    	super (message);
	}
    
    public PDFException(Exception e) 
    {
        super(e);
    }

    public PDFException(String message, Exception e) 
    {
        super(message, e);
    }
}