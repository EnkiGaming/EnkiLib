package com.enkigaming.mcforge.lib.compatability;

import com.enkigaming.mc.lib.compatability.EnkiServer;
import java.util.UUID;

public class ForgeServer extends EnkiServer
{
    @Override
    protected ForgeWorld getNewWorldObject(int worldId)
    { return new ForgeWorld(worldId); }

    @Override
    protected ForgePlayer getNewPlayerObject(UUID playerId)
    { return new ForgePlayer(playerId);}
}