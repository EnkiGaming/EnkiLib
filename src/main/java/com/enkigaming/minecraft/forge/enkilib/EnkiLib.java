package com.enkigaming.minecraft.forge.enkilib;

import com.enkigaming.minecraft.forge.enkilib.eventhandlers.PlayerLogInForCachingEventHandler;
import com.enkigaming.minecraft.forge.enkilib.filehandling.FileHandlerRegistry;
import com.enkigaming.minecraft.forge.enkilib.registry.UsernameCache;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.util.logging.Logger;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = EnkiLib.MODID, name = EnkiLib.NAME, version = EnkiLib.VERSION, acceptableRemoteVersions = "*")
public class EnkiLib
{
    public static final String NAME = "EnkiLib";
    public static final String MODID = "EnkiLib";
    public static final String VERSION = "1.0";
    
    protected static EnkiLib instance;
    File saveFolder;
    UsernameCache usernameCache;
    FileHandlerRegistry fileHandling;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        saveFolder = new File(event.getModConfigurationDirectory().getParentFile(), "plugins/EnkiCore");
        fileHandling = new FileHandlerRegistry(saveFolder, null);
        usernameCache = new UsernameCache(saveFolder);
        fileHandling.register(usernameCache.getFileHandler());
        fileHandling.load();
        MinecraftForge.EVENT_BUS.register(new PlayerLogInForCachingEventHandler());
        System.out.println("EnkiLib loaded!");
    }
    
    public static EnkiLib getInstance()
    { return instance; }
    
    public UsernameCache getUsernameCache()
    { return usernameCache; }
}