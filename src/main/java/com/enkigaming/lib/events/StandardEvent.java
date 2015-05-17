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
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import org.apache.commons.lang3.NotImplementedException;

public class StandardEvent<T extends EventArgs> implements Event<T>
{
    protected final Map<EventListener<T>, Double> listeners = new HashMap<EventListener<T>, Double>();
    
    protected final Map<WeakReference<EventListener<T>>, Double> weakListeners
        = new IdentityHashMap<WeakReference<EventListener<T>>, Double>();
    
    protected final Map<Event<?>, Converger<Object, T, ? extends EventArgs>> dependentEvents
        = new HashMap<Event<?>, Converger<Object, T, ? extends EventArgs>>();
    
    protected final Map<WeakReference<Event<?>>, Converger<Object, T, ? extends EventArgs>> weakDependentEvents
        = new IdentityHashMap<WeakReference<Event<?>>, Converger<Object, T, ? extends EventArgs>>();
    
    protected Map<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>> getWeakDependantsWithGetters()
    {
        Map<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>> result
            = new HashMap<Event<? extends EventArgs>, Converger<Object, T, ? extends EventArgs>>();
        
        synchronized(weakDependentEvents)
        {
            for(Entry<WeakReference<Event<?>>, Converger<Object, T, ? extends EventArgs>> entry
                : new HashSet<Entry<WeakReference<Event<?>>, Converger<Object, T, ? extends EventArgs>>>(weakDependentEvents.entrySet()))
            {
                Event<?> event = entry.getKey().get();
                
                if(event == null)
                {
                    weakDependentEvents.remove(entry.getKey());
                    continue;
                }
                
                result.put(event, entry.getValue());
            }
        }
        
        return result;
    }
    
    protected Collection<Event<? extends EventArgs>> getWeakDependants()
    {
        Collection<Event<? extends EventArgs>> result = new HashSet<Event<? extends EventArgs>>();
        
        synchronized(weakDependentEvents)
        {
            for(WeakReference<Event<?>> key : new HashSet<WeakReference<Event<?>>>(weakDependentEvents.keySet()))
            {
                Event<?> event = key.get();
                
                if(event == null)
                {
                    weakDependentEvents.remove(key);
                    continue;
                }
                
                result.add(event);
            }
        }
        
        return result;
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
        Map<EventListener<T>, Double> returnListeners = new HashMap<EventListener<T>, Double>();
        
        synchronized(weakListeners)
        {
            for(Entry<WeakReference<EventListener<T>>, Double> entry
                    : new HashSet<Entry<WeakReference<EventListener<T>>, Double>>(weakListeners.entrySet()))
            {
                EventListener<T> entryListener = entry.getKey().get();
                
                if(entryListener != null)
                    returnListeners.put(entryListener, entry.getValue());
                else
                    weakListeners.remove(entry.getKey());
            }
        }
        
        return returnListeners;
    }
    
    protected Collection<EventListener<T>> getWeakListeners()
    {
        HashSet<EventListener<T>> returnListeners = new HashSet<EventListener<T>>();
        
        synchronized(weakListeners)
        {
            for(Entry<WeakReference<EventListener<T>>, Double> entry
                    : new HashSet<Entry<WeakReference<EventListener<T>>, Double>>(weakListeners.entrySet()))
            {
                EventListener<T> entryListener = entry.getKey().get();
                
                if(entryListener != null)
                    returnListeners.add(entryListener);
                else
                    weakListeners.remove(entry.getKey());
            }
        }
        
        return returnListeners;
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
    { throw new NotImplementedException("Not implemented yet."); }
    
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
                {
                    weakListeners.put(new WeakReference<EventListener<T>>(listener), priority);
                    return;
                }
        
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
                this.weakListeners.put(new WeakReference<EventListener<T>>(i), priority);
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
    { throw new NotImplementedException("Not implemented yet."); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<TArgs> event,
                                                   boolean stronglyRegistered)
    { throw new NotImplementedException("Not implemented yet."); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Event<TArgs> event)
    { throw new NotImplementedException("Not implemented yet."); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Event<? extends TArgs>... events)
    { throw new NotImplementedException("Not implemented yet."); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   boolean stronglyRegistered,
                                                   Collection<? extends Event<? extends TArgs>> events)
    { throw new NotImplementedException("Not implemented yet."); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Event<? extends TArgs>[] events,
                                                   boolean stronglyRegistered)
    { throw new NotImplementedException("Not implemented yet."); }
    
    @Override
    public <TArgs extends EventArgs> void register(Converger<Object, T, TArgs> eventArgsGetter,
                                                   Collection<? extends Event<? extends TArgs>> events,
                                                   boolean stronglyRegistered)
    { throw new NotImplementedException("Not implemented yet."); }

    @Override
    public EventListener<T> deregister(EventListener<T> listener)
    {
        synchronized(listeners)
        {
            if(listeners.remove(listener) != null)
                return listener;
        }
        
        WeakReference<EventListener<T>> toRemove = null;
        EventListener<T> toRemoveUnwrapped = null;
        
        synchronized(weakListeners)
        {
            for(WeakReference<EventListener<T>> i : weakListeners.keySet())
            {
                EventListener<T> iListener = i.get();
                
                if(listener.equals(iListener))
                {
                    toRemove = i;
                    toRemoveUnwrapped = iListener;
                    break;
                }
            }
            
            weakListeners.remove(toRemove);
            return toRemoveUnwrapped;
        }
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
            for(WeakReference<EventListener<T>> i
                    : new ArrayList<WeakReference<EventListener<T>>>(weakListeners.keySet()))
            {
                EventListener<T> iListener = i.get();
                
                for(EventListener<T> j : listeners)
                    if(iListener != null & iListener.equals(j))
                    {
                        deregistered.add(iListener);
                        weakListeners.remove(i);
                        break;
                    }
            }
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
        {
            for(WeakReference<Event<?>> i : new ArrayList<WeakReference<Event<?>>>(weakDependentEvents.keySet()))
            {
                Event<?> iEvent = i.get();
                
                if(iEvent == null)
                {
                    weakDependentEvents.remove(i);
                    continue;
                }
                
                if(iEvent == event)
                {
                    weakDependentEvents.remove(i);
                    return iEvent;
                }
            }
        }
        
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
            WeakDependantsLoop:
            for(WeakReference<Event<?>> i : new ArrayList<WeakReference<Event<?>>>(weakDependentEvents.keySet()))
            {
                Event<?> iEvent = i.get();
                
                if(iEvent == null)
                {
                    weakDependentEvents.remove(i);
                    continue;
                }
                
                for(Event<?> j : events)
                    if(iEvent == j)
                    {
                        weakDependentEvents.remove(i);
                        deregistered.add(iEvent);
                        continue WeakDependantsLoop;
                    }
            }
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