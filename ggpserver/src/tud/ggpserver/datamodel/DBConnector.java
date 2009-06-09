/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.datamodel;

import static org.apache.commons.collections.map.AbstractReferenceMap.SOFT;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.game.javaprover.TermFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import cs227b.teamIago.util.GameState;

/**
 * To be threadsafe, all write access to the following things must be synchronized:
 *    playerInfos 
 *    matches
 *    games 
 *    users 
 *    instance
 * 
 * It is not enough to use synchronized collections because of scenarios like the one 
 * described in updatePlayerInfo(). 
 * 
 * Note: the necessity to synchronize everything means that ONLY A SINGLE THREAD per type
 * of information (e.g., matches) can access the database. For the more complex objects
 * like matches and games, this is paid off by the time (and memory!) it takes to create 
 * one of these. Users and players might be better off without caching, although these
 * don't account for the bulk of the database activity anyway. 
 * 
 * @author Martin Günther <mintar@gmx.de>
 *
 */
public class DBConnector extends AbstractDBConnector<Term, GameState> {
	private static DBConnector instance;
	
	@SuppressWarnings("unchecked")
	private Map<String, Game<Term, GameState>> games = new ReferenceMap(SOFT, SOFT, false);
	
	@SuppressWarnings("unchecked")
	private Map<String, ServerMatch<Term, GameState>> matches= new ReferenceMap(SOFT, SOFT, false);
	
	@SuppressWarnings("unchecked")
	private Map<String, PlayerInfo> playerInfos = new ReferenceMap(SOFT, SOFT, false);

	@SuppressWarnings("unchecked")
	private Map<String, User> users = new ReferenceMap(SOFT, SOFT, false);

	private DBConnector(ReasonerFactory<Term, GameState> reasonerFactory) {
		super(reasonerFactory);
	}


