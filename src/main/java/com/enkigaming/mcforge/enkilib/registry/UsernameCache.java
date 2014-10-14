package com.enkigaming.mcforge.enkilib.registry;

import com.enkigaming.mcforge.enkilib.filehandling.CSVFileHandler;
import com.enkigaming.mcforge.enkilib.filehandling.CSVFileHandler.CSVRowMember;
import com.enkigaming.mcforge.enkilib.filehandling.FileHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UsernameCache
{
    public UsernameCache(File saveFolder)
    { fileHandler = makeFileHandler(saveFolder); }
    
    protected Map<UUID, String> recordedUsernames = new HashMap<UUID, String>();
    
    protected Lock recordedUsernamesLock = new ReentrantLock();
    protected FileHandler fileHandler;
    
    protected FileHandler makeFileHandler(File saveFolder)
    {
        return new CSVFileHandler("UsernameCache", new File(saveFolder, "UsernameCache.csv"), "Unable to load all cached usernames to UUIDs. ")
        {
            List<Map.Entry<UUID, String>> entryList;
            
            @Override
            protected void onNoFileToInterpret()
            {}

            @Override
            protected List<String> getColumnNames()
            { return Arrays.asList("Player ID", "Last recorded username"); }

            @Override
            protected void preInterpretation()
            { recordedUsernamesLock.lock(); }

            @Override
            protected boolean interpretRow(List<String> row)
            {
                if(row.size() != 2)
                    return false;
                
                UUID id;
                
                try
                { id = UUID.fromString(row.get(0)); }
                catch(IllegalArgumentException e)
                { return false; }
                
                recordedUsernames.put(id, row.get(1));
                return true;
            }

            @Override
            protected void postInterpretation()
            { recordedUsernamesLock.unlock(); }

            @Override
            protected void preSave()
            {
                recordedUsernamesLock.lock();
                entryList = new ArrayList<Map.Entry<UUID, String>>(recordedUsernames.entrySet());
            }

            @Override
            protected List<CSVRowMember> getRow(int rowNumber)
            {
                return Arrays.asList(new CSVRowMember(entryList.get(rowNumber).getKey().toString(), false),
                                     new CSVRowMember(entryList.get(rowNumber).getValue(),          true));
            }

            @Override
            protected void postSave()
            { recordedUsernamesLock.lock(); }
        };
    }
    
    public String getLastRecordedNameOf(UUID playerId)
    {
        recordedUsernamesLock.lock();
        
        try
        { return recordedUsernames.get(playerId); }
        finally
        { recordedUsernamesLock.unlock(); }
    }
    
    public UUID getLastRecordedUUIDForName(String username)
    {
        recordedUsernamesLock.lock();
        
        try
        {
            for(Map.Entry<UUID, String> entry : recordedUsernames.entrySet())
                if(entry.getValue().equalsIgnoreCase(username))
                    return entry.getKey();
            
            return null;
        }
        finally
        { recordedUsernamesLock.unlock(); }
    }
    
    public String recordUsername(UUID playerId, String username)
    {
        recordedUsernamesLock.lock();
        
        try
        {
            removeCachedUsername(username);
            return recordedUsernames.put(playerId, username);
        }
        finally
        { recordedUsernamesLock.unlock(); }
    }
    
    public void removeCachedUsername(String username)
    {
        recordedUsernamesLock.lock();
        
        try
        {
            Collection<UUID> toRemove = new ArrayList<UUID>();
            
            for(Map.Entry<UUID, String> entry : recordedUsernames.entrySet())
                if(username.equalsIgnoreCase(entry.getValue()))
                    toRemove.add(entry.getKey());
            
            for(UUID id : toRemove)
                recordedUsernames.remove(id);
        }
        finally
        { recordedUsernamesLock.unlock(); }
    }
    
    public FileHandler getFileHandler()
    { return fileHandler; }
}