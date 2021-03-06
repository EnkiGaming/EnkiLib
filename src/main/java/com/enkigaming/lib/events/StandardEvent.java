package com.enkigaming.lib.events;

import com.enkigaming.lib.collections.CollectionMethods;
import com.enkigaming.lib.collections.CombinedQueue;
import com.enkigaming.lib.collections.SortedQueue;
import static com.enkigaming.lib.convenience.SanityChecks.*;
import com.enkigaming.lib.encapsulatedfunctions.Converger;
import com.enkigaming.lib.encapsulatedfunctions.Transformer;
import com.enkigaming.lib.exceptions.NullArgumentException;
import com.enkigaming.lib.tuples.Pair;
import com.enkigaming.lib.tuples.Triplet;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

/**
 * The standard implementation of the Event interface.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 * @param <T> The type of the eventargs object passed to registered listeners.
 */
public class StandardEvent<T extends EventArgs> implements Event<T>
{
    /**
     * The strongly-registered event listeners. Each of these will have their onEvent methods called when this event is
     * raised.
     */
    protected final Map<EventListener<T>, Double> listeners = new HashMap<EventListener<T>, Double>();
    
    /**
     * The weakly-registered event listeners. Each of these will have their onEvent methods called when this event is
     * raised. When the garbage-collector collects a listener in this map (which the map itself won't stop it from
     * doing), it will no longer be contained within the map, and thus will no longer have its onEvent methods called
     * when this event is raised.
     */
    protected final Map<EventListener<T>, Double> weakListeners = new WeakHashMap<EventListener<T>, Double>();
    
    /**
     * The strongly-registered dependent events. Each of these have their listeners' onEvent methods called alongside
     * the listeners in this event. The EventArgs objects passed to each dependent event is generated using the
     * converger object stored with the dependent event.
     */
    protected final Map<Event<?>, Converger<Object, T, ? extends EventArgs>> dependentEvents
        = new HashMap<Event<?>, Converger<Object, T, ? extends EventArgs>>();
    
    /**
     * The weakly-registered dependent events. Each of these have their listeners' onEvent methods called alongside the
     * listeners in this event. The EventArgs objects passed to each dependent event is generated using the converger
     * object stored with the dependent event. When the garbage collector collects an event in this map (which the map
     * itself won't stop it from doing), it will no longer be contained within the map, and thus will no longer have its
     * listeners called alongside this event's listeners when this event is raised.
     */
    protected final Map<Event<?>, Converger<Object, T, ? extends EventArgs>> weakDependentEvents
        = new WeakHashMap<Event<?>, Converger<Object, T, ? extends EventArgs>>();
    
    /**
     * Gets a map containing all of the events weakly registered as dependent events to this one, along with the
     * convergers used for generating these event args when this event in raised.
     * @return A map containing the aforementioned events (as the keys) and convergers.
     */
    protected Map<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>> getWeakDependantsWithGetters()
    {
        synchronized(weakDependentEvents)
        { return new HashMap<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>>(weakDependentEvents); }
    }
    
    /**
     * Gets a collection containing the events weakly registered as dependent events to this one.
     * @return A collection containing the aforementioned events.
     */
    protected Collection<Event<? extends EventArgs>> getWeakDependants()
    {
        synchronized(weakDependentEvents)
        { return new HashSet<Event<? extends EventArgs>>(weakDependentEvents.keySet()); }
    }
    
    @Override
    public Collection<Event<? extends EventArgs>> getDependentEvents(boolean includeThis,
                                                                     boolean includeDependantsCascadingly)
    {
        Collection<Event<? extends EventArgs>> result = new HashSet<Event<? extends EventArgs>>();
        
        synchronized(dependentEvents)
        { result.addAll(dependentEvents.keySet()); }
        
        result.addAll(getWeakDependants());
        
        if(includeDependantsCascadingly)
        {
            Collection<Event<? extends EventArgs>> cascadingDependants = new HashSet<Event<? extends EventArgs>>();
            
            for(Event<? extends EventArgs> i : result)
                cascadingDependants.addAll(i.getDependentEvents(false, true));
            
            result.addAll(cascadingDependants);
        }
        
        if(includeThis)
            result.add(this);
        
        return result;
    }

