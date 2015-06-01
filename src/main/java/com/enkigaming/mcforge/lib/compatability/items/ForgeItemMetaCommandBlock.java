package com.enkigaming.mcforge.lib.compatability.items;

import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiBlock;
import com.enkigaming.mc.lib.compatability.EnkiServer;
import com.enkigaming.mc.lib.compatability.items.EnkiBlockMeta;
import com.enkigaming.mc.lib.compatability.items.EnkiItemMetaCommandBlock;
import com.enkigaming.mc.lib.compatability.items.ItemMetaRegistry;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import net.minecraft.item.ItemStack;

public class ForgeItemMetaCommandBlock extends EnkiItemMetaCommandBlock
{
    public ForgeItemMetaCommandBlock(EnkiBlock block)
    { super(block); }
    
    public static void registerItemMetaGetters()
    {
        CompatabilityAccess.getItemMetaRegistry().setDefaultGetter(new ItemMetaRegistry.MetaGetter()
        {
            @Override
            public ForgeItemMetaCommandBlock get(final String itemId, final Object platformSpecificMeta)
            {
                return new ForgeItemMetaCommandBlock(null)
                {
                    ItemStack sourceMeta = (ItemStack)platformSpecificMeta;
                    
                    @Override
                    public boolean isValid()
                    { return true; }
                };
            }
        });
        
        CompatabilityAccess.getItemMetaRegistry().setDefaultItemStackGetter(new ItemMetaRegistry.MetaItemStackGetter()
        {
            @Override
            public ForgeItemMetaCommandBlock get(final String itemId, final Object itemStack)
            {
                return new ForgeItemMetaCommandBlock(null)
                {
                    ItemStack sourceMeta = (ItemStack)itemStack;
                    
                    @Override
                    public boolean isValid()
                    { return true; }
                };
            }
        });
        
        CompatabilityAccess.getItemMetaRegistry().setDefaultBlockGetter(new ItemMetaRegistry.MetaBlockGetter()
        {
            @Override
            public EnkiBlockMeta get(final String itemId, final BlockCoOrdinate block)
            { return new ForgeItemMetaCommandBlock(EnkiServer.getInstance().getWorld(block.getWorldId()).getBlockAt(block)); }
        });
    }
    
    @Override
    public boolean isValid()
    { return ForgeBlockMeta.blockMetaIsStillValid(this); }

    @Override
    public String getItemId()
    { return "minecraft:command_block"; }
}