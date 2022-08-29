/**
 * 
 */
package learning.ADL;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fsm.Symbol;
import fsm.TransitionAction;
import learning.Domain;
import learning.Observation;
/**
 * @author Maxence Grand
 *
 */
public class AdlDomain {
	/**
	 * 
	 */
	private Map<Symbol, Formula> preconditions, effects;
	/**
	 * 
	 */
	private Map<Symbol, ConceptualGraph> graph;

	/**
	 * 
	 * Constructs
	 */
	public AdlDomain() {
		this.preconditions = new HashMap<>();
		this.effects = new HashMap<>();
		this.graph = new HashMap<>();
	}

	/**
	 * 
	 * Constructs 
	 * @param strips
	 */
	public AdlDomain(Domain strips) {
		this();
		strips.getDomain().forEach((s,p) -> {
			this.preconditions.put(s, new Conjonction(p.getX()));
			this.effects.put(s, Conditionnal.getCE(p.getX(), p.getY()));
		});
	}

	/**
	 * Transforme each precondition into ep if there are at least one free var
	 */
	public void step1() {
		this.preconditions.forEach((s,p) -> {
			this.preconditions.put(s, this.preconditions.get(s).checkLinkedVar(s));
		});
		this.effects.forEach((s,p) -> {
			this.effects.put(s, this.effects.get(s).checkLinkedVar(s));
		});
	}

	public void step2() {
		//Preconditions
		for(Symbol op : this.preconditions.keySet()) {
			Formula prec = this.preconditions.get(op);
			if(!(prec instanceof Conjonction)) {
				continue;
			}
			Conjonction c = (Conjonction)prec;
			Conjonction newC = new Conjonction();
			boolean merged = false;
			do {
				merged=false;
				List<Integer> tabu = new ArrayList<>();
				for(int i = 0; i < c.size(); i++) {
					if(tabu.contains(i)) {
						continue;
					}
					tabu.add(i);
					Formula tmp = c.get(i);
					if(c.get(i) instanceof Existential 
							|| c.get(i) instanceof Universal
							|| c.get(i) instanceof Conditionnal) {
						for(int j = 0; j < c.size(); j++) {
							if(tabu.contains(j)) {
								continue;
							}
							if(c.get(j) instanceof Existential) {
								if(tmp.isCoherent(c.get(j))) {
									tmp = tmp.merge(c.get(j));
									tabu.add(j);
									merged=true;
								}
							}
						}
					}
					newC.add(tmp);
				}
				c=newC;
				newC = new Conjonction();
			}while(merged);
			this.preconditions.put(op, c);
		}
		//Effects
		for(Symbol op : this.effects.keySet()) {
			Formula prec = this.effects.get(op);
			if(!(prec instanceof Conjonction)) {
				continue;
			}
			Conjonction c = (Conjonction)prec;
			Conjonction newC = new Conjonction();
			boolean merged = false;
			do {
				merged=false;
				List<Integer> tabu = new ArrayList<>();
				for(int i = 0; i < c.size(); i++) {
					if(tabu.contains(i)) {
						continue;
					}
					tabu.add(i);
					Formula tmp = c.get(i);
					if(c.get(i) instanceof Existential 
							|| c.get(i) instanceof Universal
							|| c.get(i) instanceof Conditionnal) {
						for(int j = 0; j < c.size(); j++) {
							if(tabu.contains(j)) {
								continue;
							}
							if(c.get(j) instanceof Existential
									|| c.get(j) instanceof Universal
									|| c.get(j) instanceof Conditionnal) {
								if(tmp.isCoherent(c.get(j))) {
									tmp = tmp.merge(c.get(j));
									tabu.add(j);
									merged=true;
								}
							}
						}
					}
					newC.add(tmp);
				}
				c=newC;
				newC = new Conjonction();
			}while(merged);
			this.effects.put(op, c);
		}
	}
	
	/**
	 * Lift free var
	 */
	public void lift() {
		for(Symbol op : this.preconditions.keySet()) {
			this.preconditions.put(op, this.preconditions.get(op).lift(op));
		}
		for(Symbol op : this.effects.keySet()) {
			this.effects.put(op, this.effects.get(op).lift(op));
		}
	}
	
	/**
	 * Transform Existential into Universal when its possible
	 * 
	 * @param strips
	 */
	public void step3(Domain strips) {
		for(Symbol op : this.preconditions.keySet()) {
			Formula prec = this.preconditions.get(op);
			Observation o = strips.getDomain().get(op).getX();
			if(prec instanceof Conjonction) {
				Conjonction c = (Conjonction)prec;
				Conjonction c2 = new Conjonction();
				Iterator<Formula> it = c.iterator();
				while(it.hasNext()) {
					Formula f = it.next();
					if(f instanceof Existential) {
						Existential ep = (Existential)f;
						Formula f1 = ep.getImplicationUniversal();
						if(f1.check(o, op)) {
							c2.add(f1);
						} else {
							c2.add(f);
						}
					}
					c2.add(f);
				}
				this.preconditions.put(op, c2);
			}
		}
	}
	
	/**
	 * Merge split actions
	 * @param actions
	 * @param mapActions
	 */
	public AdlDomain merge() {
		Map<Symbol, Symbol> mapActions = new HashMap<>();
		List<Symbol> actions = new ArrayList<>();
		this.preconditions.keySet().forEach(s -> {
			Symbol a = ((TransitionAction)s).getAction();
			mapActions.put(s, a);
			if(!actions.contains(a)) {
				actions.add(a);
			}
			
		});
		System.out.println(actions);
		System.out.println(mapActions);
		
		AdlDomain res = new AdlDomain();
		actions.forEach(a -> {
			res.preconditions.put(a, new Disjonction());
			res.effects.put(a, new Conjonction());
		});
		this.preconditions.forEach((a,p) -> {
			((Disjonction)res.preconditions.get(mapActions.get(a))).add(p);
		});
		this.effects.forEach((a,e) -> {
			((Conjonction)res.effects.get(mapActions.get(a))).add(e);
		});
//		res.preconditions.forEach((a,p) -> {
//			res.preconditions.put(a, ((Disjonction)p).simplify());
//		});
		return res;
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.preconditions.keySet().forEach(action -> {
			builder.append("------").append(action).append("------").append("\n");
			builder.append("PRECONDITION").append("\n");
			builder.append(this.preconditions.get(action)).append("\n");
			builder.append("EFFECT").append("\n");
			builder.append(this.effects.get(action)).append("\n");
		});
		return builder.toString();
	}
}
