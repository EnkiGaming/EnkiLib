package com.enkigaming.mcforge.enkilib.eventlisteners;

import com.enkigaming.mcforge.enkilib.EnkiLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerLogInForCachingEventListener
{
    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EnkiLib.getInstance().getUsernameCache().recordUsername(event.player.getGameProfile().getId(),
                                                              event.player.getGameProfile().getName());
    }
}