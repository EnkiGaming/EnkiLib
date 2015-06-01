package com.enkigaming.mc.lib.compatability;

import com.enkigaming.mc.lib.compatability.items.EnkiBlockMeta;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;

public abstract class EnkiBlock extends BlockCoOrdinate
{
    public EnkiBlock()
    { this(0, 0, 0, 0); }
    
    public EnkiBlock(int x, int z)
    { this(0, x, 0, z); }
    
    public EnkiBlock(int x, int y, int z)
    { this(0, x, y, z); }
    
    public EnkiBlock(int worldId, int x, int y, int z)
    { super(worldId, x, y, z); }
    
    public EnkiBlock(BlockCoOrdinate blockCoOrd)
    { super(blockCoOrd.getWorldId(), blockCoOrd.getX(), blockCoOrd.getY(), blockCoOrd.getZ()); }
    
    EnkiBlockMeta meta = null;
    
    public EnkiBlockMeta getMeta()
    {
        if(meta == null)
            meta = getNewMeta();
        
        return meta;
    }
    
    public void invalidateMeta()
    { meta = null; }
    
    protected abstract EnkiBlockMeta getNewMeta();
    
    public abstract Object getPlatformSpecificInstance();
}