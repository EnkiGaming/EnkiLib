package com.enkigaming.mc.lib.compatability.items;

import com.enkigaming.mc.lib.compatability.CommandSender;
import com.enkigaming.mc.lib.compatability.EnkiBlockMeta;
import org.apache.commons.lang3.NotImplementedException;

public class ItemMetaCommandBlock extends EnkiBlockMeta implements CommandSender
{
    @Override
    public String getItemId()
    {
        throw new NotImplementedException("Not implemented yet.");
    }

    @Override
    public int getNumericItemId()
    {
        throw new NotImplementedException("Not implemented yet.");
    }
}