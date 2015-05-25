package com.enkigaming.mcforge.lib.compatability;

import com.enkigaming.lib.events.exceptions.NoSuchUsernameException;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mcforge.lib.EnkiLib;
import com.enkigaming.mcforge.lib.eventlisteners.compatability.ForgePlayerEventBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

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
    public void teleportTo(int worldId, double x, double y, double z, double yaw, double pitch)
    { teleportTo_mainMethod(worldId, x, y, z, yaw, pitch); }
    
    @Override
    public void teleportTo(int worldId, double x, double y, double z)
    { teleportTo_mainMethod(worldId, x, y, z, Double.NaN, Double.NaN); }
    
    @Override
    public void teleportTo(double x, double y, double z, double yaw, double pitch)
    { teleportTo_mainMethod(null, x, y, z, yaw, pitch); }
    
    @Override
    public void teleportTo(double x, double y, double z)
    { teleportTo_mainMethod(null, x, y, z, Double.NaN, Double.NaN); }
    
    private void teleportTo_mainMethod(Integer newWorldId, double x, double y, double z, double yaw, double pitch)
    {
        EntityPlayer player = this.getPlatformSpecificInstance();
        player.mountEntity(null);
        int oldWorldId = player.dimension;
        
        if(Double.isNaN(yaw) || Double.isNaN(pitch)) // If direction is not provided
        {
            yaw = player.rotationYaw;
            pitch = player.rotationPitch;
        }
        
        if(newWorldId == null || newWorldId == oldWorldId) // if world is not provided/not changed.
        {
            player.setLocationAndAngles(x, y, z, (float)yaw, (float)pitch);
            return;
        }
        
        World oldWorld = DimensionManager.getWorld(oldWorldId);
        
        if (!oldWorld.isRemote && !player.isDead)
        {
            MinecraftServer server = MinecraftServer.getServer();
            WorldServer oldWorldServer = server.worldServerForDimension(oldWorldId);
            WorldServer newWorldServer = server.worldServerForDimension(newWorldId);
            
            player.dimension = newWorldId;
            oldWorld.removeEntity(player);
            player.isDead = false;
            
            if (player.isEntityAlive())
            {
                player.setLocationAndAngles(x, y, z, (float)yaw, (float)pitch);
                newWorldServer.spawnEntityInWorld(player);
                newWorldServer.updateEntityWithOptionalForce(player, false);
            }

            player.setWorld(newWorldServer);
            Entity playerInNewWorld = EntityList.createEntityByName(EntityList.getEntityString(player), newWorldServer);
            playerInNewWorld.copyDataFrom(player, true);
            newWorldServer.spawnEntityInWorld(playerInNewWorld);
            player.isDead = true; // Old player entity.
            oldWorldServer.resetUpdateEntityTick();
            newWorldServer.resetUpdateEntityTick();
        }
    }
    
    @Override
    public void print(String message)
    {
        EntityPlayer player = getPlatformSpecificInstance();
        
        if(player != null)
            player.addChatMessage(new ChatComponentText(message));
    }
}