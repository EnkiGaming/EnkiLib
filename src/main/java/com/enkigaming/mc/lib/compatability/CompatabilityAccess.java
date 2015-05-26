package com.enkigaming.mc.lib.compatability;

import java.util.UUID;

/* This isn't pretty, but at least it allows good integration with multiple platforms without changing any of the code
   of projects that use it. Maybe rename this later on to something more succinct? */
public class CompatabilityAccess
{
    public static interface Getter
    { EnkiServer getServer(); }
    
    static Getter getter;
    
    public static EnkiPlayer getPlayer(UUID playerId)
    { return getServer().getPlayer(playerId); }
    
    public static EnkiBlock getBlock(int worldId, int x, int y, int z)
    { return getServer().getWorld(worldId).getBlockAt(x, y, z); }
    
    public static EnkiWorld getWorld(int worldId)
    { return getServer().getWorld(worldId); }
    
    public static EnkiServer getServer()
    { return getter.getServer(); }
    
    public static void setGetter(Getter newGetter)
    { getter = newGetter; }
}