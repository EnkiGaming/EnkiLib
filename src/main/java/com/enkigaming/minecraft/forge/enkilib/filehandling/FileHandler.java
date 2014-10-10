package com.enkigaming.minecraft.forge.enkilib.filehandling;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Generic file handler.
 * Takes a passed file object and automates the process of actually writing to
 * and reading from the file.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 * @note Pre-interpretation and post-interpretation happen regardless of whether or not there's actually a file to interpret.
 */
public abstract class FileHandler
{
    /**
     * The constructor. File corrupted message is generated using the file name.
     * @param file The file this handler should write to and read from.
     */
    public FileHandler(String HandlerID, File file)
    { this(HandlerID, file, null, "File Corrupted: " + file.getName()); }

    /**
     * The constructor.
     * @param file The file this handler should write to and read from.
     * @param CorruptFileMessage The message to be displayed upon establishing that a file's corrupt.
     */
    public FileHandler(String HandlerID, File file, String CorruptFileMessage)
    { this(HandlerID, file, null, CorruptFileMessage); }

    /**
     * The constructor. File corrupted message is generated using the file name.
     * @param file The file this handler should write to and read from.
     * @param logger The logger that messages should be sent to.
     */
    public FileHandler(String HandlerID, File file, Logger logger)
    { this(HandlerID, file, logger, "File Corrupted: " + file.getName()); }

    /**
     * The constructor.
     * @param file The file this handler should write to and read from.
     * @param logger The logger that messages should be sent to.
     * @param CorruptFileMessage The message to be displayed upon establishing that a file's corrupt.
     */
    public FileHandler(String HandlerID, File file, Logger logger, String CorruptFileMessage)
    {
        ID = HandlerID;
        HandledFile = file;
        this.logger = logger;
        this.CorruptFileMessage = CorruptFileMessage;
    }


    String ID;
    File HandledFile;
    String CorruptFileMessage;
    Logger logger;

    final Object FileBusyFlag = new Object();
    final Object CorruptFileMessageBusyFlag = new Object();
    final Object LoggerBusyFlag = new Object();

    final List<String> PrerequisiteHandlers = new ArrayList<String>();
    

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Gets the message to be displayed upon establishing that a file's corrupt.
     * @return The aforementioned message.
     */
    public String getCorruptFileMessage()
    {
        synchronized(CorruptFileMessageBusyFlag)
        { return CorruptFileMessage; }
    }

    /**
     * Gets the file this handler reads or writes to.
     * @return The aforementioned file.
     */
    public File getFile()
    {
        synchronized(FileBusyFlag)
        { return HandledFile; }
    }

    /**
     * Gets the logger used for sending messages.
     * @return The aforementioned logger.
     */
    public Logger getLogger()
    {
        synchronized(LoggerBusyFlag)
        { return logger; }
    }

    /**
     * Sets the message to be displayed upon establishing that a file's corrupt.
     * @param cfm The aforementioned message.
     */
    public void setCorruptFileMessage(String cfm)
    {
        synchronized(CorruptFileMessageBusyFlag)
        { CorruptFileMessage = cfm; }
    }

    /**
     * Sets the file this handler reads or writes to.
     * @param file The aforementioned file.
     */
    public void setFile(File file)
    {
        synchronized(FileBusyFlag)
        { HandledFile = file; }
    }

    /**
     * Sets the logger that should be used for sending messages.
     * @param logger The aforementioned logger.
     */
    public void setLogger(Logger logger)
    {
        synchronized(LoggerBusyFlag)
        { this.logger = logger; }
    }
    //</editor-fold>

    /**
     * Is called to allow any preparation of the file before being loaded to occur.
     */
    protected abstract void preSave();

    //<editor-fold defaultstate="collapsed" desc="Abstract Methods">
    /**
     * Builds the save file.
     * This method isolates the logic for the construction of the file from the logic for writing to the file, which the handler handles.
     * @param writer The printwriter. This provides the print methods required to write to the file.
     */
    protected abstract void buildSaveFile(PrintWriter writer);

    /**
     * Is called to allow any cleaning-up of the file after being loaded to occur.
     */
    protected abstract void postSave();

    /**
     * Is called to allow any preparation of the file being loaded to occur.
     */
    protected abstract void preInterpretation();

