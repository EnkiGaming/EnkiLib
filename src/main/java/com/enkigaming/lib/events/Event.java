package com.enkigaming.lib.events;

import com.enkigaming.lib.encapsulatedfunctions.Converger;
import com.enkigaming.lib.tuples.Pair;
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
    public Map<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>> getDirectlyDependentEventsAndArgsGetters();
    
    /**
     * Gets all event listeners registered to this event.
     * @return A collection containing registered event listeners.
     */
    public Collection<EventListener<T>> getListeners();
    
    /**
     * Gets all event listeners registered to this event, mapped with double representation of their listener
     * priorities.
     * @return A map, with the listeners as the keys and a double representing the listener priorities as the values.
     */
    public Map<EventListener<T>, Double> getListenersWithPriorities();
    
    /**
     * Gets listeners of dependent events.
     * @param includeListenersOfThis Include listeners of this event.
     * @param includeDependantsCascadingly Include listeners of events that are indirectly dependent on this one.
     * @return A collection containing listeners of dependent events.
     */
    public Collection<EventListener<? extends EventArgs>> getDependentListeners(boolean includeListenersOfThis, boolean includeDependantsCascadingly);
    
    /**
     * Gets listeners of all dependent events.
     * @return A collection containing the listeners of all dependent events, including indirectly dependent events,
     * not including this event's own listeners.
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
    public Collection<EventListener<? extends EventArgs>> getThisAndDependentListeners();
    
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
    
    //<editor-fold defaultstate="collapsed" desc="Raise Alongside">
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
    public void raiseAlongside(Object sender, T args, Pair<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvent);
    
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
    public void raiseAlongside(Object sender, T args, Pair<? extends Event<? extends EventArgs>, ? extends EventArgs>... otherEvents);
    
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
    public void raiseAlongside(Object sender, T args, Collection<? extends Pair<? extends Event<? extends EventArgs>, ? extends EventArgs>> otherEvents);
    
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
     * @param otherEvents The events to raise alongside this one (as the keys) and the args to pass to them. (as values)
     */
    public void raiseAlongside(Object sender, T args, Map<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvents);
    
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
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, Pair<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvent);
    
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
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, Pair<? extends Event<? extends EventArgs>, ? extends EventArgs>... otherEvents);
    
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
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, Collection<? extends Pair<? extends Event<? extends EventArgs>, ? extends EventArgs>> otherEvents);
    
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
     * @param otherEvents The events to raise alongside this one (as the keys) and the args to pass to them. (as values)
     */
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, Map<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvents);
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Raise Post-event Alongside">
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
    public void raisePostEventAlongside(Object sender, T args, Pair<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvent);
    
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
    public void raisePostEventAlongside(Object sender, T args, Pair<? extends Event<? extends EventArgs>, ? extends EventArgs>... otherEvents);
    
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
    public void raisePostEventAlongside(Object sender, T args, Collection<? extends Pair<? extends Event<? extends EventArgs>, ? extends EventArgs>> otherEvents);
    
    /**
     * Raises multiple other events post-event while at the same time raising this one. All events are raised together,
     * alongside all dependent events, and event listener priorities are respected.
     *
     * for implementations: Marks all event args used in this as using post-event before doing anything with them and
     * used post-event once it's finished. The listener queues stored on each passed event args should hold the
     * listeners previously called by the event arg's pre-event raise, including those of dependent event args.
     * @param sender The object in which the event was raised.
     * @param args The object encapsulating relevant properties of the event raise.
     * @param otherEvents The events to raise alongside this one (as the keys) and the args to pass to them. (as values)
     */
    public void raisePostEventAlongside(Object sender, T args, Map<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvents);
