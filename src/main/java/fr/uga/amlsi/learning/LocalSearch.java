/**
 * 
 */
package fr.uga.amlsi.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.CompressedNegativeExample;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fr.uga.amlsi.learning.temporal.TemporalDomain;
import fr.uga.amlsi.main.Argument;
import fr.uga.amlsi.learning.simulator.DomainSimulator;
import fr.uga.amlsi.learning.simulator.Movement;

/**
 * @author Maxence Grand
 *
 */
public class LocalSearch {
	/**
	 * All actions
	 */
	private List<Symbol> actions;
	/**
	 * All predicates
	 */
	private List<Symbol> predicates;
	
	
	/**
	 * Getter of actions
	 * @return the actions
	 */
	public List<Symbol> getActions() {
		return actions;
	}

	/**
	 * Setter actions
	 * @param actions the actions to set
	 */
	public void setActions(List<Symbol> actions) {
		this.actions = actions;
	}

	/**
	 * Getter of predicates
	 * @return the predicates
	 */
	public List<Symbol> getPredicates() {
		return predicates;
	}

	/**
	 * Setter predicates
	 * @param predicates the predicates to set
	 */
	public void setPredicates(List<Symbol> predicates) {
		this.predicates = predicates;
	}

	/**
	 * Constructs 
	 * @param actions
	 * @param predicates
	 */
	public LocalSearch(List<Symbol> actions, List<Symbol> predicates) {
		this.actions = actions;
		this.predicates = predicates;
	}

	/**
	 * Compute the fitness of an Domain
	 * @param indiv the Domain
	 * @param data the observations
	 * @param pos I+
	 * @param neg I-
	 * @param is The initial state
	 * @return fitness score
	 */
	public float fitness(
			Domain indiv, Sample pos, Sample neg, 
			List<CompressedNegativeExample> compressed, String is) {
		//System.out.println("PASS");
		float fit;

		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		
		float fitPos = fitPositive(indiv, pos, initial);
		float fitNeg = fitNegative(indiv, neg, compressed, initial);
		float fitPrec = fitPrecondition(indiv, pos);
		float fitPost = fitPostcondition(indiv, pos);
		
		fit = (fitPos + fitNeg + fitPost + fitPrec);
		return fit;
	}
	
	/**
	 * Compute fitness for positive samples
	 *
	 * @param Domain The model
	 * @param data The observations
	 * @return fitness score
	 */
	public int fitPositive(
			Domain Domain,Sample data, Observation initial) {
		int res = 0;
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = Domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		Map<Symbol, Observation> preconditions2 = new HashMap<>();
		Map<Symbol, Observation> postconditions2 = new HashMap<>();
		for(Symbol act : this.actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			preconditions2.put(act, prec);
			postconditions2.put(act, post);
		}
		for(Trace p : data.getExamples()){
			Example plan = (Example)p;
			Observation current = initial.clone();
			for(int i = 0; i< plan.size(); i++) {
				Symbol act = plan.get(i);
				Observation prec = preconditions2.get(act);
				if(current.contains(prec)) {
					res++;
					Observation eff = postconditions2.get(act);
					for(Symbol e : eff.getPredicatesSymbols()) {
						switch(eff.getValue(e)) {
						case TRUE:
							current.addTrueObservation(e);
							break;
						case FALSE:
							current.addFalseObservation(e);
							break;
						default:
							break;
						}
					}
				}else {
					break;
				}
			}
		}
		return res;
	}
	
	/*public int fitPositiveNonSize(
			Domain Domain,Sample data, Observation initial) {
		int res = 0;
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = Domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		Map<Symbol, Observation> preconditions2 = new HashMap<>();
		Map<Symbol, Observation> postconditions2 = new HashMap<>();
		for(Symbol act : this.actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			preconditions2.put(act, prec);
			postconditions2.put(act, post);
		}
		for(Trace p : data.getExamples()){
			Example plan = (Example)p;
			int tmp = 0;
			Observation current = initial.clone();
			for(int i = 0; i< plan.size(); i++) {
				Symbol act = plan.get(i);
				Observation prec = preconditions2.get(act);
				if(current.contains(prec)) {
					tmp++;
					Observation eff = postconditions2.get(act);
					for(Symbol e : eff.getPredicatesSymbols()) {
						switch(eff.getValue(e)) {
						case TRUE:
							current.addTrueObservation(e);
							break;
						case FALSE:
							current.addFalseObservation(e);
							break;
						default:
							break;
						}
					}
				}else {
					break;
				}
			}
			if(tmp==plan.size()) {
				res++;
			}
		}
		return res;
	}*/
	
