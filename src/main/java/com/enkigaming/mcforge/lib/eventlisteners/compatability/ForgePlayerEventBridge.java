package com.enkigaming.mcforge.lib.eventlisteners.compatability;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventMethods;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mc.lib.compatability.EnkiPlayer.ConnectedArgs;
import com.enkigaming.mc.lib.compatability.EnkiPlayer.DiedArgs;
import com.enkigaming.mc.lib.compatability.EnkiServer;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class ForgePlayerEventBridge
{
    final Collection<ForgePlayer> playersToRaise = Collections.newSetFromMap(new WeakHashMap<ForgePlayer, Boolean>());
    public static ForgePlayerEventBridge instance = new ForgePlayerEventBridge();
    
    public void registerForgePlayer(ForgePlayer player)
    {
        synchronized(playersToRaise)
        { playersToRaise.add(player); }
    }
    
    Collection<ForgePlayer> getForgePlayerObjects(UUID playerId)
    {
        HashSet<ForgePlayer> fplayers = new HashSet<ForgePlayer>();
        
        synchronized(playersToRaise)
        {
            for(ForgePlayer i : playersToRaise)
                if(i.getId().equals(playerId))
                    fplayers.add(i);
        }
        
        return fplayers;
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        Map<Event<ConnectedArgs>, EnkiPlayer.ConnectedArgs> toRaise = new HashMap<Event<ConnectedArgs>, EnkiPlayer.ConnectedArgs>();
        EnkiServer.PlayerJoinedArgs serverEventArgs = new EnkiServer.PlayerJoinedArgs(event.player.getGameProfile().getId());
        
        for(ForgePlayer i : getForgePlayerObjects(event.player.getGameProfile().getId()))
            toRaise.put(i.connected, new ConnectedArgs());
        
        try
        { CompatabilityAccess.getServer().playerJoined.raiseAlongside(this, serverEventArgs, toRaise); }
        finally
        { CompatabilityAccess.getServer().playerJoined.raisePostEventAlongside(this, serverEventArgs, toRaise); }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if(!(event.entity instanceof EntityPlayer))
            return;
        
        Map<Event<DiedArgs>, DiedArgs> toRaise = new HashMap<Event<DiedArgs>, DiedArgs>();
        
        for(ForgePlayer i : getForgePlayerObjects(((EntityPlayer)event.entity).getGameProfile().getId()))
            toRaise.put(i.died, new DiedArgs());
        
        try
        { EventMethods.raiseMultiple(this, toRaise); }
        finally
        { EventMethods.raiseMultiplePostEvent(this, toRaise); }
    }
}