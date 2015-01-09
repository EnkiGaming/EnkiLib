package com.enkigaming.lib.events;

import java.util.Collection;
import java.util.Map;

/**
 * The observer pattern encapsulated into a class, designed to behave similarly to C#'s style of events while adding
 * features not present in C# events like event dependency and priorities and adhering to Java best-practice.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 * @param <T> The type of the event args objects intended to be passed to registered event listeners on an event raise.
 */
public interface Event<T extends EventArgs>
{
    //<editor-fold defaultstate="collapsed" desc="Accessors">
    /**
     * Gets events that will be automatically fired when this event is fired.
     * @param includeThis Whether or not to include this event in the returned collection.
     * @param includeDependantsCascadingly Whether or not to include events that are dependent on dependants of this event.
     * @return A collection containing the events that fire when this is fired.
     */
    public Collection<Event<? extends EventArgs>> getDependentEvents(boolean includeThis, boolean includeDependantsCascadingly);
    
    /**
     * Gets all events that will be automatically fired when this event is fired.
     * @return A collection containing the events that fire when this is fired.
     */
    public Collection<Event<? extends EventArgs>> getDependentEvents();
    
    /**
     * Gets events that will be automatically fired specifically when this event is fired, and not ones that will be
     * fired indirectly as a result of this event being fired, such as ones that automatically fire when another is
     * fired that in turn automatically fires when this is fired.
     * @return A collection containing the events that fire specifically when this is fired.
     */
    public Collection<Event<? extends EventArgs>> getDirectlyDependentEvents();
    
    /**
     * Gets this, and all events that will be automatically fired when this event is fired.
     * @return A collection containing this and the events that fire when this is fired.
     */
    public Collection<Event<? extends EventArgs>> getThisAndDependentEvents();
    
    /**
     * Gets this, as well as events that will be automatically fired specifically when this event is fired, and not ones that will be
     * fired indirectly as a result of this event being fired, such as ones that automatically fire when another is
     * fired that in turn automatically fires when this is fired.
     * @return A collection containing this and the events that fire specifically when this is fired.
     */
    public Collection<Event<? extends EventArgs>> getThisAndDirectlyDependentEvents();
    
    /**
     * Gets directly dependent events and the getters for generating required args from args passed to raises of this event.
     * @return A map containing the events and their matching DependentEventArgsGetters.
     */
    public Map<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>> getDirectlyDependentEventsAndArgsGetters();
    
    /**
     * Gets all event listeners registered to this event.
     * @return A collection containing registered event listeners.
     */
    public Collection<EventListener<T>> getListeners();
    
    /**
     * Gets listeners of dependent events.
     * @param includeListenersOfThis Include listeners of this event.
     * @param includeDependantsCascadingly Include listeners of events that are indirectly dependent on this one.
     * @return A collection containing listeners of dependent events.
     */
    public Collection<EventListener<? extends EventArgs>> getDependentListeners(boolean includeListenersOfThis, boolean includeDependantsCascadingly);
    
    /**
     * Gets listeners of all dependent events.
     * @return A collection containing the listeners of all dependent events, including indirectly dependent events.
     */
    public Collection<EventListener<? extends EventArgs>> getDependentListeners();
    
    /**
     * Gets listeners of events directly dependent on this event.
     * @return A collection containing the listeners of events specifically dependent on this one.
     */
    public Collection<EventListener<? extends EventArgs>> getDirectlyDependentListeners();
    
    /**
     * Gets listeners of this and all dependent events.
     * @return A collection containing the listeners of this event and all events dependent on this one.
     */
    public Collection<EventListener<? extends EventArgs>> getThisAndDependentEventArgs();
    
    /**
     * Gets listeners of this and directly dependent events.
     * @return A collection containing the listeners of this event and events specifically dependent on this one.
     */
    public Collection<EventListener<? extends EventArgs>> getThisAndDirectlyDependentListeners();
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Raise Methods">
    /**
     * Calls the onEvent methods on all event listeners registered to this event with a priority of less the post-event
     * priority.
     *
     * For implementations: Marks the event args used as using pre-event before doing anything with it and used
     * pre-event once it's finished. Should also store a queue containing listeners to be called on the event-args for
     * access in the post-event raise.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     */
    public void raise(Object sender, T args);
    
