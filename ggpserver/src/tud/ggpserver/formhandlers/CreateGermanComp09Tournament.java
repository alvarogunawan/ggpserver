/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de> 

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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.DuplicateInstanceException;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.Tournament;

public class CreateGermanComp09Tournament {
	private String tournamentID = "";
	private String userName = "";
	private List<String> errors = new LinkedList<String>();
	private PlayerInfo[] playerInfos = new PlayerInfo[7];
	private String[] playerNames = null;
	
	private boolean correctlyCreated = false;
	
	@SuppressWarnings("unchecked")
	private AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	private boolean finalRound;

	private static final Logger logger = Logger.getLogger(CreateGermanComp09Tournament.class.getName());
	
	public boolean isValid() throws SQLException {
		errors.clear();
		
		if (tournamentID.equals("")) {
			errors.add("tournament ID must not be empty");
		}
		if (tournamentID.length() > 40) {
			errors.add("tournament ID must not be longer than 40 characters");
		}
		if (!tournamentID.matches("[a-zA-Z0-9._-]*")) {
			errors.add("tournament ID must only contain the following characters: a-z A-Z 0-9 . _ -");
		} else if (DBConnectorFactory.getDBConnector().getTournament(tournamentID) != null) {
			// this is an "else if" such that only valid user names are checked to prevent SQL injection
			errors.add("tournament ID already exists, please pick a different one");
		}
		if(playerNames == null || (new HashSet<String>(Arrays.asList(playerNames))).size() != playerInfos.length) {
			errors.add("you have to specify " + playerInfos.length + " different players");
		}
		
		if (errors.size() > 0) {
			tournamentID = "";
			return false;
		}

		
		return true;
	}
	
	public void createTournament() throws SQLException {
		try {
			DBConnectorFactory.getDBConnector().createTournament(tournamentID, DBConnectorFactory.getDBConnector().getUser(userName));
			correctlyCreated = true;
			setPlayerInfos();
			createMatches();
		} catch (DuplicateInstanceException e) {
			errors.add("tournament ID already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	private void createMatches() throws SQLException {
		
		int[][] twoPlayerMatches1 =
		{{1, 4}, {2, 5}, {3, 6},
 		 {1, 5}, {2, 4}, {3, 7},
 		 {6, 1}, {7, 2}, {4, 3},
 		 {7, 1}, {6, 2}, {5, 3},
 		 {4, 6}, {5, 7}};
		int[][] twoPlayerMatches2 =
	 		{{2, 1},
	 		{1, 3}, {5, 4}, {7, 6},
	 		{3, 2}, {4, 7}, {6, 5}};
	
		int[][] multiPlayerMatches =
			{{1,3,5,7}, {2,4,6}}; 

		Calendar calendar = Calendar.getInstance();
		if(finalRound){
			createSinglePlayerMatches(db.getGame("maze"), 30, 14, calendar);
			createMultiPlayerMatches(twoPlayerMatches1, db.getGame("tictactoe"), 31, 15, 0.25, calendar);
			createMultiPlayerMatches(twoPlayerMatches2, db.getGame("blocker"), 32, 16, 0.5, calendar);
			createMultiPlayerMatches(multiPlayerMatches, db.getGame("blobwars"), 33, 17, 1.0, calendar);
		}else{
			createSinglePlayerMatches(db.getGame("maze"), 30, 14, calendar);
			createMultiPlayerMatches(twoPlayerMatches1, db.getGame("tictactoe"), 31, 15, 0.25, calendar);
			createMultiPlayerMatches(twoPlayerMatches2, db.getGame("blocker"), 32, 16, 0.5, calendar);
			createMultiPlayerMatches(multiPlayerMatches, db.getGame("blobwars"), 33, 17, 1.0, calendar);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createSinglePlayerMatches(Game<?,?> game, int startclock, int playclock, Calendar calendar) throws SQLException {
		if(game == null){
			logger.severe("game is null");
			return;
		}
		Tournament<?, ?> tournament = db.getTournament(getTournamentID());
		Map<RoleInterface, PlayerInfo> rolesToPlayerInfos;
		for(PlayerInfo p:playerInfos) {
			rolesToPlayerInfos = new HashMap<RoleInterface, PlayerInfo>();
			rolesToPlayerInfos.put(game.getRole(0), p);
			db.createMatch(game, startclock, playclock, rolesToPlayerInfos, tournament, calendar.getTime(), true, 1.0);
			calendar.add(Calendar.SECOND, 1);
		}
	}

	@SuppressWarnings("unchecked")
	private void createMultiPlayerMatches(int [][] matches, Game<?,?> game, int startclock, int playclock, double weight, Calendar calendar) throws SQLException {
		if(game == null){
			logger.severe("game is null");
			return;
		}
		Tournament<?, ?> tournament = db.getTournament(getTournamentID());
		Map<RoleInterface, PlayerInfo> rolesToPlayerInfos;
		for(int[] match:matches){
			rolesToPlayerInfos = new HashMap<RoleInterface, PlayerInfo>();
			int i;
			for(i=0; i<match.length; i++){
				rolesToPlayerInfos.put(game.getRole(i), playerInfos[match[i]-1]);
			}
			// fill up with random players
			for(; i<game.getNumberOfRoles(); i++) {
				rolesToPlayerInfos.put(game.getRole(i), db.getPlayerInfo(AbstractDBConnector.PLAYER_RANDOM));
			}
			db.createMatch(game, startclock, playclock, rolesToPlayerInfos, tournament, calendar.getTime(), true, weight);
			calendar.add(Calendar.SECOND, 1);
		}
	}

	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public List<String> getErrors() {
		return errors;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPlayers(String[] playerNames) {
		this.playerNames = playerNames;
		logger.info("setting players:"+Arrays.asList(playerNames));
	}

	public void setFinalRound(String finalRound) {
		this.finalRound = finalRound.equals("checked");
		logger.info("setting finalRound:" + finalRound);
	}

	private void setPlayerInfos() throws SQLException {
		for(int i=0; i<playerInfos.length; i++){
			playerInfos[i] = db.getPlayerInfo(playerNames[i]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<PlayerInfo> getPlayerInfos() throws SQLException {
		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		result.add(new RandomPlayerInfo(-1));
		result.add(new LegalPlayerInfo(-1));
		result.addAll(db.getPlayerInfos());

		return result;
	}
}
