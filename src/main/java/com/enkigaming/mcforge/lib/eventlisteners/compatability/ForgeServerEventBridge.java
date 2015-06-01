package com.enkigaming.mcforge.lib.eventlisteners.compatability;

import com.enkigaming.mc.lib.compatability.CommandSender;
import com.enkigaming.mc.lib.compatability.EnkiServer;
import com.enkigaming.mc.lib.compatability.EnkiServer.CommandArgs;
import com.enkigaming.mc.lib.compatability.items.EnkiItemMetaCommandBlock;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.CommandEvent;

public class ForgeServerEventBridge
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommand(CommandEvent event)
    {
        EnkiServer server = EnkiServer.getInstance();
        CommandSender sender = null;
        
        if(event.sender instanceof EntityPlayer)
            sender = server.getPlayer(((EntityPlayer)event.sender).getGameProfile().getId());
        else if(event.sender instanceof CommandBlockLogic)
        {
            int worldId = event.sender.getEntityWorld().provider.dimensionId;
            ChunkCoordinates xyz = event.sender.getPlayerCoordinates();
            int x = xyz.posX;
            int y = xyz.posY;
            int z = xyz.posZ;
            sender = (EnkiItemMetaCommandBlock)server.getWorld(worldId).getBlockAt(x, y, z).getMeta();
        }
        else if(event.sender instanceof MinecraftServer)
            sender = server;
        
        CommandArgs args = new CommandArgs(sender,
                                           event.command.getCommandName(),
                                           new ArrayList<String>(Arrays.asList(event.parameters)));
        
        args.setCancelled(event.isCanceled());
        server.commandDispatched.raise(this, args);
        event.setCanceled(args.isCancelled());
        server.commandDispatched.raisePostEvent(this, args);
    }
}