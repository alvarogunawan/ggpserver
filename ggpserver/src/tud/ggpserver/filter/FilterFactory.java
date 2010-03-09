/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

package tud.ggpserver.filter;

import java.util.logging.Logger;

import tud.ggpserver.filter.FilterNode.FilterType;
import tud.ggpserver.filter.rules.GameFilterRule;
import tud.ggpserver.filter.rules.PlayClockFilterRule;
import tud.ggpserver.filter.rules.PlayerFilterRule;
import tud.ggpserver.filter.rules.RoleNumberFilterRule;
import tud.ggpserver.filter.rules.StartClockFilterRule;
import tud.ggpserver.filter.rules.StartTimeFilterRule;
import tud.ggpserver.filter.rules.StatusFilterRule;
import tud.ggpserver.filter.rules.TournamentFilterRule;
import tud.ggpserver.util.IdPool;

public class FilterFactory {

	private static final Logger logger = Logger.getLogger(FilterFactory.class.getName());

	public static FilterNode createFilterNode(FilterType type, IdPool<FilterNode> ids, Filter filter) {
		FilterNode node = null;
		switch(type) {
		case And:
			node = new FilterANDOperation(ids, filter);
			break;
		case Or:
			node = new FilterOROperation(ids, filter);
			break;
		case Game:
			node = new GameFilterRule(ids, filter);
			break;
		case RoleNumber:
			node = new RoleNumberFilterRule(ids, filter);
			break;
		case PlayClock:
			node = new PlayClockFilterRule(ids, filter);
			break;
		case Player:
			node = new PlayerFilterRule(ids, filter);
			break;
		case StartClock:
			node = new StartClockFilterRule(ids, filter);
			break;
		case StartTime:
			node = new StartTimeFilterRule(ids, filter);
			break;
		case Status:
			node = new StatusFilterRule(ids, filter);
			break;
		case Tournament:
			node = new TournamentFilterRule(ids, filter);
			break;
		default:
			logger.severe("unknown type:"+type);
		}
		return node;
	}
}
