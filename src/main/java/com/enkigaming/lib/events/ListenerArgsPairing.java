package com.enkigaming.lib.events;

// I could have made this generic, but it really doesn't matter since it's only ever referred to in-code using the
// widest restriction of what the type param would be. It just makes things simpler not having this generic.

/**
 * A pair, encapsulating an event listener and event args. Intended to match event listeners to the event args that
 * should be passed to them during an event raise.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
public class ListenerArgsPairing
{
    /**
     * Constructor.
     * @param listener The EventListener to be held in this pairing.
     * @param args The EventArgs to be held in this pairing.
     */
    public ListenerArgsPairing(EventListener<? extends EventArgs> listener, EventArgs args)
    {
        this.listener = listener;
        this.args = args;
    }

    final private EventListener<? extends EventArgs> listener;
    final private EventArgs args;

    /**
     * Gets the EventListener object held in this pairing.
     * @return The EventListener.
     */
    public EventListener<? extends EventArgs> getListener()
    { return listener; }

    /**
     * Gets the EventArgs object held in this pairing.
     * @return The EventArgs.
     */
    public EventArgs getArgs()
    { return args; }
}