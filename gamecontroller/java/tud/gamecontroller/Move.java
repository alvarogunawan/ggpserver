package tud.gamecontroller;

import cs227b.teamIago.resolver.Expression;

public class Move {
	Expression expr;
	
	public Move(Expression expression) {
		this.expr=expression;
	}

	public String toString(){
		return expr.toString();
	}
}
