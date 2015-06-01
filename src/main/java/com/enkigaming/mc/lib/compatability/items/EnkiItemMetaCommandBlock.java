package com.enkigaming.mc.lib.compatability.items;

import com.enkigaming.mc.lib.compatability.CommandSender;
import com.enkigaming.mc.lib.compatability.EnkiBlock;

public abstract class EnkiItemMetaCommandBlock extends EnkiBlockMeta implements CommandSender
{
    public EnkiItemMetaCommandBlock(EnkiBlock block)
    { super(block); }
}