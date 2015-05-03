package com.enkigaming.mc.lib.pvp;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventListener;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.misc.BlockCoOrdinate;
import com.enkigaming.mc.lib.misc.TickCountdownTimer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
        public GameStartedArgs(Collection<UUID> playersPlaying,
                               Collection<PvpTeam> teamsPlaying,
                               GameState initialGameState,
                               String startingMessage,
                               int gameTimeInMinutes,
                               int minTeamsRequired)
        {
            this.playersPlaying    = playersPlaying;
            this.teamsPlaying      = teamsPlaying;
            this.startingMessage   = startingMessage;
            this.gameTimeInMinutes = gameTimeInMinutes;
            this.minTeamsRequired  = minTeamsRequired;
            this.initialGameState  = initialGameState;
        }
        
        Collection<UUID> playersPlaying;
        Collection<PvpTeam> teamsPlaying;
        GameState initialGameState;
        String startingMessage;
        int gameTimeInMinutes;
        int minTeamsRequired;
        
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
        
        public Collection<PvpTeam> getTeamsPlaying()
        { return teamsPlaying; }
        
        public int getMinimumTeamsRequiredToStart()
        { return minTeamsRequired; }
        
        public GameState getInitialGameState()
        { return initialGameState; }
        
        public GameState setInitialGameState(GameState newState)
        {
            GameState old = initialGameState;
            initialGameState = newState;
            return old;
        }
    }
    
    public static class GameFinishedArgs extends StandardEventArgs
    {
        public GameFinishedArgs(PvpTeam winningTeam, Collection<UUID> remainingPlayers, String gameOverMessage)
        {
            this.winningTeam = winningTeam;
            this.remainingPlayers = remainingPlayers;
            this.gameOverMessage = gameOverMessage;
        }
        
        PvpTeam winningTeam;
        Collection<UUID> remainingPlayers;
        String gameOverMessage;
        
        public PvpTeam getWinningTeam()
        { return winningTeam; }
        
        public Collection<UUID> getRemainingPlayers()
        { return remainingPlayers; }
        
        public String getGameOverMessage()
        { return gameOverMessage; }
        
        public String setGameOverMessage(String newMessage)
        {
            String old = gameOverMessage;
            gameOverMessage = newMessage;
            return old;
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
        minNumberOfTeams = 2;
        
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
            { startGame(); }
        });
        
        /* return gameTimer listeners here */
    }
    
    Map<UUID, PlayerGameState> players;
    Collection<PvpTeam> teams;
    BlockCoOrdinate lobbySpawn;
    GameStates possibleGameStates;
    GameState gameState;
    TickCountdownTimer lobbyTimer, gameTimer;
    int minNumberOfTeams;
    
    public final Event<PlayerJoinedArgs> playerJoined = new StandardEvent<PlayerJoinedArgs>();
    public final Event<GameStartedArgs> gameStarted = new StandardEvent<GameStartedArgs>();
    
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
    
    public boolean addPlayer(UUID playerId)
    {
        PlayerJoinedArgs args = new PlayerJoinedArgs(playerId, lobbySpawn);
        playerJoined.raise(this, args);
        boolean added;
        
        try
        {
            if(!args.isCancelled())
            {
                added = true;
                players.put(playerId, PlayerGameState.inLobby);
                if(args.getStartingPosition() != null)
                    CompatabilityAccess.getPlayer(playerId).teleportTo(args.getStartingPosition());
            }
            else
                added = false;
        }
        finally
        { playerJoined.raisePostEvent(this, args); }
        
        return added;
    }
    
    public void removePlayer(UUID playerId)
    { players.remove(playerId); }
    
    public void removePlayer(UUID playerId, BlockCoOrdinate whereToTpThem)
    {
        removePlayer(playerId);
        CompatabilityAccess.getPlayer(playerId).teleportTo(whereToTpThem);
    }
    
    public void startLobbyCountdown(int minutes)
    {
        lobbyTimer.setTimeLeft(minutes, 0);
        lobbyTimer.start();
    }
    
    public void startGameCountdown(int minutes)
    {
        gameTimer.setTimeLeft(minutes, 0);
        gameTimer.start();
    }
    
    public void startGame()
    {
        String startMessage = "The game is starting.";
        int gameTimeInMinutes = 30;
        boolean abort = false;

        GameStartedArgs startedArgs = new GameStartedArgs(new HashSet<UUID>(players.keySet()),
                                                          null, // filled in later down.
                                                          possibleGameStates.inGame,
                                                          startMessage,
                                                          gameTimeInMinutes,
                                                          minNumberOfTeams);

        try
        {
            teams = getNewTeams(startedArgs.getPlayers());
            startedArgs.teamsPlaying = new HashSet<PvpTeam>(teams);
                
            if(teams.size() < minNumberOfTeams)
            {
                startedArgs.setStartingMessage("Not enough players to start.");
                abort = true;
            }
            
            gameStarted.raise(this, startedArgs);
            
            if(!startedArgs.isCancelled())
            {
                messagePlayers(startedArgs.getStartingMessage());
                
                if(!abort)
                {
                    gameState = startedArgs.getInitialGameState();
                    teleportPlayersToStartingPositions();

                    for(UUID playerId : startedArgs.getPlayers())
                        players.put(playerId, PlayerGameState.inGame);

                    startGameCountdown(startedArgs.getGameTimeInMinutes());
                }
            }
            else
                abort = true;
        }
        finally
        { gameStarted.raisePostEvent(this, startedArgs); }
        
        if(abort)
            startLobbyCountdown(5);
    }
    
    public void messagePlayers(String message)
    {
        for(UUID playerId : players.keySet())
            CompatabilityAccess.getPlayer(playerId).print(message);
    }
    
    public void messageIngamePlayers(String message)
    {
        for(Entry<UUID, PlayerGameState> player : players.entrySet())
            if(player.getValue() == PlayerGameState.inGame)
                CompatabilityAccess.getPlayer(player.getKey()).print(message);
    }
    
    public void messageLobbyPlayers(String message)
    {
        for(Entry<UUID, PlayerGameState> player : players.entrySet())
            if(player.getValue() == PlayerGameState.inLobby)
                CompatabilityAccess.getPlayer(player.getKey()).print(message);
    }
    
    /**
     * Checks whether or not all passed players are on the same team.
     * @param players The players to check.
     * @return True if all players are on the same team. Otherwise, false.
     */
    public boolean playersAreOnSameTeam(UUID... players)
    {
        for(PvpTeam team : teams)
        {
            if(team.containsAllPlayers(players))
                return true;
            
            if(team.containsAnyPlayers(players)) // Operating on the assumption that a player can't be in >1 team.
                return false;
        }
        
        return false;
    }
    
    public boolean teamIsStillInGame(PvpTeam team)
    {
        for(UUID id : team.getPlayers())
            if(players.get(id) == PlayerGameState.inGame)
                return true;
        
        return false;
    }
    
    public boolean thereIsMoreThanOneTeamInGame()
    {
        int teamsInGame = 0;
        
        for(PvpTeam team : teams)
            if(teamIsStillInGame(team))
                if(++teamsInGame > 1)
                    return true;
        
        return false;
    }
    
    public void declareLoser(UUID player)
    {
        players.put(player, PlayerGameState.inLobby);
        teleportPlayerToLobby(player);
        declareRemainingTeamWinnerIfOnlyOneLeft();
    }
    
    public void declareWinner(PvpTeam team)
    {
        
    }
    
    public void declareRemainingTeamWinnerIfOnlyOneLeft()
    {
        PvpTeam currentWinningTeam = null;
        
        for(PvpTeam team : teams)
            if(teamIsStillInGame(team))
                if(currentWinningTeam == null)
                    currentWinningTeam = team;
                else
                    return;
        
        if(currentWinningTeam == null)
            return;
        
        declareWinner(currentWinningTeam);
    }
    
    public abstract void teleportPlayersToStartingPositions();
    
    protected abstract GameStates getNewGameStatesObject();
    
    protected abstract Collection<PvpTeam> getNewTeams(Collection<UUID> players);
}