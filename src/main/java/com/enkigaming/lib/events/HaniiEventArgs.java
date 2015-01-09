package com.enkigaming.lib.events;

import com.enkigaming.lib.events.exceptions.EventArgsFinishedBeforeStartedException;
import com.enkigaming.lib.events.exceptions.EventArgsModifiedWhenImmutableException;
import com.enkigaming.lib.events.exceptions.EventArgsMultipleUseException;
import com.enkigaming.lib.events.exceptions.EventArgsUsedPostBeforePreException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class HaniiEventArgs implements EventArgs
{
    protected enum Status
    {
        Unused,
        UsingPreEvent,
        UsedPreEvent,
        UsingPostEvent,
        UsedPostEvent
    }
    
    Event<? extends EventArgs> event = null;
    boolean cancelled = false;
    boolean mutable = true;
    EventArgs parentArgs = null;
    Status status = Status.Unused;
    Queue<ListenerArgsPairing> listenerQueue = null;
    
    final Set<EventArgs> relatedMasterArgs = new HashSet<EventArgs>();
    final Set<EventArgs> dependentArgs = new HashSet<EventArgs>();
    
    final Object cancelledBusy = new Object();
    final Object mutableBusy = new Object();
    final Object parentArgsBusy = new Object();
    final Object eventBusy = new Object();
    final Object statusBusy = new Object();
    final Object listenerQueueBusy = new Object();
    
    @Override
    public boolean isCancelled()
    {
        synchronized(parentArgsBusy)
        {
            if(getParentArgs() != null)
                return getParentArgs().isCancelled();
        }
        
        synchronized(cancelledBusy)
        { return cancelled; }
    }

    @Override
    public boolean setCancelled(boolean cancellation)
    {
        if(shouldBeMutable())
        {
            synchronized(parentArgsBusy)
            {
                EventArgs parent = getParentArgs();
                
                if(parent != null)
                    return parent.setCancelled(cancellation);
            }
            
            synchronized(cancelledBusy)
            {
                boolean oldValue = cancelled;
                cancelled = cancellation;
                return oldValue;
            }
        }
        
        throw new EventArgsModifiedWhenImmutableException();
    }

    @Override
    public boolean shouldBeMutable()
    {
        synchronized(parentArgsBusy)
        {
            if(getParentArgs() != null)
                return getParentArgs().shouldBeMutable();
        }
        
        synchronized(mutableBusy)
        { return mutable; }
    }
    
    protected void makeImmutable()
    {
        synchronized(parentArgsBusy)
        {
            EventArgs parent = getParentArgs();

            if(parent != null)
            {
                parent.getTechnicalAccessor().makeImmutable();
                return;
            }
        }
        
        synchronized(mutableBusy)
        { mutable = false; }
    }

    @Override
    public Collection<EventArgs> getRelatedMasterArgs()
    {
        synchronized(parentArgs)
        {
            EventArgs master = getMasterArgs();
            
            if(master != null)
                return master.getRelatedMasterArgs();
        }
        
        synchronized(relatedMasterArgs)
        { return new ArrayList<EventArgs>(relatedMasterArgs); }
    }
    
    @Override
    public Collection<EventArgs> getRelatedArgs()
    {
        Collection<EventArgs> masters = new HashSet<EventArgs>(getRelatedMasterArgs());
        EventArgs thisMaster = getMasterArgs();
        
        if(thisMaster == null)
            masters.add(this);
        else
            masters.add(thisMaster);
        
        Collection<EventArgs> relatedArgs = new HashSet<EventArgs>();
        
        for(EventArgs i : masters)
            relatedArgs.addAll(i.getDependentArgs(true, true));
        
        return relatedArgs;
    }
    
    protected void addRelatedMasterArgs(EventArgs args)
    {
        synchronized(relatedMasterArgs)
        { relatedMasterArgs.add(args); }
    }

    protected void addRelatedMasterArgs(EventArgs... args)
    {
        synchronized(relatedMasterArgs)
        { relatedMasterArgs.addAll(Arrays.asList(args)); }
    }

    protected void addRelatedMasterArgs(Collection<? extends EventArgs> args)
    {
        synchronized(relatedMasterArgs)
        { relatedMasterArgs.addAll(args); }
    }

    @Override
    public Collection<EventArgs> getDependentArgs(boolean getDependantsCascadingly)
    { return getDependentArgs(false, getDependantsCascadingly); }

    @Override
    public Collection<EventArgs> getDependentArgs(boolean includeThis, boolean getDependantsCascadingly)
    {
        Collection<EventArgs> returnArgs;
        
        synchronized(dependentArgs)
        { returnArgs = new HashSet<EventArgs>(dependentArgs); }
        
        if(getDependantsCascadingly)
        {
            Collection<EventArgs> cascaded = new HashSet<EventArgs>();
            
            for(EventArgs i : returnArgs)
                cascaded.addAll(i.getDependentArgs(false, true));
            
            returnArgs.addAll(cascaded);
        }
        
        if(includeThis)
            returnArgs.add(this);
        
        return returnArgs;
    }

    @Override
    public Collection<EventArgs> getDependentArgs()
    { return getDependentArgs(false, true); }

    @Override
    public Collection<EventArgs> getDirectlyDependentArgs()
    { return getDependentArgs(false, false); }
    
    protected void addDependentArgs(EventArgs args)
    {
        synchronized(dependentArgs)
        { dependentArgs.add(args); }
    }

    @Override
    public EventArgs getParentArgs()
    {
        synchronized(parentArgsBusy)
        { return parentArgs; }
    }
    
    protected void setParentArgs(EventArgs args)
    {
        synchronized(parentArgsBusy)
        { parentArgs = args; }
    }

    @Override
    public EventArgs getMasterArgs()
    {
        synchronized(parentArgsBusy)
        {
            if(parentArgs == null)
                return null;
            
            if(parentArgs.getParentArgs() == null)
                return parentArgs;
            
            return parentArgs.getMasterArgs();
        }
    }

    @Override
    public Event<? extends EventArgs> getEvent()
    {
        synchronized(eventBusy)
        { return event; }
    }
    
    protected void setEvent(Event<? extends EventArgs> event)
    {
        synchronized(eventBusy)
        { this.event = event; }
    }
    
    protected void markAsUsingPreEvent()
    {
        synchronized(statusBusy)
        {
            if(status != Status.Unused)
                throw new EventArgsMultipleUseException();
            
            status = Status.UsingPreEvent;
        }
    }

    protected void markAsUsedPreEvent()
    {
        synchronized(statusBusy)
        {
            switch(status)
            {
                case Unused:
                    throw new EventArgsFinishedBeforeStartedException("Has not been marked as using pre-event.");
                case UsingPreEvent:
                {
                    status = Status.UsedPreEvent;
                    break;
                }
                case UsedPreEvent:
                case UsingPostEvent:
                case UsedPostEvent:
                    throw new EventArgsMultipleUseException("Already used pre-event.");
            }
        }
    }

    protected void markAsUsingPostEvent()
    {
        synchronized(statusBusy)
        {
            switch(status)
            {
                case Unused:
                    throw new EventArgsUsedPostBeforePreException("Has not been marked as being used pre-event.");
                case UsingPreEvent:
                    throw new EventArgsUsedPostBeforePreException("Has not finished being used pre-event.");
                case UsedPreEvent:
                {
                    status = Status.UsingPostEvent;
                    break;
                }
                case UsingPostEvent:
                case UsedPostEvent:
                    throw new EventArgsMultipleUseException("Already used post-event.");
            }
        }
    }

    protected void markAsUsedPostEvent()
    {
        synchronized(statusBusy)
        {
            switch(status)
            {
                case Unused:
                    throw new EventArgsUsedPostBeforePreException("Has not been marked as being used pre-event.");
                case UsingPreEvent:
                    throw new EventArgsUsedPostBeforePreException("Has not finished being used pre-event.");
                case UsedPreEvent:
                    throw new EventArgsFinishedBeforeStartedException("Has not been marked as using post-event.");
                case UsingPostEvent:
                {
                    status = Status.UsedPostEvent;
                    break;
                }
                case UsedPostEvent:
                    throw new EventArgsMultipleUseException("Already used post-event.");
            }
        }
    }
    
    protected void setListenerQueue(Queue<ListenerArgsPairing> queue)
    {
        synchronized(listenerQueueBusy)
        { listenerQueue = queue; }
    }
    
    protected Queue<ListenerArgsPairing> getListenerQueue()
    {
        synchronized(listenerQueueBusy)
        { return listenerQueue; }
    }

    @Override
    public TechnicalAccessor getTechnicalAccessor()
    {
        return new TechnicalAccessor()
        {
            @Override
            public void markAsUsingPreEvent()
            { HaniiEventArgs.this.markAsUsingPreEvent(); }

            @Override
            public void markAsUsedPreEvent()
            { HaniiEventArgs.this.markAsUsedPreEvent(); }

            @Override
            public void markAsUsingPostEvent()
            { HaniiEventArgs.this.markAsUsingPostEvent(); }

            @Override
            public void markAsUsedPostEvent()
            { HaniiEventArgs.this.markAsUsedPostEvent(); }

            @Override
            public void setEvent(Event<? extends EventArgs> event)
            { setEvent(event); }

            @Override
            public void setParentArgs(EventArgs args)
            { HaniiEventArgs.this.setParentArgs(args); }

            @Override
            public void makeImmutable()
            { HaniiEventArgs.this.makeImmutable(); }

            @Override
            public void addDependentArgs(EventArgs args)
            { HaniiEventArgs.this.addDependentArgs(args); }

            @Override
            public void addRelatedMasterArgs(EventArgs args)
            { HaniiEventArgs.this.addRelatedMasterArgs(args); }

            @Override
            public void addRelatedMasterArgs(EventArgs... args)
            { HaniiEventArgs.this.addRelatedMasterArgs(args); }

            @Override
            public void addRelatedMasterArgs(Collection<? extends EventArgs> args)
            { HaniiEventArgs.this.addRelatedMasterArgs(args); }

            @Override
            public void setListenerQueue(Queue<ListenerArgsPairing> listenerQueue)
            { HaniiEventArgs.this.setListenerQueue(listenerQueue); }

            @Override
            public Queue<ListenerArgsPairing> getListenerQueue()
            { return HaniiEventArgs.this.getListenerQueue(); }
        };
    }
    
}