package com.enkigaming.lib.events.exceptions;

public class EventArgsMultipleUseException extends RuntimeException
{
    public EventArgsMultipleUseException() { super(); }

    public EventArgsMultipleUseException(String message) { super(message); }

    public EventArgsMultipleUseException(String message, Throwable cause) { super(message, cause); }

    public EventArgsMultipleUseException(Throwable cause) { super(cause); }
}