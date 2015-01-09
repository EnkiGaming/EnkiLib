package com.enkigaming.lib.events;

import com.enkigaming.lib.collections.CombinedQueue;
import com.enkigaming.lib.collections.SortedQueue;
import com.enkigaming.lib.events.EventArgs;
import com.enkigaming.lib.misc.Lambda;
import com.enkigaming.lib.exceptions.NullArgumentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class HaniiEvent<T extends EventArgs> implements Event<T>
{
    final List<EventListener<T>> listeners = new ArrayList<EventListener<T>>();
    
    final Map<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>> dependentEvents
    = new HashMap<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>>();
    
    @Override
    public Collection<Event<? extends EventArgs>> getDependentEvents(boolean includeThis, boolean includeDependantsCascadingly)
    {
        if(!includeThis && !includeDependantsCascadingly)
            synchronized(dependentEvents)
            { return dependentEvents.keySet(); }
        
        List<Event<? extends EventArgs>> returnList = null;
        
        synchronized(dependentEvents)
        { returnList = new ArrayList<Event<? extends EventArgs>>(dependentEvents.keySet()); }
        
        if(includeDependantsCascadingly)
        {
            Collection<Event<? extends EventArgs>> cascadingDependants = new HashSet<Event<? extends EventArgs>>();
            
            for(Event<? extends EventArgs> i : returnList)
                cascadingDependants.addAll(i.getDependentEvents(false, true));
            
            returnList.addAll(cascadingDependants);
        }
        
        if(includeThis)
            returnList.add(this);
        
        return returnList;
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
    public Map<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>> getDirectlyDependentEventsAndArgsGetters()
    {
        synchronized(dependentEvents)
        { return new HashMap<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>>(dependentEvents); }
    }

    @Override
    public Collection<EventListener<T>> getListeners()
    {
        synchronized(listeners)
        { return new ArrayList<EventListener<T>>(listeners); }
    }

    @Override
    public Collection<EventListener<? extends EventArgs>> getDependentListeners(boolean includeListenersOfThis, boolean includeDependantsCascadingly)
    { return getListenersFrom(getDependentEvents(includeListenersOfThis, includeDependantsCascadingly)); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getDependentListeners()
    { return getListenersFrom(getDependentEvents()); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getDirectlyDependentListeners()
    { return getListenersFrom(getDirectlyDependentEvents()); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getThisAndDependentEventArgs()
    { return getListenersFrom(getThisAndDependentEvents()); }

    @Override
    public Collection<EventListener<? extends EventArgs>> getThisAndDirectlyDependentListeners()
    { return getListenersFrom(getThisAndDirectlyDependentEvents()); }

    @Override
    public void raise(Object sender, T args)
    {
        if(args == null)
            throw new NullArgumentException("args");
        
        args.getTechnicalAccessor().markAsUsingPreEvent();
        
        Queue<ListenerArgsPairing> listenersQueue = EventMethods.getThisAndDependentArgsAsQueue(this, sender, args);
        
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
    public void raiseAlongside(Object sender, T args, EventWithArgs<? extends EventArgs> otherEvent)
    { raiseAlongside(sender, args, true, otherEvent); }

    @Override
    public void raiseAlongside(Object sender, T args, EventWithArgs<? extends EventArgs>... otherEvents)
    { raiseAlongside(sender, args, true, otherEvents); }

    @Override
    public void raiseAlongside(Object sender, T args, Collection<EventWithArgs<? extends EventArgs>> otherEvents)
    { raiseAlongside(sender, args, true, otherEvents); }
    
    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, EventWithArgs<? extends EventArgs> otherEvent)
    { raiseAlongside(sender, args, shareCancellation, Arrays.<EventWithArgs<? extends EventArgs>>asList(otherEvent)); }

    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, EventWithArgs<? extends EventArgs>... otherEvents)
    { raiseAlongside(sender, args, shareCancellation, Arrays.asList(otherEvents)); }

    @Override
    public void raiseAlongside(Object sender, T args, boolean shareCancellation, Collection<EventWithArgs<? extends EventArgs>> otherEvents)
    {
        // Sanity checks
        if(otherEvents == null)
            throw new NullArgumentException("otherEvents.");
        
        if(args == null)
            throw new NullArgumentException("args.");
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            if(i == null)
                throw new NullArgumentException("Member of otherEvents.");
            else if(i.getArgs() == null)
                throw new NullArgumentException(".getArgs() of member of otherEvents.");
            else if(i.getEvent() == null)
                throw new NullArgumentException(".getEvent() of member of otherEvents.");
        
        // make sure args have not already been used or currently being used.
        args.getTechnicalAccessor().markAsUsingPreEvent();
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            i.getArgs().getTechnicalAccessor().markAsUsingPreEvent();
        
        // Relate all passed args together
        Collection<EventArgs> passedArgs = new ArrayList<EventArgs>(EventMethods.getArgsFrom(otherEvents));
        passedArgs.add(args);
        
        EventMethods.relateArgs(passedArgs);
        
        // Get queue of listeners and args to raise.
        
        // > Place queues containing required listeners into collection
        Collection<Queue<ListenerArgsPairing>> queues = new ArrayList<Queue<ListenerArgsPairing>>();
        
        args.getTechnicalAccessor().setListenerQueue(EventMethods.getThisAndDependentArgsAsQueue(this, sender, args));
        queues.add(args.getTechnicalAccessor().getListenerQueue());
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
        {
            // The generic type arguments of i.getArgs and i.getEvent are guaranteed to match.
            i.getArgs().getTechnicalAccessor().setListenerQueue(EventMethods.getThisAndDependentArgsAsQueue((Event<EventArgs>)i.getEvent(), sender, i.getArgs()));
            queues.add(i.getArgs().getTechnicalAccessor().getListenerQueue());
        }
        
        // > Amalgamate separate queues into single combined queue
        Queue<ListenerArgsPairing> combinedQueue = getCombinedListenerQueue(queues);
        
        // Call all listeners in queue with a priority before post-event
        callListenersPreEvent(sender, combinedQueue, shareCancellation);
        
        args.getTechnicalAccessor().markAsUsedPreEvent();
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            i.getArgs().getTechnicalAccessor().markAsUsedPreEvent();
    }

    @Override
    public void raisePostEventAlongside(Object sender, T args, EventWithArgs<? extends EventArgs> otherEvent)
    { raisePostEventAlongside(sender, args, Arrays.<EventWithArgs<? extends EventArgs>>asList(otherEvent)); }

    @Override
    public void raisePostEventAlongside(Object sender, T args, EventWithArgs<? extends EventArgs>... otherEvents)
    { raisePostEventAlongside(sender, args, Arrays.asList(otherEvents)); }

    @Override
    public void raisePostEventAlongside(Object sender, T args, Collection<EventWithArgs<? extends EventArgs>> otherEvents)
    {
        // Sanity checks
        if(otherEvents == null)
            throw new NullArgumentException("otherEvents.");
        
        if(args == null)
            throw new NullArgumentException("args.");
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            if(i == null)
                throw new NullArgumentException("Member of otherEvents.");
            else if(i.getArgs() == null)
                throw new NullArgumentException(".getArgs() of member of otherEvents.");
            else if(i.getEvent() == null)
                throw new NullArgumentException(".getEvent() of member of otherEvents.");
        
        // Ensure args have been used already pre-event and haven't been used yet post-event.
        args.getTechnicalAccessor().markAsUsingPostEvent();
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            i.getArgs().getTechnicalAccessor().markAsUsingPostEvent();
        
        // Create combined queue containing queues from all passed args, containing all listeners left to be fired.
        
        Collection<Queue<ListenerArgsPairing>> queues = new ArrayList<Queue<ListenerArgsPairing>>();
        
        args.getTechnicalAccessor().getListenerQueue();
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            queues.add(i.getArgs().getTechnicalAccessor().getListenerQueue());
        
        CombinedQueue<ListenerArgsPairing> combinedQueue = getCombinedListenerQueue(queues);
        
        // Fire remaining listeners
        callListenersPostEvent(sender, combinedQueue);
        
        args.getTechnicalAccessor().markAsUsedPostEvent();
        
        for(EventWithArgs<? extends EventArgs> i : otherEvents)
            i.getArgs().getTechnicalAccessor().markAsUsedPostEvent();
    }
    
    protected void callListenersPreEvent(Object sender, Queue<ListenerArgsPairing> queue, boolean shareCancellation)
    {
        // Call all listeners in the queue with a priority before post-event.
        boolean reachedPostEventListeners = false;
        boolean currentCancellationState = false;
        
        while(!queue.isEmpty() && !reachedPostEventListeners)
        {
            ListenerArgsPairing current = queue.peek();
            double currentPriority = current.getListener().getPriority();
            
            if(currentPriority >= ListenerPriority.Post.getNumericalValue())
                reachedPostEventListeners = true;
            else
            {
                queue.remove();
                
                // Args should be made immutable before being passed to listeners with a monitor priority or greater.
                if(currentPriority >= ListenerPriority.Monitor.getNumericalValue())
                    current.getArgs().getTechnicalAccessor().makeImmutable();
                
                if(shareCancellation)
                    current.getArgs().setCancelled(currentCancellationState);
                
                // The type arguments of current.getListener() are guaranteed to match the type arguments of
                // current.getArgs().
                ((EventListener<EventArgs>)current.getListener()).onEvent(sender, current.getArgs());
                
                if(shareCancellation)
                    currentCancellationState = current.getArgs().isCancelled();
            }
        }
    }
    
    protected void callListenersPostEvent(Object sender, Queue<ListenerArgsPairing> queue)
    {
        while(!queue.isEmpty())
        {
            ListenerArgsPairing current = queue.poll();
            
            // The type arguments of current.getListener() are guaranteed to match the type arguments of
            // current.getArgs().
            ((EventListener<EventArgs>)current.getListener()).onEvent(sender, current.getArgs());
        }
    }
    
    private static <T> CombinedQueue<ListenerArgsPairing> getCombinedListenerQueue(Collection<? extends Queue<ListenerArgsPairing>> queues)
    {
        return new CombinedQueue<ListenerArgsPairing>(queues, new Lambda<ListenerArgsPairing, Comparable>()
        {
            @Override
            public Comparable getMember(ListenerArgsPairing parent)
            { return parent.getListener().getPriority(); }
        });
    }

    @Override
    public void register(EventListener<T> listener)
    {
        synchronized(listeners)
        {
            if(listeners.contains(listener))
                return;

            listeners.add(listener);
        }
    }

    @Override
    public <TArgs extends EventArgs> void register(Event<TArgs> event, DependentEventArgsGetter<T, TArgs> eventArgsGetter)
    {
        synchronized(dependentEvents)
        { dependentEvents.put((Event<EventArgs>)event, (DependentEventArgsGetter<T, EventArgs>)eventArgsGetter); }
    }

    @Override
    public boolean deregister(EventListener<T> listener)
    {
        synchronized(listeners)
        { return listeners.remove(listener); }
    }

    @Override
    public boolean deregister(Event<? extends EventArgs> event)
    {
        synchronized(dependentEvents)
        { return dependentEvents.remove(event) != null; }
    }
    
    private static Collection<EventListener<? extends EventArgs>> getListenersFrom(Collection<Event<? extends EventArgs>> events)
    {
        List<EventListener<? extends EventArgs>> listeners = new ArrayList<EventListener<? extends EventArgs>>();
        
        for(Event<? extends EventArgs> i : events)
            listeners.addAll(i.getListeners());
        
        return listeners;
    }
}