package com.enkigaming.lib.events;

/**
 * Single-method class responsible for generating an eventargs object from another for passing to the listeners of a
 * dependent event.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 * @param <ParentArgs> The type of the eventargs object being used to generate the child args.
 * @param <ChildArgs> The type of the eventargs object being generated.
 */
public interface DependentEventArgsGetter<ParentArgs extends EventArgs, ChildArgs extends EventArgs>
{
    /**
     * Generated a dependent eventargs object using the passed eventargs object, and possibly the sender.
     * @param sender The object on which the event was raised.
     * @param parentArgs The eventargs object being used to generate another eventargs object.
     * @return The generated eventargs object.
     */
    public abstract ChildArgs getDependentArgs(Object sender, ParentArgs parentArgs);
}