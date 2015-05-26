package com.enkigaming.mc.lib.pvp;

import com.enkigaming.lib.events.Event;
import com.enkigaming.lib.events.EventListener;
import com.enkigaming.lib.events.StandardEvent;
import com.enkigaming.lib.events.StandardEventArgs;
import com.enkigaming.mc.lib.compatability.CompatabilityAccess;
import com.enkigaming.mc.lib.compatability.EnkiPlayer;
import com.enkigaming.mc.lib.misc.PlayerPosition;
import com.enkigaming.mc.lib.misc.TickCountdownTimer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

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
        
        protected void addSubStates(Collection<? extends GameState> states)
        { subStates.addAll(states); }
        
        protected void addAsSubStateTo(GameState state)
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
        public PlayerJoinedArgs(UUID playerId, PlayerPosition startingPosition)
        {
            this.playerId = playerId;
            this.destination = startingPosition;
        }
        
        UUID playerId;
        PlayerPosition destination;
        
        public UUID getPlayerId()
        { return playerId; }
        
        public EnkiPlayer getPlayer()
        { return CompatabilityAccess.getPlayer(playerId); }
        
        public PlayerPosition getTeleportDestination()
        { return destination; }
        
        public PlayerPosition setTeleportDestination(PlayerPosition newStartingPosition)
        {
            checkMutability();
            PlayerPosition temp = destination;
            destination = newStartingPosition;
            return temp;
        }
    }
    
    public static class PlayerLeftArgs extends StandardEventArgs
    {
        public PlayerLeftArgs(UUID playerId)
        {
            this.playerId = playerId;
            destination = null;
        }
        
        public PlayerLeftArgs(UUID playerId, PlayerPosition destination)
        {
            this.playerId = playerId;
            this.destination = destination;
        }
        
        UUID playerId;
        PlayerPosition destination;
        
        public UUID getPlayerId()
        { return playerId; }
        
        public EnkiPlayer getPlayer()
        { return CompatabilityAccess.getPlayer(playerId); }
        
        public PlayerPosition getTeleportDestination()
        { return destination; }
        
        public PlayerPosition setTeleportDestination(PlayerPosition newDestination)
        {
            checkMutability();
            PlayerPosition old = destination;
            destination = newDestination;
            return old;
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
        
        public Collection<UUID> getPlayerIds()
        { return new HashSet<UUID>(playersPlaying); }
        
        public Collection<EnkiPlayer> getPlayers()
        {
            HashSet<EnkiPlayer> players = new HashSet<EnkiPlayer>();
            
            for(UUID id : playersPlaying)
                players.add(CompatabilityAccess.getPlayer(id));
            
            return players;
        }
        
        public String getStartingMessage()
        { return startingMessage; }
        
        public String setStartingMessage(String message)
        {
            checkMutability();
            String old = startingMessage;
            startingMessage = message;
            return old;
        }
        
        public int getGameTimeInMinutes()
        { return gameTimeInMinutes; }
        
        public int setGameTimeInMinutes(int minutes)
        {
            checkMutability();
            int old = gameTimeInMinutes;
            gameTimeInMinutes = minutes;
            return old;
        }
        
        public Collection<PvpTeam> getTeamsPlaying()
        { return new ArrayList<PvpTeam>(teamsPlaying); }
        
        public int getMinimumTeamsRequiredToStart()
        { return minTeamsRequired; }
        
        public GameState getInitialGameState()
        { return initialGameState; }
        
        public GameState setInitialGameState(GameState newState)
        {
            checkMutability();
            GameState old = initialGameState;
            initialGameState = newState;
            return old;
        }
    }
    
    public static class GameFinishedArgs extends StandardEventArgs
    {
        public GameFinishedArgs(PvpTeam winningTeam,
                                Collection<UUID> remainingPlayers,
                                PlayerPosition respawnLocation,
                                int minutesUntilNextGame,
                                String gameOverMessage,
                                String winnerMessage)
        {
            this.winningTeam          = winningTeam;
            this.remainingPlayers     = remainingPlayers;
            this.respawnLocation      = respawnLocation;
            this.minutesUntilNextGame = minutesUntilNextGame;
            this.gameOverMessage      = gameOverMessage;
            this.winnerMessage        = winnerMessage;
        }
        
        PvpTeam winningTeam;
        Collection<UUID> remainingPlayers;
        PlayerPosition respawnLocation;
        int minutesUntilNextGame;
        String gameOverMessage;
        String winnerMessage;
        
        public PvpTeam getWinningTeam()
        { return winningTeam; }
        
        public Collection<UUID> getRemainingPlayers()
        { return new ArrayList<UUID>(remainingPlayers); }
        
        public String getGameOverMessage()
        { return gameOverMessage; }
        
        public String setGameOverMessage(String newMessage)
        {
            checkMutability();
            String old = gameOverMessage;
            gameOverMessage = newMessage;
            return old;
        }
        
        public String getWinnerMessage()
        { return winnerMessage; }
        
        public String setWinnerMessage(String newMessage)
        {
            checkMutability();
            String old = winnerMessage;
            winnerMessage = newMessage;
            return old;
        }
        
        public PlayerPosition getRespawnLocation()
        { return respawnLocation; }
        
        public PlayerPosition setRespawnLocation(PlayerPosition newLocation)
        {
            checkMutability();
            PlayerPosition old = respawnLocation;
            respawnLocation = newLocation;
            return old;
        }
        
        public int getMinutesUntilNextGame()
        { return minutesUntilNextGame; }
        
        public int setMinutesUntilNextGame(int minutes)
        {
            checkMutability();
            int old = minutesUntilNextGame;
            minutesUntilNextGame = minutes;
            return old;
        }
    }
    
    public PvpGame()
    {
        players = new HashMap<UUID, PlayerGameState>();
        lobbySpawn = null;
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
    
    final Map<UUID, PlayerGameState> players;
    Collection<PvpTeam> teams;
    PlayerPosition lobbySpawn;
    GameStates possibleGameStates;
    GameState gameState;
    TickCountdownTimer lobbyTimer, gameTimer;
    int minNumberOfTeams;
    
    final Object lobbySpawnBusy = new Object();
    final Object teamsBusy = new Object();
    
    public final Event<PlayerJoinedArgs> playerJoined = new StandardEvent<PlayerJoinedArgs>();
    public final Event<PlayerLeftArgs>   playerLeft   = new StandardEvent<PlayerLeftArgs>();
    public final Event<GameStartedArgs>  gameStarted  = new StandardEvent<GameStartedArgs>();
    public final Event<GameFinishedArgs> gameFinished = new StandardEvent<GameFinishedArgs>();
    
    public void teleportPlayersToLobby()
    {
        PlayerPosition destination;
        Collection<UUID> playerIds;
        
        synchronized(lobbySpawnBusy)
        { destination = lobbySpawn; }
        
        if(destination == null)
        {
            System.out.print("Attempted to teleport PvpGame players to lobby when no lobby position has been set.");
            return;
        }
        
        synchronized(players)
        { playerIds = new ArrayList<UUID>(players.keySet()); }
        
        for(UUID playerId : playerIds)
            CompatabilityAccess.getPlayer(playerId).teleportTo(destination);
    }
    
    public void teleportPlayerToLobby(UUID playerId)
    {
        PlayerPosition destination;
        
        synchronized(lobbySpawnBusy)
        { destination = lobbySpawn; }
        
        if(destination == null)
        {
            System.out.print("Attempted to teleport a PvpGame player to lobby when no lobby position has been set.");
            return;
        }
        
        CompatabilityAccess.getPlayer(playerId).teleportTo(destination);
    }
    
    public void teleportPlayersTo(PlayerPosition destination)
    {
        Collection<UUID> playerIds;
        
        synchronized(players)
        { playerIds = new ArrayList<UUID>(players.keySet()); }
        
        for(UUID playerId : playerIds)
            CompatabilityAccess.getPlayer(playerId).teleportTo(destination);
    }
    
    public GameStates getPossibleGameStates()
    { return possibleGameStates; }
    
    public GameState getCurrentGameState()
    { return gameState; }
    
    public boolean addPlayer(UUID playerId)
    {
        PlayerPosition destination;
        
        synchronized(lobbySpawnBusy)
        { destination = lobbySpawn; }
        
        PlayerJoinedArgs args = new PlayerJoinedArgs(playerId, destination);
        playerJoined.raise(this, args);
        boolean added;
        
        try
        {
            if(!args.isCancelled())
            {
                added = true;
                synchronized(players)
                { players.put(playerId, PlayerGameState.inLobby); }
                if(args.getTeleportDestination() != null)
                    CompatabilityAccess.getPlayer(playerId).teleportTo(args.getTeleportDestination());
            }
            else
                added = false;
        }
        finally
        { playerJoined.raisePostEvent(this, args); }
        
        return added;
    }
    
    public void removePlayer(UUID playerId)
    { removePlayer(playerId, null); }
    
    public void removePlayer(UUID playerId, PlayerPosition whereToTpThem)
    {
        PlayerLeftArgs args = new PlayerLeftArgs(playerId, whereToTpThem);
        playerLeft.raise(this, args);
        
        try
        {
            if(!args.isCancelled())
            {
                synchronized(players)
                { players.remove(playerId); }
                
                if(args.getTeleportDestination() != null)
                    args.getPlayer().teleportTo(args.getTeleportDestination());
            }
        }
        finally
        { playerLeft.raise(this, args); }
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
        Collection<UUID> playerIds;
        
        synchronized(players)
        { playerIds = new HashSet<UUID>(players.keySet()); }

        GameStartedArgs startedArgs = new GameStartedArgs(playerIds,
                                                          null, // filled in later down.
                                                          possibleGameStates.inGame,
                                                          startMessage,
                                                          gameTimeInMinutes,
                                                          minNumberOfTeams);

        try
        {
            synchronized(teamsBusy)
            {
                teams = getNewTeams(startedArgs.getPlayerIds());
                startedArgs.teamsPlaying = new HashSet<PvpTeam>(teams);
                
                if(teams.size() < minNumberOfTeams)
                {
                    startedArgs.setStartingMessage("Not enough players to start.");
                    abort = true;
                    startedArgs.setCancelled(true);
                }
            }
            
            gameStarted.raise(this, startedArgs);
            
            if(!startedArgs.isCancelled())
            {
                messagePlayers(startedArgs.getStartingMessage());
                
                if(!abort)
                {
                    gameState = startedArgs.getInitialGameState();
                    teleportPlayersToStartingPositions();

                    synchronized(players)
                    {
                        for(UUID playerId : startedArgs.getPlayerIds())
                            players.put(playerId, PlayerGameState.inGame);
                    }

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
        synchronized(players)
        {
            for(UUID playerId : players.keySet())
                CompatabilityAccess.getPlayer(playerId).print(message);
        }
    }
    
    public void messageIngamePlayers(String message)
    {
        synchronized(players)
        {
            for(Entry<UUID, PlayerGameState> player : players.entrySet())
                if(player.getValue() == PlayerGameState.inGame)
                    CompatabilityAccess.getPlayer(player.getKey()).print(message);
        }
    }
    
    public void messageLobbyPlayers(String message)
    {
        synchronized(players)
        {
            for(Entry<UUID, PlayerGameState> player : players.entrySet())
                if(player.getValue() == PlayerGameState.inLobby)
                    CompatabilityAccess.getPlayer(player.getKey()).print(message);
        }
    }
    
    /**
     * Checks whether or not all passed players are on the same team.
     * @param players The players to check.
     * @return True if all players are on the same team. Otherwise, false.
     */
    public boolean playersAreOnSameTeam(UUID... players)
    {
        synchronized(teamsBusy)
        {
            for(PvpTeam team : teams)
            {
                if(team.containsAllPlayers(players))
                    return true;
                
                if(team.containsAnyPlayers(players)) // Operating on the assumption that a player can't be in >1 team.
                    return false;
            }
        }
        
        return false;
    }
    
    public boolean teamIsStillInGame(PvpTeam team)
    {
        synchronized(players)
        {
            for(UUID id : team.getPlayers())
                if(players.get(id) == PlayerGameState.inGame)
                    return true;
        }
        
        return false;
    }
    
    public boolean thereIsMoreThanOneTeamInGame()
    {
        int teamsInGame = 0;
        
        synchronized(teamsBusy)
        {
            for(PvpTeam team : teams)
                if(teamIsStillInGame(team))
                    if(++teamsInGame > 1)
                        return true;
        }
        
        return false;
    }
    
    public void declareLoser(UUID player)
    {
        synchronized(players)
        { players.put(player, PlayerGameState.inLobby); }
        
        teleportPlayerToLobby(player);
        declareRemainingTeamWinnerIfOnlyOneLeft();
    }
    
    public void declareWinner(PvpTeam team)
    {
        PlayerPosition destination;
        
        synchronized(lobbySpawnBusy)
        { destination = lobbySpawn; }
        
        GameFinishedArgs args;
        List<UUID> remainingPlayers;
        String winnerMessage = "Congratulations! You won!";
        
        synchronized(players)
        {
            remainingPlayers = new ArrayList<UUID>();
            
            for(Entry<UUID, PlayerGameState> i : new ArrayList<Entry<UUID, PlayerGameState>>(players.entrySet()))
            {
                if(players.put(i.getKey(), PlayerGameState.inLobby) == PlayerGameState.inGame)
                    remainingPlayers.add(i.getKey());
            }
        }
        
        StringBuilder finishedMessageBuilder = new StringBuilder("Game over! The winners were: ");
        
        for(int i = 0; i < remainingPlayers.size(); i++)
        {
            UUID playerId = remainingPlayers.get(i);
            EnkiPlayer player = CompatabilityAccess.getPlayer(playerId);
            
            if(i == remainingPlayers.size() - 1)
                finishedMessageBuilder.append("and ");
            
            finishedMessageBuilder.append(player.getUsername());
            
            if(i != remainingPlayers.size() - 1)
                finishedMessageBuilder.append(", ");
            else
                finishedMessageBuilder.append(".");
        }
        
        args = new GameFinishedArgs(
                team, remainingPlayers, destination, 5, finishedMessageBuilder.toString(), winnerMessage);
        
        gameFinished.raise(this, args);
        
        try
        {
            if(!args.isCancelled())
            {
                team.messagePlayers(args.getWinnerMessage());
                messagePlayers(args.getGameOverMessage());
                
                synchronized(players)
                {
                    for(UUID i : new ArrayList<UUID>(players.keySet()))
                        players.put(i, PlayerGameState.inLobby);
                }
                
                synchronized(teams)
                { teams.clear(); }
                
                teleportPlayersTo(args.getRespawnLocation());
                gameState = possibleGameStates.waitingForNewGame;
            }
            else
                System.out.print("The PvpGame.gameFinished event was cancelled by a listener.");
        }
        finally
        {
            gameFinished.raisePostEvent(this, args);
            startLobbyCountdown(args.getMinutesUntilNextGame());
        }
    }
    
    public void declareRemainingTeamWinnerIfOnlyOneLeft()
    {
        PvpTeam currentWinningTeam = null;
        
        synchronized(teamsBusy)
        {
            for(PvpTeam team : teams)
                if(teamIsStillInGame(team))
                    if(currentWinningTeam == null)
                        currentWinningTeam = team;
                    else
                        return;
        }
        
        if(currentWinningTeam == null)
            return;
        
        declareWinner(currentWinningTeam);
    }
    
    public abstract void teleportPlayersToStartingPositions();
    
    protected abstract GameStates getNewGameStatesObject();
    
    protected abstract Collection<PvpTeam> getNewTeams(Collection<UUID> players);
}