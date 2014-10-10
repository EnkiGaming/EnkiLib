package com.enkigaming.minecraft.forge.enkilib.filehandling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Generic file-handling registry.
 * Allows you to register pre-created file-handlers and provides universal "save" and "load" options.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
public class FileHandlerRegistry
{
    /**
     * Constructs the filehandler registry.
     * @param plugin The bukkit plugin this handles the file handlers for.
     */
    public FileHandlerRegistry(File saveFolder, Logger logger)
    {
        this.logger = logger;
        Handlers = new ArrayList<FileHandler>();
        this.saveFolder = saveFolder;
    }
    
    List<FileHandler> Handlers;
    Logger logger;
    File saveFolder;

    final Object HandlersBusyFlag = new Object();
    final Object DataFolderBusyFlag = new Object();


    /**
     * Registers a new filehandler.
     * @param handler The handler to register.
     */
    public void Register(FileHandler handler)
    {
        synchronized(HandlersBusyFlag)
        { Handlers.add(handler); }
    }

    /**
     * Saves all registered files.
     */
    public void Save()
    {
        synchronized(HandlersBusyFlag)
        {
            synchronized(DataFolderBusyFlag)
            {
                if(StartSave())
                    for(FileHandler i : Handlers)
                        i.save();
                else
                    print("Save failed - Cannot create plugin directory.");
            }
        }
    }

    /**
     * Ensures that the plugin directory exists.
     * @return True if the directory existed or was created successfully. Else, false.
     */
    boolean StartSave()
    {
        if(!saveFolder.exists())
            return saveFolder.mkdir();
        else return true;
    }

    /**
     * Loads all registered files.
     */
    public void Load()
    {
        List<FileHandler> HandlersToLoad = new ArrayList<FileHandler>();
        boolean CantLoadAll = false;

        synchronized(HandlersBusyFlag)
        {
            HandlersToLoad.addAll(Handlers);

            synchronized(DataFolderBusyFlag)
            {
                saveFolder.mkdirs();

                List<String> HandlersLoaded = new ArrayList<String>();
                CantLoadAll = false;

                for(FileHandler i : HandlersToLoad)
                    i.preInterpretation();

                while(HandlersToLoad.size() > 0 && !CantLoadAll)
                {
                    List<FileHandler> LoadedThisRound = new ArrayList<FileHandler>();

                    for(FileHandler i : HandlersToLoad)
                    {
                        boolean HandlerReady = true;

                        if(i.PrerequisiteHandlers.size() > 0)
                        {
                            for(int j = 0; j < i.PrerequisiteHandlers.size() && HandlerReady; j++)
                            {
                                boolean PrerequisiteLoaded = false;

                                for(int k = 0; k < HandlersLoaded.size() && !PrerequisiteLoaded; k++)
                                    if(HandlersLoaded.get(k).equalsIgnoreCase(i.PrerequisiteHandlers.get(j)))
                                        PrerequisiteLoaded = true;

                                if(!PrerequisiteLoaded)
                                    HandlerReady = false;
                            }
                        }

                        if(HandlerReady)
                        {
                            i.load();
                            LoadedThisRound.add(i);
                            HandlersLoaded.add(i.ID);
                        }
                    }

                    for(FileHandler i : LoadedThisRound)
                        HandlersToLoad.remove(i);

                    if(LoadedThisRound.size() <= 0 && HandlersToLoad.size() > 0)
                        CantLoadAll = true;
                }
            }
        }

        if(CantLoadAll)
            print("Cannot load all filehandlers; some require filehandlers that aren't registered, or a circular requirement exists.");

          // Original version. Oh, times were so much simpler back then ...
//        for(FileHandler i : Handlers)
//            i.load();
    }
    
    void print(String toPrint)
    {
        if(logger != null)
            logger.info(toPrint);
        else
            System.out.println(toPrint);
    }
}