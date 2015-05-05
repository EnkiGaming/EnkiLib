package com.enkigaming.mc.lib.compatability;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import java.util.UUID;

public abstract class EnkiServer
{
    public static class PlayerConnectionArgs extends StandardEventArgs
    {
        public PlayerConnectionArgs(UUID playerId)
        { this.playerId = playerId; }
        
        UUID playerId;
        
        public UUID getPlayerId()
        { return playerId; }
        
        public EnkiPlayer getPlayer()
        { return CompatabilityAccess.getPlayer(playerId); }
    }
    
    public static class PlayerJoinedArgs extends PlayerConnectionArgs
    {
        public PlayerJoinedArgs(UUID playerId)
        { super(playerId); }
    }
    
    public static class PlayerLeftArgs extends PlayerConnectionArgs
    {
        public PlayerLeftArgs(UUID playerId)
        { super(playerId); }
    }
    
    public final Event<PlayerJoinedArgs> playerJoined = new StandardEvent<PlayerJoinedArgs>();
    
    public final Event<PlayerLeftArgs> playerLeft = new StandardEvent<PlayerLeftArgs>();
}