//</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Mutators">
    //<editor-fold defaultstate="collapsed" desc="Listener Registration">
    /**
     * Registers an event listener to this event, such that its onEvent method will be called when this event is raised
     * through any of the available raise methods, passing in the object that raised the event as the sender, and an
     * EventArgs object containing relevant information pertaining to the event raise.
     * @param listener The listener to register.
     */
    public void register(EventListener<T> listener);
    
    /**
     * Registers an array of event listeners to this event, such that their onEvent methods will be called when this
     * event is raised through any of the available raise methods, passing in the object that raise the event as the
     * sender, and an EventArgs object containing relevant information pertaining to the event raise.
     * @param listeners The listeners to register.
     */
    public void register(EventListener<T>... listeners);
    
    /**
     * Registers a collection of event listeners to this event, such that their onEvent methods will be called when this
     * event is raised through any of the available raise methods, passing in the object that raises the event as the
     * sender, and an EventArgs object containing relevant information pertaining to the event raise.
     * @param listeners The listeners to register.
     */
    public void register(Collection<EventListener<T>> listeners);
    
    /**
     * Registers an event listener to this event with the specified listener priority (as defined in the
     * ListenerPriority enum), such that its onEvent method will be called when this event is raised through any of the
     * available raise methods, passing in the object that raised the event as the sender, and an EventArgs object
     * containing relevant information pertaining to the event raise.
     * @param listener The listener to register.
     * @param priority The priority at which to register the listener, where lower is earlier and higher is later, as
     * defined in the ListenerPriority enum.
     */
    public void register(EventListener<T> listener, double priority);
    
    /**
     * Registers an event listener to this event with the specified listener priority, such that its onEvent method will
     * be called when this event is raised through any of the available raise methods, passing in the object that raised
     * the event as the sender, and an EventArgs object containing relevant information pertaining to the event raise.
     * @param listener The listener to register.
     * @param priority The priority at which to register the listener.
     */
    public void register(EventListener<T> listener, ListenerPriority priority);
    
    /**
     * Registers an array of event listeners to this event with this specified listener priority (as defined in the
     * ListenerPriority enum), such that their onEvent methods will be called when this event is raised through any of
     * the available raise methods, passing in the object that raised the event as the sender, and an EventArgs object
     * containing the relevant information pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners, where lower is earlier and higher is later, as
     * defined in the ListenerPriority enum.
     */
    public void register(EventListener<T>[] listeners, double priority);
    
    /**
     * Registers an array of event listeners to this event with this specified listener priority, such that their
     * onEvent methods will be called when this event is raised through any of the available raise methods, passing in
     * the object that raised the event as the sender, and an EventArgs object containing the relevant information
     * pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners.
     */
    public void register(EventListener<T>[] listeners, ListenerPriority priority);
    
    /**
     * Registers a collection of event listeners to this event with this specified listener priority (as defined in the
     * ListenerPriority enum), such that their onEvent methods will be called when this event is raised through any of
     * the available raise methods, passing in the object that raised the event as the sender, and an EventArgs object
     * containing the relevant information pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners, where lower is earlier and higher is later, as
     * defined in the ListenerPriority enum.
     */
    public void register(Collection<EventListener<T>> listeners, double priority);
    
    /**
     * Registers a collection of event listeners to this event with this specified listener priority, such that their
     * onEvent methods will be called when this event is raised through any of the available raise methods, passing in
     * the object that raised the event as the sender, and an EventArgs object containing the relevant information
     * pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners.
     */
    public void register(Collection<EventListener<T>> listeners, ListenerPriority priority);
    
    /**
     * Registers an event listener to this event with the specified listener priority (as defined in the
     * ListenerPriority enum), such that its onEvent method will be called when this event is raised through any of the
     * available raise methods, passing in the object that raised the event as the sender, and an EventArgs object
     * containing relevant information pertaining to the event raise.
     * @param listener The listener to register.
     * @param priority The priority at which to register the listener, where lower is earlier and higher is later, as
     * defined in the ListenerPriority enum.
     */
    public void register(double priority, EventListener<T> listener);
    
    /**
     * Registers an event listener to this event with the specified listener priority, such that its onEvent method will
     * be called when this event is raised through any of the available raise methods, passing in the object that raised
     * the event as the sender, and an EventArgs object containing relevant information pertaining to the event raise.
     * @param listener The listener to register.
     * @param priority The priority at which to register the listener.
     */
    public void register(ListenerPriority priority, EventListener<T> listener);
    
    /**
     * Registers an array of event listeners to this event with this specified listener priority, such that their
     * onEvent methods will be called when this event is raised through any of the available raise methods, passing in
     * the object that raised the event as the sender, and an EventArgs object containing the relevant information
     * pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners, where lower is earlier and higher is later, as
     * defined in the ListenerPriority enum.
     */
    public void register(double priority, EventListener<T>... listeners);
    
    /**
     * Registers an array of event listeners to this event with this specified listener priority, such that their
     * onEvent methods will be called when this event is raised through any of the available raise methods, passing in
     * the object that raised the event as the sender, and an EventArgs object containing the relevant information
     * pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners.
     */
    public void register(ListenerPriority priority, EventListener<T>... listeners);
    
    /**
     * Registers a collection of event listeners to this event with this specified listener priority (as defined in the
     * ListenerPriority enum), such that their onEvent methods will be called when this event is raised through any of
     * the available raise methods, passing in the object that raised the event as the sender, and an EventArgs object
     * containing the relevant information pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners, where lower is earlier and higher is later, as
     * defined in the ListenerPriority enum.
     */
    public void register(double priority, Collection<EventListener<T>> listeners);
    
    /**
     * Registers a collection of event listeners to this event with this specified listener priority, such that their
     * onEvent methods will be called when this event is raised through any of the available raise methods, passing in
     * the object that raised the event as the sender, and an EventArgs object containing the relevant information
     * pertaining to the event raise.
     * @param listeners The listeners to register.
     * @param priority The priority at which to register the listeners.
     */
    public void register(ListenerPriority priority, Collection<EventListener<T>> listeners);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Dependent Event Registration">
    /**
     * Registers an event, alongside an event args getter to generate the required EventArgs object, as a dependent
     * event of this one. It will then be raised (calling the onEvent method of all of its registered listeners) when
     * this is raised, with event listeners being called according to priority alongside (id est not before or after)
     * the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param event The event being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     */
    public <TArgs extends EventArgs> void register(Event<TArgs> event, Converger<Object, T, TArgs> eventArgsGetter);
    
    /**
     * Registers an event, alongside an event args getter to generate the required EventArgs object, as a dependent
     * event of this one. It will then be raised (calling the onEvent method of all of its registered listeners) when
     * this is raised, with event listeners being called according to priority alongside (id est not before or after)
     * the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param event The event being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter, Event<TArgs> event);
    
    /**
     * Registers an array of events, alongside an event args getter to generate the required EventArgs objects, as
     * dependent events of this one. They will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id east not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the events being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs objects using the information from the EventArgs object passed to a raise of this object.
     * @param events The events being registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<? extends TArgs>... events);
    
    /**
     * Registers a collection of events, alongside an event args getter to generate the required EventArgs objects, as
     * dependent events of this one. They will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id east not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the events being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs objects using the information from the EventArgs object passed to a raise of this object.
     * @param events The events being registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Collection<? extends Event<? extends TArgs>> events);
    
    /**
     * Registers an event, alongside an event args getter to generate the required EventArgs object, as a dependent
     * event of this one. It will then be raised (calling the onEvent method of all of its registered listeners) when
     * this is raised, with event listeners being called according to priority alongside (id est not before or after)
     * the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param event The event being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Event<TArgs> event,
                                                   Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered);
    
    /**
     * Registers an event, alongside an event args getter to generate the required EventArgs object, as a dependent
     * event of this one. It will then be raised (calling the onEvent method of all of its registered listeners) when
     * this is raised, with event listeners being called according to priority alongside (id est not before or after)
     * the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param event The event being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<TArgs> event,
                                                   boolean stronglyRegistered);
    
    /**
     * Registers an event, alongside an event args getter to generate the required EventArgs object, as a dependent
     * event of this one. It will then be raised (calling the onEvent method of all of its registered listeners) when
     * this is raised, with event listeners being called according to priority alongside (id est not before or after)
     * the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param event The event being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Event<TArgs> event);
    
    /**
     * Registers an array of events, alongside an event args getter to generate the required EventArgs objects, as
     * dependent events of this one. They will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id est not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param events The events being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Event<? extends TArgs>... events);
    
    /**
     * Registers a collection of events, alongside an event args getter to generate the required EventArgs objects, as
     * dependent events of this one. They will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id est not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param events The events being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Collection<? extends Event<? extends TArgs>> events);
    
    /**
     * Registers an array of events, alongside an event args getter to generate the required EventArgs objects, as
     * dependent events of this one. They will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id est not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param events The events being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<? extends TArgs>[] events,
                                                   boolean stronglyRegistered);
    
    /**
     * Registers a collection of events, alongside an event args getter to generate the required EventArgs objects, as
     * dependent events of this one. They will then be raised (calling the onEvent method of all of its registered
     * listeners) when this is raised, with event listeners being called according to priority alongside (id est not
     * before or after) the event listeners of this event.
     * @param <TArgs> The type of the EventArgs used by the event being registered.
     * @param events The events being registered.
     * @param eventArgsGetter The converger which, upon calling the get method in it during a raise, will generate the
     * required EventArgs object using the information from the EventArgs object passed to a raise of this object.
     * @param stronglyRegistered Whether or not the event should be strongly registered. If false, the event will be
     * weakly registered, and will be deregistered from the event when the Java GC decides to collect the event being
     * registered.
     */
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Collection<? extends Event<? extends TArgs>> events,
                                                   boolean stronglyRegistered);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Listener Deregistration">
    /**
     * Removes an event listener from this event, stopping its onEvent method from being called when this event is
     * raised in any manner, unless it is re-registered.
     * @param listener The event listener object to deregister.
     * @return The listener deregistered as a result of this method call, or null if no listener was removed.
     */
    public EventListener<T> deregister(EventListener<T> listener);
    
    /**
     * Removes an array of event listeners from this event, stopping their onEvent methods from being called when this
     * event is raised in any manner, unless they're re-registered.
     * @param listeners The event listener objects to deregister.
     * @return The listeners deregistered as a result of this method call, in a collection.
     */
    public Collection<EventListener<T>> deregister(EventListener<T>... listeners);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Dependent Event Deregistration">
    /**
     * Removes an event from this event as a dependant, stopping it from being raised alongside this one. (Except,
     * obviously, when manually raised alongside)
     * @param event The event to deregister.
     * @return The event deregistered as a result of this method call, or null if no event was deregistered.
     */
    public Event<? extends EventArgs> deregister(Event<? extends EventArgs> event);
    
    /**
     * Removes an array of events from this event's dependants, stopping them from being raised alongside this one
     * whenever this one is raised. (Except, obviously, when manually raised alongside)
     * @param events The events to deregister.
     * @return The events deregistered as a result of this method call, in a collection.
     */
    public Collection<Event<? extends EventArgs>> deregister(Event<? extends EventArgs>... events);
    //</editor-fold>
    
    // No deregistration of collections of event listeners or events, thanks type erasure :<
    //</editor-fold>
}