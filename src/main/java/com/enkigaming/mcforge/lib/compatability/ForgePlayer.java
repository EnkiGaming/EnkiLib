package com.enkigaming.mcforge.lib.compatability;

import com.enkigaming.lib.events.exceptions.NoSuchUsernameException;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mcforge.lib.EnkiLib;
import com.enkigaming.mcforge.lib.eventlisteners.compatability.ForgePlayerEventBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class ForgePlayer extends EnkiPlayer
{
    protected class ForgePlayerTeleporter extends Teleporter
    {
        // Provided by mcjty, ty <3
        
        public ForgePlayerTeleporter(WorldServer world, double x, double y, double z)
        {
            super(world);
            this.worldServerInstance = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        private final WorldServer worldServerInstance;
        private double x, y, z;

        @Override
        public void placeInPortal(Entity pEntity, double p2, double p3, double p4, float p5)
        {
            this.worldServerInstance.getBlock((int) this.x, (int) this.y, (int) this.z);   //dummy load to maybe gen chunk

            pEntity.setPosition(this.x, this.y, this.z);
            pEntity.motionX = 0.0f;
            pEntity.motionY = 0.0f;
            pEntity.motionZ = 0.0f;
        }
    }
    
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
        double fixedYaw = yaw, fixedPitch = pitch;
        
        if(Double.isNaN(yaw) || Double.isNaN(pitch)) // If direction is not provided
        {
            fixedYaw = player.rotationYaw;
            fixedPitch = player.rotationPitch;
        }
        
        if(newWorldId == null || newWorldId == oldWorldId) // if world is not provided/not changed.
        {
            player.setLocationAndAngles(x, y, z, (float)fixedYaw, (float)fixedPitch);
            return;
        }
        
        // The following is borrowed RFTools, provided and with permission from mcjty. Thankyou <3
        
        int oldDimension = player.worldObj.provider.dimensionId;
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
        WorldServer worldServer = MinecraftServer.getServer().worldServerForDimension(newWorldId);
        player.addExperienceLevel(0);
        MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(entityPlayerMP, newWorldId, new ForgePlayerTeleporter(worldServer, x, y, z));
        
        if (oldDimension == 1)
        {
            // For some reason teleporting out of the end does weird things.
            
            player.setPositionAndUpdate(x, y, z);
            worldServer.spawnEntityInWorld(player);
            worldServer.updateEntityWithOptionalForce(player, false);
        }
        
        // Why is EntityPlayer.setRotation a protected method?
        player.rotationYaw = (float)fixedYaw % 360.0F;
        player.rotationPitch = (float)fixedPitch % 360.0F;
    }
    
    @Override
    public void print(String message)
    {
        EntityPlayer player = getPlatformSpecificInstance();
        
        if(player != null)
            player.addChatMessage(new ChatComponentText(message));
    }
}