    /**
     * Calls the onEvent methods on all event listeners registered to this event with a priority of or greater than the
     * post-event priority.
     *
     * For implementations: Marks the event args used as using post-event before doing anything with it and used
     * post-event once it's finished. Can access the listener queue stored in the passed args. The same generated args
     * objects should be used as were used post-event, and it's recommended to access them via the stored listener
     * queue.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     */
    public void raisePostEvent(Object sender, T args);
    
    /**
     * Raises multiple other events while at the same time raising this one. All events are raised together, alongside
     * all dependent events, and event listener priorities are respected.
     *
     * For implementations: Marks all event args used in this as using pre-event before doing anything with them and
     * used pre-event once it's finished. Should store the listener queues for each set of args and their dependent
     * args in the master args to be collated, so that this method can be called from any of the events and, as long as
     * all of the same events used are passed in barring the one the method is being called on, the results should be
     * the same.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvent The event to raise alongside this one and the args to pass to it.
     */
    public void raiseAlongside(Object sender, T args, EventWithArgs<? extends EventArgs> otherEvent);
    
    /**
     * Raises multiple other events while at the same time raising this one. All events are raised together, alongside
     * all dependent events, and event listener priorities are respected.
     *
     * For implementations: Marks all event args used in this as using pre-event before doing anything with them and
     * used pre-event once it's finished. Should store the listener queues for each set of args and their dependent
     * args in the master args to be collated, so that this method can be called from any of the events and, as long as
     * all of the same events used are passed in barring the one the method is being called on, the results should be
     * the same.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvents The events to raise alongside this one and the args to pass to them.
     */
    public void raiseAlongside(Object sender, T args, EventWithArgs<? extends EventArgs>... otherEvents);
    
    /**
     * Raises multiple other events while at the same time raising this one. All events are raised together, alongside
     * all dependent events, and event listener priorities are respected.
     *
     * For implementations: Marks all event args used in this as using pre-event before doing anything with them and
     * used pre-event once it's finished. Should store the listener queues for each set of args and their dependent
     * args in the master args to be collated, so that this method can be called from any of the events and, as long as
     * all of the same events used are passed in barring the one the method is being called on, the results should be
     * the same.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvents The events to raise alongside this one and the args to pass to them.
     */
    public void raiseAlongside(Object sender, T args, Collection<EventWithArgs<? extends EventArgs>> otherEvents);
    
