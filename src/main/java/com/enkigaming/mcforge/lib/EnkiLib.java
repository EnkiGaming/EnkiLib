package com.enkigaming.mcforge.lib;

import com.enkigaming.mcforge.lib.eventlisteners.PlayerLogInForCachingEventListener;
import com.enkigaming.mcforge.lib.eventlisteners.WorldSaveEventListener;
import com.enkigaming.lib.filehandling.FileHandlerRegistry;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiServer;
import com.enkigaming.mc.lib.compatability.items.EnkiBlockMeta;
import com.enkigaming.mc.lib.compatability.items.EnkiItemMeta;
import com.enkigaming.mc.lib.compatability.items.ItemMetaRegistry;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import com.enkigaming.mcforge.lib.compatability.ForgeServer;
import com.enkigaming.mcforge.lib.compatability.items.ForgeBlockMeta;
import com.enkigaming.mcforge.lib.compatability.items.ForgeItemMeta;
import com.enkigaming.mcforge.lib.compatability.items.ForgeItemMetaCommandBlock;
import com.enkigaming.mcforge.lib.eventlisteners.SecondPassedEventListener;
import com.enkigaming.mcforge.lib.eventlisteners.compatability.ForgePlayerEventBridge;
import com.enkigaming.mcforge.lib.registry.UsernameCache;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;
import java.util.UUID;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = EnkiLib.MODID, name = EnkiLib.NAME, version = EnkiLib.VERSION, acceptableRemoteVersions = "*")
public class EnkiLib
{
    public static final String NAME = "EnkiLib";
    public static final String MODID = "EnkiLib";
    public static final String VERSION = "7.0.0";

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
            
            @Override
            public EnkiServer getServer()
            { return server; }
        });
        
        registerItemMetaGetters();
    }
    
    private void registerEvents()
    {
        // TO DO: Remove static .instance fields and allow listener objects to be accessed from the mod class. (this)
        
        FMLCommonHandler.instance().bus().register(ForgePlayerEventBridge.instance);
        MinecraftForge.EVENT_BUS.register(ForgePlayerEventBridge.instance);
        
        FMLCommonHandler.instance().bus().register(new PlayerLogInForCachingEventListener());
        MinecraftForge.EVENT_BUS.register(new WorldSaveEventListener());
        FMLCommonHandler.instance().bus().register(new SecondPassedEventListener());
    }
    
    private void registerItemMetaGetters()
    {
        registerDefaultItemMetaGetters();
        
        ForgeItemMetaCommandBlock.registerItemMetaGetters();
    }
    
    private void registerDefaultItemMetaGetters()
    {
        CompatabilityAccess.getItemMetaRegistry().setDefaultGetter(new ItemMetaRegistry.MetaGetter()
        {
            @Override
            public EnkiItemMeta get(final String itemId, Object platformSpecificMeta)
            {
                final ItemStack stack = (ItemStack)platformSpecificMeta;
                
                if(stack.getItem() instanceof ItemBlock)
                {
                    return new ForgeBlockMeta(null)
                    {
                        final String id = itemId;
                        final ItemStack innerMeta = stack;
                        
                        @Override
                        public String getItemId()
                        { return id; }
                    };
                }
                
                return new ForgeItemMeta()
                {
                    final String id = itemId;
                    final ItemStack innerMeta = stack;
                    
                    @Override
                    public String getItemId()
                    { return id; }
                };
            }
        });
        
        CompatabilityAccess.getItemMetaRegistry().setDefaultItemStackGetter(new ItemMetaRegistry.MetaItemStackGetter()
        {
            @Override
            public EnkiItemMeta get(final String itemId, Object platformSpecificMeta)
            {
                final ItemStack stack = (ItemStack)platformSpecificMeta;
                
                if(stack.getItem() instanceof ItemBlock)
                {
                    return new ForgeBlockMeta(null)
                    {
                        final String id = itemId;
                        final ItemStack innerMeta = stack;
                        
                        @Override
                        public String getItemId()
                        { return id; }
                    };
                }
                
                return new ForgeItemMeta()
                {
                    final String id = itemId;
                    final ItemStack innerMeta = stack;
                    
                    @Override
                    public String getItemId()
                    { return id; }
                };
            }
        });
        
        CompatabilityAccess.getItemMetaRegistry().setDefaultBlockGetter(new ItemMetaRegistry.MetaBlockGetter()
        {
            @Override
            public EnkiBlockMeta get(final String itemId, final BlockCoOrdinate block)
            {
                return new ForgeBlockMeta(EnkiServer.getInstance().getWorld(block.getWorldId()).getBlockAt(block))
                {
                    final String id = itemId;
                    
                    @Override
                    public String getItemId()
                    { return id; }
                };
            }
        });
    }
    
    //========== Convenience Methods ==========
    
    public static String getLastRecordedNameOf(UUID playerId)
    { return getInstance().getUsernameCache().getLastRecordedNameOf(playerId); }
    
    public static UUID getLastRecordedIDForName(String username)
    { return getInstance().getUsernameCache().getLastRecordedIDForName(username); }
}