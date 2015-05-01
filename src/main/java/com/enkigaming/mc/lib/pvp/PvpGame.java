package com.enkigaming.mc.lib.pvp;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventListener;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import com.enkigaming.mc.lib.misc.TickCountdownTimer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;

public abstract class PvpGame
{
    public static enum PlayerGameState
    {
        inLobby,
        inGame
    }
    
    public static class GameState
    {
        public GameState(String name)
        { this.name = name; }
        
        public GameState(String name, GameState substateOf)
        {
            this.name = name;
            substateOf.addSubState(this); // Object is constructed at this point.
        }
        
        protected String name;
        protected Set<GameState> subStates = new HashSet<GameState>();
        
        public String getName()
        { return name; }
        
        protected void addSubState(GameState state)
        { subStates.add(state); }
        
        protected void addSubStates(GameState... states)
        { Collections.addAll(subStates, states); }
        
        public void addSubStates(Collection<? extends GameState> states)
        { subStates.addAll(states); }
        
        public void addAsSubStateTo(GameState state)
        { state.addSubState(this); }
        
        public boolean hasSubState(GameState state)
        { 
            for(GameState i : subStates)
            {
                if(i == state)
                    return true;
                
                if(i.hasSubState(state))
                    return true;
            }
            
            return false;
        }
    }
    
    public static class GameStates
    {
        public final GameState waitingForNewGame = new GameState("WaitingForNewGame");
        public final GameState inGame = new GameState("InGame");
        
        /*
        New states can be added by declaring them in a subclass of GameStates. Substates (such as "inDeathMatch" being
        a substate of "inGame" may be added and declared substate at the time of declaration with the following as
        example:
        
        public final GameState inDeathMatch = new GameState("InDeathMatch", inGame);
        */
    }
    
    public static class PlayerJoinedArgs extends StandardEventArgs
    {
        public PlayerJoinedArgs(UUID playerId, BlockCoOrdinate startingPosition)
        {
            this.playerId = playerId;
            this.startingPosition = startingPosition;
        }
        
        UUID playerId;
        BlockCoOrdinate startingPosition;
        
        public UUID getPlayerId()
        { return playerId; }
        
        public BlockCoOrdinate getStartingPosition()
        { return startingPosition; }
        
        public BlockCoOrdinate setStartingPosition(BlockCoOrdinate newStartingPosition)
        {
            BlockCoOrdinate temp = startingPosition;
            startingPosition = newStartingPosition;
            return temp;
        }
    }
    
    public static class GameStartedArgs extends StandardEventArgs
    {
        public GameStartedArgs(Collection<UUID> playersPlaying, String startingMessage)
        {
            this.playersPlaying = playersPlaying;
            this.startingMessage = startingMessage;
        }
        
        Collection<UUID> playersPlaying;
        String startingMessage;
        int gameTimeInMinutes;
        
        public Collection<UUID> getPlayers()
        { return new HashSet<UUID>(playersPlaying); }
        
        public String getStartingMessage()
        { return startingMessage; }
        
        public String setStartingMessage(String message)
        {
            String old = startingMessage;
            startingMessage = message;
            return old;
        }
        
        public int getGameTimeInMinutes()
        {
            throw new NotImplementedException("Not implemented yet.");
        }
        
        public int setGameTimeInMinutes(int minutes)
        {
            throw new NotImplementedException("Not implemented yet.");
        }
    }
    
