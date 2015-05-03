package com.enkigaming.mcforge.lib.eventlisteners;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventArgs;
import com.enkigaming.lib.tuples.Pair;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mc.lib.compatability.EnkiPlayer.DiedArgs;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class PlayerDeathPostListener
{
//    Map<UUID, Collection<WeakReference<Event<EnkiPlayer.DiedArgs>>>> eventsToRaise
//        = new HashMap<UUID, Collection<WeakReference<Event<EnkiPlayer.DiedArgs>>>>();
//    
//    final Object eventsToRaiseBusy = new Object();
//    
//    public void addEventToRaise(UUID playerId, Event<EnkiPlayer.DiedArgs> diedEvent)
//    {
//        synchronized(eventsToRaiseBusy)
//        {
//            Collection<WeakReference<Event<EnkiPlayer.DiedArgs>>> eventsForPlayer = eventsToRaise.get(playerId);
//            
//            if(eventsForPlayer == null)
//            {
//                eventsForPlayer = new HashSet<WeakReference<Event<EnkiPlayer.DiedArgs>>>();
//                eventsToRaise.put(playerId, eventsForPlayer);
//            }
//            
//            eventsForPlayer.add(new WeakReference<Event<EnkiPlayer.DiedArgs>>(diedEvent));
//        }
//    }
    
    Map<WeakReference<ForgePlayer>, DiedArgs> waitingArgs = new HashMap<WeakReference<ForgePlayer>, DiedArgs>();
    
    final Object waitingArgsBusy = new Object();
    
    void clearUnusedWaitingArgs()
    {
        synchronized(waitingArgsBusy)
        {
            Collection<WeakReference<ForgePlayer>> toRemove = new HashSet<WeakReference<ForgePlayer>>();
            
            for(WeakReference<ForgePlayer> ref : waitingArgs.keySet())
                if(ref.get() == null)
                    toRemove.add(ref);
            
            for(WeakReference<ForgePlayer> ref : toRemove)
                waitingArgs.remove(ref);
        }
    }
    
    public void addArgsForPostEvent(Map<WeakReference<ForgePlayer>, DiedArgs> argsToAdd)
    {
        synchronized(waitingArgsBusy)
        {
            clearUnusedWaitingArgs();
            waitingArgs.putAll(argsToAdd);
        }
    }
    
    @SubscribeEvent
    public void onPlayerDeathPost(LivingDeathEvent event)
    {
        if(!(event.entity instanceof EntityPlayer))
            return;
        
        Collection<Pair<Event<?>, EventArgs>> toRaise = new HashSet<Pair<Event<?>, EventArgs>>();
        
        synchronized(waitingArgsBusy)
        {
            clearUnusedWaitingArgs();
            
            for(Entry<WeakReference<ForgePlayer>, DiedArgs> playerAndArgs : waitingArgs.entrySet())
            {
                /* continue writing. */
            }
        }
    }
}