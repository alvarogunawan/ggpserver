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

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.scrambling.IdentityGameScrambler;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.util.Digester;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;


/**
 * The get###() methods return a new instance of ###, if it is
 * already in the database, else null. <br />
 * 
 * The create###() methods create a new instance of ###, add it to the
 * database and return it. If an object with the same key already exists in the database, a
 * DuplicateInstanceException will be thrown. <br />
 * 
 * @author martin
 * 
 */
public abstract class AbstractDBConnector<TermType extends TermInterface, ReasonerStateInfoType> {
	private static final Logger logger = Logger.getLogger(AbstractDBConnector.class.getName());

	public static final int MATCHID_MAXLENGTH = 40;

//	private final AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory;
	private static final GameScramblerInterface gamescrambler;

	private static DataSource datasource;
	
	private Collection<PlayerStatusListener<TermType, ReasonerStateInfoType>> playerStatusListeners = new ArrayList<PlayerStatusListener<TermType, ReasonerStateInfoType>>();
	
	static {
		gamescrambler = new IdentityGameScrambler();    // we don't do any scrambling (yet)
	}

	protected abstract MoveFactoryInterface<? extends MoveInterface<TermType>> getMoveFactory();

	protected abstract ReasonerInterface<TermType, ReasonerStateInfoType> getReasoner(String gameDescription, String name);

	
	public void clearCache() {
		// this class doesn't cache anything, so there is nothing to do here.
	}
	
