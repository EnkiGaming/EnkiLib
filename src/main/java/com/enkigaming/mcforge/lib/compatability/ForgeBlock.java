package com.enkigaming.mcforge.lib.compatability;

import com.enkigaming.mc.lib.compatability.EnkiBlock;
import com.enkigaming.mc.lib.compatability.items.EnkiBlockMeta;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

public class ForgeBlock extends EnkiBlock
{
    public ForgeBlock()
    { super(); }
    
    public ForgeBlock(int x, int z)
    { super(x, z); }
    
    public ForgeBlock(int x, int y, int z)
    { super(x, y, z); }
    
    public ForgeBlock(int worldId, int x, int y, int z)
    { super(worldId, x, y, z); }
    
    public ForgeBlock(BlockCoOrdinate blockCoOrd)
    { super(blockCoOrd); }

    @Override
    protected EnkiBlockMeta getNewMeta()
    { throw new NotImplementedException("Not implemented yet."); }

    @Override
    public Object getPlatformSpecificInstance()
    { return ((World)this.getWorld().getPlatformSpecificInstance()).getBlock(x, y, z); }
}