    public PvpGame()
    {
        players = new HashMap<UUID, PlayerGameState>();
        lobbySpawn = new BlockCoOrdinate();
        possibleGameStates = getNewGameStatesObject();
        gameState = possibleGameStates.waitingForNewGame;
        lobbyTimer = new TickCountdownTimer(60);
        gameTimer = new TickCountdownTimer(60);
        
        lobbyTimer.ticked.register(new EventListener<TickCountdownTimer.TickedArgs>()
        {
            @Override
            public void onEvent(Object sender, TickCountdownTimer.TickedArgs args)
            {
                int secondsLeft = args.getNumberOfSecondsLeft();
                
                if(secondsLeft % 60 == 0 && secondsLeft != 0)
                    messagePlayers(secondsLeft / 60 + " minutes until the game starts.");
                else if(secondsLeft == 30 || secondsLeft == 20 || secondsLeft  > 10)
                    messagePlayers(secondsLeft + " seconds to go.");
            }
        });
        
        lobbyTimer.finished.register(new EventListener<TickCountdownTimer.FinishedArgs>()
        {
            @Override
            public void onEvent(Object sender, TickCountdownTimer.FinishedArgs args)
            {
                Collection<UUID> playersPlaying = new HashSet<UUID>(players.keySet());
                String startMessage = "The game is starting.";
                int gameTimeInMinutes = 30;
                
                GameStartedArgs startedArgs = new GameStartedArgs(playersPlaying, startMessage);
                
                try
                {
                    gameStarted.raise(this, startedArgs);
                    messagePlayers(startedArgs.getStartingMessage());
                    teams = getNewTeams(startedArgs.getPlayers());
                    teleportPlayersToStartingPositions();
                    
                    for(UUID playerId : startedArgs.getPlayers())
                        players.put(playerId, PlayerGameState.inGame);
                    
                    gameTimer.setTimeLeft(gameTimeInMinutes, 0);
                    gameTimer.start();
                }
                finally
                { gameStarted.raisePostEvent(this, startedArgs); }
            }
        });
        
        /* return gameTimer listeners here */
    }
    
    Map<UUID, PlayerGameState> players;
    Collection<PvpTeam> teams;
    BlockCoOrdinate lobbySpawn;
    GameStates possibleGameStates;
    GameState gameState;
    TickCountdownTimer lobbyTimer, gameTimer;
    
    public static final Event<PlayerJoinedArgs> playerJoined = new StandardEvent<PlayerJoinedArgs>();
    public static final Event<GameStartedArgs> gameStarted = new StandardEvent<GameStartedArgs>();
    
    public void teleportPlayersToLobby()
    {
        for(UUID playerId : players.keySet())
            CompatabilityAccess.getPlayer(playerId).teleportTo(lobbySpawn);
    }
    
    public void teleportPlayerToLobby(UUID playerId)
    { CompatabilityAccess.getPlayer(playerId).teleportTo(lobbySpawn); }
    
    public GameStates getPossibleGameStates()
    { return possibleGameStates; }
    
    public GameState getCurrentGameState()
    { return gameState; }
    
    public void addPlayer(UUID playerId)
    {
        PlayerJoinedArgs args = new PlayerJoinedArgs(playerId, lobbySpawn);
        playerJoined.raise(this, args);
        
        try
        {
            players.put(playerId, PlayerGameState.inLobby);
            if(args.getStartingPosition() != null)
                CompatabilityAccess.getPlayer(playerId).teleportTo(args.getStartingPosition());
        }
        finally
        { playerJoined.raisePostEvent(this, args); }
    }
    
    public void removePlayer(UUID playerId)
    { players.remove(playerId); }
    
    public void removePlayer(UUID playerId, BlockCoOrdinate whereToTpThem)
    {
        removePlayer(playerId);
        CompatabilityAccess.getPlayer(playerId).teleportTo(whereToTpThem);
    }
    
    public void startLobbyCountdown()
    {
        lobbyTimer.setTimeLeft(5, 0);
        lobbyTimer.start();
    }
    
    public void startGameCountdown()
    {
        gameTimer.setTimeLeft(30, 0);
        gameTimer.start();
    }
    
    public void messagePlayers(String message)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public void messageIngamePlayers(String message)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public void messageLobbyPlayers(String message)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    /**
     * Checks whether or not all passed players are on the same team.
     * @param players The players to check.
     * @return True if all players are on the same team. Otherwise, false.
     */
    public boolean playersAreOnSameTeam(UUID... players)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public boolean teamIsStillInGame(PvpTeam team)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public boolean thereIsMoreThanOneTeamInGame()
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public void declareLoser(UUID player)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public void declareWinner(PvpTeam team)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    public abstract void teleportPlayersToStartingPositions();
    
    protected abstract GameStates getNewGameStatesObject();
    
    protected abstract Collection<PvpTeam> getNewTeams(Collection<UUID> players);
}