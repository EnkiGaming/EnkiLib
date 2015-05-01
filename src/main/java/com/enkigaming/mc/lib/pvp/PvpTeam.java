package com.enkigaming.mc.lib.pvp;

import java.util.Collection;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;

public class PvpTeam
{
    public PvpTeam(UUID... players)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public PvpTeam(Collection<UUID> players)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    String name;
    Collection<UUID> players;
    
    /**
     * Gets the players currently in the team.
     * @return A collection containing the player IDs of the players in this team.
     */
    public Collection<UUID> getPlayers()
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Adds a player to the team.
     * @return True if the player was added. Otherwise false. (If the player was already in the team)
     */
    public boolean addPlayer()
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Removes a player from the team.
     * @return True if the player was removed. Otherwise false. (If the player wasn't in the team.)
     */
    public boolean removePlayer()
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Checks whether or not the player is in this team.
     * @param playerId The ID of the player to check for.
     * @return True if the player is in the team. Otherwise, false.
     */
    public boolean containsPlayer(UUID playerId)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Checks whether or not all of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if all of the players are in the team. Otherwise, false.
     */
    public boolean containsAllPlayers(UUID... playerIds)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Checks whether or not all of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if all of the players are in the team. Otherwise, false.
     */
    public boolean containsAllPlayers(Collection<? extends UUID> playerIds)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Checks whether or not any of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if any of the players are in the team. Otherwise, false.
     */
    public boolean containsAnyPlayers(UUID... playerIds)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Checks whether or not any of the passed players are in the team.
     * @param playerIds The IDs of the players to check for.
     * @return True if any of the players are in the team. Otherwise, false.
     */
    public boolean containsAnyPlayers(Collection<? extends UUID> playerIds)
    {
        throw new NotImplementedException("Not implemented yet.");
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
        throw new NotImplementedException("Not implemented yet.");
    }
}