    /**
     * Interprets the contents of the file.
     * This method isolates the logic for the interpretation of the file from the logic for reading from the file, which the handler handles.
     * @param Lines The contents of the file, with each line split up into a different string.
     * @return True if file loads flawlessly. False if the file is corrupted.
     */
    protected abstract boolean interpretFile(List<String> Lines);

    /**
     * Is called to allow any cleaning-up of the file being loaded to occur.
     */
    protected abstract void postInterpretation();

    /**
     * What happens when the filehandler attempts to load a file that doesn't exist.
     * This should normally be used to flag this fact up on the console, and load default values if applicable.
     */
    protected abstract void onNoFileToInterpret();
    //</editor-fold>

    /**
     * Saves the file.
     */
    public void save()
    {
        try
        {
            synchronized(FileBusyFlag)
            {
                HandledFile.mkdirs();
                if(HandledFile.exists()) HandledFile.delete();

                HandledFile.createNewFile();

                FileWriter fw = new FileWriter(HandledFile, true);
                PrintWriter pw = new PrintWriter(fw);

                preSave();
                buildSaveFile(pw);
                postSave();

                pw.flush();
                pw.close();
                fw.close();
            }
        }
        catch(IOException e)
        { e.printStackTrace(); }
    }

    /**
     * Loads the file.
     */
    public void load()
    {
        synchronized(FileBusyFlag)
        {
            if(HandledFile.exists())
            {
                try
                {
                    DataInputStream input = new DataInputStream(new FileInputStream(HandledFile));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    List<String> Lines = new ArrayList<String>();

                    for(String i = ""; i != null; i = reader.readLine())
                        Lines.add(i);
                    try
                    {
                        if(!interpretFile(Lines))
                        {
                            copyFile(HandledFile, new File(HandledFile.getParentFile(), appendCorruptedNote(HandledFile.getName())));
                            synchronized(LoggerBusyFlag)
                            {
                                synchronized(CorruptFileMessageBusyFlag)
                                { print(CorruptFileMessage); }
                            }
                        }
                    }
                    finally
                    {
                        postInterpretation();

                        reader.close();
                        input.close();
                    }
                }
                catch(IOException e)
                { e.printStackTrace(); }
            }
            else
            {
                try
                { onNoFileToInterpret(); }
                finally
                { postInterpretation(); }
            }
        }
    }

    /**
     * Specifies that this filehandler should only ever load after another has already loaded.
     * @param HandlerID The ID of the filehandler to load after.
     */
    public void mustLoadAfterHandler(String HandlerID)
    {
        if(HandlerID != null)
            synchronized(PrerequisiteHandlers)
            { PrerequisiteHandlers.add(HandlerID); }
        else
            throw new IllegalArgumentException("MustLoadAfterHandler(String) requires a non-null string as the handler ID.");
    }

    /**
     * Gets the file-name passed with the addition of a corrupted note specifying the date and time the file was deemed to be corrupt.
     * @param FileName The file-name to add the tag to.
     * @return The file-name with the tag appended.
     */
    String appendCorruptedNote(String FileName)
    {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH|mm|ss");
        String CorruptedNote = " (Corrupted " + format.format(now) + ")";

        String[] FileNameParts = HandledFile.getName().split("\\.");

        if(FileNameParts.length > 1)
        {
            String FileNameWithoutExtension = "";
            String Extension = FileNameParts[FileNameParts.length - 1];

            for(int i = 0; i < FileNameParts.length - 1; i++)
                FileNameWithoutExtension = FileNameWithoutExtension + FileNameParts[i];

            return FileNameWithoutExtension + CorruptedNote + "." + Extension;
        }
        else
            return FileNameParts[0] + CorruptedNote;
    }

    /**
     * Makes a copy of a few in a new path.
     * @param sourceFile The file to copy.
     * @param destFile The file to copy the source file to.
     * @throws IOException Input and output related errors pertaining to the file input and file output streams as a result of the functionality of this method.
     */
    void copyFile(File sourceFile, File destFile) throws IOException
    {
        if(!destFile.exists())
            destFile.createNewFile();

        FileChannel source = null;
        FileChannel destination = null;

        try
        {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally
        {
            if(source != null)
                source.close();

            if(destination != null)
                destination.close();
        }
    }
    
    void print(String toPrint)
    {
        if(logger != null)
            logger.info(toPrint);
        else
            System.out.println(toPrint);
    }
}