    /**
     * Raises multiple other events while at the same time raising this one. All events are raised together, alongside
     * all dependent events, and event listener priorities are respected.
     *
     * For implementations: Marks all event args used in this as using pre-event before doing anything with them and
     * used pre-event once it's finished. Should store the listener queues for each set of args and their dependent
     * args in the master args to be collated, so that this method can be called from any of the events and, as long as
     * all of the same events used are passed in barring the one the method is being called on, the results should be
     * the same.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param shareCancellation whether to share the cancellation state between the different passed events or not.
     * @param otherEvent The event to raise alongside this one and the args to pass to it.
     */
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, EventWithArgs<? extends EventArgs> otherEvent);
    
    /**
     * Raises multiple other events while at the same time raising this one. All events are raised together, alongside
     * all dependent events, and event listener priorities are respected.
     *
     * For implementations: Marks all event args used in this as using pre-event before doing anything with them and
     * used pre-event once it's finished. Should store the listener queues for each set of args and their dependent
     * args in the master args to be collated, so that this method can be called from any of the events and, as long as
     * all of the same events used are passed in barring the one the method is being called on, the results should be
     * the same.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param shareCancellation whether to share the cancellation state between the different passed events or not.
     * @param otherEvents The events to raise alongside this one and the args to pass to them.
     */
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, EventWithArgs<? extends EventArgs>... otherEvents);
    
    /**
     * Raises multiple other events while at the same time raising this one. All events are raised together, alongside
     * all dependent events, and event listener priorities are respected.
     *
     * For implementations: Marks all event args used in this as using pre-event before doing anything with them and
     * used pre-event once it's finished. Should store the listener queues for each set of args and their dependent
     * args in the master args to be collated, so that this method can be called from any of the events and, as long as
     * all of the same events used are passed in barring the one the method is being called on, the results should be
     * the same.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param shareCancellation whether to share the cancellation state between the different passed events or not.
     * @param otherEvents The events to raise alongside this one and the args to pass to them.
     */
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, Collection<EventWithArgs<? extends EventArgs>> otherEvents);
    
    /**
     * Raises multiple other events post-event while at the same time raising this one. All events are raised together,
     * alongside all dependent events, and event listener priorities are respected.
     *
     * for implementations: Marks all event args used in this as using post-event before doing anything with them and
     * used post-event once it's finished. The listener queues stored on each passed event args should hold the
     * listeners previously called by the event arg's pre-event raise, including those of dependent event args.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvent The event to raise alongside this one and the args to pass to it.
     */
    public void raisePostEventAlongside(Object sender, T args, EventWithArgs<? extends EventArgs> otherEvent);
    
    /**
     * Raises multiple other events post-event while at the same time raising this one. All events are raised together,
     * alongside all dependent events, and event listener priorities are respected.
     *
     * for implementations: Marks all event args used in this as using post-event before doing anything with them and
     * used post-event once it's finished. The listener queues stored on each passed event args should hold the
     * listeners previously called by the event arg's pre-event raise, including those of dependent event args.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvents The events to raise alongside this one and the args to pass to them.
     */
    public void raisePostEventAlongside(Object sender, T args, EventWithArgs<? extends EventArgs>... otherEvents);
    
    /**
     * Raises multiple other events post-event while at the same time raising this one. All events are raised together,
     * alongside all dependent events, and event listener priorities are respected.
     *
     * for implementations: Marks all event args used in this as using post-event before doing anything with them and
     * used post-event once it's finished. The listener queues stored on each passed event args should hold the
     * listeners previously called by the event arg's pre-event raise, including those of dependent event args.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvents The events to raise alongside this one and the args to pass to them.
     */
    public void raisePostEventAlongside(Object sender, T args, Collection<EventWithArgs<? extends EventArgs>> otherEvents);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Mutators">
    /**
     * Register an event listener to this event, such that its onEvent method will be called when this event is raised
     * through any of the available raise methods, passing in the object that raised the event as the sender, and an
     * EventArgs object containing relevant information pertaining to the event raise.
     * @param listener The listener to register.
     */
    public void register(EventListener<T> listener);
    
    /**
     * Registers an event, alongside a DependentEventArgsGetter to generate the required EventArgs object, as a
     * dependent event of this one. It will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id est not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param event The event being registered.
     * @param eventArgsGetter The object which, upon calling the getDependentArgs method in it during a raise, will
     * generate the required EventArgs object using the information from the EventArgs object passed to a raise of this
     * object.
     */
    public <TArgs extends EventArgs> void register(Event<TArgs> event, DependentEventArgsGetter<T, TArgs> eventArgsGetter);
    
    /**
     * Removes an event listener from this event, stopping its onEvent method from being called when this event is
     * raised in any manner, unless it is re-registered.
     * @param listener The event listener object to deregister.
     * @return True if an event listener was successfully deregistered from the event, false if it wasn't. (Such as
     * because the event listener wasn't registered to the event in order to be deregistered)
     */
    public boolean deregister(EventListener<T> listener);
    
    /**
     * Removes an event from this event as a dependant, stopping it from being raised alongside this one. (Except,
     * obviously, when manually raised alongside)
     * @param event The event to deregister.
     * @return True if an event was successfully deregistered from the event, false if it wasn't. (Such as because the
     * event wasn't registered as a dependant in order to be deregistered)
     */
    public boolean deregister(Event<? extends EventArgs> event);
    //</editor-fold>
}