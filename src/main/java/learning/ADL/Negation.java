/**
 * 
 */
package learning.ADL;

import java.util.List;
import java.util.Map;

import fsm.Symbol;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public class Negation extends Formula{
	/**
	 * 
	 */
	private Formula formula;
	
	/**
	 * Constructs 
	 * @param formula
	 */
	public Negation(Formula formula) {
		this.formula = formula;
	}

	
	/**
	 * Getter of formula
	 * @return the formula
	 */
	public Formula getFormula() {
		return formula;
	}


	/**
	 * Setter formula
	 * @param formula the formula to set
	 */
	public void setFormula(Formula formula) {
		this.formula = formula;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(NOT "+this.getFormula()+")";
	}


	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		
		if(this.formula instanceof Atom) {
			Formula f = this.formula.checkLinkedVar(act);
			if(f instanceof Existential) {
				Existential tmp = (Existential)f;
				return new Existential(tmp.getFree(), tmp.getLinked(), new Negation(this.formula));
			}
		}
		return new Negation(this.formula.checkLinkedVar(act));
	}


	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Negation) {
			return this.formula.equivalence(((Negation)other).formula);
		}
		return false;
	}


	@Override
	public Formula generalize(Map<String, String> mapping) {
		return new Negation(this.formula.generalize(mapping));
	}
	
	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		return formula.getVar();
	}


	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof Negation) {
			return this.formula.equals(((Negation)o).formula);
		}
		return false;
	}


	@Override
	public boolean produce(Formula F) {
		// TODO Auto-generated method stub
		return this.equals(F);
	}

	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		return !this.formula.check(o, a);
	}


	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		return this.formula.only(p);
	}
}
