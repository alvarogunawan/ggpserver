/*
    Copyright (C) 2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>
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

package tud.ggpserver.filter.rules;

import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.FilterNode;
import tud.ggpserver.filter.matcher.Matcher;
import tud.ggpserver.filter.matcher.StringMatcher;
import tud.ggpserver.util.IdPool;

public abstract class StringMatchFilterRule extends MatchFilterRule<String>{
	
	public StringMatchFilterRule(IdPool<FilterNode> ids, FilterType type, Filter filter) {
		super(ids, type, filter);
	}
	
	@Override
	public Matcher<String> createMatcher() {
		return new StringMatcher(String.valueOf(getID()));
	}

	public boolean patternMatches(String s) {
		return ((StringMatcher)matcher).patternMatches(s);
	}

}
