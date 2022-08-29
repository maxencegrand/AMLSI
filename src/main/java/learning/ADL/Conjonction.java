/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fsm.Symbol;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public class Conjonction extends Formula {
	/**
	 * 
	 */
	List<Formula> formulas;
	
	/**
	 * 
	 * Constructs
	 */
	public Conjonction() {
		this.formulas = new ArrayList<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param o
	 */
	public Conjonction(Observation o) {
		this();
		o.getPredicatesSymbols().forEach(p -> {
			switch(o.getValue(p)) {
			case FALSE:
				this.add(new Negation(new Atom(p)));
				break;
			case TRUE:
				this.add(new Atom(p));
				break;
			default:
				break;
			
			}
		});
	}
	
	/**
	 * 
	 * Constructs 
	 * @param l
	 */
	public Conjonction(List<Formula> l) {
		this();
		l.forEach(c -> this.add(c));
	}
	
	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Formula e) {
		if(e instanceof Conjonction) {
			boolean b = true;
			for(Formula f : ((Conjonction)e).formulas) {
				if(!this.contains(f)) {
					b &= this.add(f);
				}
			}
			return b;
		} else {
			if(!this.contains(e)) {
				return formulas.add(e);
			}
			return false;
		}
	}

	/**
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		formulas.clear();
	}


	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return formulas.contains(o);
	}


	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public Formula get(int index) {
		return formulas.get(index);
	}


	/**
	 * @return
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return formulas.isEmpty();
	}


	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<Formula> iterator() {
		return formulas.iterator();
	}


	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return formulas.size();
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(this.isEmpty()) {
			return "";
		} else if(this.size() <= 1) {
			return this.get(0).toString();
		}
		StringBuilder builder = new StringBuilder();
		builder.append("(AND").append("\n");
		for(Formula f : this.formulas) {
			builder.append("\t").append(f.toString()).append("\n");
		}
		builder.append(")");
		return builder.toString();
	}

	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		Conjonction conj = new Conjonction();
		Iterator<Formula> it = this.iterator();
		while(it.hasNext()) {
			conj.add(it.next().checkLinkedVar(act));
		}
		return conj;
	}
	
	public boolean containsEquivalent(Formula f) {
		for(Formula f1 : this.formulas) {
			if(f1.equivalence(f)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Conjonction) {
			Conjonction o = (Conjonction) other;
			if(this.size() != o.size()) {
				return false;
			}
			for(Formula f1 : o.formulas) {
				if(!this.containsEquivalent(f1)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public Formula generalize(Map<String, String> mapping) {
		// TODO Auto-generated method stub
		Conjonction res = new Conjonction();
		for(Formula f1 : formulas) {
			res.add(f1.generalize(mapping));
		}
		return res;
	}

	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		Map<String, String> res = new HashMap<>();
		this.formulas.forEach(F -> {
			res.putAll(F.getVar());
		});
		return res;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof Conjonction) {
			Conjonction other = (Conjonction)o;
			if(this.size() != other.size()) {
				return false;
			}
			for(Formula f : this.formulas) {
				if(!other.formulas.contains(f)) {
					return false;
				}
			}
			for(Formula f : other.formulas) {
				if(!this.formulas.contains(f)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public Formula lift(Symbol act) {
		System.out.println("Lift conjonction");
		Conjonction o = new Conjonction();
		Iterator<Formula> it = this.iterator();
		while(it.hasNext()) {
			Formula f = it.next();
			f = f.lift(act);
			o.add(f);
		}
		return o;
	}

	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		boolean b = true;
		Iterator<Formula> it = this.iterator();
		while(it.hasNext()) {
			Formula f = it.next();
//			System.out.println(f);
			b &= f.check(o, a);
//			System.out.println(b);
		}
		return b;
	}

	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		Iterator<Formula> it = this.iterator();
		boolean b = true;
		while(it.hasNext() && b) {
			b &= it.next().only(p);
		}
		return b;
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	public Conjonction getOnly(List<String> p) {
		// TODO Auto-generated method stub
		Iterator<Formula> it = this.iterator();
		Conjonction c = new Conjonction();
		while(it.hasNext()) {
			Formula f = it.next();
			if(f.only(p)) {
				c.add(f);
			}
		}
		return c;
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	public Conjonction getNotOnly(List<String> p) {
		// TODO Auto-generated method stub
		Iterator<Formula> it = this.iterator();
		Conjonction c = new Conjonction();
		while(it.hasNext()) {
			Formula f = it.next();
			if(!f.only(p)) {
				c.add(f);
			}
		}
		return c;
	}
	
	
}
