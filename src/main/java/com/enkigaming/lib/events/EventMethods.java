package com.enkigaming.lib.events;

import com.enkigaming.lib.collections.CollectionMethods;
import com.enkigaming.lib.tuples.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class EventMethods
{
    //<editor-fold defaultstate="collapsed" desc="Multiple raise">
    public static void raiseMultiple(Object sender, Pair<? extends Event<?>, ? extends EventArgs>... events)
    { raiseMultiple(sender, Arrays.asList(events)); }
    
    public static void raiseMultiple(Object sender, boolean shareCancellation,
                                     Pair<? extends Event<?>, ? extends EventArgs>... events)
    { raiseMultiple(sender, shareCancellation, Arrays.asList(events)); }
    
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
    
    public static void raiseMultiple(Object sender, Map<? extends Event<?>, ? extends EventArgs> events)
    { raiseMultiple(sender, CollectionMethods.getMapAsCollectionOfPairs(events)); }
    
    public static void raiseMultiple(Object sender, boolean shareCancellation,
                                     Map<? extends Event<?>, ? extends EventArgs> events)
    { raiseMultiple(sender, shareCancellation, CollectionMethods.getMapAsCollectionOfPairs(events)); }
    
    public static void raiseMultiplePostEvent(Object sender, Pair<? extends Event<?>, ? extends EventArgs>... events)
    { raiseMultiple(sender, Arrays.asList(events)); }
    
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
    
    public static void raiseMultiplePostEvent(Object sender, Map<? extends Event<?>, ? extends EventArgs> events)
    { raiseMultiplePostEvent(sender, CollectionMethods.getMapAsCollectionOfPairs(events)); }
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
}