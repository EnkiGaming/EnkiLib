package com.enkigaming.mc.lib.pvp;

import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mc.lib.compatability.EnkiServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;

public class PvpTeam
{
    public PvpTeam(UUID... players)
    { this.players = new HashSet<UUID>(Arrays.asList(players)); }
    
    public PvpTeam(Collection<? extends UUID> players)
    { this.players = new HashSet<UUID>(players); }
    
    public PvpTeam(String name, UUID... players)
    {
        this.name = name;
        this.players = new HashSet<UUID>(Arrays.asList(players));
    }
    
    public PvpTeam(String name, Collection<? extends UUID> players)
    {
        this.name = name;
        this.players = new HashSet<UUID>(players);
    }
    
    protected String name = null;
    final protected Collection<UUID> players;
    
    final protected Object nameBusy = new Object();
    
    /**
     * Gets the players currently in the team.
     * @return A collection containing the player IDs of the players in this team.
     */
    public Collection<UUID> getPlayers()
    {
        synchronized(players)
        { return new ArrayList<UUID>(players); }
    }
    
    /**
     * Adds a player to the team.
     * @param playerId The player ID of the player to add to the team.
     * @return True if the player was added. Otherwise false. (If the player was already in the team)
     */
    public boolean addPlayer(UUID playerId)
    {
        synchronized(players)
        { return players.add(playerId); }
    }
    
    /**
     * Removes a player from the team.
     * @param playerId The player ID of the player to remove from the team.
     * @return True if the player was removed. Otherwise false. (If the player wasn't in the team.)
     */
    public boolean removePlayer(UUID playerId)
    {
        synchronized(players)
        { return players.remove(playerId); }
    }
    
    /**
     * Checks whether or not the player is in this team.
     * @param playerId The ID of the player to check for.
     * @return True if the player is in the team. Otherwise, false.
     */
    public boolean containsPlayer(UUID playerId)
    {
        synchronized(players)
        { return players.contains(playerId); }
    }
    
    /**
     * Checks whether or not all of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if all of the players are in the team. Otherwise, false.
     */
    public boolean containsAllPlayers(UUID... playerIds)
    {
        synchronized(players)
        {
            for(UUID i : playerIds)
                if(!players.contains(i))
                    return false;
            
            return true;
        }
    }
    
    /**
     * Checks whether or not all of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if all of the players are in the team. Otherwise, false.
     */
    public boolean containsAllPlayers(Collection<? extends UUID> playerIds)
    {
        synchronized(players)
        {
            for(UUID i : playerIds)
                if(!players.contains(i))
                    return false;
            
            return true;
        }
    }
    
    /**
     * Checks whether or not any of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if any of the players are in the team. Otherwise, false.
     */
    public boolean containsAnyPlayers(UUID... playerIds)
    {
        synchronized(players)
        {
            for(UUID i : playerIds)
                if(players.contains(i))
                    return true;
            
            return false;
        }
    }
    
    /**
     * Checks whether or not any of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if any of the players are in the team. Otherwise, false.
     */
    public boolean containsAnyPlayers(Collection<? extends UUID> playerIds)
    {
        synchronized(players)
        {
            for(UUID i : playerIds)
                if(players.contains(i))
                    return true;
            
            return false;
        }
    }
    
    /**
     * Gets the team name.
     * @return The team name.
     */
    public String getName()
    { return name; }
    
    /**
     * Sets the team name.
     * @param newName The new name for the team.
     * @return The old team name.
     */
    public String setName(String newName)
    {
        String old = name;
        name = newName;
        return old;
    }
    
    /**
     * Sends a chat message to all players in the team.
     * @param message The message to send.
     */
    public void messagePlayers(String message)
    {
        EnkiServer server = EnkiServer.getInstance();
        
        for(UUID i : getPlayers())
        {
            EnkiPlayer player = server.getPlayer(i);
            
            if(player != null)
                player.print(message);
        }
    }
}