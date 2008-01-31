package tud.gamecontroller;

import java.io.File;

import cs227b.teamIago.parser.Axioms;

public class Game implements GameInterface {

	private Reasoner reasoner;
	private String gameDescription;
	private String name;
		
	private Game(String gameDescription, String name) {
		this.gameDescription=gameDescription;
		this.name=name;
		reasoner=new Reasoner(gameDescription);
		
	}

	public static Game readFromFile(String filename) {
		String gameDescription=Axioms.loadStringFromFile(filename);
		return new Game(gameDescription, (new File(filename)).getName());
	}

	public int getNumberOfRoles() {
		return reasoner.GetRoles().size();
	}

	public State getInitialState() {
		return new State(reasoner, reasoner.getInitialState());
	}

	public Role getRole(int roleindex) {
		return new Role(reasoner.GetRoles().get(roleindex-1));
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
