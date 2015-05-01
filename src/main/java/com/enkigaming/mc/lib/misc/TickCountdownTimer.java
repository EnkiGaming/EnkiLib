package com.enkigaming.mc.lib.misc;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventListener;
import com.enkigaming.lib.events.ListenerPriority;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.enkigaming.mc.lib.compatability.CompatabilityEvents;
import com.enkigaming.mc.lib.compatability.CompatabilityEvents.SecondTickArgs;

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
    
    EventListener<SecondTickArgs> secondPassedListener = new EventListener<SecondTickArgs>()
    {
        // Once support for Java 7 is dropped, this can be switched for a lambda expression.
        @Override
        public void onEvent(Object sender, SecondTickArgs args)
        { tick(); }
    };
    
    public Event<TickedArgs> ticked = new StandardEvent<TickedArgs>();
    public Event<FinishedArgs> finished = new StandardEvent<FinishedArgs>();
    
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
    { CompatabilityEvents.secondPassed.register(ListenerPriority.Monitor, secondPassedListener); }
    
    public void resume()
    { start(); } // purely syntactic, for using alongside pause();
    
    public void pause()
    { CompatabilityEvents.secondPassed.deregister(secondPassedListener); }
    
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
            { CompatabilityEvents.secondPassed.deregister(secondPassedListener); }
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
}