	/**
	 * Compute fitness for negative samples
	 *
	 * @param Domain The model
	 * @param data The observations
	 * @return fitness score
	 */
	public int fitNegative(
			Domain Domain, Sample data,
			List<CompressedNegativeExample> compressed, Observation initial) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = Domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		Map<Symbol, Observation> preconditions2 = new HashMap<>();
		Map<Symbol, Observation> postconditions2 = new HashMap<>();
		for(Symbol act : this.actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			preconditions2.put(act, prec);
			postconditions2.put(act, post);
			
		}
		int res = 0;

		for(CompressedNegativeExample plan : compressed){
			Observation current = initial.clone();
			for(int i = 0; i< plan.getPrefix().size(); i++) {
				//Test Negatives
				for(Example n : plan.getNegativeIndex(i)) {
					Observation current2 = current.clone();
					for(Symbol a : n.getActionSequences()) {
						Observation prec = preconditions2.get(a);
						if(!current2.contains(prec)) {
							res++;
							break;
						} else {
							Observation eff = postconditions2.get(a);
							for(Symbol e : eff.getPredicatesSymbols()) {
								switch(eff.getValue(e)) {
								case TRUE:
									current2.addTrueObservation(e);
									break;
								case FALSE:
									current2.addFalseObservation(e);
									break;
								default:
									break;
								}
							}
						}
					}
				}
				if(i < plan.getPrefix().size()) {
					//Test Positive
					Symbol a = plan.getPrefix().get(i);
					Observation prec = preconditions2.get(a);
					if(!current.contains(prec)) {
						break;
					}
					//Apply Effect
					Observation eff = postconditions2.get(a);
					for(Symbol e : eff.getPredicatesSymbols()) {
						switch(eff.getValue(e)) {
						case TRUE:
							current.addTrueObservation(e);
							break;
						case FALSE:
							current.addFalseObservation(e);
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * Compute fitness for preconditions
	 *
	 * @param Domain The model
	 * @param data The observations
	 * @return fitness score
	 */
	public float fitPrecondition(
			Domain Domain,
			Sample data) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = Domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		Map<Symbol, Observation> preconditions2 = new HashMap<>();
		Map<Symbol, Observation> postconditions2 = new HashMap<>();
		for(Symbol act : this.actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			preconditions2.put(act, prec);
			postconditions2.put(act, post);
		}
		
		int fit=0, err=0;
		for(Trace plan : data.getExamples()) {
			ObservedExample obs = (ObservedExample) plan;
			for(int i = 0; i< obs.size(); i++) {
				Symbol act = obs.get(i);
				Observation ante = obs.ante(i);
				Observation prec = preconditions2.get(act);
				for(Symbol prop : prec.getPredicatesSymbols()) {
					switch(prec.getValue(prop)) {
					case TRUE:
						switch(ante.getValue(prop)) {
						case TRUE:
							fit++;
							break;
						case FALSE:
							err++;
							break;
						default:
							break;
						}
						break;
					case FALSE:
						switch(ante.getValue(prop)) {
						case FALSE:
							fit++;
							break;
						case TRUE:
							err++;
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
				}
				
			}
		}
		return fit-err;
	}
	
	/**
	 * Compute fitness for effects
	 *
	 * @param Domain The model
	 * @param data The observations
	 * @return fitness score
	 */
	public float fitPostcondition(
			Domain Domain,
			Sample data) {
		Pair<Float, Float> p = new Pair<>((float)0, (float)0);
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = Domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		Map<Symbol, Observation> preconditions2 = new HashMap<>();
		Map<Symbol, Observation> postconditions2 = new HashMap<>();
		for(Symbol act : this.actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			preconditions2.put(act, prec);
			postconditions2.put(act, post);
		}
		
		int fit=0, err=0;
		for(Trace plan : data.getExamples()) {
			ObservedExample obs = (ObservedExample) plan;
			for(int i = 0; i< obs.size(); i++) {
				Symbol act = obs.get(i);
				Observation ante = obs.ante(i);
				Observation next = obs.post(i);
				Observation post = postconditions2.get(act);
				for(Symbol eff : post.getPredicatesSymbols()) {
					switch(post.getValue(eff)) {
					case TRUE:
						switch(next.getValue(eff)) {
						case TRUE:
							if(ante.getValue(eff)!=post.getValue(eff) ) {
								fit++;
							} else {
								err++;
							}
							break;
						case FALSE:
							err++;
							break;
						default:
							break;
						}
						break;
					case FALSE:
						switch(next.getValue(eff)) {
						case FALSE:
							if(ante.getValue(eff)!=post.getValue(eff)) {
								fit++;
							}else {
								err++;
							}
							break;
						case TRUE:
							err++;
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
				}
			}
		}
		data.getExamples().forEach((plan) ->{
			ObservedExample obs = (ObservedExample) plan;
			for(int i = 0; i< obs.size(); i++) {
				Symbol act = obs.get(i);
				Observation ante = obs.ante(i);
				Observation next = obs.post(i);
				Observation post = postconditions2.get(act);
				p.setX(post.countEqual(next) + p.getX() - post.countEqual(ante.common(next)));
				p.setY(post.countDifferent(next) + p.getY());
			}
		});
		return fit-err;
	}
	
	/**
	 * Generate all neighbors ie all consistent Domain with only one
	 * difference with Domain
	 * @return A set of Domain
	 */
	/*public List<Domain> neighbors(Domain domain) {
		List<Domain> res = new ArrayList<>();
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			domain.getDomain().entrySet()) {
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getX().getValue(s)) {
					case TRUE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(true, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			for(Symbol s : entry.getValue().getY().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getY().getValue(s)) {
					case TRUE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case FALSE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(false, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return res;
	}*/
	
	/*public List<Domain> neighbors(Domain domain, Symbol op) {
		List<Domain> res = new ArrayList<>();
		for(Symbol s : domain.getDomain().get(op).getX().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(domain.getDomain().get(op).getX().getValue(s)) {
					case TRUE:
						clone1.mutation(true, op, s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(true, op, s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(true, op, s, Observation.Value.FALSE);
						clone2.mutation(true, op, s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			for(Symbol s : domain.getDomain().get(op).getY().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(domain.getDomain().get(op).getY().getValue(s)) {
					case TRUE:
						clone1.mutation(false, op, s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case FALSE:
						clone1.mutation(false, op, s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(false, op, s, Observation.Value.FALSE);
						clone2.mutation(false, op, s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		return res;
	}*/
	
	/**
	 * Generate all neighbors ie all consistent Domain with only one
	 * difference with Domain
	 * @return A set of Domain
	 */
	/*public Map<Domain, Movement> neighborsMovements(Domain domain) {
		Map<Domain, Movement> res = new LinkedHashMap<>();
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			domain.getDomain().entrySet()) {
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getX().getValue(s)) {
					case TRUE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.put(clone1, new Movement(entry.getKey(), s, true, Observation.Value.ANY, Observation.Value.TRUE));
						} 
						break;
					case FALSE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.put(clone1, new Movement(entry.getKey(), s, true, Observation.Value.ANY, Observation.Value.FALSE));
						}
						break;
					case ANY:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(true, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.put(clone1, new Movement(entry.getKey(), s, true, Observation.Value.FALSE, Observation.Value.ANY));
						}
						if(clone2.isConsistant()) {
							res.put(clone2, new Movement(entry.getKey(), s, true, Observation.Value.TRUE, Observation.Value.ANY));
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			for(Symbol s : entry.getValue().getY().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getY().getValue(s)) {
					case TRUE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.put(clone1, new Movement(entry.getKey(), s, false, Observation.Value.ANY, Observation.Value.TRUE));
						}
						break;
					case FALSE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.put(clone1, new Movement(entry.getKey(), s, false, Observation.Value.ANY, Observation.Value.FALSE));
						}
						break;
					case ANY:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(false, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.put(clone1, new Movement(entry.getKey(), s, false, Observation.Value.FALSE, Observation.Value.ANY));
						}
						if(clone2.isConsistant()) {
							res.put(clone2, new Movement(entry.getKey(), s, false, Observation.Value.TRUE, Observation.Value.ANY));
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return res;
	}*/
	
	/**
	 * Generate all neighbors ie all consistent Domain with only one
	 * difference with Domain
	 * @return A set of Domain
	 */
	/*public List<Movement> neighborsM(Domain domain) {
		List<Movement> res = new ArrayList<>();
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			domain.getDomain().entrySet()) {
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				switch(entry.getValue().getX().getValue(s)) {
				case TRUE:
					res.add(new Movement(entry.getKey(), s, true, Observation.Value.ANY, Observation.Value.TRUE));
					break;
				case FALSE:
					res.add(new Movement(entry.getKey(), s, true, Observation.Value.ANY, Observation.Value.FALSE));
					break;
				case ANY:
					res.add(new Movement(entry.getKey(), s, true, Observation.Value.FALSE, Observation.Value.ANY));
					res.add(new Movement(entry.getKey(), s, true, Observation.Value.TRUE, Observation.Value.ANY));
					break;
				default:
					break;
				}
			}
			for(Symbol s : entry.getValue().getY().getPredicatesSymbols()) {
				switch(entry.getValue().getY().getValue(s)) {
				case TRUE:
					res.add(new Movement(entry.getKey(), s, false, Observation.Value.ANY, Observation.Value.TRUE));
					break;
				case FALSE:
					res.add(new Movement(entry.getKey(), s, false, Observation.Value.ANY, Observation.Value.FALSE));
					break;
				case ANY:
					res.add(new Movement(entry.getKey(), s, false, Observation.Value.FALSE, Observation.Value.ANY));
					res.add(new Movement(entry.getKey(), s, false, Observation.Value.TRUE, Observation.Value.ANY));
					break;
				default:
					break;
				}
			}
		}
		return res;
	}*/
	
 	/**
 	 * Generate all neighbors i.e. all consistant Domain with only one difference with Domain
	 * @return A set of Domain
	 */
	public List<Domain> neighbors(Domain d) {
		List<Domain> neighbors = new ArrayList<>();
		List<Symbol> operators = d.getOperators();
		for(Symbol op : operators) {
			//Change Precondition
			for(Symbol p : d.getPrecond(op).getPredicatesSymbols()) {
				Domain clone = null;
				switch (d.getPrecond(op).getValue(p)) {
					case TRUE:
						//if(d.getPostcond(op).getValue(p) != Observation.Value.FALSE) {
							clone = d.clone();
							clone.mutation(true, op, p, Observation.Value.ANY);
							if(clone.isConsistant()) {
								neighbors.add(clone);
							}
						//}
						break;
					case FALSE:
						clone = d.clone();
						clone.mutation(true, op, p, Observation.Value.ANY);
						if(clone.isConsistant()) {
							neighbors.add(clone);
						}
						break;
					case ANY:
						//if(d.getPostcond(op).getValue(p) != Observation.Value.TRUE) {
							clone = d.clone();
							clone.mutation(true, op, p, Observation.Value.TRUE);
							if(clone.isConsistant()) {
							neighbors.add(clone);
							}
						//}
						//if(d.getPostcond(op).getValue(p) != Observation.Value.FALSE) {
							clone = d.clone();
							clone.mutation(true, op, p, Observation.Value.FALSE);
							if(clone.isConsistant()) {
								neighbors.add(clone);
							}
						//}
						break;
					default:
						break;
				}
			}
			for(Symbol p : d.getPostcond(op).getPredicatesSymbols()) {
				Domain clone = null;
				switch (d.getPostcond(op).getValue(p)) {
					case TRUE:
					case FALSE:
						clone = d.clone();
						clone.mutation(false, op, p, Observation.Value.ANY);
						if(clone.isConsistant()) {
							neighbors.add(clone);
						}
						break;
					case ANY:
						//if(d.getPrecond(op).getValue(p) != Observation.Value.TRUE) {
							clone = d.clone();
							clone.mutation(false, op, p, Observation.Value.TRUE);
							if(clone.isConsistant()) {
								neighbors.add(clone);
							}
						//}
						//if(d.getPrecond(op).getValue(p) == Observation.Value.TRUE) {
							clone = d.clone();
							clone.mutation(false, op, p, Observation.Value.FALSE);
							if(clone.isConsistant()) {
								neighbors.add(clone);
							}
						//}
						break;
					default:
						break;
				}
			}
		}
		return neighbors;
	}
	
 	/**
	 * Generate all neighbors ie all consistent Domain with only one
	 * difference with Domain
	 * @return A set of Domain
	 */
	public Map<Domain, Movement> neighborsMovements(Domain d) {
		Map<Domain, Movement> res = new HashMap<>();
		List<Symbol> operators = d.getOperators();
		for(Symbol op : operators) {
			//Change Precondition
			for(Symbol p : d.getPrecond(op).getPredicatesSymbols()) {
				Domain clone = null;
				switch (d.getPrecond(op).getValue(p)) {
					case TRUE:
						//if(d.getPostcond(op).getValue(p) != Observation.Value.FALSE) {
							clone = d.clone();
							clone.mutation(true, op, p, Observation.Value.ANY);
							if(clone.isConsistant()) {
								res.put(clone,new Movement(
										op,
										p,
										true,
										Observation.Value.ANY,
										Observation.Value.TRUE));
							}
						//}
						break;
					case FALSE:
						clone = d.clone();
						clone.mutation(true, op, p, Observation.Value.ANY);
						if(clone.isConsistant()) {
							res.put(clone,new Movement(
									op,
									p,
									true,
									Observation.Value.ANY,
									Observation.Value.FALSE));
						}
						break;
					case ANY:
						//if(d.getPostcond(op).getValue(p) != Observation.Value.TRUE) {
							clone = d.clone();
							clone.mutation(true, op, p, Observation.Value.TRUE);
							if(clone.isConsistant()) {
								res.put(clone,new Movement(
										op,
										p,
										true,
										Observation.Value.TRUE,
										Observation.Value.ANY));
							}
						//}
						//if(d.getPostcond(op).getValue(p) != Observation.Value.FALSE) {
							clone = d.clone();
							clone.mutation(true, op, p, Observation.Value.FALSE);
							if(clone.isConsistant()) {
								res.put(clone,new Movement(
										op,
										p,
										true,
										Observation.Value.FALSE,
										Observation.Value.ANY));
							}
						//}
						break;
					default:
						break;
				}
			}
			for(Symbol p : d.getPostcond(op).getPredicatesSymbols()) {
				Domain clone = null;
				switch (d.getPostcond(op).getValue(p)) {
					case TRUE:
						clone = d.clone();
						clone.mutation(false, op, p, Observation.Value.ANY);
						if(clone.isConsistant()) {
							res.put(clone,new Movement(
									op,
									p,
									false,
									Observation.Value.ANY,
									Observation.Value.TRUE));
						}
						break;
					case FALSE:
						clone = d.clone();
						clone.mutation(false, op, p, Observation.Value.ANY);
						if(clone.isConsistant()) {
							res.put(clone,new Movement(
									op,
									p,
									false,
									Observation.Value.ANY,
									Observation.Value.FALSE));
						}
						break;
					case ANY:
						//if(d.getPrecond(op).getValue(p) != Observation.Value.TRUE) {
							clone = d.clone();
							clone.mutation(false, op, p, Observation.Value.TRUE);
							if(clone.isConsistant()) {
								res.put(clone,new Movement(
										op,
										p,
										false,
										Observation.Value.TRUE,
										Observation.Value.ANY));
							}
						//}
						//if(d.getPrecond(op).getValue(p) == Observation.Value.TRUE) {
							clone = d.clone();
							clone.mutation(false, op, p, Observation.Value.FALSE);
							if(clone.isConsistant()) {
								res.put(clone,new Movement(
										op,
										p,
										false,
										Observation.Value.FALSE,
										Observation.Value.ANY));
							}
						//}
						break;
					default:
						break;
				}
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param indiv
	 * @param pos
	 * @param neg
	 * @param compressed
	 * @param is
	 */
	public void printFitnessComponents(
			Domain indiv, Sample pos, Sample neg, 
			List<CompressedNegativeExample> compressed, String is) {
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		float fitPos = this.fitPositive(indiv, pos, initial);
		float fitNeg = this.fitNegative(indiv, neg, compressed, initial);
		float fitPrec = this.fitPrecondition(indiv, pos);
		float fitPost = this.fitPostcondition(indiv, pos);
		System.out.println(fitPos+" "+fitNeg+" "+fitPrec+" "+fitPost);
	}
	
	public void printFitnessComponents(
			Domain indiv, Sample pos, Sample neg, 
			List<CompressedNegativeExample> compressed, Observation initial) {
		float fitPos = this.fitPositive(indiv, pos, initial);
		float fitNeg = this.fitNegative(indiv, neg, compressed, initial);
		float fitPrec = this.fitPrecondition(indiv, pos);
		float fitPost = this.fitPostcondition(indiv, pos);
		System.out.println("Fit positive : "+fitPos);
		System.out.println("Fit negative : "+fitNeg);
		System.out.println("Fit precondition : "+fitPrec);
		System.out.println("Fit postcondition : "+fitPost);
		System.out.println("Fitness : "+(fitPos+fitNeg+fitPrec+fitPost));
	}
	
	/**
	 * 
	 * @param indiv
	 * @param pos
	 * @param neg
	 * @param compressed
	 * @param is
	 * @param size
	 * @param epoch
	 * @param tabou
	 * @return
	 */
	public Domain tabu(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int size, 
			int epoch,
			List<Domain> tabou) {
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		DomainsQueue queue = new DomainsQueue(size, tabou);
		float fit = fitness(indiv, pos, neg, compressed, is);
		queue.add(indiv, fit);
		tabou.remove(indiv);
		int i =0;
		for(i =0; i < epoch; i ++) {
			try {
				Domain choosen = queue.next();
				DomainSimulator tabu = new DomainSimulator(choosen, predicates, actions, initial);
				List<Domain> neigh = this.neighbors(choosen);
				Map<Domain, Movement> neighBis= this.neighborsMovements(choosen);
				for(Domain ind : neigh) {
					if(! queue.inTabou(ind) && this.pairewiseConsistance(ind, pos)) {
						Movement m = neighBis.get(ind);
						tabu.makeMovement(m);
						fit = tabu.fitness(pos, compressed);
						tabu.getDomain();
						tabu.backtrack(m);
						queue.add(ind, fit);
					}
				}
			}catch(IllegalArgumentException e){
				break;
			}
		}
		try {
			queue.next();
		}catch(IllegalArgumentException e){
			
		}
		Domain res = tabou.get(0);
		float fitRef = fitness(res, pos, neg, compressed, is);
		for(Domain ind : tabou) {
			fit = fitness(ind, pos, neg, compressed, is);
			if(fit > fitRef) {
				res = ind;
				fitRef = fit;
			}
		}
		return res;
	}
	
	/*public Domain tabu_movements(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int size, 
			int epoch) {
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		float fit = fitness(indiv, pos, neg, compressed, is);
		SearchStorage queue = new SearchStorage(size, fit);
		int i =0;
		Domain current = indiv.clone();
		for(i =0; i < epoch; i ++) {
			try {
				System.out.println((i+1)+"/"+epoch);
				List<Movement> choosen = queue.next();
				current.makeMovement(choosen);
				DomainSimulator tabu = new DomainSimulator(current, predicates, actions, initial);
				List<Movement> neigh= this.neighborsM(current);
				for(Movement m : neigh) {
					if(queue.contains(choosen, m)) {
						continue;
					}
					current.makeMovement(m);
					if(current.isConsistant()) {
						tabu.makeMovement(m);
						fit = tabu.fitness(pos, compressed);
						//tabu.getDomain();
						tabu.backtrack(m);
						queue.add(choosen, m, fit);
					}
					current.backtrack(m);
				}
				current.backtrack(choosen);
			}catch(IllegalArgumentException e){
				//System.out.println("coucou");
				//e.printStackTrace();
				break;
			}
		}
		
		return queue.getMaxDomain(indiv);
	}*/
	
	/*public Domain tabu(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int size, 
			int epoch,
			List<Domain> tabou,
			Symbol op) {
		DomainsQueue queue = new DomainsQueue(size, tabou);
		float fit = fitness(indiv, pos, neg, compressed, is);
		queue.add(indiv, fit);
		tabou.remove(indiv);
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		int i =0;
		for(i =0; i < epoch; i ++) {
			try {
				Domain choosen = queue.next();
				DomainSimulator tabu = new DomainSimulator(choosen, predicates, actions, initial);
				List<Domain> neigh = this.neighbors(choosen, op);
				Map<Domain, Movement> neighBis= this.neighborsMovements(choosen);
				for(Domain ind : neigh) {
					if(! queue.inTabou(ind)) {
						tabu = new DomainSimulator(ind, predicates, actions, initial);
						Movement m = neighBis.get(ind);
						tabu.makeMovement(m);
						fit = tabu.fitness(pos, compressed);
						tabu.backtrack(m);
						queue.add(ind, fit);
					}
				}
			}catch(IllegalArgumentException e){
				break;
			}
		}
		try {
			queue.next();
		}catch(IllegalArgumentException e){
			
		}
		Domain res = tabou.get(0);
		float fitRef = fitness(res, pos, neg, compressed, is);
		for(Domain ind : tabou) {
			fit = fitness(ind, pos, neg, compressed, is);
			if(fit > fitRef) {
				res = ind;
				fitRef = fit;
			}
		}

		return res;
	}*/
	
	/*public Domain tabuTemporal(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int size, 
			int epoch,
			List<Domain> tabou,
			Map<Symbol, Float> duration,
			List<Symbol> tempActions) {
		DomainsQueue queue = new DomainsQueue(size, tabou);
		float fit = fitness(indiv, pos, neg, compressed, is);
		queue.add(indiv, fit);
		tabou.remove(indiv);
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		int i =0;
		for(i =0; i < epoch; i ++) {
			try {
				Domain choosen = queue.next();
				DomainSimulator tabu = new DomainSimulator(choosen, predicates, actions, initial);
				List<Domain> neigh = this.neighbors(choosen);
				Map<Domain, Movement> neighBis= this.neighborsMovements(choosen);
				//System.out.println(i+" "+neigh.size()+" "+neighBis.size());
				for(Domain ind : neigh) {
					ind = TemporalDomain.convert2(
							ind.clone(), duration, Argument.isTwoOp(), 
							predicates, actions, tempActions);
					if(! queue.inTabou(ind)) {
						tabu = new DomainSimulator(ind, predicates, actions, initial);
						//Movement m = neighBis.get(ind);
						//tabu.makeMovement(m);
						fit = tabu.fitness(pos, compressed);
						//tabu.backtrack(m);
						queue.add(ind, fit);
					}
				}
			}catch(IllegalArgumentException e){
				break;
			}
		}
		try {
			queue.next();
		}catch(IllegalArgumentException e){
			
		}
		Domain res = tabou.get(0);
		float fitRef = fitness(res, pos, neg, compressed, is);
		for(Domain ind : tabou) {
			fit = fitness(ind, pos, neg, compressed, is);
			if(fit > fitRef) {
				res = ind;
				fitRef = fit;
			}
		}

		return res;
	}*/
	
	/*public Domain tabu(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int epoch) {
		float fit = fitness(indiv, pos, neg, compressed, is);
		
		float previous=fit, current=fit;
		do {
		//	System.out.println(indiv.isConsistant()+" "+(indiv instanceof Domain));
			previous=current;
			//if(current > previous) {
				for(Symbol op : indiv.getDomain().keySet()) {
					indiv = this.tabu(indiv, pos, neg, compressed, is, 3, epoch, new ArrayList<>(), op);
					current = fitness(indiv, pos, neg, compressed, is);
				}
			//}
		} while(previous < current);
		indiv = this.tabu(indiv, pos, neg, compressed, is, 3, epoch, new ArrayList<>());
		return indiv;
	}*/
	
	/**
	 * 
	 * @param d
	 * @param I
	 * @return
	 */
	public boolean pairewiseConsistance(Domain d, Sample I) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> p = d.decode();
		Map<Symbol, Observation> preconditions = p.getX();
		Map<Symbol, Observation> postconditions = p.getY();
		Map<Symbol, Observation> preconditions2 = new HashMap<>();
		Map<Symbol, Observation> postconditions2 = new HashMap<>();
		for(Symbol act : this.actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			preconditions2.put(act, prec);
			postconditions2.put(act, post);
		}
		Map<Symbol, List<Symbol>> pair = I.getPair();
		for(Symbol a1 : pair.keySet()) {
			Observation post = postconditions2.get(a1);
			Observation precP = preconditions2.get(a1);
			for(Symbol a2 : pair.get(a1)) {
				Observation prec = preconditions2.get(a2);
				for(Symbol pred : prec.getPredicatesSymbols()) {
					switch(prec.getValue(pred)) {
					case TRUE:
						switch(post.getValue(pred)) {
						case FALSE:
							return false;
						case ANY:
						case MISSED:
							switch(precP.getValue(pred)) {
							case FALSE:
								return false;
							default:
								break;
							}
							break;
						default:
							break;
						
						}
						break;
					case FALSE:
						switch(post.getValue(pred)) {
						case TRUE:
							return false;
						case ANY:
						case MISSED:
							switch(precP.getValue(pred)) {
							case TRUE:
								return false;
							default:
								break;
							}
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return true;
	}
}