    @Override
    public Collection<Event<? extends EventArgs>> getDependentEvents()
    { return getDependentEvents(false, true); }

    @Override
    public Collection<Event<? extends EventArgs>> getDirectlyDependentEvents()
    { return getDependentEvents(false, false); }

    @Override
    public Collection<Event<? extends EventArgs>> getThisAndDependentEvents()
    { return getDependentEvents(true, true); }

    @Override
    public Collection<Event<? extends EventArgs>> getThisAndDirectlyDependentEvents()
    { return getDependentEvents(true, false); }

    @Override
    public Map<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>>
        getDirectlyDependentEventsAndArgsGetters()
    {
        Map<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>> result = new HashMap<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>>();
        
        synchronized(dependentEvents)
        { result.putAll(dependentEvents); }
        
        result.putAll(getWeakDependantsWithGetters());
        
        return result;
    }
    
    protected Map<EventListener<T>, Double> getWeakListenersWithPriorities()
    {
        synchronized(weakListeners)
        { return new HashMap<EventListener<T>, Double>(weakListeners); }
    }
    
    protected Collection<EventListener<T>> getWeakListeners()
    {
        synchronized(weakListeners)
        { return new HashSet<EventListener<T>>(weakListeners.keySet()); }
    }
        
    @Override
    public Collection<EventListener<T>> getListeners()
    {
        Collection<EventListener<T>> returnListeners = new HashSet<EventListener<T>>();
        
        synchronized(listeners)
        { returnListeners.addAll(listeners.keySet()); }
        
        returnListeners.addAll(getWeakListeners());
        
        return returnListeners;
    }

    @Override
    public Map<EventListener<T>, Double> getListenersWithPriorities()
    {
        Map<EventListener<T>, Double> returnListeners = new HashMap<EventListener<T>, Double>();
        
        synchronized(listeners)
        { returnListeners.putAll(listeners); }
        
        returnListeners.putAll(getWeakListenersWithPriorities());
        
        return returnListeners;
    }

    @Override
    public Collection<EventListener<? extends EventArgs>> getDependentListeners(boolean includeListenersOfThis,
                                                                                boolean includeDependantsCascadingly)
    {
        // Collection that allows multiple values, as the same listener may be registered to multiple result.
        Collection<EventListener<?>> returnListeners = new ArrayList<EventListener<?>>();

        for(Event<?> i : getDependentEvents(includeListenersOfThis, includeDependantsCascadingly))
            returnListeners.addAll(i.getListeners());
        
        return returnListeners;
    }

