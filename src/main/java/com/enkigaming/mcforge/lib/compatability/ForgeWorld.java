package com.enkigaming.mcforge.lib.compatability;

import com.enkigaming.lib.misc.coordinates.CoOrdinate3d;
import com.enkigaming.mc.lib.compatability.EnkiWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ForgeWorld extends EnkiWorld
{
    public ForgeWorld(int worldId)
    { super(worldId); }
    
    @Override
    public World getPlatformSpecificInstance()
    { return DimensionManager.getWorld(this.getWorldId()); }

    @Override
    public String getName()
    { return getPlatformSpecificInstance().getWorldInfo().getWorldName(); }

    @Override
    protected ForgeBlock getNewBlockObject(CoOrdinate3d coOrd)
    { return new ForgeBlock(this.getWorldId(), coOrd.getX(), coOrd.getY(), coOrd.getZ()); }
}