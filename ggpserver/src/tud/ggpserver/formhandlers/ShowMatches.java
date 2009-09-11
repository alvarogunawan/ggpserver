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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class ShowMatches extends AbstractPager {
	private String playerName = null;
	private String gameName = null;
	private String tournamentID = null;

	private List<? extends ServerMatch<?,?>> matches = null;

	@SuppressWarnings("unchecked")
	protected final AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	
	private int rowCountMatches = -1;
	private int startRow = -1;
	

	@Override
	public int getStartRow() {
		if (startRow == -1) {
			try {
				// startRow hasn't been set --> display last page
				startRow = calcStartRowFromPage(getNumberOfPages());
			} catch (SQLException e) {
				startRow = 0;
			}
		}
		return startRow ;
	}
	
	@Override
	protected void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends ServerMatch<?, ?>> getMatches() throws SQLException {
		if (matches == null) {
			matches = db.getMatches(getStartRow(), getNumDisplayedRows(), playerName, gameName, tournamentID, excludeNewMatches());
		}
		return matches;
	}

	protected boolean excludeNewMatches() {
		return true;
	}
	
	@Override
	protected int getRowCount() throws SQLException {
		if (rowCountMatches == -1) {
			rowCountMatches = db.getRowCountMatches(playerName, gameName, tournamentID, excludeNewMatches());
		}
		return rowCountMatches;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	
	public String getTournamentID() {
		return tournamentID;
	}

	@SuppressWarnings("unused")
	public void setTournamentID(String tournamentID) throws SQLException {
		this.tournamentID = tournamentID;
	}

	@Override
	public String getTableName() {
		return "matches";
	}

	@Override
	public String getTargetJsp() {
		String result = "show_matches.jsp";
		List<String> parameters = new ArrayList<String>(3);
		
		if (playerName != null) {
			parameters.add("playerName=" + playerName);
		}
		if (gameName != null) {
			parameters.add("gameName=" + gameName);
		}
		if (tournamentID != null) {
			parameters.add("tournamentID=" + tournamentID);
		}
		
		if (!parameters.isEmpty()) {
			result += "?";
		}
		
		Iterator<String> it = parameters.iterator();
		while (it.hasNext()) {
			result += it.next();
			if (it.hasNext()) {
				result += "&";
			}
		}
		
		return result;
	}
}
