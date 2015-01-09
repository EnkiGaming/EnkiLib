package com.enkigaming.lib.events;

import com.enkigaming.lib.exceptions.NullArgumentException;

/**
 * A pair, encapsulating an Event and EventArgs object into one object. Intended to match an event up to an eventargs
 * object that should be passed to one of the event's listeners on event raise. Immutable.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 * @param <Args> The type of the event args used by the event and eventargs objects.
 */
public class EventWithArgs<Args extends EventArgs>
{
    /**
     * Constructor. Does not handle null values.
     * @param event The event to include in this pairing.
     * @param args The eventargs to include in this pairing.
     */
    public EventWithArgs(Event<Args> event, Args args)
    {
        if(args == null)
            throw new NullArgumentException("Args");

        if(event == null)
            throw new NullArgumentException("Event");

        this.event = event;
        this.args = args;
    }

    /**
     * The event component on this pairing.
     */
    final private Event<Args> event;
    
    /**
     * The eventargs component of this pairing.
     */
    final private Args args;

    /**
     * Gets the Event object held in this pairing.
     * @return The Event.
     */
    public Event<Args> getEvent()
    { return event; }

    /**
     * Gets the EventArgs object held in this pairing.
     * @return The EventArgs.
     */
    public Args getArgs()
    { return args; }
}