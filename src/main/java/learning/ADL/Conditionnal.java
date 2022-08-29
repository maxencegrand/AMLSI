/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fsm.Symbol;
import fsm.TransitionAction;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public class Conditionnal extends Formula {
	/**
	 * 
	 */
	private Formula condition, consequence;
	
	
	/**
	 * Constructs 
	 * @param condition
	 * @param consequence
	 */
	public Conditionnal(Formula condition, Formula consequence) {
		this.condition = condition;
		this.consequence = consequence;
	}
	
	/**
	 * 
	 * Constructs 
	 * @param pred
	 * @param v
	 * @param prec
	 */
	public Conditionnal(Symbol pred, Observation.Value v, Observation prec) {
		switch(v) {
		case FALSE:
			this.consequence = new Negation(new Atom(pred));
			break;
		case TRUE:
			this.consequence = new Atom(pred);
			break;
		default:
			break;
		
		}
		this.condition = new Conjonction(prec);
		
		//Add equality
		List<String> p1 = new ArrayList<>(this.getVar().keySet());
		List<String> p2 = new ArrayList<>(this.getVar().keySet());
		
		List<Formula> tmp = new ArrayList<>();
		
		for(int i = 0; i < p1.size(); i++) {
			String v1 = p1.get(i);
			String pv1 = this.getVar().get(v1);
			for(int j = 0; j<i; j++) {
				String v2 = p2.get(j);
				if(v1.equals(v2)) {
					continue;
				}
				String pv2 = this.getVar().get(v2);
				if(pv1.equals(pv2)) {
					tmp.add(new Negation(new Equality(v1,v2,pv1,pv2)));
				}
			}
		}
		
		if(!tmp.isEmpty()) {
			if(!(this.condition instanceof Conjonction)) {
				Conjonction c = new Conjonction();
				c.add(condition);
				condition=c;
			}
			tmp.forEach(eq -> ((Conjonction)condition).add(eq));
		}
	}
	
	/**
	 * 
	 * @param prec
	 * @param eff
	 * @return
	 */
	public static Conjonction getCE (Observation prec, Observation eff) {
		Conjonction res = new Conjonction();
		eff.getPredicates().forEach((pred, v) -> {
			switch(v) {
			case FALSE:
			case TRUE:
				res.add(new Conditionnal(
						pred,
						v,
						new Observation(prec,Formula.dependent(
								pred, 
								prec.getPredicatesSymbols()))));
			default:
				break;
			}
			
		});
		return res;
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
		builder.append("(WHEN").append("\n");
		builder.append("\t").append(this.getCondition()).append("\n");
		builder.append("\t").append(this.getConsequence()).append("\n");
		builder.append(")").append("\n");
		return builder.toString();
	}

	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		if(act instanceof TransitionAction) {
			TransitionAction a = (TransitionAction)act;
			List<String> linked = a.linked();
			if(linked.containsAll(this.getVar().keySet())) {
				return this;
			} else {
				Map<String, String> param = new LinkedHashMap<>();
				for(String p : this.getVar().keySet()) {
					if(a.free().contains(p)) {
						param.put(p, a.getParameters().get(p));
					}
				}
				return new Universal(param, this);
			}
		}
		return this;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Conditionnal) {
			Conditionnal imp = (Conditionnal) other;
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
		return new Conditionnal(
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
		if(o instanceof Conditionnal) {
			Conditionnal other = (Conditionnal)o;
			return this.condition.equals(other.condition) &&
					this.consequence.equals(other.consequence);
		}
		return false;
	}
	
	public Formula merge(Formula F) {
		if(! (F instanceof Conditionnal)) {
			return super.merge(this);
		}
		Conditionnal other = (Conditionnal) F;
		return new Conditionnal(condition.merge(other.condition),
				consequence.merge(other.consequence));
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
