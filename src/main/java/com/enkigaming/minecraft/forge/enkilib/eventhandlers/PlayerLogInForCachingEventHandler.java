package com.enkigaming.minecraft.forge.enkilib.eventhandlers;

import com.enkigaming.minecraft.forge.enkilib.EnkiLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerLogInForCachingEventHandler
{
    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    { EnkiLib.getInstance().getUsernameCache().recordUsername(event.player.getGameProfile().getId(),
                                                              event.player.getGameProfile().getName()); }
}