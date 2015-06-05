package com.enkigaming.lib.events;

import com.enkigaming.lib.collections.CollectionMethods;
import com.enkigaming.lib.tuples.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Static methods relating to events and their implementation/execution.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
public class EventMethods
{
    //<editor-fold defaultstate="collapsed" desc="Multiple raise">
    /**
     * Raises multiple events together using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param events An array of pairs of the events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiple(Object sender, Pair<? extends Event<?>, ? extends EventArgs>... events)
    { raiseMultiple(sender, Arrays.asList(events)); }
    
    /**
     * Raises multiple events together using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param shareCancellation Whether or not the eventargs passed to listeners in different events should share
     * cancellation states.
     * @param events An array of pairs of the events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiple(Object sender, boolean shareCancellation,
                                     Pair<? extends Event<?>, ? extends EventArgs>... events)
    { raiseMultiple(sender, shareCancellation, Arrays.asList(events)); }
    
    /**
     * Raises multiple events together using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param events A collection of pairs of the events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiple(Object sender,
                                     Collection<? extends Pair<? extends Event<?>, ? extends EventArgs>> events)
    {
        Collection<Pair<? extends Event<?>, ? extends EventArgs>> alongsideEvents
                = new ArrayList<Pair<? extends Event<?>, ? extends EventArgs>>(events);
        
        Pair<? extends Event<?>, ? extends EventArgs> toCallOn = null;
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : alongsideEvents)
        {
            toCallOn = i;
            break;
        }
        
        if(toCallOn == null)
            return;
        
        alongsideEvents.remove(toCallOn);
        ((Event<EventArgs>)toCallOn.getFirst()).raiseAlongside(sender, toCallOn.getSecond(), events);
    }
    
    /**
     * Raises multiple events together using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param shareCancellation Whether or not the eventargs passed to listeners in different events should share
     * cancellation states.
     * @param events A collection of pairs of the events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiple(Object sender, boolean shareCancellation,
                                     Collection<? extends Pair<? extends Event<?>, ? extends EventArgs>> events)
    {
        Collection<Pair<? extends Event<?>, ? extends EventArgs>> alongsideEvents
                = new ArrayList<Pair<? extends Event<?>, ? extends EventArgs>>(events);
        Pair<? extends Event<?>, ? extends EventArgs> toCallOn = null;
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : alongsideEvents)
        {
            toCallOn = i;
            break;
        }
        
        if(toCallOn == null)
            return;
        
        alongsideEvents.remove(toCallOn);
        ((Event<EventArgs>)toCallOn.getFirst()).raiseAlongside(sender, toCallOn.getSecond(), shareCancellation, events);
    }
    
    /**
     * Raises multiple events together using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param events A map of the events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiple(Object sender, Map<? extends Event<?>, ? extends EventArgs> events)
    { raiseMultiple(sender, CollectionMethods.getMapAsCollectionOfPairs(events)); }
    
    /**
     * Raises multiple events together using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param shareCancellation Whether or not the eventargs passed to listeners in different events should share
     * cancellation states.
     * @param events A map of the events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiple(Object sender, boolean shareCancellation,
                                     Map<? extends Event<?>, ? extends EventArgs> events)
    { raiseMultiple(sender, shareCancellation, CollectionMethods.getMapAsCollectionOfPairs(events)); }
    
    /**
     * Raises multiple events together post-event using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param events An array of pairs of events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiplePostEvent(Object sender, Pair<? extends Event<?>, ? extends EventArgs>... events)
    { raiseMultiple(sender, Arrays.asList(events)); }
    
    /**
     * Raises multiple events together post-event using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param events A collection of pairs of events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiplePostEvent(Object sender,
                                              Collection<? extends Pair<? extends Event<?>, ? extends EventArgs>> events)
    {
        Collection<Pair<? extends Event<?>, ? extends EventArgs>> alongsideEvents
                = new ArrayList<Pair<? extends Event<?>, ? extends EventArgs>>(events);
        
        Pair<? extends Event<?>, ? extends EventArgs> toCallOn = null;
        
        for(Pair<? extends Event<?>, ? extends EventArgs> i : alongsideEvents)
        {
            toCallOn = i;
            break;
        }
        
        if(toCallOn == null)
            return;
        
        alongsideEvents.remove(toCallOn);
        ((Event<EventArgs>)toCallOn.getFirst()).raisePostEventAlongside(sender, toCallOn.getSecond(), events);
    }
    
    /**
     * Raises multiple events together post-event using the EventArgs objects they're paired with.
     * @param sender The object that caused the event raise.
     * @param events A map of events to be raised and the eventargs to be passed to their listeners.
     */
    public static void raiseMultiplePostEvent(Object sender, Map<? extends Event<?>, ? extends EventArgs> events)
    { raiseMultiplePostEvent(sender, CollectionMethods.getMapAsCollectionOfPairs(events)); }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Event args relating">
    /**
     * Causes all eventargs objects passed in to return eachother in their related args methods.
     * @param args An array of the args to relate together.
     */
    public static void relateArgs(EventArgs... args)
    { relateArgs(Arrays.asList(args)); }
    
    /**
     * Causes all eventargs objects passed in to return eachother in their related args methods.
     * @param args A collection of the args to relate together.
     */
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
}