package com.enkigaming.mcforge.lib.eventlisteners;

import com.enkigaming.lib.events.Event;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiPlayer.ConnectedArgs;
import com.enkigaming.mc.lib.compatability.EnkiServer.PlayerJoinedArgs;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class PlayerLogInPrePostListener
{
    // Single listener for both pre and post as PlayerLoggedIn can only be listened to post-event, and has no pre-event
    // equivalent.
    
    final Collection<ForgePlayer> playersToRaise = Collections.newSetFromMap(new WeakHashMap<ForgePlayer, Boolean>());
    public static PlayerLogInPrePostListener instance = new PlayerLogInPrePostListener();
    
    public void registerForgePlayer(ForgePlayer player)
    {
        synchronized(playersToRaise)
        { playersToRaise.add(player); }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        Map<Event<ConnectedArgs>, ConnectedArgs> toRaise = new HashMap<Event<ConnectedArgs>, ConnectedArgs>();
        PlayerJoinedArgs serverEventArgs = new PlayerJoinedArgs(event.player.getGameProfile().getId());
        
        synchronized(playersToRaise)
        {
            for(ForgePlayer i : playersToRaise)
                if(event.player.getGameProfile().getId().equals(i.getId()))
                    toRaise.put(i.connected, new ConnectedArgs());
        }
        
        try
        { CompatabilityAccess.getServer().playerJoined.raiseAlongside(this, serverEventArgs, toRaise); }
        finally
        { CompatabilityAccess.getServer().playerJoined.raisePostEventAlongside(this, serverEventArgs, toRaise); }
    }
}