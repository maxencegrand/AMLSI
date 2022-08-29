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
public class Existential extends Formula {

	/**
	 * 
	 */
	private Map<String, String> free;
	/**
	 * 
	 */
	private Map<String, String> linked;
	/**
	 * 
	 */
	private Formula F;
	
	
	/**
	 * Constructs 
	 * @param free
	 * @param f
	 */
	public Existential(Map<String, String> free,Map<String, String> linked, Formula f) {
		if(free.keySet().size() < 1) {
			throw new IllegalArgumentException("At least one free variable is required.");
		}
		this.free = free;
		this.linked = linked;
		F = f;
		//Add equality
		List<String> p1 = new ArrayList<>(this.free.keySet());
		List<String> p2 = new ArrayList<>(this.linked.keySet());
				
		List<Formula> tmp = new ArrayList<>();
//		System.out.println(p1+" "+p2);
		for(int i = 0; i < p1.size(); i++) {
			String v1 = p1.get(i);
			String pv1 = this.getVar().get(v1);
			for(int j = 0; j < p2.size(); j++) {
				String v2 = p2.get(j);
				if(v1.equals(v2)) {
					continue;
				}
				String pv2 = this.linked.get(v2);
//				System.out.println(v1+" "+pv1+" "+v2+" "+pv2);
				if(pv1.equals(pv2)) {
					tmp.add(new Negation(new Equality(v1,v2,pv1,pv2)));
				}
			}
		}
//		System.out.println(tmp);
		if(!tmp.isEmpty()) {
			if(!(this.F instanceof Conjonction)) {
				Conjonction c = new Conjonction();
				c.add(F);
				F=c;
			}
			tmp.forEach(eq -> ((Conjonction)F).add(eq));
		}
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
		builder.append("(EXISTS (");
		this.free.forEach((k,v) -> {
			builder.append(k+" - "+v+" ");
		});
		builder.append(") ").append(this.getF()).append(")");
		return builder.toString();
	}
	
	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		return new Existential(this.free, this.linked, this.F.checkLinkedVar(act));
	}


	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Existential) {
			Existential o = (Existential) other;
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
			Existential u1 = (Existential) this.generalize(param1);
			Existential u2 = (Existential) o.generalize(param2);
			return u1.getF().equivalence(u2.getF());
		}
		return false;
	}


	@Override
	public Formula generalize(Map<String, String> mapping) {
		// TODO Auto-generated method stub
		Map<String, String> p = new HashMap<>();
		Map<String, String> p2 = new HashMap<>();
		free.forEach((k,v) -> {
			if(mapping.containsKey(k)) {
				p.put(mapping.get(k), free.get(k));
			} else {
				p.put(k, free.get(k));
			}
		});
		linked.forEach((k,v) -> {
			if(mapping.containsKey(k)) {
				p2.put(mapping.get(k), linked.get(k));
			} else {
				p2.put(k, linked.get(k));
			}
		});
		return new Existential(p, p2, this.getF().generalize(mapping));
	}


	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		return F.getVar();
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public boolean isCoherent(Formula F) {
		if(F instanceof Existential) {
			Existential other = (Existential)F;
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
		if(! (F instanceof Existential)) {
			return super.merge(F);
		}
		Existential other = (Existential)F;
		return new Existential(this.free, this.linked, this.F.merge(other.F));
	}
	
	public boolean equals(Object o) {
		if(o instanceof Existential) {
			Existential other = (Existential)o;
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
		return new Existential(newFree, this.linked, this.F.generalize(param));
	}
	
	public Formula convert(
			Conjonction strips,
			List<String> linked,
			List<String> free) {
		System.out.println("******************");
		System.out.println(this);
		System.out.println(strips);
		return this;
	}



	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		for(Map<String, String> mapping : super.allInstances(free, a.getParameters())) {
			Formula bis = this.F.generalize(mapping);
			if(this.F.generalize(mapping).check(o, a)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public Formula getUniversal() {
		return new Universal(this.free, this.F);
	}
	
	public Formula getImplicationUniversal() {
		if(this.F instanceof Conjonction) {
			Conjonction cond = ((Conjonction)F).getNotOnly(new ArrayList<>(this.free.keySet()));
			Conjonction cons = ((Conjonction)F).getOnly(new ArrayList<>(this.free.keySet()));
			if(!cond.isEmpty() && !cons.isEmpty()) {
				Implication imp = new Implication(cond, cons);
				return new Universal(this.free, imp);
			}
			return this.getUniversal();
		} else {
			return this.getUniversal();
		}
	}
	
	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		return this.F.only(p);
	}



	/**
	 * Getter of linked
	 * @return the linked
	 */
	public Map<String, String> getLinked() {
		return linked;
	}
	
}
