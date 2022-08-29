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
public class Atom extends Formula {
	/**
	 * 
	 */
	private Symbol predicate;
	
	
	/**
	 * Constructs 
	 * @param predicate
	 * @param value
	 */
	public Atom(Symbol predicate) {
		this.predicate = predicate;
	}

	
	/**
	 * Getter of predicate
	 * @return the predicate
	 */
	public Symbol getPredicate() {
		return predicate;
	}


	/**
	 * Setter predicate
	 * @param predicate the predicate to set
	 */
	public void setPredicate(Symbol predicate) {
		this.predicate = predicate;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return this.getPredicate().toString();
	}

	/**
	 * 
	 * @param act
	 * @return
	 */
	public Formula checkLinkedVar(Symbol act) {
		if(act instanceof TransitionAction) {
			TransitionAction a = (TransitionAction)act;
			//System.out.println(predicate+":"+a+" "+a.getAction()+" "+a.linked()+" "+a.free());
			List<String> linked = a.linked();
			if(linked.containsAll(this.getVar().keySet())) {
				return this;
			} else {
				Map<String, String> param = new LinkedHashMap<>();
				Map<String, String> param2 = new LinkedHashMap<>();
				for(String p : predicate.getListParameters()) {
					if(a.free().contains(p)) {
						param.put(p, a.getParameters().get(p));
					}
				}
				for(String p : act.getListParameters()) {
					if(a.free().contains(p)) {
						//param.put(p, a.getParameters().get(p));
					} else {
						param2.put(p, a.getParameters().get(p));
					}
				}
				return new Existential(param, param2, this);
			}
		}
		return this;
	}

	/**
	 * 
	 */
	@Override
	public boolean equivalence(Formula other) {
		if(other instanceof Atom) {
			return this.predicate.equals(((Atom)other).predicate);
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public Formula generalize(Map<String, String> mapping) {
		// TODO Auto-generated method stub
		Map<String, String> mapping2 = new HashMap<>();
		mapping2.putAll(mapping);
		for(String p : this.predicate.getListParameters()) {
			if(!mapping2.containsKey(p)) {
				mapping2.put(p, p);
			}
		}
		return new Atom(this.predicate.generalize(mapping2));
	}


	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		Map<String, String> res = new HashMap<>();
		res.putAll(this.predicate.getParameters());
		return res;
	}


	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof Atom) {
			return this.predicate.equals(((Atom)o).predicate);
		}
		return false;
	}


	@Override
	public boolean produce(Formula F) {
		// TODO Auto-generated method stub
		return this.equals(F);
	}


	/**
	 * 
	 */
	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		if(o.getPredicates().containsKey(predicate)) {
			switch(o.getPredicates().get(predicate)) {
			case TRUE:
				return true;
			default:
				return false;
			}
		}
		return false;
	}


	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		boolean b = true;
		for(String str : this.predicate.getListParameters()) {
			b &= p.contains(str);
		}
		return b;
	}
}
