package com.enkigaming.mcforge.lib.eventlisteners;

import com.enkigaming.mcforge.lib.EnkiLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldSaveEventListener
{
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event)
    { EnkiLib.getInstance().getFileHandling().save(); }
}