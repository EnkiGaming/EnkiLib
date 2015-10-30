package com.enkigaming.mc.lib.misc;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

public class TickCountdownTimer
{
    public static class TickedArgs extends StandardEventArgs
    {
        public TickedArgs(int secondsLeft)
        { this.secondsLeft = secondsLeft; }
        
        int secondsLeft;
        
        public int getNumberOfSecondsLeft()
        { return secondsLeft; }
        
        public int setNumberOfSecondsLeft(int newNumberOfSecondsLeft)
        {
            int temp = secondsLeft;
            secondsLeft = newNumberOfSecondsLeft;
            return temp;
        }
    }
    
    public static class FinishedArgs extends StandardEventArgs
    {} // nothing, atm
    
    public TickCountdownTimer(int seconds)
    { numberOfSecondsLeft = seconds; }
    
    public TickCountdownTimer(int minutes, int seconds)
    { this((minutes * 60) + seconds); }
    
    public TickCountdownTimer(int hours, int minutes, int seconds)
    { this((((hours * 60) + minutes) * 60) + seconds); }
    
    int numberOfSecondsLeft;
    
    final Object secondsLeftBusy = new Object();
    
    public Event<TickedArgs> ticked = new StandardEvent<TickedArgs>();
    public Event<FinishedArgs> finished = new StandardEvent<FinishedArgs>();
    
    private static final Collection<TickCountdownTimer> objectsToTick = Collections.newSetFromMap(new WeakHashMap<TickCountdownTimer, Boolean>());
    
    public int setTimeLeft(int seconds)
    {
        synchronized(secondsLeftBusy)
        {
            int old = numberOfSecondsLeft;
            numberOfSecondsLeft = seconds;
            return old;
        }
    }
    
    public int setTimeLeft(int minutes, int seconds)
    { return setTimeLeft((minutes * 60) + seconds); }
    
    public int setTimeLeft(int hours, int minutes, int seconds)
    { return setTimeLeft((((hours * 60) + minutes) * 60) + seconds); }
    
    public void start()
    {
        synchronized(objectsToTick)
        { objectsToTick.add(this); }
    }
    
    public void resume()
    { start(); } // purely syntactic, for using alongside pause();
    
    public void pause()
    {
        synchronized(objectsToTick)
        { objectsToTick.remove(this); }
    }
    
    public void finish()
    {
        FinishedArgs args = new FinishedArgs();
        
        try
        { finished.raise(this, args); }
        finally
        {
            try
            { finished.raisePostEvent(secondsLeftBusy, args); }
            finally
            {
                synchronized(objectsToTick)
                { objectsToTick.remove(this); }
            }
        }
    }
    
    protected void tick()
    {
        synchronized(secondsLeftBusy)
        {
            TickedArgs args = new TickedArgs(numberOfSecondsLeft - 1);

            try
            {
                ticked.raise(this, args);
                numberOfSecondsLeft = args.getNumberOfSecondsLeft();
            }
            finally
            {
                try
                { ticked.raisePostEvent(this, args); }
                finally
                { if(numberOfSecondsLeft <= 0) finish(); }
            }
        }
    }
    
    public static void passSecond()
    {
        synchronized(objectsToTick)
        {
            for(TickCountdownTimer i : objectsToTick)
                i.tick();
        }
    }
}