package com.enkigaming.mcforge.lib.eventlisteners;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventMethods;
import com.enkigaming.mc.lib.compatability.EnkiPlayer.DiedArgs;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class PlayerDeathPrePostListener
{
    // Single listener for both pre and post as forge provides literally no way of reliably listening to
    // LivingDeathEvent or player deaths in general post-event short of setting a timer/tick timer to fire later on.
    
    final Collection<ForgePlayer> playersToRaise = Collections.newSetFromMap(new WeakHashMap<ForgePlayer, Boolean>());
    public static PlayerDeathPrePostListener instance = new PlayerDeathPrePostListener();
    
    public void registerForgePlayer(ForgePlayer player)
    {
        synchronized(playersToRaise)
        { playersToRaise.add(player); }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if(!(event.entity instanceof EntityPlayer))
            return;
        
        Map<Event<DiedArgs>, DiedArgs> toRaise = new HashMap<Event<DiedArgs>, DiedArgs>();
        UUID playerId = ((EntityPlayer)event.entity).getGameProfile().getId();
        
        synchronized(playersToRaise)
        {
            for(ForgePlayer i : playersToRaise)
                if(i.getId().equals(playerId))
                    toRaise.put(i.died, new DiedArgs());
        }
        
        try
        { EventMethods.raiseMultiple(this, toRaise); }
        finally
        { EventMethods.raiseMultiplePostEvent(this, toRaise); }
    }
}