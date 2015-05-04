package com.enkigaming.mcforge.lib.eventlisteners;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventArgs;
import com.enkigaming.lib.events.EventMethods;
import com.enkigaming.lib.tuples.Pair;
import com.enkigaming.mc.lib.compatability.EnkiPlayer.DiedArgs;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class PlayerDeathPreListener
{
    public static final PlayerDeathPreListener instance = new PlayerDeathPreListener(PlayerDeathPostListener.instance);
    
    public PlayerDeathPreListener(PlayerDeathPostListener partner)
    { postEventPartner = partner; }
    
    Map<UUID, Collection<WeakReference<ForgePlayer>>> players
        = new HashMap<UUID, Collection<WeakReference<ForgePlayer>>>();
    
    PlayerDeathPostListener postEventPartner;
    
    final Object eventsToRaiseBusy = new Object();
    
    public void addEventToRaise(ForgePlayer forgePlayer)
    {
        synchronized(eventsToRaiseBusy)
        {
            Collection<WeakReference<ForgePlayer>> playersToRaise = players.get(forgePlayer.getId());
            
            if(playersToRaise == null)
            {
                playersToRaise = new HashSet<WeakReference<ForgePlayer>>();
                players.put(forgePlayer.getId(), playersToRaise);
            }
            
            //eventsForPlayer.add(new WeakReference<Event<DiedArgs>>(diedEvent));
            playersToRaise.add(new WeakReference<ForgePlayer>(forgePlayer));
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDeathPre(LivingDeathEvent event)
    {
        UUID playerId = ((EntityPlayer)event.entity).getGameProfile().getId();
        Collection<Pair<Event<?>, EventArgs>> eventsToRaise = new HashSet<Pair<Event<?>, EventArgs>>();
        
        Map<WeakReference<ForgePlayer>, DiedArgs> argsForPostEvent
                = new HashMap<WeakReference<ForgePlayer>, DiedArgs>();
        
        synchronized(eventsToRaiseBusy)
        {
            if(!(event.entity instanceof EntityPlayer))
                return;
            
            Collection<WeakReference<ForgePlayer>> playersToRaise = players.get(playerId);

            if(playersToRaise == null)
                return;

            Collection<WeakReference<ForgePlayer>> toRemove = null;
            
            for(WeakReference<ForgePlayer> ref : playersToRaise)
            {
                ForgePlayer playerToRaise = ref.get();
                
                if(playerToRaise == null)
                {
                    if(toRemove == null)
                        toRemove = new HashSet<WeakReference<ForgePlayer>>();
                    
                    toRemove.add(ref);
                    continue;
                }
                
                DiedArgs args = new DiedArgs();
                eventsToRaise.add(new Pair<Event<?>, EventArgs>(playerToRaise.died, args));
                argsForPostEvent.put(ref, args);
            }
        }
            
        EventMethods.raiseMultiple(this, true, eventsToRaise);

        postEventPartner.addArgsForPostEvent(argsForPostEvent);
    }
}