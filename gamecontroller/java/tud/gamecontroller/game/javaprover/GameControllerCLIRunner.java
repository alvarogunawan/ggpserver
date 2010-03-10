/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.game.javaprover;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.cli.AbstractGameControllerCLIRunner;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;
import cs227b.teamIago.util.GameState;

public class GameControllerCLIRunner
		extends AbstractGameControllerCLIRunner<
			Term,
			GameState
		> {

	public GameControllerCLIRunner(ReasonerFactory<Term, GameState> reasonerFactory, GDLVersion gdlVersion) {
		super(reasonerFactory, gdlVersion);
	}

	public static void main(String[] args) {
		ReasonerFactory<Term, GameState> reasonerFactory = new ReasonerFactory<Term, GameState>() {
			public ReasonerInterface<Term, GameState> createReasoner(String gameDescription, String gameName) {
				return new Reasoner(gameDescription);
			}
		};
		
		GameControllerCLIRunner gcRunner=new GameControllerCLIRunner(reasonerFactory, GDLVersion.v1);
		gcRunner.runFromCommandLine(args);
	}

	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory());
	}
}
