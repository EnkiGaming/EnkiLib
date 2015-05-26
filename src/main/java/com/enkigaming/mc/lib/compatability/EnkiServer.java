package com.enkigaming.mc.lib.compatability;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.google.common.cache.CacheBuilder;
import java.util.Map;
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
    
    protected final Map<Integer, EnkiWorld> worlds = CacheBuilder.newBuilder()
                                                                 .concurrencyLevel(1)
                                                                 .weakValues()
                                                                 .<Integer, EnkiWorld>build()
                                                                 .asMap();
    
    protected final Map<UUID, EnkiPlayer> players = CacheBuilder.newBuilder()
                                                                .concurrencyLevel(1)
                                                                .weakValues()
                                                                .<UUID, EnkiPlayer>build()
                                                                .asMap();
    
    public final Event<PlayerJoinedArgs> playerJoined = new StandardEvent<PlayerJoinedArgs>();
    
    public final Event<PlayerLeftArgs> playerLeft = new StandardEvent<PlayerLeftArgs>();
    
    public static EnkiServer getInstance()
    { return CompatabilityAccess.getServer(); }
    
    public EnkiWorld getWorld(int worldId)
    {
        synchronized(worlds)
        {
            EnkiWorld world = worlds.get(worldId);
            
            if(world == null)
            {
                world = getNewWorldObject(worldId);
                worlds.put(worldId, world);
            }
            
            return world;
        }
    }
    
    public EnkiPlayer getPlayer(UUID playerId)
    {
        synchronized(players)
        {
            EnkiPlayer player = players.get(playerId);
            
            if(player == null)
            {
                player = getNewPlayerObject(playerId);
                players.put(playerId, player);
            }
            
            return player;
        }
    }
    
    protected abstract EnkiWorld getNewWorldObject(int worldId);
    
    protected abstract EnkiPlayer getNewPlayerObject(UUID playerId);
}