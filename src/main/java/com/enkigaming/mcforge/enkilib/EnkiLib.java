package com.enkigaming.mcforge.enkilib;

import com.enkigaming.mcforge.enkilib.eventhandlers.PlayerLogInForCachingEventHandler;
import com.enkigaming.mcforge.enkilib.eventhandlers.WorldSaveEventHandler;
import com.enkigaming.mcforge.enkilib.filehandling.FileHandlerRegistry;
import com.enkigaming.mcforge.enkilib.registry.UsernameCache;
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
    public static final String VERSION = "B1.0.2";
    
    protected static EnkiLib instance;
    File saveFolder;
    UsernameCache usernameCache;
    FileHandlerRegistry fileHandling;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        saveFolder = new File(event.getModConfigurationDirectory().getParentFile(), "plugins/EnkiCore");
        fileHandling = new FileHandlerRegistry();
        usernameCache = new UsernameCache(saveFolder);
        fileHandling.register(usernameCache.getFileHandler());
        fileHandling.load();
        FMLCommonHandler.instance().bus().register(new PlayerLogInForCachingEventHandler());
        MinecraftForge.EVENT_BUS.register(new WorldSaveEventHandler());
        System.out.println("EnkiLib loaded!");
    }
    
    public static EnkiLib getInstance()
    { return instance; }
    
    public UsernameCache getUsernameCache()
    { return usernameCache; }
    
    public FileHandlerRegistry getFileHandling()
    { return fileHandling; }
    
    //========== Convenience Methods ==========
    
    public static String getLastRecordedNameOf(UUID playerId)
    { return getInstance().getUsernameCache().getLastRecordedNameOf(playerId); }
}