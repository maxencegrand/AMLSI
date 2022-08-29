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
public class Disjonction extends Formula {

	/**
	 * 
	 */
	List<Formula> formulas;

	/**
	 * 
	 * Constructs
	 */
	public Disjonction() {
		this.formulas = new ArrayList<>();
	}

	/**
	 * 
	 * Constructs 
	 * @param l
	 */
	public Disjonction(List<List<Formula>> l) {
		this();
		l.forEach(l1 -> {
			if(l1.size() == 1) {
				this.add(l1.get(0));
			} else if(l1.size() > 1) {
				this.add(new Conjonction(l1));
			}
		});
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Formula e) {
		if(this.contains(e)) {
			return false;
		}
		return formulas.add(e);
	}


	/**
	 * @param index
	 * @param element
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Formula element) {
		formulas.add(index, element);
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
		builder.append("(OR").append("\n");
		for(Formula f : this.formulas) {
			builder.append("\t").append(f.toString()).append("\n");
		}
		builder.append("");
		return builder.toString();
	}


	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		Disjonction disj = new Disjonction();
		Iterator<Formula> it = this.iterator();
		while(it.hasNext()) {
			disj.add(it.next().checkLinkedVar(act));
		}
		return disj;
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
			res.add(f1);
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
		if(o instanceof Disjonction) {
			Disjonction other = (Disjonction)o;
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
	
	public Formula simplify() {
		List<List<Formula>> form = new ArrayList<>(), form2 = new ArrayList<>();
		List<Formula> conj = new ArrayList<>();
		Iterator<Formula> it = this.iterator();
		while(it.hasNext()) {
			Formula f = it.next();
			List<Formula> tmp = new ArrayList<>();
			if(f instanceof Conjonction) {
				Iterator<Formula> it2 = ((Conjonction)f).iterator();
				while(it2.hasNext()) {
					tmp.add(it2.next());
				}
			} else {
				tmp.add(f);
			}
			form.add(tmp);
		}
		for(int i = 0; i < form.size(); i++) {
			for(int j = 0; j < form.get(i).size(); j++) {
				Formula f1 = form.get(i).get(j);
				boolean b = true;
				for(int k = 0; k < form.size(); k++) {
					b &= form.get(k).contains(f1);
				}
				if(b) {
					conj.add(f1);
				}
			}
		}
		for(int i = 0; i < form.size(); i++) {
			List<Formula> tmp = new ArrayList<>();
			for(int j = 0; j < form.get(i).size(); j++) {
				Formula f1 = form.get(i).get(j);
				if(!conj.contains(f1)) {
					tmp.add(f1);
				}
			}
			form2.add(tmp);
		}
		Disjonction res = new Disjonction(form2);
		Conjonction c = new Conjonction(conj);
		c.add(res);
		return c;
	}

	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		boolean b = false;
		Iterator<Formula> it = this.iterator();
		while(it.hasNext()) {
			b |= it.next().check(o, a);
		}
		return false;
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
}
