package com.enkigaming.mc.lib.compatability;

import com.enkigaming.mc.lib.compatability.items.ItemMetaRegistry;

/* This isn't pretty, but at least it allows good integration with multiple platforms without changing any of the code
   of projects that use it. Maybe rename this later on to something more succinct? */
public class CompatabilityAccess
{
    public static interface Getter
    { EnkiServer getServer(); }
    
    static Getter getter;
    static final ItemMetaRegistry itemMetaRegistry = new ItemMetaRegistry();
    
    static EnkiServer getServer()
    { return getter.getServer(); }
    
    public static void setGetter(Getter newGetter)
    { getter = newGetter; }
    
    public static ItemMetaRegistry getItemMetaRegistry()
    { return itemMetaRegistry; }
}