package com.enkigaming.lib.events;

import com.enkigaming.lib.collections.SortedQueue;
import com.enkigaming.lib.misc.Lambda;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

public class EventMethods
{
    //<editor-fold defaultstate="collapsed" desc="Multiple raise">
    public static void raiseMultiple(Object sender, EventWithArgs<? extends EventArgs>... events)
    { raiseMultiple(sender, Arrays.asList(events)); }
    
    public static void raiseMultiple(Object sender, Collection<EventWithArgs<? extends EventArgs>> events)
    {
        Collection<EventWithArgs<? extends EventArgs>> alongsideEvents
        = new ArrayList<EventWithArgs<? extends EventArgs>>(events);
        
        EventWithArgs<? extends EventArgs> toCallOn = null;
        
        for(EventWithArgs<? extends EventArgs> i : alongsideEvents)
        {
            toCallOn = i;
            break;
        }
        
        if(toCallOn == null)
            return;
        
        alongsideEvents.remove(toCallOn);
        ((Event<EventArgs>)toCallOn.getEvent()).raiseAlongside(sender, toCallOn.getArgs(), events);
    }
    
    public static void raiseMultiple(Object sender, boolean shareCancellation, Collection<EventWithArgs<? extends EventArgs>> events)
    {
        Collection<EventWithArgs<? extends EventArgs>> alongsideEvents
        = new ArrayList<EventWithArgs<? extends EventArgs>>(events);
        
        EventWithArgs<? extends EventArgs> toCallOn = null;
        
        for(EventWithArgs<? extends EventArgs> i : alongsideEvents)
        {
            toCallOn = i;
            break;
        }
        
        if(toCallOn == null)
            return;
        
        alongsideEvents.remove(toCallOn);
        ((Event<EventArgs>)toCallOn.getEvent()).raiseAlongside(sender, toCallOn.getArgs(), shareCancellation, events);
    }
    
    public static void raiseMultiplePostEvent(Object sender, EventWithArgs<? extends EventArgs>... events)
    { raiseMultiple(sender, Arrays.asList(events)); }
    
    public static void raiseMultiplePostEvent(Object sender, Collection<EventWithArgs<? extends EventArgs>> events)
    {
        Collection<EventWithArgs<? extends EventArgs>> alongsideEvents
        = new ArrayList<EventWithArgs<? extends EventArgs>>(events);
        
        EventWithArgs<? extends EventArgs> toCallOn = null;
        
        for(EventWithArgs<? extends EventArgs> i : alongsideEvents)
        {
            toCallOn = i;
            break;
        }
        
        if(toCallOn == null)
            return;
        
        alongsideEvents.remove(toCallOn);
        ((Event<EventArgs>)toCallOn.getEvent()).raisePostEventAlongside(sender, toCallOn.getArgs(), events);
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Event args relating">
    public static void relateArgs(EventArgs... args)
    { relateArgs(Arrays.asList(args)); }
    
    public static void relateArgs(Collection<? extends EventArgs> args)
    {
        for(EventArgs i : args)
        {
            Collection<EventArgs> toRelate = new ArrayList<EventArgs>(args);
            toRelate.remove(i);
            
            i.getTechnicalAccessor().addRelatedMasterArgs(toRelate);
        }
    }
//</editor-fold>
    
    public static Collection<EventArgs> getArgsFrom(Collection<? extends EventWithArgs<? extends EventArgs>> eventsWithArgs)
    {
        Collection<EventArgs> args = new ArrayList<EventArgs>();
        
        for(EventWithArgs<? extends EventArgs> i : eventsWithArgs)
            args.add(i.getArgs());
        
        return args;
    }
    
    public static <T extends EventArgs> Collection<EventArgs> generateThisAndDependentArgs(Event<T> event, Object sender, T args)
    {
        Collection<EventArgs> allArgs = new HashSet<EventArgs>();
        
        args.getTechnicalAccessor().setEvent(event);
        allArgs.add(args);
        
        Map<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>> eventsAndArgsGetters
            = event.getDirectlyDependentEventsAndArgsGetters();
        
        for(Map.Entry<Event<? extends EventArgs>, DependentEventArgsGetter<T, ? extends EventArgs>> i : eventsAndArgsGetters.entrySet())
        {
            EventArgs iArgs = i.getValue().getDependentArgs(sender, args);
            
            iArgs.getTechnicalAccessor().setEvent(event);
            iArgs.getTechnicalAccessor().setParentArgs(args);
            
            args.getTechnicalAccessor().addDependentArgs(iArgs);
            
            // The generic type arguments of iArgs are guaranteed to match the generic type arguments of i.getKey().
            allArgs.addAll(generateThisAndDependentArgs((Event<EventArgs>)i.getKey(), sender, iArgs));
        }
        
        return allArgs;
    }
    
    static <T extends EventArgs> Queue<ListenerArgsPairing> getThisAndDependentArgsAsQueue(Event<T> event, Object sender, T args)
    {
        Collection<ListenerArgsPairing> listenerArgsPairings = new ArrayList<ListenerArgsPairing>();
        
        for(EventArgs i : generateThisAndDependentArgs(event, sender, args))
            for(EventListener j : i.getEvent().getListeners())
                listenerArgsPairings.add(new ListenerArgsPairing(j, i));
        
        return new SortedQueue<ListenerArgsPairing>(listenerArgsPairings, new Lambda<ListenerArgsPairing, Comparable>()
        {
            @Override
            public Comparable getMember(ListenerArgsPairing parent)
            { return parent.getListener().getPriority(); }
        });
    }
}