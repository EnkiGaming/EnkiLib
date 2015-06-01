package com.enkigaming.mcforge.lib.compatability.items;

import com.enkigaming.mc.lib.compatability.EnkiBlock;
import com.enkigaming.mc.lib.compatability.items.EnkiBlockMeta;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.block.Block;

public abstract class ForgeBlockMeta extends EnkiBlockMeta
{
    public ForgeBlockMeta(EnkiBlock block)
    { super(block); }
    
    @Override
    public boolean isValid()
    { return blockMetaIsStillValid(this); }
    
    /**
     * General implementation isValid for forge, for classes that inherit from their generic enki counterparts rather
     * than from the forge counterpart of EnkiBlockMeta.
     * @param meta The block meta to check.
     * @return Whether or not the block meta is still valid for the block it's in. That is, if it's currently the right
     * type of block meta for the ID of the block such that casts will be performed successfully and methods specific
     * to that type on the block meta will still perform correctly. Returns true if the block meta hasn't been placed
     * in the world.
     */
    public static boolean blockMetaIsStillValid(EnkiBlockMeta meta)
    {
        if(meta.getCorrespondingBlock() == null)
            return true;
        
        UniqueIdentifier implementationId = GameRegistry.findUniqueIdentifierFor((Block)meta.getCorrespondingBlock().getPlatformSpecificInstance());
        String actualId = implementationId.modId + ":" + implementationId.name;
        return actualId.equals(meta.getItemId());
    }
}