package com.enkigaming.mcforge.enkilib.eventhandlers;

import com.enkigaming.mcforge.enkilib.EnkiLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldSaveEventHandler
{
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event)
    { EnkiLib.getInstance().getFileHandling().save(); }
}