package com.enkigaming.mc.lib.compatability;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import com.enkigaming.mc.lib.misc.PlayerPosition;
import java.util.UUID;

public abstract class EnkiPlayer
{
    public static class DiedArgs extends StandardEventArgs
    { /* Nothing atm */ }
    
    public static class ConnectedArgs extends StandardEventArgs
    { /* Nothing atm */ }
    
    public static class DisconnectedArgs extends StandardEventArgs
    { /* Nothing atm */ }
    
    public final Event<DiedArgs> died = new StandardEvent<DiedArgs>();
    
    public final Event<ConnectedArgs> connected = new StandardEvent<ConnectedArgs>();
    
    public final Event<DisconnectedArgs> disconnected = new StandardEvent<DisconnectedArgs>();
    
    public abstract UUID getId();
    
    public abstract String getUsername();
    
    public abstract String getDisplayName();
    
    public abstract Object getPlatformSpecificInstance();
    
    public abstract Integer getWorldId();
    
    public EnkiWorld getWorld()
    { return CompatabilityAccess.getWorld(getWorldId()); }
    
    public abstract void teleportTo(int worldId, double x, double y, double z, double yaw, double pitch);
    
    public abstract void teleportTo(int worldId, double x, double y, double z);
    
    public abstract void teleportTo(double x, double y, double z, double yaw, double pitch);
    
    public abstract void teleportTo(double x, double y, double z);
    
    public void teleportTo(BlockCoOrdinate destination)
    {
        this.teleportTo(destination.getWorldId(),
                        destination.getX() + 0.5,
                        destination.getY() + 0.5,
                        destination.getZ() + 0.5);
    }
    
    public void teleportTo(BlockCoOrdinate destination, double yaw, double pitch)
    {
        this.teleportTo(destination.getWorldId(),
                        destination.getX() + 0.5,
                        destination.getY() + 0.5,
                        destination.getZ() + 0.5,
                        yaw,
                        pitch);
    }
    
    public void teleportTo(PlayerPosition destination)
    {
        if(destination.directionWasSpecified())
            this.teleportTo(destination.getWorldId(), destination.getX(),   destination.getY(),
                            destination.getZ(),       destination.getYaw(), destination.getPitch());
        else
            this.teleportTo(destination.getWorldId(), destination.getX(), destination.getY(), destination.getZ());
    }
    
    /**
     * Prints a message for a player in chat. (Such that only the player can see the message)
     * @param message The text to be printed.
     */
    public abstract void print(String message);
}