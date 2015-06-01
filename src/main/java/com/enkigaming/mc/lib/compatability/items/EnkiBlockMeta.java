package com.enkigaming.mc.lib.compatability.items;

import com.enkigaming.mc.lib.compatability.EnkiBlock;

public abstract class EnkiBlockMeta extends EnkiItemMeta
{
    public EnkiBlockMeta(EnkiBlock block)
    { this.block = block; }
    
    /**
     * The block object this meta corresponds to. Null means that the block meta represents a block that is not placed
     * in the world. e.g. one held in a player's inventory.
     */
    final EnkiBlock block;
    
    public EnkiBlock getCorrespondingBlock()
    { return block; }
    
    /**
     * Whether or not the type of this EnkiBlockMeta object still accurately reflects the underlying implementation's
     * block.
     * @return True if it still accurately reflects the implement's block. Otherwise, false.
     */
    public abstract boolean isValid();
}