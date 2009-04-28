package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;

import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Match;


public class ViewMatch {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ViewMatch.class.getName());

	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	
	private Match<?, ?> match;
	private int stepNumber = 1;

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public void setMatchID(String matchID) throws NamingException, SQLException {
		match = db.getMatch(matchID);
	}

	public Match getMatch() {
		return match;
	}
	
	public List<String> getMoves() {
		if ((stepNumber < 1) || stepNumber > (match.getNumberOfStates() - 1)) {  // -1, because there is one less jointmove than states
			return new LinkedList<String>();
		}
		return match.getJointMovesStrings().get(stepNumber - 1);
	}
	
	public List<GameControllerErrorMessage> getErrorMessages() {
		if ((stepNumber < 1) || (stepNumber > match.getNumberOfStates())) {
			return new LinkedList<GameControllerErrorMessage>();
		}
		if (match.getNumberOfStates() > match.getErrorMessages().size()) {
			logger.severe("getErrorMessages().size() smaller than getNumberOfStates()! Causing match: " + match.toString());        //$NON-NLS-1$s
			throw new InternalError("getErrorMessages().size() smaller than getNumberOfStates()! Causing match: " + match.toString());
		}
		return match.getErrorMessages().get(stepNumber - 1);
	}
}
