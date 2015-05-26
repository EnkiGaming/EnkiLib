package com.enkigaming.mc.lib.compatability;

import com.enkigaming.lib.misc.coordinates.CoOrdinate3d;
import com.enkigaming.lib.misc.coordinates.XYZCoOrdSet;
import com.google.common.cache.CacheBuilder;
import java.util.Map;

public abstract class EnkiWorld
{
    public EnkiWorld(int worldId)
    { this.worldId = worldId; }
    
    final int worldId;
    
    protected final Map<CoOrdinate3d, EnkiBlock> blocks = CacheBuilder.newBuilder()
                                                                      .concurrencyLevel(1)
                                                                      .weakValues()
                                                                      .<CoOrdinate3d, EnkiBlock>build()
                                                                      .asMap();
    
    public abstract Object getPlatformSpecificInstance();
    
    public int getWorldId()
    { return worldId; }
    
    public EnkiBlock getBlockAt(CoOrdinate3d coOrd)
    {
        synchronized(blocks)
        {
            EnkiBlock block = blocks.get(coOrd);
            
            if(block == null)
            {
                block = getNewBlockObject(coOrd);
                blocks.put(coOrd, block);
            }
            
            return block;
        }
    }
    
    public EnkiBlock getBlockAt(int x, int y, int z)
    { return getBlockAt(new CoOrdinate3d(x, y, z)); }
    
    public EnkiBlock getBlockAt(XYZCoOrdSet coOrd)
    { return getBlockAt(new CoOrdinate3d(coOrd)); }
    
    public abstract String getName();
    
    protected abstract EnkiBlock getNewBlockObject(CoOrdinate3d coOrd);
}