/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
                  2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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
import java.util.List;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnectorFactory;

public class ShowPlayers extends AbstractPager {
	private List<? extends PlayerInfo> playerInfos = null;

	private List<? extends PlayerInfo> getAllPlayers() throws SQLException {
		if (playerInfos == null) {
			playerInfos = DBConnectorFactory.getDBConnector().getPlayerInfos();
		}
		return playerInfos;
	}
	
	public List<? extends PlayerInfo> getPlayers() throws SQLException {
		return getAllPlayers().subList(getStartRow(), Math.min(getStartRow() + getNumDisplayedRows() - 1, getAllPlayers().size() -  1));
	}

	@Override
	public String getTableName() {
		return "players";
	}

	@Override
	public String getTargetJsp() {
		return "show_players.jsp";
	}

	@Override
	public String getTitleOfPage(int pageNumber) throws SQLException {
		int firstIndex = (pageNumber - 1)*getNumDisplayedRows();
		if (firstIndex>=0 && firstIndex < getAllPlayers().size())
			return getAllPlayers().get(firstIndex).getName() + ", ...";
		else
			return null;
	}
}
