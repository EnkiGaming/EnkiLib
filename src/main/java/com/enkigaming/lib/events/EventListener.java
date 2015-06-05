package com.enkigaming.lib.events;

/**
 * Listener that gets registered to events in order to run arbitrary code when an event is raised. Immutable.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 * @param <T> The type of the eventargs object to be passed into the onEvent method, the type of eventargs used by the
 * event this gets registered to.
 */
public interface EventListener<T extends EventArgs>
{
    /**
     * The method called when the event this listener is registered to is raised.
     * @param sender The object on which the event was raised.
     * @param args The eventargs object passed to all event listeners of the event this method is being called by.
     */
    public void onEvent(Object sender, T args);
    //</editor-fold>
}