package com.enkigaming.lib.testing;

import static org.junit.Assert.fail;

public abstract class ExceptionAssertion
{
    public ExceptionAssertion(String message, Class<? extends Exception> exceptionType)
    {
        try
        { code(); }
        catch(Exception e)
        {
            if(!exceptionType.isInstance(e))
                fail(message + ": Exception other than specified exception (" + exceptionType.getSimpleName() + ") thrown.");

            return;
        }

        fail(message + ": " + "Specified exception (" + exceptionType.getSimpleName() + ") not thrown.");
    }

    public ExceptionAssertion(Class<? extends Exception> exceptionType)
    { this("", exceptionType); }
    
    // Asserts that one of any of the passed exceptions are thrown.
    public ExceptionAssertion(String message, Class<? extends Exception>... exceptionTypes)
    {
        String exceptionsListed = "";
        
        for(Class<Exception> i : (Class<Exception>[])exceptionTypes)
            exceptionsListed += i.getSimpleName() + ", ";
        
        exceptionsListed = exceptionsListed.substring(0, exceptionsListed.length() - ", ".length());
        
        try
        { code(); }
        catch(Exception e)
        {
            if(!exceptionIsSpecified(e, exceptionTypes))
                fail(message + ": Exception other than one of specified exceptions (" + exceptionsListed + ") thrown.");

            return;
        }

        fail(message + ": " + "No specified exception (" + exceptionsListed + ") thrown.");
    }
    
    public ExceptionAssertion(Class<? extends Exception>... exceptionTypes)
    { this("", exceptionTypes); }
    
    private boolean exceptionIsSpecified(Exception e, Class<? extends Exception>[] es)
    {
        for(Class<Exception> i : (Class<Exception>[])es)
            if(i.isInstance(e))
                return true;
        
        return false;
    }

    public abstract void code() throws Exception;
}