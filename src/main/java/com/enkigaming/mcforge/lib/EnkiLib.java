package com.enkigaming.mcforge.lib;

import com.enkigaming.mcforge.lib.eventlisteners.PlayerLogInForCachingEventListener;
import com.enkigaming.mcforge.lib.eventlisteners.WorldSaveEventListener;
import com.enkigaming.lib.filehandling.FileHandlerRegistry;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiBlock;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mcforge.lib.compatability.ForgeBlock;
import com.enkigaming.mcforge.lib.compatability.ForgePlayer;
import com.enkigaming.mcforge.lib.registry.UsernameCache;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.util.UUID;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = EnkiLib.MODID, name = EnkiLib.NAME, version = EnkiLib.VERSION, acceptableRemoteVersions = "*")
public class EnkiLib
{
    public static final String NAME = "EnkiLib";
    public static final String MODID = "EnkiLib";
    public static final String VERSION = "2.1";
    
    /*
    Versioning:
    
    Increment first if major overhaul, or major breaking changes.
    
    Increment second for changes that change contracts/interfaces (such as adding new classes, etc.) and/or make minor
    breaking changes in small/obscure ways that don't warrant a full major version increment.
    
    Increment third for changes that don't affect contracts/interfaces, or do but only in minor ways, or for additions
    to contracts/interfaces that relate to the previous third-number-incrementing update.
    
    Increment fourth for changes that fix/tweak something from the last release, and generally don't affect
    interfaces/contracts,
    */
    
    protected static EnkiLib instance;
    File saveFolder;
    UsernameCache usernameCache;
    FileHandlerRegistry fileHandling;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        saveFolder = new File(event.getModConfigurationDirectory().getParentFile(), "plugins/EnkiLib");
        fileHandling = new FileHandlerRegistry();
        usernameCache = new UsernameCache(saveFolder);
        fileHandling.register(usernameCache.getFileHandler());
        fileHandling.load();
        FMLCommonHandler.instance().bus().register(new PlayerLogInForCachingEventListener());
        MinecraftForge.EVENT_BUS.register(new WorldSaveEventListener());
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
            @Override
            public EnkiPlayer getPlayer(UUID playerId)
            { return new ForgePlayer(playerId); }

            @Override
            public EnkiBlock getBlock(int worldId, int x, int y, int z)
            { return new ForgeBlock(worldId, x, y, z); }
        });
    }
    
    //========== Convenience Methods ==========
    
    public static String getLastRecordedNameOf(UUID playerId)
    { return getInstance().getUsernameCache().getLastRecordedNameOf(playerId); }
    
    public static UUID getLastRecordedIDForName(String username)
    { return getInstance().getUsernameCache().getLastRecordedIDForName(username); }
}