package com.enkigaming.mcforge.lib;

import com.enkigaming.mcforge.lib.eventlisteners.PlayerLogInForCachingEventListener;
import com.enkigaming.mcforge.lib.eventlisteners.WorldSaveEventListener;
import com.enkigaming.lib.filehandling.FileHandlerRegistry;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiBlock;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mc.lib.compatability.EnkiServer;
import com.enkigaming.mc.lib.compatability.EnkiWorld;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import com.enkigaming.mcforge.lib.compatability.ForgeBlock;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import com.enkigaming.mcforge.lib.compatability.ForgeServer;
import com.enkigaming.mcforge.lib.compatability.ForgeWorld;
import com.enkigaming.mcforge.lib.eventlisteners.PlayerDeathPostListener;
import com.enkigaming.mcforge.lib.eventlisteners.PlayerDeathPreListener;
import com.enkigaming.mcforge.lib.eventlisteners.SecondPassedEventListener;
import com.enkigaming.mcforge.lib.registry.UsernameCache;
import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = EnkiLib.MODID, name = EnkiLib.NAME, version = EnkiLib.VERSION, acceptableRemoteVersions = "*")
public class EnkiLib
{
    public static final String NAME = "EnkiLib";
    public static final String MODID = "EnkiLib";
    public static final String VERSION = "r1.1.3";

    /*
    Versioning:
    
    Increment first for breaking change. Id est, changes that remove public classes, remove public/package/protected
    methods/constructors/variables, increase the strictness of the privacy modifier of fields, move things to different
    packages, etc.
    
    Increment second for changes that modify class contracts/interfaces in ways that don't break compatability with
    previous versions. e.g. adding public classes, adding public/package/protected fields/constructors, adding
    overloads, etc.
    
    Increment third for changes that don't affect the public contract/interface. e.g. adding/modifying javadoc,
    rewriting methods, changing implementations, fixing bugs in methods, etc. Classes (creating, modifying, etc.) that
    act purely as event handlers in forge's event system should be treated as implementation details, and thus only
    warrant incrementing the third part of the version number rather than the second.
    */
    
    protected static EnkiLib instance;
    File saveFolder;
    UsernameCache usernameCache;
    FileHandlerRegistry fileHandling;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        initialiseCompatabilityAccess();
        saveFolder = new File(event.getModConfigurationDirectory().getParentFile(), "plugins/EnkiLib");
        fileHandling = new FileHandlerRegistry();
        usernameCache = new UsernameCache(saveFolder);
        fileHandling.register(usernameCache.getFileHandler());
        fileHandling.load();
        registerEvents();
        System.out.println("EnkiLib loaded!");
    }
    
    public static EnkiLib getInstance()
    { return instance; }
    
    public UsernameCache getUsernameCache()
    { return usernameCache; }
    
    public FileHandlerRegistry getFileHandling()
    { return fileHandling; }
    
    private void initialiseCompatabilityAccess()
    {
        CompatabilityAccess.setGetter(new CompatabilityAccess.Getter()
        {
            EnkiServer server = new ForgeServer();
            
            // Ensures that all objects returned using the same arguments will be the same objects, for getPlayer,
            // getBlock, and getWorld
            Map<UUID,            ForgePlayer> players = new MapMaker().weakValues().makeMap();
            Map<BlockCoOrdinate, ForgeBlock>  blocks  = new MapMaker().weakValues().makeMap();
            Map<Integer,         ForgeWorld>  worlds  = new MapMaker().weakValues().makeMap();
            
            @Override
            public EnkiPlayer getPlayer(UUID playerId)
            {
                ForgePlayer player = players.get(playerId);
                
                if(player == null)
                {
                    player = new ForgePlayer(playerId);
                    players.put(playerId, player);
                }
                
                return player;
            }

            @Override
            public EnkiBlock getBlock(int worldId, int x, int y, int z)
            {
                BlockCoOrdinate blockCoOrd = new BlockCoOrdinate(worldId, x, y, z);
                ForgeBlock block = blocks.get(blockCoOrd);
                
                if(block == null)
                {
                    block = new ForgeBlock(blockCoOrd);
                    blocks.put(blockCoOrd, block);
                }
                
                return block;
            }

            @Override
            public EnkiWorld getWorld(int worldId)
            {
                ForgeWorld world = worlds.get(worldId);
                
                if(world == null)
                {
                    world = new ForgeWorld(worldId);
                    worlds.put(worldId, world);
                }
                
                return world;
            }
            
            @Override
            public EnkiServer getServer()
            { return server; }
        });
    }
    
    private void registerEvents()
    {
        MinecraftForge.EVENT_BUS.register(PlayerDeathPreListener.instance);
        MinecraftForge.EVENT_BUS.register(PlayerDeathPostListener.instance);
        
        FMLCommonHandler.instance().bus().register(new PlayerLogInForCachingEventListener());
        MinecraftForge.EVENT_BUS.register(new WorldSaveEventListener());
        FMLCommonHandler.instance().bus().register(new SecondPassedEventListener());
    }
    
    //========== Convenience Methods ==========
    
    public static String getLastRecordedNameOf(UUID playerId)
    { return getInstance().getUsernameCache().getLastRecordedNameOf(playerId); }
    
    public static UUID getLastRecordedIDForName(String username)
    { return getInstance().getUsernameCache().getLastRecordedIDForName(username); }
}