/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
public class Universal extends Formula{
	/**
	 * 
	 */
	private Map<String, String> free;
	/**
	 * 
	 */
	private Formula F;


	/**
	 * Constructs 
	 * @param free
	 * @param f
	 */
	public Universal(Map<String, String> free, Formula f) {
		if(free.keySet().size() < 1) {
			throw new IllegalArgumentException("At least one free variable is required.");
		}
		this.free = free;
		F = f;
	}


	/**
	 * Getter of free
	 * @return the free
	 */
	public Map<String, String> getFree() {
		return free;
	}


	/**
	 * Setter free
	 * @param free the free to set
	 */
	public void setFree(Map<String, String> free) {
		this.free = free;
	}


	/**
	 * Getter of f
	 * @return the f
	 */
	public Formula getF() {
		return F;
	}


	/**
	 * Setter f
	 * @param f the f to set
	 */
	public void setF(Formula f) {
		F = f;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append("(FORALL (");
		this.free.forEach((k,v) -> {
			builder.append(k+" - "+v+" ");
		});
		builder.append(") ").append(this.getF()).append(")");
		return builder.toString();
	}


	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		return new Universal(this.free, this.F.checkLinkedVar(act));
	}


	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Universal) {
			Universal o = (Universal) other;
			if(free.size() != o.free.size()) {
				return false;
			}
			Map<String, String> param1 = new HashMap<>();
			Map<String, String> param2 = new HashMap<>();
			List<String> p1 = new ArrayList<>(this.free.keySet());
			List<String> p2 = new ArrayList<>(o.free.keySet());
			for(int i = 0; i < p1.size(); i++) {
				String p = "?y"+i;
				param1.put(p1.get(i), p);
				param2.put(p2.get(i), p);
			}
			Universal u1 = (Universal) this.generalize(param1);
			Universal u2 = (Universal) o.generalize(param2);
			return u1.getF().equivalence(u2.getF());
		}
		return false;
	}


	@Override
	public Formula generalize(Map<String, String> mapping) {
		// TODO Auto-generated method stub
		Map<String, String> p = new HashMap<>();
		free.forEach((k,v) -> {
			if(mapping.containsKey(k)) {
				p.put(mapping.get(k), free.get(k));
			} else {
				p.put(k, free.get(k));
			}
		});
		return new Universal(p, this.getF().generalize(mapping));
	}

	/**
	 * 
	 * @param other
	 * @return
	 */
	public boolean isCoherent(Formula F) {
		if(F instanceof Universal) {
			Universal other = (Universal)F;
			if(free.size() != other.free.size()) {
				return false;
			}
			for(String p : free.keySet()) {
				if(!other.free.containsKey(p)) {
					return false;
				}
				if(!free.get(p).equals(other.free.get(p))) {
					return false;
				}
			}
			return this.F.isCoherent(other.F);
		}
		return true;
	}
	
	/**
	 * 
	 */
	public Formula merge(Formula F) {
		if(! (F instanceof Universal)) {
			return super.merge(F);
		}
		Universal other = (Universal)F;
		return new Universal(this.free, this.F.merge(other.F));
	}
	
	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		return F.getVar();
	}
	
	public boolean equals(Object o) {
		if(o instanceof Universal) {
			Universal other = (Universal)o;
			if(free.size() != other.free.size()) {
				return false;
			}
			for(String p : free.keySet()) {
				if(!other.free.containsKey(p)) {
					return false;
				}
				if(!free.get(p).equals(other.free.get(p))) {
					return false;
				}
			}
			return this.F.equals(other.F);
		}
		return false;
	}
	
	public Formula lift(Symbol act) {
		if(!(act instanceof TransitionAction)) {
			return super.lift(act);
		}
		TransitionAction a = (TransitionAction)act;
		Map<String, String> param = new LinkedHashMap<>();
		Map<String, String> newFree = new LinkedHashMap<>();
		a.linked().forEach(p -> param.put(p, p));
		int i = 0;
		for(String str : free.keySet()) {
			String tmp = "?y"+i;
			param.put(str, tmp);
			newFree.put(tmp, this.free.get(str));
			i++;
		}
		return new Universal(newFree, this.F.generalize(param));
	}


	@Override
	public boolean produce(Formula F) {
		// TODO Auto-generated method stub
		if(F instanceof Disjonction) {
			Disjonction d = (Disjonction)F;
			boolean b = false;
			Iterator<Formula> it = d.iterator();
			while(it.hasNext()) {
				b |= this.produce(it.next());
			}
			return b;
		} else if (F instanceof Conjonction) {
			Conjonction c = (Conjonction)F;
			boolean b = true;
			Iterator<Formula> it = c.iterator();
			while(it.hasNext()) {
				b &= this.produce(it.next());
			}
			return b;
		} else if (F instanceof Existential) {
			
		} else if (F instanceof Universal) {
			
		} else if (F instanceof Conditionnal) {
			
		} else if (F instanceof Implication) {
			
		} else {
			
		}
		return false;
	}


	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		for(Map<String, String> mapping : super.allInstances(free, a.getParameters())) {
			Formula bis = this.F.generalize(mapping);
			if(!this.F.generalize(mapping).check(o, a)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		return this.F.only(p);
	}
}
