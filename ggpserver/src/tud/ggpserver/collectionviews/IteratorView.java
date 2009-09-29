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

package tud.ggpserver.collectionviews;

import java.util.Iterator;

public class IteratorView<T, T0> implements Iterator<T> {

	protected Iterator<T0> iterator;
	protected Mapping<T0, T> mapping;

	public IteratorView(Iterator<T0> iterator, Mapping<T0, T> mapping) {
		this.iterator = iterator;
		this.mapping = mapping;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return mapping.map(iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}
