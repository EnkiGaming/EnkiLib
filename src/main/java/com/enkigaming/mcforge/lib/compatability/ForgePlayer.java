package com.enkigaming.mcforge.lib.compatability;

import com.enkigaming.lib.events.exceptions.NoSuchUsernameException;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mcforge.lib.EnkiLib;
import com.enkigaming.mcforge.lib.eventlisteners.compatability.ForgePlayerEventBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.NotImplementedException;

public class ForgePlayer extends EnkiPlayer
{
    public ForgePlayer(UUID playerId)
    {
        this.playerId = playerId;
        initialiseEvents();
    }
    
    public ForgePlayer(String Username)
    {
        // Attempt to get UUID from username cache.
        playerId = EnkiLib.getLastRecordedIDForName(Username);
        
        if(playerId != null)
        {
            initialiseEvents();
            return;
        }
        
        // Attempt to get UUID from online players.
        List<EntityPlayer> playersOnline
            = new ArrayList<EntityPlayer>(MinecraftServer.getServer().getConfigurationManager().playerEntityList);
        
        for(EntityPlayer i : playersOnline)
            if(i.getGameProfile().getName().equalsIgnoreCase(Username))
            {
                playerId = i.getGameProfile().getId();
                initialiseEvents();
                return;
            }
        
        // Give up D:
        throw new NoSuchUsernameException(Username);
    }
    
    public ForgePlayer(EntityPlayer player)
    {
        playerId = player.getGameProfile().getId();
        initialiseEvents();
    }
    
    public void initialiseEvents()
    { ForgePlayerEventBridge.instance.registerForgePlayer(this); }
    
    UUID playerId;
    
    @Override
    public UUID getId()
    { return playerId; }

    @Override
    public String getUsername()
    {
        // Attempt to get name from username cache.
        String name = EnkiLib.getLastRecordedNameOf(playerId);
        
        if(name != null)
            return name;
        
        // Attempt to get name from online players.
        List<EntityPlayer> playersOnline
            = new ArrayList<EntityPlayer>(MinecraftServer.getServer().getConfigurationManager().playerEntityList);
        
        for(EntityPlayer i : playersOnline)
            if(i.getGameProfile().getId().equals(playerId))
                return i.getGameProfile().getName();
        
        // Give up D:
        return null;
    }

    @Override
    public String getDisplayName()
    { return getPlatformSpecificInstance().getDisplayName(); }

    @Override
    public EntityPlayer getPlatformSpecificInstance()
    {
        List<EntityPlayer> playersOnline
            = new ArrayList<EntityPlayer>(MinecraftServer.getServer().getConfigurationManager().playerEntityList);
        
        for(EntityPlayer i : playersOnline)
            if(i.getGameProfile().getId().equals(playerId))
                return i;
        
        return null;
    }

    @Override
    public Integer getWorldId()
    { return getPlatformSpecificInstance().worldObj.provider.dimensionId; }

    @Override
    public void teleportTo(int worldId, double x, double y, double z)
    {
        EntityPlayer player = getPlatformSpecificInstance();
        int currentWorldId = player.worldObj.provider.dimensionId;
        
        if(currentWorldId == worldId)
            player.setPosition(x, y, z);
        else
            /* teleport player to position in dimension */
            throw new NotImplementedException("Still need to implement teleporting players across dimensions.");
    }
    
    @Override
    public void teleportTo(int worldId, double x, double y, double z, double yaw, double pitch)
    {
        EntityPlayer player = getPlatformSpecificInstance();
        int currentWorldId = player.worldObj.provider.dimensionId;
        
        if(currentWorldId == worldId)
            player.setPositionAndRotation(x, y, z, (float)yaw, (float)pitch);
        else
            /* teleport player to position in dimension */
            throw new NotImplementedException("Still need to implement teleporting players across dimensions.");
    }
    
    @Override
    public void print(String message)
    {
        EntityPlayer player = getPlatformSpecificInstance();
        
        if(player != null)
            player.addChatMessage(new ChatComponentText(message));
    }
}