    @Override
    public Collection<EventListener<? extends EventArgs>> getDependentListeners()
    { return getDependentListeners(false, true); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getDirectlyDependentListeners()
    { return getDependentListeners(false, false); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getThisAndDependentListeners()
    { return getDependentListeners(true, true); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getThisAndDirectlyDependentListeners()
    { return getDependentListeners(true, false); }
    
    protected void callListenersPreEvent(Object sender,
                                         Queue<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>> listenersQueue,
                                         boolean shareCancellation)
    {
        boolean currentCancellationState = false;
        
        while(!listenersQueue.isEmpty())
        {
            Triplet<EventListener<? extends EventArgs>, Double, EventArgs> current = listenersQueue.peek();
            double currentPriority = current.getSecond();
            
            if(currentPriority >= ListenerPriority.Post.getNumericalValue())
                break;
            
            listenersQueue.remove();
            
            if(currentPriority >= ListenerPriority.Monitor.getNumericalValue())
                current.getThird().getTechnicalAccessor().makeImmutable();
            
            if(shareCancellation)
                current.getThird().setCancelled(currentCancellationState);
            
            // The type arguments of current.getListener() are guaranteed to match the type arguments of
            // current.getArgs().
            ((EventListener<EventArgs>)current.getFirst()).onEvent(sender, current.getThird());
            
            if(shareCancellation)
                currentCancellationState = current.getThird().isCancelled();
        }
    }
    
    protected void callListenersPostEvent(Object sender,
                                          Queue<Triplet<EventListener<?>, Double, EventArgs>> listenersQueue)
    {
        while(!listenersQueue.isEmpty())
        {
            Triplet<EventListener<?>, Double, EventArgs> current = listenersQueue.poll();
            
            // The type arguments of current.getListener() are guaranteed to match the type arguments of
            // current.getArgs().
            ((EventListener<EventArgs>)current.getFirst()).onEvent(sender, current.getThird());
        }
    }

    @Override
    public void raise(Object sender, T args)
    {
        if(args == null)
            throw new NullArgumentException("args");
        
        args.getTechnicalAccessor().markAsUsingPreEvent();
        
        Queue<Triplet<EventListener<?>, Double, EventArgs>> listenersQueue
            = getThisAndDependentArgsAsQueue(sender, args);
        
        // Sharing cancellation state not necessary: As all event args will be derived from the passed args, they'll all
        // defer their cancellation state to it.
        callListenersPreEvent(sender, listenersQueue, false);
        
        // Attach queue to args for later reference.
        args.getTechnicalAccessor().setListenerQueue(listenersQueue);
        
        args.getTechnicalAccessor().markAsUsedPreEvent();
    }

    @Override
    public void raisePostEvent(Object sender, T args)
    {
        if(args == null)
            throw new NullArgumentException("args");
        
        args.getTechnicalAccessor().markAsUsingPostEvent();
        callListenersPostEvent(sender, args.getTechnicalAccessor().getListenerQueue());
        args.getTechnicalAccessor().markAsUsedPostEvent();
    }

    @Override
    public void raiseAlongside(Object sender, T args,
                               Pair<? extends Event<?>, ? extends EventArgs> otherEvent)
    { raiseAlongside(sender, args, true, otherEvent); }

    @Override
    public void raiseAlongside(Object sender, T args,
                               Pair<? extends Event<?>, ? extends EventArgs>... otherEvents)
    { raiseAlongside(sender, args, true, otherEvents); }

    @Override
    public void raiseAlongside(Object sender, T args,
                               Collection<? extends Pair<? extends Event<?>, ? extends EventArgs>> otherEvents)
    { raiseAlongside(sender, args, true, otherEvents); }
    
    @Override
    public void raiseAlongside(Object sender, T args,
                               Map<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvents)
    { raiseAlongside(sender, args, true, otherEvents); }

    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation,
                               Pair<? extends Event<?>, ? extends EventArgs> otherEvent)
    { raiseAlongside(sender, args, shareCancellation, Arrays.asList(otherEvent)); }

    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation,
                               Pair<? extends Event<?>, ? extends EventArgs>... otherEvents)
    { raiseAlongside(sender, args, shareCancellation, Arrays.asList(otherEvents)); }

    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation,
                               Collection<? extends Pair<? extends Event<?>, ? extends EventArgs>> otherEvents)
    {
        // Sanity checks.
        // Mark all args as currently using pre event.
        // Get queue of listeners and args to raise.
        // > Assign queues to all args to be raised.
        // > Combine queues into single queue.
        // > Call all listeners in combined queue with priorities before post.
        // Mark all args as having been used pre event.
        
        deepNullCheck(new Pair<Object, String>(otherEvents, "otherEvents"),
                      new Pair<Object, String>(args,        "args"       ));
        
        args.getTechnicalAccessor().markAsUsingPreEvent();
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
            i.getSecond().getTechnicalAccessor().markAsUsingPreEvent();
        
        Collection<Queue<Triplet<EventListener<?>, Double, EventArgs>>> queues
            = new HashSet<Queue<Triplet<EventListener<?>, Double, EventArgs>>>();
        
        args.getTechnicalAccessor().setListenerQueue(getThisAndDependentArgsAsQueue(sender, args));
        queues.add(args.getTechnicalAccessor().getListenerQueue());
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
        {
            i.getSecond().getTechnicalAccessor()
                .setListenerQueue(getEventAndDependantsArgsAsQueue(i.getFirst(), sender, i.getSecond()));
            
            queues.add(i.getSecond().getTechnicalAccessor().getListenerQueue());
        }
        
        Queue<Triplet<EventListener<?>, Double, EventArgs>> combinedQueue
            = new CombinedQueue<Triplet<EventListener<?>, Double, EventArgs>>
                (queues, new Transformer<Triplet<EventListener<?>, Double, EventArgs>, Comparable>()
        {
            @Override
            public Comparable get(Triplet<EventListener<?>, Double, EventArgs> parent)
            { return parent.getSecond(); }
        });
        
        callListenersPreEvent(sender, combinedQueue, shareCancellation);
        
        args.getTechnicalAccessor().markAsUsedPreEvent();
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
            i.getSecond().getTechnicalAccessor().markAsUsedPreEvent();
    }
    
    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation,
                               Map<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvents)
    { raiseAlongside(sender, args, shareCancellation, CollectionMethods.getMapAsCollectionOfPairs(otherEvents)); }

    @Override
    public void raisePostEventAlongside(Object sender, T args,
                                        Pair<? extends Event<?>, ? extends EventArgs> otherEvent)
    { raisePostEventAlongside(sender, args, Arrays.asList(otherEvent)); }

    @Override
    public void raisePostEventAlongside(Object sender, T args,
                                        Pair<? extends Event<?>, ? extends EventArgs>... otherEvents)
    { raisePostEventAlongside(sender, args, Arrays.asList(otherEvents)); }

    @Override
    public void raisePostEventAlongside(Object sender, T args,
                                        Collection<? extends Pair<? extends Event<?>, ? extends EventArgs>> otherEvents)
    {
        // Sanity checks
        // Mark all args as being used post-event.
        // Combine listener queues from args.
        // Call listeners
        // Mark all args as having been used post-event.
        
        if(otherEvents == null)
            throw new NullArgumentException("otherEvents.");
        
        if(args == null)
            throw new NullArgumentException("args.");
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
            if(i == null)
                throw new NullArgumentException("Member of otherEvents.");
        
        args.getTechnicalAccessor().markAsUsingPostEvent();
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
            i.getSecond().getTechnicalAccessor().markAsUsingPostEvent();
        
        Collection<Queue<Triplet<EventListener<?>, Double, EventArgs>>> queues
            = new HashSet<Queue<Triplet<EventListener<?>, Double, EventArgs>>>();
        
        queues.add(args.getTechnicalAccessor().getListenerQueue());
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
            queues.add(i.getSecond().getTechnicalAccessor().getListenerQueue());
        
        Queue<Triplet<EventListener<?>, Double, EventArgs>> combinedQueue
            = new CombinedQueue<Triplet<EventListener<?>, Double, EventArgs>>
                (queues, new Transformer<Triplet<EventListener<?>, Double, EventArgs>, Comparable>()
        {
            @Override
            public Comparable get(Triplet<EventListener<?>, Double, EventArgs> parent)
            { return parent.getSecond(); }
        });
        
        callListenersPostEvent(sender, combinedQueue);
        
        args.getTechnicalAccessor().markAsUsedPostEvent();
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : otherEvents)
            i.getSecond().getTechnicalAccessor().markAsUsedPostEvent();
    }
    
    @Override
    public void raisePostEventAlongside(Object sender, T args,
                                        Map<? extends Event<? extends EventArgs>, ? extends EventArgs> otherEvents)
    { raisePostEventAlongside(sender, args, CollectionMethods.getMapAsCollectionOfPairs(otherEvents)); }
    
    @Override
    public void register(EventListener<T> listener)
    { register(listener, ListenerPriority.Normal.getNumericalValue()); }
    
    @Override
    public void register(EventListener<T>... listeners)
    { register(Arrays.asList(listeners), ListenerPriority.Normal.getNumericalValue()); }
    
    @Override
    public void register(Collection<EventListener<T>> listeners)
    { register(listeners, ListenerPriority.Normal.getNumericalValue()); }
    
    @Override
    public void register(EventListener<T> listener, double priority) // Main
    {
        for(Method method : listener.getClass().getMethods())
            if(method.isAnnotationPresent(WeakListener.class))
                synchronized(weakListeners)
                { weakListeners.put(listener, priority); }
        
        synchronized(listeners)
        { listeners.put(listener, priority); }
    }
    
    @Override
    public void register(EventListener<T> listener, ListenerPriority priority)
    { register(listener, priority.getNumericalValue()); }
    
    @Override
    public void register(EventListener<T>[] listeners, double priority)
    { register(Arrays.asList(listeners), priority); }
    
    @Override
    public void register(EventListener<T>[] listeners, ListenerPriority priority)
    { register(Arrays.asList(listeners), priority.getNumericalValue()); }
    
    @Override
    public void register(Collection<EventListener<T>> listeners, double priority) // Main
    {
        Collection<EventListener<T>> strongs = new HashSet<EventListener<T>>();
        Collection<EventListener<T>> weaks   = new HashSet<EventListener<T>>();
        
        for(EventListener<T> i : listeners)
        {
            boolean wasWeak = false;
            
            for(Method method : i.getClass().getMethods())
                if(method.isAnnotationPresent(WeakListener.class))
                {
                    weaks.add(i);
                    wasWeak = true;
                    break;
                }
            
            if(!wasWeak)
                strongs.add(i);
        }
        
        synchronized(this.listeners)
        {
            for(EventListener<T> i : strongs)
                this.listeners.put(i, priority);
        }
        
        synchronized(weakListeners)
        {
            for(EventListener<T> i : weaks)
                this.weakListeners.put(i, priority);
        }
    }
    
    @Override
    public void register(Collection<EventListener<T>> listeners, ListenerPriority priority)
    { register(listeners, priority.getNumericalValue()); }
    
    @Override
    public void register(double priority, EventListener<T> listener)
    { register(listener, priority); }
    
    @Override
    public void register(ListenerPriority priority, EventListener<T> listener)
    { register(listener, priority.getNumericalValue()); }
    
    @Override
    public void register(double priority, EventListener<T>... listeners)
    { register(Arrays.asList(listeners), priority); }
    
    @Override
    public void register(ListenerPriority priority, EventListener<T>... listeners)
    { register(Arrays.asList(listeners), ListenerPriority.Normal.getNumericalValue()); }
    
    @Override
    public void register(double priority, Collection<EventListener<T>> listeners)
    { register(listeners, priority); }
    
    @Override
    public void register(ListenerPriority priority, Collection<EventListener<T>> listeners)
    { register(listeners, priority.getNumericalValue()); }

    @Override
    public <TArgs extends EventArgs> void register(Event<TArgs> event, Converger<Object, T, TArgs> eventArgsGetter)
    { register(eventArgsGetter, event); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter, Event<TArgs> event)
    {
        synchronized(dependentEvents) 
        { dependentEvents.put(event, eventArgsGetter); }
    }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<? extends TArgs>... events)
    { register(eventArgsGetter, Arrays.asList(events)); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Collection<? extends Event<? extends TArgs>> events)
    {
        synchronized(dependentEvents)
        {
            for(Event<? extends TArgs> i : events)
                dependentEvents.put(i, eventArgsGetter);
        }
    }
    
    @Override
    public <TArgs extends EventArgs> void register(Event<TArgs> event,
                                                   Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered)
    { register(eventArgsGetter, event, stronglyRegistered); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<TArgs> event,
                                                   boolean stronglyRegistered)
    {
        if(stronglyRegistered)
        {
            register(eventArgsGetter, event);
            return;
        }
        
        synchronized(weakDependentEvents)
        { weakDependentEvents.put(event, eventArgsGetter); }
    }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Event<TArgs> event)
    { register(eventArgsGetter, event, stronglyRegistered); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Event<? extends TArgs>... events)
    { register(eventArgsGetter, Arrays.asList(events), stronglyRegistered); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Collection<? extends Event<? extends TArgs>> events)
    { register(eventArgsGetter, events, stronglyRegistered); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<? extends TArgs>[] events,
                                                   boolean stronglyRegistered)
    { register(eventArgsGetter, Arrays.asList(events), stronglyRegistered); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Collection<? extends Event<? extends TArgs>> events,
                                                   boolean stronglyRegistered)
    {
        if(stronglyRegistered)
        {
            register(eventArgsGetter, events);
            return;
        }
        
        synchronized(weakDependentEvents)
        {
            for(Event<? extends TArgs> event : events)
                weakDependentEvents.put(event, eventArgsGetter);
        }
    }

    @Override
    public EventListener<T> deregister(EventListener<T> listener)
    {
        synchronized(listeners)
        {
            if(listeners.remove(listener) != null)
                return listener;
        }
        
        synchronized(weakListeners)
        { return weakListeners.remove(listener) != null ? listener : null; }
    }

    @Override
    public Collection<EventListener<T>> deregister(EventListener<T>... listeners)
    {
        Collection<EventListener<T>> deregistered = new HashSet<EventListener<T>>();
        
        synchronized(this.listeners)
        {
            for(EventListener<T> i : listeners)
                if(this.listeners.remove(i) != null)
                    deregistered.add(i);
        }
        
        synchronized(weakListeners)
        {
            for(EventListener<T> i : listeners)
                if(weakListeners.remove(i) != null)
                    deregistered.add(i);
        }
        
        return deregistered;
    }

    @Override
    public Event<? extends EventArgs> deregister(Event<? extends EventArgs> event)
    {
        synchronized(dependentEvents)
        {
            if(dependentEvents.remove(event) != null)
                return event;
        }
        
        synchronized(weakDependentEvents)
        { weakDependentEvents.remove(event); }
        
        return null;
    }

    @Override
    public Collection<Event<? extends EventArgs>> deregister(Event<? extends EventArgs>... events)
    {
        Collection<Event<? extends EventArgs>> deregistered = new HashSet<Event<? extends EventArgs>>();
        
        synchronized(dependentEvents)
        {
            for(Event<? extends EventArgs> i : events)
                if(this.dependentEvents.remove(i) != null)
                    deregistered.add(i);
        }
        
        synchronized(weakDependentEvents)
        {
            for(Event<? extends EventArgs> i : events)
                if(weakDependentEvents.remove(i) != null)
                    deregistered.add(i);
        }
        
        return deregistered;
    }
    
    protected static Collection<EventArgs> getArgsFrom(Collection<? extends Pair<Event<?>, EventArgs>> eventsWithArgs)
    {
        Collection<EventArgs> args = new ArrayList<EventArgs>();
        
        for(Pair<Event<?>, EventArgs> i : eventsWithArgs)
            args.add(i.getSecond());
        
        return args;
    }
    
    // Needs to be static. Calls itself recursively on other event objects that may or may not have this method.
    private static <T extends EventArgs> Collection<EventArgs> generateThisAndDependentArgs(Event<T> event, Object sender, T args)
    {
        Collection<EventArgs> allArgs = new HashSet<EventArgs>();
        
        args.getTechnicalAccessor().setEvent(event);
        allArgs.add(args);
        
        Map<Event<?>, Converger<Object, T, ? extends EventArgs>> eventsAndArgsGetters
            = event.getDirectlyDependentEventsAndArgsGetters();
        
        for(Map.Entry<Event<?>, Converger<Object, T, ? extends EventArgs>> i : eventsAndArgsGetters.entrySet())
        {
            EventArgs iArgs = i.getValue().get(sender, args);
            
            iArgs.getTechnicalAccessor().setEvent(event);
            iArgs.getTechnicalAccessor().setParentArgs(args);
            
            args.getTechnicalAccessor().addDependentArgs(iArgs);
            
            // The generic type arguments of iArgs are guaranteed to match the generic type arguments of i.getKey().
            allArgs.addAll(generateThisAndDependentArgs((Event<EventArgs>)i.getKey(), sender, iArgs));
        }
        
        return allArgs;
    }
    
    // 6/6/2015 - I'm reading this back and dismaying at how much of a mess the internal technical methods of this
    //            class are, while trying to document it. What's daft is that the tidier approach I used in previous
    //            incarnations of my events system used a tinier internal class signature, which resulted in far
    //            messier actual method code. Maybe the poor method names just reflect the spaghetti in my head when I
    //            was working through writing the implementation. I'll probably come back here at some point in the
    //            future and rewrite the technical methods maybe just to make them seem less ... technical? To put it
    //            diplomatically? But that probably won't be until I have a reason to go through this thoroughly, like
    //            when I eventually need to translate my events system into C#. (While this was originally modelled
    //            after C#'s event system, it's grown past it, and now includes some features, particularly priorities,
    //            that I'd miss going back to just using standard C# events. Because they're pretty much just delegates
    //            with bells and whistles, I can't even just make a sub-class of its events that adds some of those
    //            missing features :< ) But for now, it works, and I have no reason nor need to tamper with it. I'd just
    //            like to direct you towards this comic strip: 
    protected Queue<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>> getThisAndDependentArgsAsQueue(Object sender, T args)
    {
        Collection<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>> listenerArgsPairings = new HashSet<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>>();
        
        for(EventArgs i : generateThisAndDependentArgs(this, sender, args))
            for(Map.Entry<? extends EventListener<? extends EventArgs>, Double> j : i.getEvent().getListenersWithPriorities().entrySet())
                listenerArgsPairings.add(new Triplet<EventListener<? extends EventArgs>, Double, EventArgs>(j.getKey(), j.getValue(), i));
        
        return new SortedQueue<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>>(listenerArgsPairings, new Transformer<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>, Comparable>()
        {
            @Override
            public Comparable get(Triplet<EventListener<? extends EventArgs>, Double, EventArgs> parent)
            { return parent.getSecond(); }
        });
    }
    
    /**
     * Gets a collection containing the eventargs objects to be passed to all dependent events during an event raise.
     * @param event The main event.
     * @param sender The object that caused the event raise.
     * @param args The eventargs object being passed into the main event.
     * @return A collection containing eventargs, which should themselves contain the events they should be passed to
     * the listeners of.
     */
    protected static Collection<EventArgs> generateEventAndDependantsArgs(Event<? extends EventArgs> event,
                                                                          Object sender,
                                                                          EventArgs args)
    {
        // I believe this is technically referred to as the "Fuck it all" pattern. Replace this whole bit with something
        // cleaner at a later date. Maybe when I look into optimising this.
        Event<EventArgs> castedEvent = (Event<EventArgs>)event;
        
        Collection<EventArgs> allArgs = new HashSet<EventArgs>();
        
        args.getTechnicalAccessor().setEvent(event);
        allArgs.add(args);
        
        Map<Event<?>, Converger<Object, EventArgs, ? extends EventArgs>> eventsAndArgsGetters
            = castedEvent.getDirectlyDependentEventsAndArgsGetters();
        
        for(Map.Entry<Event<?>, Converger<Object, EventArgs, ? extends EventArgs>> i : eventsAndArgsGetters.entrySet())
        {
            EventArgs iArgs = i.getValue().get(sender, args);
            
            iArgs.getTechnicalAccessor().setEvent(event);
            iArgs.getTechnicalAccessor().setParentArgs(args);
            
            args.getTechnicalAccessor().addDependentArgs(iArgs);
            
            // The generic type arguments of iArgs are guaranteed to match the generic type arguments of i.getKey().
            allArgs.addAll(generateThisAndDependentArgs((Event<EventArgs>)i.getKey(), sender, iArgs));
        }
        
        return allArgs;
    }
    
    /**
     * Gets a queue containing all of the dependent events (At the time of calling) along with the eventargs objects to
     * be passed to them and their priorities, in order of priority, starting with listeners of the priority that should
     * be first.
     * @param event The initial event being called, from which to derive dependants.
     * @param sender The object that caused the event raise.
     * @param args The eventargs object to be passed to "event"'s own listeners.
     * @return A queue containing triplets, containing the resulting event-listeners, their priorities, and the
     * eventargs objects to be passed to them, sorted by their priorities so that the earliest priorities are next in
     * the queue.
     */
    protected static Queue<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>> getEventAndDependantsArgsAsQueue(Event<?> event, Object sender, EventArgs args)
    {
        Collection<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>> listenerArgsPairings = new HashSet<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>>();
        
        for(EventArgs i : generateEventAndDependantsArgs(event, sender, args))
            for(Map.Entry<? extends EventListener<? extends EventArgs>, Double> j : i.getEvent().getListenersWithPriorities().entrySet())
                listenerArgsPairings.add(new Triplet<EventListener<? extends EventArgs>, Double, EventArgs>(j.getKey(), j.getValue(), i));
        
        return new SortedQueue<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>>(listenerArgsPairings, new Transformer<Triplet<EventListener<? extends EventArgs>, Double, EventArgs>, Comparable>()
        {
            @Override
            public Comparable get(Triplet<EventListener<? extends EventArgs>, Double, EventArgs> parent)
            { return parent.getSecond(); }
        });
    }
}