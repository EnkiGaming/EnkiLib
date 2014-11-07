package com.enkigaming.mcforge.enkilib.eventlisteners;

import com.enkigaming.mcforge.enkilib.EnkiLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldSaveEventListener
{
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event)
    { EnkiLib.getInstance().getFileHandling().save(); }
}