package tud.gamecontroller.term;

import java.util.List;

public abstract class TermDelegator<TermType extends TermInterface> implements TermInterface {

	private TermType term;

	public TermDelegator(TermType term) {
		this.term=term;
	}
	
	public TermType getTerm(){
		return term;
	}

	public List<? extends TermInterface> getArgs() {
		return term.getArgs();
	}

	public String getKIFForm() {
		return term.getKIFForm();
	}

	public String getName() {
		return term.getName();
	}

	public String getPrefixForm() {
		return term.getPrefixForm();
	}

	public boolean isCompound() {
		return term.isCompound();
	}

	public boolean isConstant() {
		return term.isConstant();
	}

	public boolean isVariable() {
		return term.isVariable();
	}
	
	public boolean isGround() {
		return term.isGround();
	}
	
	public String toString() {
		return term.toString();
	}

	public boolean equals(Object obj) {
		if(obj instanceof TermDelegator){
			return term.equals(((TermDelegator<?>)obj).getTerm());
		}else if(obj instanceof TermInterface){
			return term.equals(obj);
		}else{
			return false;
		}
	}

	public int hashCode() {
		return term.hashCode();
	}
}
