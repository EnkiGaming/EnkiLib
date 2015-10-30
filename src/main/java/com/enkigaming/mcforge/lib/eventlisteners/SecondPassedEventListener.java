package com.enkigaming.mcforge.lib.eventlisteners;

import com.enkigaming.mc.lib.misc.TickCountdownTimer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class SecondPassedEventListener
{
    byte tickCounter = 0;
    
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        if(++tickCounter == 20)
        {
            tickCounter = 0;
            TickCountdownTimer.passSecond();
        }
    }
}