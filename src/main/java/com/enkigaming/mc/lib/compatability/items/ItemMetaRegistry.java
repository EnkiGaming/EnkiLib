package com.enkigaming.mc.lib.compatability.items;

import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import java.util.HashMap;
import java.util.Map;

public class ItemMetaRegistry
{
    public static interface MetaGetter
    { public EnkiItemMeta get(String itemId, Object platformSpecificMeta); }
    
    public static interface MetaBlockGetter
    { public EnkiBlockMeta get(String itemId, BlockCoOrdinate block); }
    
    public static interface MetaItemStackGetter
    { public EnkiItemMeta get(String itemId, Object itemStack); }
    
    protected MetaGetter          defaultGetter          = null;
    protected MetaBlockGetter     defaultBlockGetter     = null;
    protected MetaItemStackGetter defaultItemStackGetter = null;
    
    protected final Map<String, MetaGetter>          getters          = new HashMap<String, MetaGetter>();
    protected final Map<String, MetaBlockGetter>     blockGetters     = new HashMap<String, MetaBlockGetter>();
    protected final Map<String, MetaItemStackGetter> itemStackGetters = new HashMap<String, MetaItemStackGetter>();
    
    protected final Object defaultGetterBusy          = new Object();
    protected final Object defaultBlockGetterBusy     = new Object();
    protected final Object defaultItemStackGetterBusy = new Object();
    
    public void setDefaultGetter(MetaGetter getter)
    {
        synchronized(defaultGetterBusy)
        { defaultGetter = getter; }
    }
    
    public void setDefaultBlockGetter(MetaBlockGetter getter)
    {
        synchronized(defaultBlockGetterBusy)
        { defaultBlockGetter = getter; }
    }
    
    public void setDefaultItemStackGetter(MetaItemStackGetter getter)
    {
        synchronized(defaultItemStackGetterBusy)
        { defaultItemStackGetter = getter; }
    }
    
    public void setGetterFor(String itemId, MetaGetter getter)
    {
        synchronized(getters)
        { getters.put(itemId, getter); }
    }
    
    public void setBlockGetterFor(String itemId, MetaBlockGetter getter)
    {
        synchronized(blockGetters)
        { blockGetters.put(itemId, getter); }
    }
    
    public void setItemStackGetterFor(String itemId, MetaItemStackGetter getter)
    {
        synchronized(itemStackGetters)
        { itemStackGetters.put(itemId, getter); }
    }
    
    /**
     * Gets a new EnkiItemMeta object for the passed item ID, hooking into the passed item meta.
     * @param itemId The item ID of the item this is a meta for.
     * @param platformSpecificMeta The platform-specific meta that will be referenced by the new EnkiItemMeta object.
     * @return A new EnkiItemMeta object representing the passed platform-specific item meta object.
     */
    public EnkiItemMeta getNewMetaFor(String itemId, Object platformSpecificMeta)
    {
        MetaGetter getter;
        
        synchronized(getters)
        { getter = getters.get(itemId); }
        
        if(getter == null)
            synchronized(defaultGetterBusy)
            { getter = defaultGetter; }
        
        return getter == null ? null : getter.get(itemId, platformSpecificMeta);
    }
    
    /**
     * Gets a new EnkiBlockMeta object for the passed item ID, hooking into the meta of the block at the passed block
     * coördinates.
     * @param itemId The item ID of the block this is a meta for.
     * @param block The coördinates of the block to make the meta for.
     * @return A new EnkiBlockMeta object representing the meta of the platform-specific block at the coördinates given.
     */
    public EnkiBlockMeta getNewMetaForBlock(String itemId, BlockCoOrdinate block)
    {
        MetaBlockGetter getter;
        
        synchronized(blockGetters)
        { getter = blockGetters.get(itemId); }
        
        if(getter == null)
            synchronized(defaultBlockGetterBusy)
            { getter = defaultBlockGetter; }
        
        return getter == null ? null : getter.get(itemId, block);
    }
    
    /**
     * Gets a new EnkiItemMeta object for the passed item ID, hooking into the meta of the passed itemstack.
     * @param itemId The item ID of the item this is a meta for.
     * @param itemStack The itemstack from which to grab the platform-specific meta information.
     * @return A new EnkiItemMeta object representing the platform-specific item meta meta of the passed item stack.
     */
    public EnkiItemMeta getNewMetaForItemStack(String itemId, Object itemStack)
    {
        MetaItemStackGetter getter;
        
        synchronized(itemStackGetters)
        { getter = itemStackGetters.get(itemId); }
        
        if(getter == null)
            synchronized(defaultItemStackGetterBusy)
            { getter = defaultItemStackGetter; }
        
        return getter == null ? null : getter.get(itemId, itemStack);
    }
}