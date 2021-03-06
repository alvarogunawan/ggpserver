/*
    Copyright (C) 2009-2010 Martin Günther <mintar@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com> 
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

package tud.ggpserver.datamodel.matches;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GameControllerListener;
import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DuplicateInstanceException;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.dblists.DynamicDBBackedList;
import tud.ggpserver.datamodel.dblists.ErrorMessageAccessor;
import tud.ggpserver.datamodel.dblists.JointMovesAccessor;
import tud.ggpserver.datamodel.dblists.StringStateAccessor;

	/**
	 * It's possible to have several RunningMatch instances representing 
	 * the same match: We can't assume that the first instance is cached.
	 * Only one of them will actually be running the match, but the others
	 * can be used for display.   
	 */
public class RunningMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends StartedMatch<TermType, ReasonerStateInfoType>
		implements
		RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>>,
		GameControllerListener {

	private static final Logger logger = Logger.getLogger(RunningMatch.class.getName());

	private GameScramblerInterface gameScrambler;

	private Map<RoleInterface<TermType>, Player<TermType, State<TermType, ReasonerStateInfoType>>> players;
	private List<Player<TermType, State<TermType, ReasonerStateInfoType>>> orderedPlayers = null;

	/**
	 * stepNumber is always the same as errorMessages.size().
	 * 
	 *  before the game starts: stepNumber == 0
	 *  after START:            stepNumber == 1
	 *  after first PLAY:       stepNumber == 2
	 *  ...
	 *  after n^th PLAY:        stepNumber == n + 1
	 *  after STOP:             stepNumber == n + 1 (intentionally!)
	 *  
	 */
	private int stepNumber = 0;
	
	public RunningMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			String tournamentID,
			double weight,
			User owner, AbstractDBConnector<TermType, ReasonerStateInfoType> db,
			GameScramblerInterface gameScrambler) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, db);
		this.gameScrambler = gameScrambler;
	}
	
	/**
	 * Sets this matches status to "aborted", and returns the new aborted match.
	 * This object must not be used any more after calling this method.
	 */
	public AbortedMatch<TermType, ReasonerStateInfoType> toAborted() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_ABORTED);
		return getDB().getAbortedMatch(getMatchID());
	}
	
	/**
	 * Sets this matches status to "finished", and returns the new finished match.
	 * This object must not be used any more after calling this method.
	 */
	public FinishedMatch<TermType, ReasonerStateInfoType> toFinished() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_FINISHED);
		return getDB().getFinishedMatch(getMatchID());
	}

	@Override
	public String getStatus() {
		return ServerMatch.STATUS_RUNNING;
	}
	
	@Override
	public List<List<String>> getJointMovesStrings() {
		if (jointMovesStrings == null) {
			jointMovesStrings = new DynamicDBBackedList<List<String>>(new JointMovesAccessor(getMatchID(), getDB()), true); 
		}
		return jointMovesStrings;
	}

	@Override
	public List<Pair<Date,String>> getStringStates() {
		if (stringStates == null) {
			stringStates = new DynamicDBBackedList<Pair<Date,String>>(new StringStateAccessor(getMatchID(), getDB(), getGame().getStylesheet()), false);
		}
		return stringStates;
	}

	@Override
	public List<List<GameControllerErrorMessage>> getErrorMessages() {
		// error messages shouldn't be cached for a running match, because
		// sometimes there is one more state than error messages, and the db
		// can't know if there was no error, or if it hasn't been written
		// yet, so a wrong (empty) result might get cached for the currently
		// running state.
		return new DynamicDBBackedList<List<GameControllerErrorMessage>>(new ErrorMessageAccessor(getMatchID(), getDB()), true);
	}

	@Override
	public List<? extends Player<TermType, State<TermType, ReasonerStateInfoType>>> getOrderedPlayers() {
		initPlayers();
		if (orderedPlayers == null) {
			orderedPlayers = new LinkedList<Player<TermType, State<TermType, ReasonerStateInfoType>>>();
			for (RoleInterface<TermType> role : getGame().getOrderedRoles()) {
				orderedPlayers.add(players.get(role));
			}
		}
		return orderedPlayers;
	}

	@Override
	public Player<TermType, State<TermType, ReasonerStateInfoType>> getPlayer(RoleInterface<TermType> role) {
		initPlayers();
		return players.get(role);
	}

	@Override
	public Collection<? extends Player<TermType, State<TermType, ReasonerStateInfoType>>> getPlayers() {
		initPlayers();
		return players.values();
	}
	
	private synchronized void initPlayers() {
		if (players == null) {
			players = new HashMap<RoleInterface<TermType>, Player<TermType, State<TermType, ReasonerStateInfoType>>>();
			
			for (RoleInterface<TermType> role : getGame().getOrderedRoles()) {
				PlayerInfo playerInfo = getRolesToPlayerInfos().get(role);
				
				Player<TermType, State<TermType, ReasonerStateInfoType>> player = PlayerFactory.<TermType, State<TermType, ReasonerStateInfoType>> createPlayer(playerInfo, gameScrambler);
				players.put(role, player);
			}
		}
	}
	

	/**
	 * The actual order of calls and START / PLAY / STOP messages is like this (if the initial state is not terminal):<br>
	 * 
	 * gameStarted() --- adds errorMessages(0), xmlStates(0)<br>
	 * START         --- all errors go to errorMessages(0)<br>
	 * PLAY          --- all errors go to errorMessages(0)<br>
	 * gameStep()    --- adds errorMessages(1), xmlStates(1), jointMoves(0), jointMovesStrings(0)<br>
	 * PLAY          --- all errors go to errorMessages(1)<br>
	 * gameStep()...
	 * ...<br>
	 * PLAY          --- all errors go to errorMessages(n-1)<br>
	 * gameStep()    --- adds errorMessages(n), jointMoves(n-1), jointMovesStrings(n-1), currentState is terminal -> state is not added <br>
	 * gameStopped() --- adds xmlStates(n)<br>
	 * STOP          --- all errors go to errorMessages(n)<br>
	 * 
	 * So finally, errorMessages and xmlStates will have size n+1, while jointMoves and jointMovesStrings will have size (n).
	 * 
	 * ===============
	 * 
	 * If the initial state is terminal, the order will be like this:
	 * 
	 * gameStarted() --- adds errorMessages(0), DOES NOT ADD xmlStates(0)<br>
	 * START         --- all errors go to errorMessages(0)<br>
	 * gameStopped() --- adds xmlStates(0)<br>
	 * STOP          --- all errors go to errorMessages(0)<br>
	 * 
	 * Like above, errorMessages and xmlStates will have size n+1 [== 1], while jointMoves and jointMovesStrings will have size (n) [== 0].
	 */
	@Override
	public void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
		stepNumber++;
		
		// only store the non-terminal states, because the terminal state will be stored in gameStopped()
		if (!currentState.isTerminal()) {
			// usually, the initial state won't be terminal, but of course it's possible to write such a stupid game
			updateState(currentState);
		}
	}

	@Override
	public void gameStep(JointMoveInterface<? extends TermInterface> jointMove, StateInterface<? extends TermInterface, ?> currentState) {
		updateJointMove(jointMove); // this has to be done BEFORE storing the state (because updateXmlState reads jointMoves)

		stepNumber++;
		
		if (!currentState.isTerminal()) {
			// only store the non-terminal states, because the terminal state will be stored in gameStopped()
			updateState(currentState);
		}		
	}

	@Override
	public void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		assert(currentState.isTerminal());

		updateGoalValues(goalValues);
		updateState(currentState);
	}
	
	/* (non-Javadoc)
	 * @see tud.ggpserver.datamodel.matches.ErrorMessageListener#addErrorMessage(tud.gamecontroller.logging.GameControllerErrorMessage)
	 */
	@Override
	public void notifyErrorMessage(GameControllerErrorMessage errorMessage) {
		if(stepNumber < 1) {
			logger.severe("error before step 1 in match " + getMatchID() + ":" + errorMessage);
		} else {
			try {
				getDB().addErrorMessage(getMatchID(), stepNumber, errorMessage);
			} catch (SQLException e) {
				logger.severe("GameControllerErrorMessage - exception: " + e); //$NON-NLS-1$
			}
		}
	}

	private void updateGoalValues(Map<? extends RoleInterface<?>, Integer> goalValues) {
		try {
			getDB().setMatchGoalValues(this, goalValues);
		} catch (SQLException e) {
			logger.severe("Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		}
	}

	private void updateJointMove(JointMoveInterface<? extends TermInterface> jointMove) {
		try {
			getDB().addJointMove(getMatchID(), stepNumber, jointMove);
		} catch (SQLException e) {
			logger.severe("JointMoveInterface<? extends TermInterface> - exception: " + e); //$NON-NLS-1$
		} catch (DuplicateInstanceException e) {
			logger.severe("JointMoveInterface<? extends TermInterface> - exception: " + e); //$NON-NLS-1$
		}
	}

	private void updateState(StateInterface<? extends TermInterface, ?> currentState) {
		String state = currentState.toString();
		
		assert (stepNumber == getStringStates().size() + 1);
	
		try {
			getDB().addState(getMatchID(), stepNumber, state);
		} catch (SQLException e) {
			logger.severe("StateInterface<? extends TermInterface,?>, Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		} catch (DuplicateInstanceException e) {
			logger.severe("StateInterface<? extends TermInterface,?>, Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		}
	}
	
	public int getCurrentStep() {
		return stepNumber;
	}
}
