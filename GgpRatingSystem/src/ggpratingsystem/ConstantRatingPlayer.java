/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

package ggpratingsystem;

import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingFactory;
import ggpratingsystem.ratingsystems.RatingSystemType;

public class ConstantRatingPlayer extends Player {

	protected ConstantRatingPlayer(String name) {
		super(name);
	}

	@Override
	public Rating getRating(RatingSystemType type) {
		return RatingFactory.makeRating(type, this);
	}
	

}