	public User createUser(String userName, String password) throws DuplicateInstanceException, SQLException {
		Connection con = getConnection(); 
		PreparedStatement ps = null;
		
		try { 
//			String hashedPass = RealmBase.Digest(password, "SHA-1", null);
			String hashedPass = Digester.digest(password, "SHA-1");
			
			ps = con.prepareStatement("INSERT INTO `users` (`user_name`, `user_pass`) VALUES (?, ?);");
			ps.setString(1, userName);
			ps.setString(2, hashedPass);
			ps.executeUpdate();
			try {ps.close();} catch (SQLException e) {}

			ps = con.prepareStatement("INSERT INTO `user_roles` (`user_name`, `role_name`) VALUES (?, 'member');");
			ps.setString(1, userName);
			ps.executeUpdate();
			
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("java.security.MessageDigest supports SHA-1, so we shouldn't ever get this error!?");
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("String, String - Creating new user: " + userName); //$NON-NLS-1$
		return new User(userName);
	}

	public User getUser(String userName) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		User result = null;

		try { 
			ps = con.prepareStatement("SELECT `user_name` FROM `users` WHERE `user_name` = ?;");
			ps.setString(1, userName);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				logger.info("String - Returning new User: " + userName); //$NON-NLS-1$
				result = new User(userName);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
		return result;
	}
	
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status)
			throws DuplicateInstanceException, SQLException {
		
		assert(!name.equals("Legal"));
		assert(!name.equals("Random"));

		Connection con = getConnection();
		PreparedStatement ps = null;
		try { 

			ps = con.prepareStatement("INSERT INTO `players` (`name` , `host` , `port` , `owner`, `status`) VALUES (?, ?, ?, ?, ?);");
			ps.setString(1, name);
			ps.setString(2, host);
			ps.setInt(3, port);
			ps.setString(4, owner.getUserName());
			ps.setString(5, status);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("String, String, int, User, String - Creating new RemotePlayerInfo: " + name); //$NON-NLS-1$
		RemotePlayerInfo result = new RemotePlayerInfo(name, host, port, owner, status);
		
		for (PlayerStatusListener<TermType, ReasonerStateInfoType> listener : playerStatusListeners) {
			listener.notifyStatusChange(result);
		}
		return result;
	}
	
	public PlayerInfo getPlayerInfo(String name) throws SQLException {
		if (name.equals("Legal")) {
			logger.info("String - Returning new LegalPlayerInfo"); //$NON-NLS-1$
			return new LegalPlayerInfo(-1);
		} else if (name.equals("Random")) {
			logger.info("String - Returning new RandomPlayerInfo"); //$NON-NLS-1$
			return new RandomPlayerInfo(-1);
		}
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		RemotePlayerInfo result = null;
		
		try { 
			
			ps = con.prepareStatement("SELECT `host` , `port` , `owner` , `status` FROM `players` WHERE `name` = ?;");
			ps.setString(1, name);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				logger.info("String - Returning new RemotePlayerInfo: " + name); //$NON-NLS-1$
				result = new RemotePlayerInfo(name, rs.getString("host"), rs.getInt("port"), getUser(rs.getString("owner")), rs.getString("status"));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public Game<TermType, ReasonerStateInfoType> createGame(String gameDescription,
			String name, String stylesheet, boolean enabled) throws DuplicateInstanceException,
			SQLException {
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("INSERT INTO `games` (`name` , `gamedescription` , `stylesheet`, `enabled`) VALUES (?, ?, ?, ?);");
			ps.setString(1, name);
			ps.setString(2, gameDescription);
			ps.setString(3, stylesheet);
			ps.setBoolean(4, enabled);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("String, String - Creating new game: " + name); //$NON-NLS-1$
		return new Game<TermType, ReasonerStateInfoType>(gameDescription, name, getReasoner(gameDescription, name), stylesheet, enabled);
	}

	public Game<TermType, ReasonerStateInfoType> getGame(String name) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		Game<TermType,ReasonerStateInfoType> result = null;
		
		try { 
			ps = con.prepareStatement("SELECT `gamedescription` , `stylesheet`, `enabled` FROM `games` WHERE `name` = ?;");
			ps.setString(1, name);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				String gameDescription = rs.getString("gamedescription");
				String stylesheet = rs.getString("stylesheet");
				boolean enabled = rs.getBoolean("enabled");
				
				logger.info("String - Returning new game: " + name); //$NON-NLS-1$
				result = new Game<TermType, ReasonerStateInfoType>(gameDescription, name, getReasoner(gameDescription, name), stylesheet, enabled);
				
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public Match<TermType, ReasonerStateInfoType> createMatch(String matchID, Game<TermType, ReasonerStateInfoType> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayers,
			Date startTime) throws DuplicateInstanceException,
			SQLException {
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		try {

			ps = con.prepareStatement("INSERT INTO `matches` (`match_id` , `game` , `start_clock` , `play_clock` , `start_time`) VALUES (?, ?, ?, ?, ?);");
			ps.setString(1, matchID);
			ps.setString(2, game.getName());
			ps.setInt(3, startclock);
			ps.setInt(4, playclock);
			ps.setTimestamp(5, new Timestamp(startTime.getTime()));
			
			ps.executeUpdate();
			ps.close();
			
			// players
			ps = con.prepareStatement("INSERT INTO `match_players` (`match_id` , `player` , `roleindex`) VALUES (?, ?, ?);");
			
			ps.setString(1, matchID);
			
			Integer roleindex = 0;
			for (RoleInterface<TermType> role : game.getOrderedRoles()) {
				ps.setString(2, rolesToPlayers.get(role).getName());
				ps.setString(3, roleindex.toString());
		
				ps.executeUpdate();
				roleindex++;
			}
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("String, Game<TermType,ReasonerStateInfoType>, int, int, Map<? extends RoleInterface<TermType>,? extends PlayerInfo>, Date - Creating new match: " + matchID); //$NON-NLS-1$
		return new Match<TermType, ReasonerStateInfoType>(matchID, game, startclock, playclock,
				rolesToPlayers, startTime, getMoveFactory(), gamescrambler, this);
	}
	
	/**
	 * Creates a match with a generated match ID.
	 */
	public Match<TermType, ReasonerStateInfoType> createMatch(Game<TermType, ReasonerStateInfoType> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayers,
			Date startTime) throws SQLException {
		
		long number = System.currentTimeMillis();
		
		Match<TermType, ReasonerStateInfoType> match = null;
		
		while (match == null) {
			String firstPart = /*"Match." +*/ game.getName();
			String secondPart = "." + Long.toString(number);
			if (firstPart.length() + secondPart.length() > MATCHID_MAXLENGTH) {
				firstPart = firstPart.substring(0, MATCHID_MAXLENGTH - secondPart.length() - 2) + "..";
			}
			String matchID = firstPart + secondPart;
			try {
				match = createMatch(matchID, game, startclock, playclock, rolesToPlayers, startTime);
			} catch (DuplicateInstanceException e) {
				number++;
			}
		}
		
		return match;
	}	

	public Match<TermType, ReasonerStateInfoType> getMatch(String matchID)
			throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps_roles = null;
		ResultSet rs = null;

		Match<TermType, ReasonerStateInfoType> result = null;

		try {
			ps = con.prepareStatement("SELECT `game` , `start_clock` , `play_clock` , `start_time` , `status` FROM `matches` WHERE `match_id` = ?;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				Game<TermType, ReasonerStateInfoType> game = getGame(rs.getString("game"));
				String status = rs.getString("status");
				
				ps_roles = con.prepareStatement("SELECT `player` , `roleindex` , `goal_value` FROM `match_players` WHERE `match_id` = ?;");
				ps_roles.setString(1, matchID);

				ResultSet rs_roles = ps_roles.executeQuery();

				Map<RoleInterface<TermType>, PlayerInfo> playerinfos = new HashMap<RoleInterface<TermType>, PlayerInfo>();				
				Map<RoleInterface<TermType>, Integer> goalValues = new HashMap<RoleInterface<TermType>, Integer>();
				
				while (rs_roles.next()) {
					int roleindex = rs_roles.getInt("roleindex");
					RoleInterface<TermType> role = game.getOrderedRoles().get(roleindex);
					PlayerInfo playerInfo = getPlayerInfo(rs_roles.getString("player"));
					playerInfo.setRoleindex(roleindex);
						
					playerinfos.put(role, playerInfo);
					goalValues.put(role, rs_roles.getInt("goal_value"));
				}				

				logger.info("String - Returning new match: " + matchID); //$NON-NLS-1$

				result = new Match<TermType, ReasonerStateInfoType>(matchID, game, rs.getInt("start_clock"), 
						rs.getInt("play_clock"), playerinfos, rs.getTimestamp("start_time"), getMoveFactory(), gamescrambler, this);
				result.setStatus(status);
				if (status.equals(Match.STATUS_FINISHED)) {
					result.setGoalValues(goalValues);
				}
				List<String> states = getStates(matchID);
				result.setXmlStates(states);
				result.setErrorMessages(getErrorMessages(result, states.size()));
				result.setJointMovesStrings(getJointMovesStrings(matchID));				
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (ps_roles != null)
				try {ps_roles.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public int getRowCount(String tableName) throws SQLException {
		Connection con = getConnection();
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = con.createStatement();
			statement.execute("SELECT COUNT( * ) FROM `" + tableName + "`;");
			rs = statement.getResultSet();
			
			
			if (rs.next()) {
				return rs.getInt(1);				
			} 
			throw new SQLException("Something went wrong.");
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (statement != null)
				try {statement.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

	}
	
	public int getRowCountPlayerGameMatches(String playerName, String gameName) throws SQLException {
		if(playerName == null && gameName == null){
			return getRowCount("matches");
		}

		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if(playerName == null){
				// if (gameName == null) {
					// can't happen	
				// } else {
					ps = con.prepareStatement("SELECT COUNT( `match_id` ) FROM `matches` WHERE `game` = ?;");
					ps.setString(1, gameName);
				// }
			} else {
				if (gameName == null) {
					ps = con.prepareStatement("SELECT COUNT( `m`.`match_id` ) "
							+ "FROM `matches` AS `m`, `match_players` AS `p` " 
							+ "WHERE `m`.`match_id` = `p`.`match_id` AND `player` = ? ;");
					ps.setString(1, playerName);
				} else {
					ps = con.prepareStatement("SELECT COUNT( `m`.`match_id` ) "
							+ "FROM `matches` AS `m`, `match_players` AS `p` " 
							+ "WHERE `m`.`match_id` = `p`.`match_id` AND `m`.`game` = ? AND `player` = ? ;");
					ps.setString(1, gameName);
					ps.setString(2, playerName);
				}
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);				
			} 
			throw new SQLException("Something went wrong.");
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		}
	}
	
	
	public List<Game<?, ?>> getGames(int startRow, int numDisplayedRows) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Game<?, ?>> result = new LinkedList<Game<?, ?>>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `games` LIMIT ? , ?;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getGame(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<Game<TermType, ReasonerStateInfoType>> getAllEnabledGames() throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Game<TermType,ReasonerStateInfoType>> result = new LinkedList<Game<TermType,ReasonerStateInfoType>>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `games` WHERE `enabled`=TRUE;");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getGame(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public List<User> getUsers(int startRow, int numDisplayedRows) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<User> result = new LinkedList<User>();
		
		try {
			ps = con.prepareStatement("SELECT `user_name` FROM `users` LIMIT ? , ?;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getUser(rs.getString("user_name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<PlayerInfo> getPlayerInfos(int startRow, int numDisplayedRows) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `players` LIMIT ? , ?;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getPlayerInfo(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<PlayerInfo> getPlayerInfos(String status) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
			// it is important that a new list is returned here,
			// since the result of this method will be shuffled in
			// AbstractRoundRobinScheduler.createPlayerInfos()
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `players` where `status` = ?;");
			ps.setString(1, status);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getPlayerInfo(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<RemotePlayerInfo> getPlayerInfosForUser(String userName) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<RemotePlayerInfo> result = new LinkedList<RemotePlayerInfo>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `players` where `owner` = ?;");
			ps.setString(1, userName);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add((RemotePlayerInfo) getPlayerInfo(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<Match<TermType, ReasonerStateInfoType>> getMatches(int startRow, int numDisplayedRows, String playerName, String gameName) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Match<TermType, ReasonerStateInfoType>> result = new LinkedList<Match<TermType, ReasonerStateInfoType>>();
		
		try {
			if (playerName == null) {
				if (gameName == null ) {
					ps = con.prepareStatement("SELECT `match_id` FROM `matches` ORDER BY `start_time` LIMIT ? , ?;");
					ps.setInt(1, startRow);
					ps.setInt(2, numDisplayedRows);
				} else {
					ps = con.prepareStatement("SELECT `match_id` FROM `matches` WHERE `game` = ? ORDER BY `start_time` LIMIT ? , ?;");
					ps.setString(1, gameName);
					ps.setInt(2, startRow);
					ps.setInt(3, numDisplayedRows);
				}
			} else {
				if (gameName == null) {
					ps = con.prepareStatement("SELECT `m`.`match_id` "
						+ "FROM `matches` AS `m`, `match_players` AS `p` " 
						+ "WHERE `m`.`match_id` = `p`.`match_id` AND `player` = ? " 
						+ "ORDER BY `start_time` LIMIT ? , ?;");
					ps.setString(1, playerName);
					ps.setInt(2, startRow);
					ps.setInt(3, numDisplayedRows);
				} else {
					ps = con.prepareStatement("SELECT `m`.`match_id` "
							+ "FROM `matches` AS `m`, `match_players` AS `p` " 
							+ "WHERE `m`.`match_id` = `p`.`match_id` AND `m`.`game` = ? AND `player` = ? " 
							+ "ORDER BY `start_time` LIMIT ? , ?;");
						ps.setString(1, gameName);
						ps.setString(2, playerName);
						ps.setInt(3, startRow);
						ps.setInt(4, numDisplayedRows);
				}
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getMatch(rs.getString("match_id")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	protected void setMatchStatus(Match<? extends TermType, ReasonerStateInfoType> match, String status) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("UPDATE `ggpserver`.`matches` SET `status` = ? WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setString(1, status);
			ps.setString(2, match.getMatchID());
			ps.executeUpdate();
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}
	
	protected void setMatchGoalValues(Match<? extends TermType, ReasonerStateInfoType> match, Map<? extends RoleInterface<?>, Integer> goalValues) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			
			List<? extends RoleInterface<?>> orderedRoles = match.getGame().getOrderedRoles();
			
			for (int roleIndex = 0; roleIndex < orderedRoles.size(); roleIndex++) {
				ps = con.prepareStatement("UPDATE `ggpserver`.`match_players` SET `goal_value` = ? WHERE `match_players`.`match_id` = ? AND `match_players`.`roleindex` = ? LIMIT 1 ;");
				
				ps.setInt(1, goalValues.get(orderedRoles.get(roleIndex)));
				ps.setString(2, match.getMatchID());
				ps.setInt(3, roleIndex);
				ps.executeUpdate();
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	protected void addErrorMessage(String matchID, int stepNumber, GameControllerErrorMessage errorMessage) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("INSERT INTO `errormessages` (`match_id` , `step_number`, `type`, `message`, `player`) VALUES (?, ?, ?, ?, ?);");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			ps.setString(3, errorMessage.getType());
			ps.setString(4, errorMessage.getMessage());
			ps.setString(5, errorMessage.getPlayerName());
			
			ps.executeUpdate();
//		this shouldn't happen because we use a generated primary key:		
//		} catch (MySQLIntegrityConstraintViolationException e) {
//			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
//			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	protected void addJointMove(String matchID, int stepNumber, JointMoveInterface<? extends TermInterface> jointMove) throws SQLException, DuplicateInstanceException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {

			int roleindex = 0;
			for (MoveInterface<? extends TermInterface> move : jointMove.getOrderedMoves()) {
				ps = con.prepareStatement("INSERT INTO `moves` (`match_id` , `step_number`, `roleindex`, `move`) VALUES (?, ?, ?, ?);");
				ps.setString(1, matchID);
				ps.setInt(2, stepNumber);
				ps.setInt(3, roleindex);
				ps.setString(4, move.getTerm().getKIFForm());
				
				ps.executeUpdate();
				roleindex++;
			}
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	protected void addState(String matchID, int stepNumber, String xmlState) throws SQLException, DuplicateInstanceException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		
		try {
			ps = con.prepareStatement("INSERT INTO `states` (`match_id` , `step_number`, `state`) VALUES (?, ?, ?);");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			ps.setString(3, xmlState);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}
	
	/**
	 * Returns a list of lists of error messages for each step number. The first
	 * element of result is the list of error messages for step number 1, and so
	 * on. Unfortunately, step number counting starts with 1, so to get the
	 * error messages for step number i, use result.get(i - 1).
	 */
	private List<List<GameControllerErrorMessage>> getErrorMessages(Match<TermType, ReasonerStateInfoType> match, int numberOfStates) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<List<GameControllerErrorMessage>> result = new ArrayList<List<GameControllerErrorMessage>>(numberOfStates);
		
		for (int i = 0; i < numberOfStates; i++) {
			result.add(new LinkedList<GameControllerErrorMessage>());
		}
		
		try {
			ps = con.prepareStatement("SELECT `step_number`, `type`, `message`, `player` FROM `errormessages` where `match_id` = ? ORDER BY `step_number`;");
			ps.setString(1, match.getMatchID());
			rs = ps.executeQuery();
			
			while (rs.next()) {
				int errorMsgStepNumber = rs.getInt("step_number");   // step number counting starts with 1 
				if (errorMsgStepNumber > numberOfStates) {
					String message = "errorMsgStepNumber bigger than numberOfStates! Causing match id: " + match.getMatchID();
					logger.severe(message);        //$NON-NLS-1$s
					throw new InternalError(message);					
				}
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(rs.getString("type"), rs.getString("message"), match, rs.getString("player"));
				result.get(errorMsgStepNumber - 1).add(errorMessage);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	private List<List<String>> getJointMovesStrings(String matchID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<List<String>> result = new LinkedList<List<String>>();
		
		try {
			ps = con.prepareStatement("SELECT `step_number`, `roleindex`, `move` FROM `moves` where `match_id` = ? ORDER BY `step_number`, `roleindex`;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			List<String> jointMove = null;
			int stepNumber = 0;
			while (rs.next()) {
				int roleIndex = rs.getInt("roleindex");
				if (roleIndex == 0) {
					stepNumber++;
					if (jointMove != null) {
						result.add(jointMove);
					}
					jointMove = new LinkedList<String>();
				}
				assert(jointMove != null);
				jointMove.add(rs.getString("move"));

				assert(rs.getInt("step_number") == stepNumber);
				assert(roleIndex == jointMove.size() - 1);
			}
			if (jointMove != null) {
				result.add(jointMove);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	private List<String> getStates(String matchID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<String> result = new LinkedList<String>();

		try {
			ps = con.prepareStatement("SELECT `step_number`, `state` FROM `states` where `match_id` = ? ORDER BY `step_number`;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			int stepNumber = 1;
			while (rs.next()) {
				assert(rs.getInt("step_number") == stepNumber);
				result.add(rs.getString("state"));
				stepNumber++;
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public void updatePlayerInfo(String playerName, String host, int port, User user, String status) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try { 
			ps = con.prepareStatement("UPDATE `ggpserver`.`players` "
							+ "SET `host` = ?, `port` = ?, `owner` = ?, `status` = ? "
							+ "WHERE `name` = ? LIMIT 1 ;");
			ps.setString(1, host);
			ps.setInt(2, port);
			ps.setString(3, user.getUserName());
			ps.setString(4, status);
			ps.setString(5, playerName);
			ps.executeUpdate();
			
			RemotePlayerInfo playerInfo = (RemotePlayerInfo) getPlayerInfo(playerName);
			for (PlayerStatusListener<TermType, ReasonerStateInfoType> listener : playerStatusListeners) {
				listener.notifyStatusChange(playerInfo);
			}			
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	public void updateGameInfo(String gameName, String gameDescription, String stylesheet, boolean enabled) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try { 
			ps = con.prepareStatement("UPDATE `ggpserver`.`games` "
							+ "SET `gamedescription` = ?, `stylesheet` = ?, `enabled` = ? "
							+ "WHERE `name` = ? LIMIT 1 ;");
			ps.setString(1, gameDescription);
			ps.setString(2, stylesheet);
			ps.setBoolean(3, enabled);
			ps.setString(4, gameName);
			ps.executeUpdate(); 
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}
	
	public void addPlayerStatusListener(PlayerStatusListener<TermType, ReasonerStateInfoType> listener) {
		playerStatusListeners.add(listener);
	}
	
	private static Connection getConnection() throws SQLException {
		if (datasource == null) {
			try {
				datasource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver");
			} catch (NamingException e) {
				logger.severe("exception: " + e); //$NON-NLS-1$
				throw new InternalError("Could not lookup datasource!");
			} 
		}
		return datasource.getConnection();
	}
}
