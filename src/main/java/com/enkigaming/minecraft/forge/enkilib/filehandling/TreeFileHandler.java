package com.enkigaming.minecraft.forge.enkilib.filehandling;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public abstract class TreeFileHandler extends FileHandler
{
    public static class TreeMember
    {
        public TreeMember(String name)
        { this.name = name; }
        
        protected String name;
        protected List<TreeMember> members = new ArrayList<TreeMember>();
        
        public String getName()
        { return name; }
        
        public boolean addMember(TreeMember member)
        { return members.add(member); }
        
        public boolean addMembers(Collection<TreeMember> membersToAdd)
        { return members.addAll(membersToAdd); }
        
        public List<TreeMember> getMembers()
        { return new ArrayList<TreeMember>(members); }
    }
    
    protected static class NameIndentLevelPair
    {
        public NameIndentLevelPair(String name, int indentLevel)
        {
            this.name = name;
            this.indentLevel = indentLevel;
        }
        
        protected final String name;
        protected final int indentLevel;
        
        public String getName()
        { return name; }
        
        public int getIndentLevel()
        { return indentLevel; }
    }
    
    protected static class TreeMemberPairListPair
    {
        public TreeMemberPairListPair(TreeMember member, List<NameIndentLevelPair> pairs)
        {
            this.member = member;
            this.pairs = new ArrayList<NameIndentLevelPair>(pairs);
        }
        
        protected final TreeMember member;
        protected final List<NameIndentLevelPair> pairs;
        
        public TreeMember getMember()
        { return member; }
        
        public List<NameIndentLevelPair> getPairs()
        { return pairs; }
    }

    public TreeFileHandler(String handlerId, File file)
    { super(handlerId, file); }
    
    public TreeFileHandler(String handlerId, File file, String corruptFileMessage)
    { super(handlerId, file, corruptFileMessage); }
    
    public TreeFileHandler(String handlerId, File file, Logger logger)
    { super(handlerId, file, logger); }
    
    public TreeFileHandler(String handlerId, File file, Logger logger, String corruptFileMessage)
    { super(handlerId, file, logger, corruptFileMessage); }
    
    protected String indentLevelText = "    ";
    
    @Override
    protected abstract void preSave();

    @Override
    protected void buildSaveFile(PrintWriter writer)
    {
        List<TreeMember> baseMembers = getTreeStructureOfSaveData();
        
        for(TreeMember baseMember : baseMembers)
            printMemberAndSubmembers(writer, 0, baseMember);
    }
    
    protected abstract List<TreeMember> getTreeStructureOfSaveData();
    
    protected void printMemberAndSubmembers(PrintWriter writer, int indentLevel, TreeMember member)
    {
        writer.println(appendIndent(member.getName(), indentLevel));
        
        for(TreeMember submember : member.getMembers())
            printMemberAndSubmembers(writer, indentLevel + 1, submember);
    }

    @Override
    protected abstract void postSave();

    @Override
    protected abstract void preInterpretation();

    @Override
    protected boolean interpretFile(List<String> lines)
    {
        List<NameIndentLevelPair> values = new ArrayList<NameIndentLevelPair>();
        
        for(String line : lines)
            values.add(getValue(new NameIndentLevelPair(line, 0)));
        
        fixLevels(values);
        
        return interpretTree(getTree(values));
    }
    
    protected void fixLevels(List<NameIndentLevelPair> values)
    {
        int currentLevelShouldBeAtMost = 0;
        
        for(int i = 0; i < values.size(); i++)
        {
            if(values.get(i).getIndentLevel() > currentLevelShouldBeAtMost)
                values.set(i, new NameIndentLevelPair(appendIndent(values.get(i).getName(),
                                                                   values.get(i).getIndentLevel() - currentLevelShouldBeAtMost),
                                                      currentLevelShouldBeAtMost));
            
            currentLevelShouldBeAtMost = values.get(i).getIndentLevel() + 1;
        }
    }
    
    protected abstract boolean interpretTree(List<TreeMember> tree);
    
    protected List<TreeMember> getTree(List<NameIndentLevelPair> values)
    {
        List<TreeMember> returnTreeMembers = new ArrayList<TreeMember>();
        List<TreeMemberPairListPair> pairs = new ArrayList<TreeMemberPairListPair>();
        int indentLevel = values.get(0).getIndentLevel();
        
        TreeMember currentTreeMember = new TreeMember(values.get(0).getName());
        List<NameIndentLevelPair> currentPairs = new ArrayList<NameIndentLevelPair>();
        
        for(int i = 1; i < values.size(); i++)
        {
            if(values.get(i).getIndentLevel() <= indentLevel)
            {
                pairs.add(new TreeMemberPairListPair(currentTreeMember, currentPairs));
                currentTreeMember = new TreeMember(values.get(i).getName());
                currentPairs = new ArrayList<NameIndentLevelPair>();
            }
            else
                currentPairs.add(values.get(i));
        }
        
        for(TreeMemberPairListPair pair : pairs)
        {
            pair.getMember().addMembers(getTree(pair.getPairs()));
            returnTreeMembers.add(pair.getMember());
        }
        
        return returnTreeMembers;
    }
    
    protected String appendIndent(String string, int indentLevelToAppend)
    {
        String appendedString = string;
        
        for(int i = 0; i < indentLevelToAppend; i++)
            appendedString = indentLevelText + appendedString;
        
        return appendedString;
    }
    
    protected NameIndentLevelPair getValue(NameIndentLevelPair line)
    {
        if(line.getName().startsWith(indentLevelText))
            return new NameIndentLevelPair(line.getName().substring(indentLevelText.length()), line.getIndentLevel() + 1);
        else
            return line;
    }

    @Override
    protected abstract void postInterpretation();

    @Override
    protected abstract void onNoFileToInterpret();
    
    public String setIndentLevelText(String newIndentLevelText)
    {
        String old = indentLevelText;
        indentLevelText = newIndentLevelText;
        return old;
    }
}