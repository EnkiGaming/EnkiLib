package com.enkigaming.mc.lib.misc;

import com.enkigaming.lib.misc.coordinates.CoOrdinate3d;
import com.enkigaming.lib.misc.coordinates.XYCoOrdPair;
import com.enkigaming.lib.misc.coordinates.XYZCoOrdSet;
import com.enkigaming.lib.misc.coordinates.XZCoOrdPair;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiWorld;

public class BlockCoOrdinate extends CoOrdinate3d
{
    public BlockCoOrdinate()
    { this(0, 0, 0, 0); }
    
    public BlockCoOrdinate(int x, int z)
    { this(0, x, 0, z); }
    
    public BlockCoOrdinate(int x, int y, int z)
    { this(0, x, y, z); }
    
    public BlockCoOrdinate(int worldId, int x, int y, int z)
    {
        super(x, y, z);
        this.worldId = worldId;
    }
    
    public BlockCoOrdinate(XYCoOrdPair coOrd)
    { this(coOrd.getX(), coOrd.getY(), 0); }
    
    public BlockCoOrdinate(XZCoOrdPair coOrd)
    { this(coOrd.getX(), coOrd.getZ()); }
    
    public BlockCoOrdinate(XYZCoOrdSet coOrd)
    { this(coOrd.getX(), coOrd.getY(), coOrd.getZ()); }
    
    public BlockCoOrdinate(int worldId, XYZCoOrdSet coOrd)
    { this(worldId, coOrd.getX(), coOrd.getY(), coOrd.getZ()); }
    
    public BlockCoOrdinate(BlockCoOrdinate other)
    { this(other.getWorldId(), other.getX(), other.getY(), other.getZ()); }
    
    final int worldId;
    
    public int getWorldId()
    { return worldId; }
    
    public EnkiWorld getWorld()
    { return CompatabilityAccess.getWorld(worldId); }

    @Override
    public int hashCode()
    { return 17 * super.hashCode() + this.worldId; }

    @Override
    public boolean equals(Object obj)
    {
        if(!super.equals(obj))
            return false;
        if(((BlockCoOrdinate)obj).getWorldId() != getWorldId())
            return false;
        return true;
    }
}