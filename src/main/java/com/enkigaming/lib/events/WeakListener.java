package com.enkigaming.lib.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the listener to be weakly stored. That is, being registered to an event does/should not stop this event
 * listener from being marked for deletion, whereupon the listener will no longer be registered to the event.
 * 
 * This annotation should be applied to the onEvent(Object, T) method. Functionality is undefined where this annotation
 * is applied to other methods in the listener. (where other methods are added)
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WeakListener
{
    /* Nothin' here */
}