	/**
	 * Will be synchronized on DBConnector.class .
	 */
	public static synchronized DBConnector getInstance() {
		ReasonerFactory<Term, GameState> reasonerFactory = new ReasonerFactory<Term, GameState>() {
			public ReasonerInterface<Term, GameState> createReasoner(String gameDescription, String gameName) {
				return new Reasoner(gameDescription);
			}
		};
		
		if (instance == null) {
			instance = new DBConnector(reasonerFactory);
		}
		return instance;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clearCache() {
		synchronized (games) {
			games.clear();
		}
		synchronized (playerInfos) {
			playerInfos.clear();
		}
		synchronized (matches) {
			matches.clear();
		}
		synchronized (users) {
			users.clear();
		}
		
		// Run the garbage collection. The reason for doing this is that we 
		// can generate a heap dump afterwards that doesn't contain many stale
		// objects.
		System.gc();
	}
	
	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory());
	}

	/////////////////// GAME ///////////////////
	@Override
	public Game<Term, GameState> createGame(String gameDescription, String name, String stylesheet, boolean enabled)
			throws DuplicateInstanceException, SQLException {
		synchronized (games) {
			Game<Term, GameState> result = super.createGame(gameDescription, name, stylesheet, enabled);
			games.put(name, result);
			return result;
		}
	}

	@Override
	public Game<Term, GameState> getGame(String name) throws SQLException {
		Game<Term, GameState> result = games.get(name);
		if (result == null) {
			synchronized (games) {
				result = super.getGame(name);
	
				if (result != null) {
					games.put(name, result);
				}
			}
		}
		return result;
	}

	@Override
	public void updateGameInfo(String gameName, String gameDescription,
			String stylesheet, boolean enabled) throws SQLException {
		synchronized (games) {
			// this has to be synchronized to ensure that no stale result gets re-introduced into the 
			// cache after clearing it
			clearCacheForGame(gameName);
			super.updateGameInfo(gameName, gameDescription, stylesheet, enabled);
		}
	}

	private void clearCacheForGame(String gameName) throws SQLException {
		// this has to be done first, or otherwise this call to getGame() will be cached
		Game game = getGame(gameName);
		
		synchronized (games) {
			// delete cached result, so it will be read again on next request
			games.remove(gameName);
		}
		
		// also remove all cached matches of this game (to prevent the stale-stylesheet-bug).
		synchronized (matches) {
			for (ServerMatch match : matches.values()) {
				if (match.getGame().equals(game)) {
					matches.remove(match.getMatchID());
				}
			}
		}
	}


	/////////////////// MATCH ///////////////////
	@Override
	public NewMatch<Term, GameState> createMatch(
			String matchID,
			GameInterface<Term, State<Term, GameState>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<Term>, ? extends PlayerInfo> rolesToPlayerInfos,
			Tournament<Term, GameState> tournament,
			Date startTime) throws DuplicateInstanceException,
			SQLException {
		synchronized (matches) {
			NewMatch<Term, GameState> result = super.createMatch(matchID, game, startclock, playclock, rolesToPlayerInfos, tournament, startTime);
			matches.put(matchID, result);
			return result;
		}
	}

	@Override
	public ServerMatch<Term, GameState> getMatch(String matchID)
			throws SQLException {
		ServerMatch<Term, GameState> result = matches.get(matchID);
		if (result == null) {
			synchronized (matches) {
				result = super.getMatch(matchID);
				
				if (result != null) {
					matches.put(matchID, result);
				}
			}
		}
		return result;
	}
	
	@Override
	public void deleteMatch(String matchID) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.deleteMatch(matchID);
		}
	}
	
	@Override
	public void setMatchStatus(String matchID, String status) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchStatus(matchID, status);
		}
	}


	@Override
	public void setMatchPlayclock(String matchID, int playclock) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchPlayclock(matchID, playclock);
		}
	}


	@Override
	public void setMatchPlayerInfo(String matchID, int roleNumber, PlayerInfo playerInfo) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchPlayerInfo(matchID, roleNumber, playerInfo);
		}
	}


	@Override
	public void setMatchStartclock(String matchID, int startclock) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchStartclock(matchID, startclock);
		}
	}

	
	@Override
	public void setMatchGame(NewMatch<Term, GameState> match, GameInterface<Term, State<Term, GameState>> newGame) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(match.getMatchID());
			super.setMatchGame(match, newGame);
		}
	}


	@Override
	public void setMatchGoalValues(ServerMatch<Term, GameState> match, Map<? extends RoleInterface<?>, Integer> goalValues) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(match.getMatchID());
			super.setMatchGoalValues(match, goalValues);
		}
	}


	private void clearCacheForMatch(String matchID) {
		synchronized (matches) {
			matches.remove(matchID);
		}
	}


	/////////////////// PLAYERINFO ///////////////////
	@Override
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status)
			throws DuplicateInstanceException, SQLException {
		synchronized (playerInfos) {
			RemotePlayerInfo result = super.createPlayerInfo(name, host, port, owner, status);
			playerInfos.put(name, result);
			return result;
		}
	}

	@Override
	public PlayerInfo getPlayerInfo(String name) throws SQLException {
		PlayerInfo result = playerInfos.get(name);
		if (result == null) {
			synchronized (playerInfos) {
				result = super.getPlayerInfo(name);
	
				if (result != null) {
					playerInfos.put(name, result);
				}
			}
		}
		return result;
	}

	/**
	 * This method must be synchronized with getPlayerInfo(). Otherwise, the following could happen:
	 * - Thread 1: updatePlayerInfo()  [changes player status to "active"]
	 * - Thread 1: clears player from cache
	 * - Thread 2: getPlayerInfo(), re-adds player to cache
	 * - Thread 1: super.updatePlayerInfo() calls getPlayerInfo(), passes stale result 
	 *   (status == "inactive") to notifyPlayerStatusChange(), which will think that 
	 *   the state was changed to "inactive" instead of "active".   
	 */
	@Override
	public void updatePlayerInfo(String playerName, String host, int port,
			User user, String status) throws SQLException {
		synchronized (playerInfos) {
			clearCacheForPlayer(playerName);		
			super.updatePlayerInfo(playerName, host, port, user, status);
		}
	}

	private void clearCacheForPlayer(String playerName) throws SQLException {
		// this has to be done first, or otherwise this call to getPlayerInfo() will be cached
		PlayerInfo player = getPlayerInfo(playerName);
		
		// delete cached result, so it will be read again on next request
		synchronized (playerInfos) {
			playerInfos.remove(playerName);
		}
		
		// remove all cached matches of this player. 
		synchronized (matches) {
			for (ServerMatch match : new LinkedList<ServerMatch<Term, GameState>>(matches.values())) {
					// new LinkedList(...) necessary to avoid concurrent iteration and modification
				if (match.getPlayerInfos().contains(player)) {
					clearCacheForMatch(match.getMatchID());
				}
			}
		}
	}

	/////////////////// USER ///////////////////
	@Override
	public User createUser(String userName, String password)
			throws DuplicateInstanceException, SQLException {
		synchronized (users) {
			User result = super.createUser(userName, password);
			users.put(userName, result);
			return result;
		}
	}

	@Override
	public User getUser(String userName) throws SQLException {
		User result = users.get(userName);
		if (result == null) {
			synchronized (users) {
				result = super.getUser(userName);
				
				if (result != null) {
					users.put(userName, result);
				}
			}
		}
		return result;
	}
}
