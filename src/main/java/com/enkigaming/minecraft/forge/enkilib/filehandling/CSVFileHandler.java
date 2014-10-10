package com.enkigaming.minecraft.forge.enkilib.filehandling;

// To do: Swap out the CSV-line handling mechanism with a more thorough one.

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// To do: Check Javadoc.

/**
 * Generic comma-separated-value file handler. (specific to bukkit)
 * Takes a file and automates the process of writing to the file as well as the
 * logic required to format that file as a CSV file.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
public abstract class CSVFileHandler extends FileHandler
{
    public static class CSVRowMember
    {
        public CSVRowMember(String contents, boolean wrapInQuotes)
        {
            this.contents = contents;
            this.wrapInQuotes = wrapInQuotes;
        }
        
        protected String contents;
        protected final boolean wrapInQuotes;
        
        public String getContents()
        { return contents; }
        
        void setContents(String newContents)
        { contents = newContents; }
        
        public boolean shouldBeWrappedInQuotes()
        { return wrapInQuotes; }
    }
    
    /**
     * The constructor. File corrupted message is generated using the file name.
     * @param file The file this handler should write to and read from.
     */
    public CSVFileHandler(String HandlerID, File file)
    { super(HandlerID, file); }

    /**
     * The constructor.
     * @param file The file this handler should write to and read from.
     * @param CorruptFileMessage The message to be displayed upon establishing that a file's corrupt.
     */
    public CSVFileHandler(String HandlerID, File file, String CorruptFileMessage)
    { super(HandlerID, file, CorruptFileMessage); }

    /**
     * The constructor. File corrupted message is generated using the file name.
     * @param file The file this handler should write to and read from.
     * @param logger The logger that messages should be sent to.
     */
    public CSVFileHandler(String HandlerID, File file, Logger logger)
    { super(HandlerID, file, logger); }

    /**
     * The constructor.
     * @param file The file this handler should write to and read from.
     * @param logger The logger that messages should be sent to.
     * @param CorruptFileMessage The message to be displayed upon establishing that a file's corrupt.
     */
    public CSVFileHandler(String HandlerID, File file, Logger logger, String CorruptFileMessage)
    { super(HandlerID, file, logger, CorruptFileMessage); }

    
    @Override
    protected boolean interpretFile(List<String> Lines)
    {
        boolean Corrupt = false;
        List<String> Entries = new ArrayList<String>();

        stripEmptyLines(Lines);

        if(!Lines.get(0).equalsIgnoreCase(getHeader()))
            Corrupt = true;

        for(int i = 1; i < Lines.size(); i++)
        {
            List<String> Fields = splitCSVLine(Lines.get(i));
            boolean LineIsCorrupt = false;

            if(!interpretRow(Fields))
                Corrupt = true;
        }

        return !Corrupt;
    }

    @Override
    protected abstract void onNoFileToInterpret();

    String getHeader()
    {
        List<String> ColumnNames = getColumnNames();

        if(ColumnNames.size() <= 0)
            throw new RuntimeException("There should be at least one column in a CSV file.");

        String Header = ColumnNames.get(0);

        for(int i = 1; i < ColumnNames.size(); i++)
            Header += ("," + ColumnNames.get(i));

        return Header;
    }

    void stripEmptyLines(List<String> Lines)
    {
        List<String> LinesToRemove = new ArrayList<String>();

        for(String i : Lines)
            if(i.trim().equals(""))
                LinesToRemove.add(i);

        for(String i : LinesToRemove)
            Lines.remove(i);
    }

    List<String> splitCSVLine(String ToSplit)
    {
        String[] temp = ToSplit.split(",", -1);

        for(int i = 0; i < temp.length; i++)
        {
            temp[i] = temp[i].trim();
            temp[i] = temp[i].replaceAll("^\"|\"$", "");
        }

        return Arrays.asList(temp);

        /*
         *
         * This is temporary. This simply splits the string by commas and removes whitespace + quotes.
         * The final method should:
         * ignore commas that are in quotes
         * allow use of the backspace (\) to escape quotes and commas
         * treat double-quotes as single, normal quote marks except in an empty field
         * treat quotes that aren't at the start or end of a field as normal quote marks.
         *
         */
    }

    /**
     * Gets a list of strings containing the names of each of the columns.
     * @return The aforementioned list.
     */
    protected abstract List<String> getColumnNames();

    /**
     * Action to take before the rows in the file are interpreted.
     */
    @Override
    protected abstract void preInterpretation();

    /**
     * Takes a list of strings containing the fields of a row from the file (in order from left to right) and handles the
     * strings as intended.
     * @param Row The list of strings representing the fields from a row in the file.
     * @return True if the row was interpreted correctly; false if the row was corrupt.
     */
    protected abstract boolean interpretRow(List<String> Row);

    /**
     * Action to take immediately after the rows in the file have been interpreted.
     */
    @Override
    protected abstract void postInterpretation();
    
    
    
    @Override
    protected void buildSaveFile(PrintWriter writer)
    {
        writer.println(getHeader());

        List<CSVRowMember> Fields;
        boolean Finished = false;
        String Row;

        for(int i = 0; !Finished; i++)
        {
            Fields = getRow(i);

            if(Fields == null)
                Finished = true;
            else
            {
                if(Fields.size() > 0)
                {
                    for(CSVRowMember j : Fields)
                        if(j.shouldBeWrappedInQuotes())
                            j.setContents("\"" + j.getContents() + "\"");

                    Row = Fields.get(0).getContents();

                    for(int j = 1; j < Fields.size(); j++)
                        Row += ("," + Fields.get(j).getContents());

                    writer.println(Row);
                }
                else
                    writer.println("");
            }
        }
    }

    @Override
    protected abstract void preSave();

    /**
     * Gets a list containing pairs, each representing an entry in the row.
     * The string represents the text, and the boolean represents whether or not the string should be in quotes.
     * @param RowNumber The number of the row in the data set requested.
     * @return The row requested, or null if the the RowNumber is past the end of the dataset.
     */
    protected abstract List<CSVRowMember> getRow(int RowNumber);

    @Override
    protected abstract void postSave();
}