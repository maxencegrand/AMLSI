/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsm.Symbol;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public class Implication extends Formula {

	/**
	 * 
	 */
	private Formula condition, consequence;
	
	
	/**
	 * Constructs 
	 * @param condition
	 * @param consequence
	 */
	public Implication(Formula condition, Formula consequence) {
		this.condition = condition;
		this.consequence = consequence;
	}

	
	/**
	 * Getter of condition
	 * @return the condition
	 */
	public Formula getCondition() {
		return condition;
	}


	/**
	 * Setter condition
	 * @param condition the condition to set
	 */
	public void setCondition(Formula condition) {
		this.condition = condition;
	}


	/**
	 * Getter of consequence
	 * @return the consequence
	 */
	public Formula getConsequence() {
		return consequence;
	}


	/**
	 * Setter consequence
	 * @param consequence the consequence to set
	 */
	public void setConsequence(Formula consequence) {
		this.consequence = consequence;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append("(IMPLY").append("\n");
		builder.append("\t").append(this.getCondition()).append("\n");
		builder.append("\t").append(this.getConsequence()).append("\n");
		builder.append(")").append("\n");
		return builder.toString();
	}

	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		return new Implication(
				this.condition.checkLinkedVar(act), 
				this.consequence.checkLinkedVar(act));
	}

	/**
	 * 
	 */
	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Implication) {
			Implication imp = (Implication) other;
			return imp.condition.equivalence(condition) &&
					imp.consequence.equivalence(consequence);
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public Formula generalize(Map<String, String> mapping) {
		// TODO Auto-generated method stub
		return new Implication(
				condition.generalize(mapping),
				consequence.generalize(mapping));
	}
	
	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		Map<String, String> res = new HashMap<>();
		res.putAll(this.condition.getVar());
		res.putAll(this.consequence.getVar());
		return res;
	}


	@Override
	public boolean equals(Object o) {
		if(o instanceof Implication) {
			Implication other = (Implication)o;
			return this.condition.equals(other.condition) &&
					this.consequence.equals(other.consequence);
		}
		return false;
	}


	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		if(this.condition.check(o, a)) {
			return this.consequence.check(o, a);
		}
		return true;
	}
	
	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		return this.condition.only(p) && this.consequence.only(p);
	}
}
