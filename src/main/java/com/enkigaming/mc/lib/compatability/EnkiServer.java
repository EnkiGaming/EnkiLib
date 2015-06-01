package com.enkigaming.mc.lib.compatability;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.google.common.cache.CacheBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class EnkiServer implements CommandSender
{
    public static class PlayerConnectionArgs extends StandardEventArgs
    {
        public PlayerConnectionArgs(UUID playerId)
        { this.playerId = playerId; }
        
        UUID playerId;
        
        public UUID getPlayerId()
        { return playerId; }
        
        public EnkiPlayer getPlayer()
        { return EnkiServer.getInstance().getPlayer(playerId); }
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
    
    public static class CommandArgs extends StandardEventArgs
    {
        public CommandArgs(CommandSender sender, String baseCommand, List<String> args)
        {
            this.sender = sender;
            this.baseCommand = baseCommand;
            this.args = new ArrayList<String>(args);
        }
        
        CommandSender sender;
        String baseCommand;
        List<String> args;
        
        public CommandSender getSender()
        { return sender; }
        
        public String getBaseCommand()
        { return baseCommand; }
        
        public List<String> getCommandArgs()
        { return new ArrayList<String>(args); }
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
    
    /**
     * A command is made, by a player, command-block, the console, etc. Listeners that intend on performing an action
     * when an event is fired should use the monitor or post-event priorities, and only use earlier (mutable) priorities
     * for affecting the command itself.
     */
    public final Event<CommandArgs> commandDispatched = new StandardEvent<CommandArgs>();
    
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