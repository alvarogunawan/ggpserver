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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cs227b.teamIago.util.GameState;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.scheduler.JavaProverTournamentScheduler;

public class EditTournament extends ShowMatches {
	public static final String ADD_MATCH = "add_match";
	public static final String START_MATCH = "start_match";
	public static final String ABORT_MATCH = "abort_match";
	public static final String DELETE_MATCH = "delete_match";
	public static final String CLONE_MATCH = "clone_match";
	
	private Tournament<?, ?> tournament;
	private String action;
	private ServerMatch<?, ?> match;
	private boolean correctlyPerformed = false;

	@SuppressWarnings("unchecked")
	public List<Game<?, ?>> getGames() throws SQLException {
		return db.getAllEnabledGames();
	}

	public void setMatchID(String matchID) throws SQLException {
		this.match = db.getMatch(matchID);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Tournament<?, ?> getTournament() {
		return tournament;
	}

	@Override
	public void setTournamentID(String tournamentID) throws SQLException {
		tournament = db.getTournament(tournamentID);
		super.setTournamentID(tournamentID);
	}

	@Override
	public String getTargetJsp() {
		// tournament could be null if it was not correctly set. This should
		// only happen if the user fiddled manually with the GET parameters, so
		// he deserves the NullPointerException that he or she will get.
		return "edit_tournament.jsp?tournamentID=" + tournament.getTournamentID();
	}

	@Override
	protected boolean excludeNewMatches() {
		return false;
	}
	
	public boolean isValid() {
		if (action.equals(ADD_MATCH)) {
			return true;
		} else if (action.equals(START_MATCH) && match != null && match instanceof NewMatch) {
			return true;
		} else if (action.equals(ABORT_MATCH) && match != null && match instanceof RunningMatch) {
			return true;
		} else if (action.equals(DELETE_MATCH) && match != null) {
			return true;
		} else if (action.equals(CLONE_MATCH) && match != null) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void performAction() throws SQLException {
		if (!isValid()) {
			throw new IllegalStateException("performAction() called without checking isValid() first!");
		}

		if (action.equals(ADD_MATCH)) {
			addMatch();
		} else if (action.equals(START_MATCH)) {
			assert (match instanceof NewMatch);       // checked in isValid()
			startMatch((NewMatch) match);
		} else if (action.equals(ABORT_MATCH)) {
			assert (match instanceof RunningMatch);   // checked in isValid()
			abortMatch((RunningMatch) match);
		} else if (action.equals(DELETE_MATCH)) {
			deleteMatch(match);
		} else {
			assert (action.equals(CLONE_MATCH));
			cloneMatch(match);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addMatch() throws SQLException {
		List<Game<?, ?>> allEnabledGames = db.getAllEnabledGames();
		Game<?, ?> game = allEnabledGames.get(0);
		Map<RoleInterface, PlayerInfo> rolesToPlayerInfos = new HashMap<RoleInterface, PlayerInfo>();
		
		int roleIndex = 0;
		for (RoleInterface<?> role : game.getOrderedRoles()) {
			rolesToPlayerInfos.put(role, new RandomPlayerInfo(roleIndex));
			roleIndex++;
		}
		
		db.createMatch(game, Tournament.DEFAULT_STARTCLOCK,
				Tournament.DEFAULT_PLAYCLOCK, rolesToPlayerInfos, tournament);

		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void startMatch(NewMatch match) {
		JavaProverTournamentScheduler.getInstance(tournament).start(match);
		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void abortMatch(RunningMatch match) {
		JavaProverTournamentScheduler.getInstance(tournament).abort(match);
		correctlyPerformed = true;
	}

	private void deleteMatch(ServerMatch match) throws SQLException {
		JavaProverTournamentScheduler scheduler = JavaProverTournamentScheduler.getInstance(tournament);
		if (scheduler.isRunning(match)) {
			assert (match instanceof RunningMatch);
			try {
				scheduler.abort((RunningMatch<Term, GameState>) match);
			} catch (IllegalStateException e) {
				// Match wasn't running any more, ignore
			}
		}
		db.deleteMatch(match.getMatchID());
		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void cloneMatch(ServerMatch match) throws SQLException {
		db.createMatch(match.getGame(), match.getStartclock(), match.getPlayclock(), match.getRolesToPlayerInfos(), tournament);
		correctlyPerformed = true;
	}

	public boolean isCorrectlyPerformed() {
		return correctlyPerformed;
	}
	
	@SuppressWarnings("unchecked")
	public List<PlayerInfo> getEnabledPlayerInfos() throws SQLException {
		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		result.add(new RandomPlayerInfo(-1));
		result.add(new LegalPlayerInfo(-1));
		result.addAll(db.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE));

		return result;
	}
}