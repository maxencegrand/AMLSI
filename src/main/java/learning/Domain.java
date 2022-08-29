package learning;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import fsm.Symbol;
import fsm.Trace;
import simulator.Movement;
import fsm.Bloc;
import fsm.Example;
import fsm.DFA;
import fsm.Pair;
import fsm.Sample;
import java.util.Objects;
import exception.BlocException;

/**
 * This class represents a candidate domain
 * @author Maxence Grand
 */
public class Domain {
	/**
	 * Map each action with observations representing preconditions and effects
	 */
	private Map<Symbol, Pair<Observation, Observation>> domain;
	/**
	 * The list of predicates
	 */
	private List<Symbol> P;
	/**
	 * The list of operators
	 */
	private List<Symbol> A;

	/**
	 * Constructs an Domain
	 */
	public Domain(){
		domain = new HashMap<>();
		P = new ArrayList<>();
		A = new ArrayList<>();
	}

	/**
	 * Constructs an Domain from a set of predicates
	 * @param P A set of predicates
	 */
	public Domain(List<? extends Symbol> P) {
		this();
		P.stream().filter((p) -> (! this.P.contains(p))).forEachOrdered((p) -> {
			this.P.add(p);
		});
	}

	/**
	 * Constructs an Domain from a set of predicates and a given mapping
	 * between actions and chromosomes
	 * @param P A set of predicate
	 * @param domain A mapping
	 */
	public Domain(List<? extends Symbol> P,
			Map<Symbol, Pair<Observation, Observation>> domain) {
		this(P);
		domain.entrySet().forEach((entry) -> {
			this.domain.put(entry.getKey(), entry.getValue().clone());
		});
	}

	/**
	 *
	 * Constructs an Domain from a set of predicates and given action's
	 * preconditions and effects
	 *
	 * @param P A set of predicate
	 * @param actions A set of actions
	 * @param preconditions Action's preconditions
	 * @param postconditions Action's effects
	 */
	public Domain(List<? extends Symbol> P,
			List<? extends Symbol> actions,
			Map<Symbol, Observation> preconditions,
			Map<Symbol, Observation> postconditions) {
		this(P);
		A = new ArrayList<>();
		List<Symbol> actions2 = new ArrayList<>();
		for(Symbol act : actions){
			A.add(act);
			if(actions2.contains(act.generalize())) {
				continue;
			}
			actions2.add(act.generalize());
			List<Symbol> allPredicates = new ArrayList<>();
			for(Symbol p : P) {
				if(act.compatible(p) && ! allPredicates.contains(p.generalize(act.mapping()))) {
					allPredicates.add(p.generalize(act.mapping()));
				}
			}
			act = act.generalize();
			Observation prec = Observation.getAnyObservation(allPredicates);

			if(preconditions.containsKey(act)) {
				for(Symbol pred : preconditions.get(act).getPredicatesSymbols()) {
					if(allPredicates.contains(pred)) {
						prec.addObservation(pred,preconditions.get(act).getValue(pred));
					}
				}
			}
			//Postcondition
			Observation post = Observation.getAnyObservation(allPredicates);
			if(postconditions.containsKey(act)) {
				for(Symbol pred : postconditions.get(act).getPredicatesSymbols()) {
					if(allPredicates.contains(pred)) {
						post.addObservation(pred,postconditions.get(act).getValue(pred));
					}
				}
			}
			this.domain.put(act, new Pair<>(prec,post));
		}
	}

	/**
	 *
	 * Constructs an Domain from a set of predicates and given action's
	 * preconditions and effects
	 *
	 * @param P A set of predicate
	 * @param actions A set of actions
	 * @param prePos Action's positive preconditions
	 * @param preNeg Action's negative preconditions
	 * @param addList Action's positive effects
	 * @param delList Action's negative effects
	 */
	public Domain(
			List<? extends Symbol> P,
			List<? extends Symbol> actions,
			Map<Symbol, List<String>> prePos,
			Map<Symbol, List<String>> preNeg,
			Map<Symbol, List<String>> addList,
			Map<Symbol, List<String>> delList
			) {
		this(P);
		A = new ArrayList<>();
		List<Symbol> actions2 = new ArrayList<>();

		for(Symbol act : actions){
			A.add(act);
			if(actions2.contains(act.generalize())) {
				continue;
			}
			actions2.add(act.generalize());
			List<Symbol> allPredicates = new ArrayList<>();
			for(Symbol p : P) {
				if(act.compatible(p) && ! allPredicates.contains(
                                                p.generalize(act.mapping()))) {
					allPredicates.add(p.generalize(act.mapping()));
				}
			}
			act = act.generalize();
			//Precondtions
			Observation prec = Observation.getAnyObservation(allPredicates);
			if(prePos.containsKey(act)) {
				for(String str : prePos.get(act)) {
					Symbol pred = Domain.getSymbolFromDeictic(
							allPredicates, str);
					if(allPredicates.contains(pred)) {
						prec.addTrueObservation(pred);
					}
				}
			}
			if(preNeg.containsKey(act)) {
				for(String str : preNeg.get(act)) {
					Symbol pred = Domain.getSymbolFromDeictic(
							allPredicates, str);
					if(allPredicates.contains(pred)) {
						prec.addFalseObservation(pred);
					}
				}
			}
			//Postcondition
			Observation post = Observation.getAnyObservation(allPredicates);
			if(addList.containsKey(act)) {
				for(String str : addList.get(act)) {
					Symbol pred = Domain.getSymbolFromDeictic(
							allPredicates, str);
					if(allPredicates.contains(pred)) {
						post.addTrueObservation(pred);
					}
				}
			}
			if(delList.containsKey(act)) {
				for(String str : delList.get(act)) {
					Symbol pred = Domain.getSymbolFromDeictic(
							allPredicates, str);
					if(allPredicates.contains(pred)) {
						post.addFalseObservation(pred);
					}
				}
			}
			this.domain.put(act, new Pair<>(prec,post));
		}
	}
	
	/**
	 * Clone an Domain
	 * @return an Domain
	 */
	@Override
	public Domain clone() {
		/*Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> tmp =
				decode();
		return new Domain(this.P, this.A, tmp.getX(), tmp.getY());*/
		Domain d = new Domain();
		d.A.addAll(this.A);
		d.P.addAll(this.P);
		this.domain.forEach((k,v) -> d.domain.put(k, new Pair<>(v.getX().clone(), v.getY().clone())));
		return d;
	}

	
	
	/**
	 * Setter domain
	 * @param domain the domain to set
	 */
	public void setDomain(Map<Symbol, Pair<Observation, Observation>> domain) {
		this.domain = domain;
	}

	/**
	 * Getter of domain
	 * @return the domain
	 */
	public Map<Symbol, Pair<Observation, Observation>> getDomain() {
		return domain;
	}

	
	/**
	 * Getter of p
	 * @return the p
	 */
	public List<Symbol> getP() {
		return P;
	}

	/**
	 * Getter of a
	 * @return the a
	 */
	public List<Symbol> getA() {
		return A;
	}

	/**
	 * 
	 * @param P
	 */
	public void setP(List<Symbol> P) {
		this.P=P;
	}
	
	/**
	 * 
	 * @param P
	 */
	public void setA(List<Symbol> A) {
		this.A=A;
	}
	
	/**
	 * Translate Domains into action's preconditions and effects
	 * @return Action's preconditions and effects
	 */
	public Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> decode() {
		Map<Symbol, Observation> preconditions = new HashMap<>();
		Map<Symbol, Observation> postconditions = new HashMap<>();
		this.domain.entrySet().forEach((entry) -> {
			Symbol a = entry.getKey();
			preconditions.put(a, entry.getValue().getX());
			postconditions.put(a, entry.getValue().getY());
		});
		return new Pair<>(preconditions, postconditions);
	}

	/**
	 * Get the preconditions of a given action
	 * @param a An action
	 * @return Action a's preconditions
	 */
	public Observation getPrecond(Symbol a) {
		return this.domain.get(a).getX();
	}

	/**
	 * Get the effects of a given action
	 * @param a An action
	 * @return Action a's effects
	 */
	public Observation getPostcond(Symbol a) {
		return this.domain.get(a).getY();
	}

	/**
	 * The hashcode
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		hash = Objects.hashCode(toString());
		return hash;
	}

	/**
	 * Equality test
	 * @param o An Domain
	 * @return True if the two Domains are equal
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Domain) {
			Domain other = (Domain) o;
			boolean b = true;
			for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
				this.domain.entrySet()) {
				if(other.domain.keySet().contains(entry.getKey())) {
					b &= entry.getValue().getX().equals
							(other.domain.get(entry.getKey()).getX());
					b &= entry.getValue().getY().equals
							(other.domain.get(entry.getKey()).getY());
					if(!b) {
						return b;
					}
				} else {
					return false;
				}
			}
			return b;
		} else {
			return false;
		}
	}

	/**
	 * Check the consistence of the Domain with the ARMS' assumptions
	 * @return True e  Domain is consistant
	 */
	public boolean isConsistant() {
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			this.domain.entrySet()) {
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				switch(entry.getValue().getX().getValue(s)) {
	            case TRUE:
	                switch(entry.getValue().getY().getValue(s)) {
	                case TRUE:
	                    return false;
	                case FALSE:
	                    break;
	                case ANY:
	                    break;
	                default:
	                    break;
	                }
	                break;
	            case FALSE:
	                switch(entry.getValue().getY().getValue(s)) {
	                case TRUE:
	                    break;
	                case FALSE:
	                    return false;
	                case ANY:
	                    break;
	                default:
	                    break;
	                }
	                break;
	            default:
	                switch(entry.getValue().getY().getValue(s)) {
	                case TRUE:
	                    break;
	                case FALSE:
	                    return false;
	                case ANY:
	                    break;
	                default:
	                    break;
	                }
	                break;
	            }
			}
		}
		return true;
	}


	/**
	 * String representation of an Domain
	 * @return A string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("----------------------------------------------------\n");
		this.domain.forEach((op, prop) -> {
			builder.append("******Operator "+op+"******\n");
			builder.append("Preconditions\n");
			prop.getX().getPredicates().forEach((p,v) -> {
				switch(v) {
				case ANY:
					break;
				case FALSE:
					builder.append("\t not "+p+"\n");
					break;
				case MISSED:
					break;
				case TRUE:
					builder.append("\t"+p+"\n");
					break;
				default:
					break;
				
				}
			});
			builder.append("Effects\n");
			prop.getY().getPredicates().forEach((p,v) -> {
				switch(v) {
				case ANY:
					break;
				case FALSE:
					builder.append("\t not "+p+"\n");
					break;
				case MISSED:
					break;
				case TRUE:
					builder.append("\t"+p+"\n");
					break;
				default:
					break;
				
				}
			});
		});
		return builder.toString();
	}

	/**
	 * Mutate the candidate domain
	 * @param prec Boolean : if true then the precondition mutates
	 * @param operator The operator to mutate
	 * @param predicate The predicate to change
	 * @param v The new value
	 */
	public void mutation(
			boolean prec,
			Symbol operator,
			Symbol predicate,
			Observation.Value v) {
		if(prec) {
			this.domain.get(operator).getX().addObservation(predicate, v);
		} else {
			this.domain.get(operator).getY().addObservation(predicate, v);
		}
	}

	/**
	 * Constructs a candidate domain from the deictic representation
	 * @see <a href="https://arxiv.org/abs/1210.4889"> lsonio</a>
	 * @param allPredicates All predicates
	 * @param str The string vector
	 * @return A candidate domain
	 */
	private static Symbol getSymbolFromDeictic(
			List<Symbol> allPredicates, String str) {
		if(str.contains(":")) {
			String[] tab = str.split(":");
			for(Symbol s : allPredicates) {
				if(tab[0].equals(s.getName())) {
					boolean b = true;
					for(int i = 1; i < tab.length-1; i++) {
						b &= tab[i].equals(
								s.getListParameters().get(i));
					}
					b &= tab[tab.length-1].equals(s.getListParameters().get(0));
					if(b) {
						return s;
					}
				}
			}
		} else {
			for(Symbol s : allPredicates) {
				if(str.equals(s.getName())) {
					return s;
				}
			}
		}
		return null;
	}

	/**
	 * Backtrack preconditions
	 * @param initial The isitial state
	 * @param pos Data
	 */
	public void backtrackPrecondition(Observation initial, Sample pos) {
		pos.getExamples().forEach(ex -> {
			this.backtrackPrecondition(initial, (Example)ex);
		});
	}

    /**
	 * Backtrack preconditions
	 * @param initial The isitial state
	 * @param ex An example
	 */
	public void backtrackPrecondition(Observation initial, Example ex) {
		List<Observation> newObs = new ArrayList<>();
		newObs.add(initial.clone());
		for(int i =0; i < ex.size(); i++) {
			Observation o = newObs.get(i).clone();
			Symbol act = ex.get(i);
			Symbol op = act.generalize();
			Observation prec = this.domain.get(op).getX().instanciate(
					utils.Utils.reverseParamMap(act.mapping()));

			//Check preconditions
			List<Symbol> preconditionToRemove = new ArrayList<>();
			for(Map.Entry<Symbol, Observation.Value> entry :
				prec.getPredicates().entrySet()) {
				Symbol p = entry.getKey();
				Observation.Value v = entry.getValue();
				switch(v) {
				case TRUE:
					switch(o.getValue(p)) {
					case FALSE:
						preconditionToRemove.add(p.generalize(act.mapping()));
						break;
					case ANY:
						preconditionToRemove.add(p.generalize(act.mapping()));
						break;
					case MISSED:
						preconditionToRemove.add(p.generalize(act.mapping()));
						break;
					default:
						break;
					}
					break;
				case FALSE:
					switch(o.getValue(p)) {
					case TRUE:
						preconditionToRemove.add(p.generalize(act.mapping()));
						break;
					case ANY:
						preconditionToRemove.add(p.generalize(act.mapping()));
						break;
					case MISSED:
						preconditionToRemove.add(p.generalize(act.mapping()));
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			};
			//Remove unsatisfied preconditions
			preconditionToRemove.forEach(p -> {
				this.mutation(true, op, p, Observation.Value.ANY);
			});
			//Add effects
			Observation eff = this.domain.get(op).getY().instanciate(
					utils.Utils.reverseParamMap(act.mapping()));
			o = o.addEffect(eff);
			newObs.add(o);
		}
	}

	/***
	 * Backtrack negative effects
	 */
	public void backtrackNegativeEffects() {
		for(Symbol operator : domain.keySet()) {
			for(Symbol p : this.domain.get(operator).getX().getPredicatesSymbols()) {
				switch(this.domain.get(operator).getY().getValue(p) ) {
				case TRUE:
					switch(this.domain.get(operator).getX().getValue(p)) {
					case FALSE:
						break;
					case ANY:
						break;
					case MISSED:
						break;
					default:
						this.mutation(false, operator, p, Observation.Value.ANY);
						break;
					}
					break;
				case FALSE:
					switch(this.domain.get(operator).getX().getValue(p)) {
					case TRUE:
						break;
					default:
						this.mutation(false, operator, p, Observation.Value.ANY);
						break;
					}
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Remove effects uncompat(ible with the automaton given as input
	 * @param A The automaton
	 * @param ante The mapping ante
	 * @param post The mapping post
	 */
	public void backtrackUncompatibleEffects(
			DFA A, Mapping ante, Mapping post) {
		Map<Symbol, List<Symbol>> observedEffects = new HashMap<>();
		for(Bloc b : A.getPartition().getBlocs()) {
			int q = b.min();
			try {
				List<Symbol> out = A.getPossibleBlocTransition(q);
				for(Symbol a : out) {
					int qPrime = A.getBlocTransition(b,a).min();
					//k : predicate
					//v : value in state qPrime (post)
					post.getStates(qPrime, a).getPredicates().forEach((k,v) -> {
						if(a.compatible(k)) {
							Symbol op = a.generalize();
							Symbol p = k.generalize(a.mapping());
							Observation ch = this.domain.get(op).getY();
							switch(ante.getStates(q, a).getValue(k)) {
							case TRUE:
								switch(v) {
								case FALSE:
									switch(ch.getValue(p)) {
									case FALSE:
										if(!observedEffects.containsKey(op)) {
											observedEffects.put(
                                                        op, new ArrayList<>());
										}
										if(!observedEffects.get(op).
                                                                contains(p)) {
											observedEffects.get(op).add(p);
										}
										break;
									default:
										break;
									};
									break;
								default:
									break;
								}
								break;
							case FALSE:
								switch(v) {
								case TRUE:
									switch(ch.getValue(p)) {
									case TRUE:
										if(!observedEffects.containsKey(op)) {
											observedEffects.put(
                                                        op, new ArrayList<>());
										}
										if(!observedEffects.get(op).contains(p)) {
											observedEffects.get(op).add(p);
										}
										break;
									default:
										break;
									};
									break;
								default:
									break;
								}
								break;
							default:
								break;
							}

						}
					});
				}
			} catch (BlocException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		observedEffects.forEach((op,l) -> {
			Observation ch = this.domain.get(op).getY();
			ch.removeAllExcept(l);
		});
	}

	/**
	 *
 	 * Remove preconditions uncompat(ible with the automaton given as input
 	 * @param A The automaton
 	 * @param ante The mapping ante
 	 * @param post The mapping post
	 */
	public void backtrackUncompatiblePreconditions(
			DFA A, Mapping ante, Mapping post) {
		Map<Symbol, List<Symbol>> observedEffects = new HashMap<>();
		for(Bloc b : A.getPartition().getBlocs()) {
			int q = b.min();
			try {
				List<Symbol> out = A.getPossibleBlocTransition(q);
				for(Symbol a : out) {
					ante.getStates(q, a).getPredicates().forEach((k,v) -> {
						if(a.compatible(k)) {
							Symbol op = a.generalize();
							Symbol p = k.generalize(a.mapping());
							Observation ch = this.domain.get(op).getX();
							switch(v) {
							case FALSE:
								switch(ch.getValue(p)) {
								case FALSE:
									if(!observedEffects.containsKey(op)) {
										observedEffects.put(op,
                                                            new ArrayList<>());
									}
									if(!observedEffects.get(op).contains(p)) {
										observedEffects.get(op).add(p);
									}
									break;
								default:
									break;
								};
								break;
							case TRUE:
								switch(ch.getValue(p)) {
								case TRUE:
									if(!observedEffects.containsKey(op)) {
										observedEffects.put(op,
                                                            new ArrayList<>());
									}
									if(!observedEffects.get(op).contains(p)) {
										observedEffects.get(op).add(p);
									}
									break;
								default:
									break;
								};
								break;
							default:
								break;
							}
						}
					});
				}
			} catch (BlocException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		observedEffects.forEach((op,l) -> {
			Observation ch = this.domain.get(op).getX();
			ch.removeAllExcept(l);
		});
	}
	
	/**
	 *
	 * Remove preconditions uncompat(ible with the automaton given as input
	 * @param A The automaton
	 * @param ante The mapping ante
	 * @param post The mapping post
	 */
	public void backtrackUncompatiblePreconditions2(
			DFA A, Mapping ante, Mapping post) {
		Map<Symbol, List<Symbol>> observedEffects = new HashMap<>();
		for(Bloc b : A.getPartition().getBlocs()) {
			int q = b.min();
			try {
				List<Symbol> out = A.getPossibleBlocTransition(q);
				for(Symbol a : out) {
					ante.getStates(q, a).getPredicates().forEach((k,v) -> {
						if(a.compatible(k)) {
							Symbol op = a.generalize();
							Symbol p = k.generalize(a.mapping());
							Observation ch = this.domain.get(op).getX();
							switch(v) {
							case FALSE:
								switch(ch.getValue(p)) {
								case TRUE:
								case ANY:
									if(!observedEffects.containsKey(op)) {
										observedEffects.put(op,
                                                            new ArrayList<>());
									}
									if(!observedEffects.get(op).contains(p)) {
										observedEffects.get(op).add(p);
									}
									break;
								default:
									break;
								};
								break;
							case TRUE:
								switch(ch.getValue(p)) {
								case FALSE:
								case ANY:
										if(!observedEffects.containsKey(op)) {
										observedEffects.put(op,
                                                            new ArrayList<>());
									}
									if(!observedEffects.get(op).contains(p)) {
										observedEffects.get(op).add(p);
									}
									break;
								default:
									break;
								};
								break;
							default:
								break;
							}
						}
					});
				}
			} catch (BlocException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		observedEffects.forEach((op,l) -> {
			Observation ch = this.domain.get(op).getX();
			ch.removeAll(l);
		});
	}

	/**
	 * Compute the rate of sequences accepted by the action model
	 * @param is The initial state
	 * @param s The sample
	 * @return The rate of accepted sequences
	 */
	public float rateOfAccepted(Observation is, Sample s) {
		int N = 0, acc = 0;
		for(Trace ex : s.getExamples()) {
			N++;
			acc = this.accept(is, (Example)ex) ? acc+1  : acc;
//			if(!this.accept(is, (Example)ex)) {
//				System.out.println(ex.getActionSequences());
//				System.out.println(this.rejectedIndex(is, (Example)ex));
//			}
		}
		return (float) acc/N;
	}

	/**
	 * Check if all sequences are accepted by the action model
	 * @param is The initial state
	 * @param s The sample
	 * @return True if the rate of accepted sequences is 1
	 */
	public boolean acceptAll(Observation is, Sample s) {
		return this.rateOfAccepted(is, s) == 1;
	}

	/**
	 *
 	 * Check if all sequences are rekected by the action model
 	 * @param is The initial state
 	 * @param s The sample
 	 * @return True if the rate of accepted sequences is 0
	 */
	public boolean rejectAll(Observation is, Sample s) {
		return this.rateOfAccepted(is, s) == 0;
	}

	/**
	 * Remove effects from rejected pair which are used by a precondition in
     * the positive sample
	 * @param m Rejected pair
	 * @param is The initial state
	 * @param pos Positive sample
	 * @return Rejected pair which are never used in the positive sample
	 */
	private Map<Pair<Symbol, String>, List<Symbol>> removeMandatoryEffects
	(Map<Pair<Symbol, String>, List<Symbol>> m, Observation is, Sample pos) {
		for(Trace t : pos.getExamples()) {
			Example ex = (Example)t;
			Observation current = is.clone();

			for(int i = 0; i < ex.size(); i++) {
				Symbol act = ex.get(i);
				Observation eff = this.getPostcond(act.generalize());
				eff = eff.instanciate(utils.Utils.reverseParamMap(
						act.mapping()));
				if(i < ex.size()-1) {
					Symbol op = act.generalize();
					String next = ex.get(i+1).getName();
					if(m.containsKey(new Pair<>(op, next))) {
						Observation prec = this.getPrecond(
								ex.get(i+1).generalize());
						prec = prec.instanciate(utils.Utils.reverseParamMap(
								ex.get(i+1).mapping()));
						for(Symbol p : eff.getPredicatesSymbols()) {
							if(ex.get(i+1).compatible(p) &&
									prec.getPredicatesSymbols().contains(p)) {
								switch(prec.getValue(p)) {
								case TRUE:
									switch(current.getValue(p)) {
									case FALSE:
										Symbol pred = p.generalize(act.mapping());
										m.get(new Pair<>(op,next)).remove(pred);

									default:
										break;
									}
									break;
								case FALSE:
									switch(current.getValue(p)) {
									case TRUE:
										Symbol pred = p.generalize(act.mapping());
										m.get(new Pair<>(op,next)).remove(pred);

									default:
										break;
									}
									break;
								default:
									break;
								}
							}

						}
						if(m.get(new Pair<>(op,next)).isEmpty()) {
							m.remove(new Pair<>(op,next));
						}
					}
				}
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
		return m;
	}

	/**
	 * Compute all rejected pair, i.e. pair of (effect action) such that the
     * effect causes the acceptation of a negative example for a complete
     * training sample
	 * @param is The initial state
	 * @param pos The positive sample
	 * @param neg The negative sample
	 * @return All rejected pair
	 */
	private Pair<Map<Pair<Symbol, String>, List<Symbol>>,
	Map<Pair<Symbol, String>, Integer>>
	rejectedPair(Observation is, Sample pos, Sample neg) {
		Map<Pair<Symbol, String>, Integer> res = new HashMap<>();
		Map<Pair<Symbol, String>, List<Symbol>> res2 = new HashMap<>();
		for(Trace t : neg.getExamples()) {
			Example ex = (Example)t;
			Map<Pair<Symbol, String>,List<Symbol>> pp = this.rejectedPair(is, ex);
			if(pp==null) {
				continue;
			}
			for(Map.Entry<Pair<Symbol, String>, List<Symbol>> ent : pp.entrySet())  {
				List<Symbol> l = ent.getValue();
				Pair<Symbol, String> p = ent.getKey();
				if(l.isEmpty()) {
					continue;
				}
				if(!res.containsKey(p)) {
					res.put(p, 0);
					res2.put(p, new ArrayList<>());
				}
				res.put(p, res.get(p)+1);
				for(Symbol e : l) {
					if(!res2.get(p).contains(e)) {
						res2.get(p).add(e);
					}
				}
			}

		}
		res2 = this.removeMandatoryEffects(res2, is, pos);
		List<Pair<Symbol, String>> toRemove = new ArrayList<>();
		for(Pair<Symbol, String> p : res.keySet()) {
			if(! res2.containsKey(p)) {
				toRemove.add(p);
			}
		}
		toRemove.forEach(p -> res.remove(p));
		return new Pair<>(res2,res);
	}

	private Pair<Map<Pair<Symbol, String>, List<Symbol>>,
	Map<Pair<Symbol, String>, Integer>>	rejectedPair(
			Observation is, 
			Sample pos, 
			List<CompressedNegativeExample> neg) {
		Map<Pair<Symbol, String>, Integer> res = new HashMap<>();
		Map<Pair<Symbol, String>, List<Symbol>> res2 = new HashMap<>();
		for(CompressedNegativeExample t : neg) {
			Map<Pair<Symbol, String>,List<Symbol>> pp = this.rejectedPair(is, t);
			if(pp==null) {
				continue;
			}
			for(Map.Entry<Pair<Symbol, String>, List<Symbol>> ent : pp.entrySet())  {
				List<Symbol> l = ent.getValue();
				Pair<Symbol, String> p = ent.getKey();
				if(l.isEmpty()) {
					continue;
				}
				if(!res.containsKey(p)) {
					res.put(p, 0);
					res2.put(p, new ArrayList<>());
				}
				res.put(p, res.get(p)+1);
				for(Symbol e : l) {
					if(!res2.get(p).contains(e)) {
						res2.get(p).add(e);
					}
				}
			}

		}
		res2 = this.removeMandatoryEffects(res2, is, pos);
		List<Pair<Symbol, String>> toRemove = new ArrayList<>();
		for(Pair<Symbol, String> p : res.keySet()) {
			if(! res2.containsKey(p)) {
				toRemove.add(p);
			}
		}
		toRemove.forEach(p -> res.remove(p));
		return new Pair<>(res2,res);
	}
	/**
	 * Compute all rejected pair, i.e. pair of (effect action) such that the
     * effect causes the acceptation of a negative example for a negative
     * example
	 * @param is The initial state
	 * @param ex The example
	 * @return All rejected pair
	 */
	private Map<Pair<Symbol, String>,List<Symbol>> rejectedPair
                                            (Observation is, Example ex) {
		if(this.accept(is, ex)) {
			Observation current = is.clone();
			for(int i = 0; i<ex.size(); i++) {
				Symbol act = ex.get(i);
				Observation prec = this.getPrecond(act.generalize());
				prec = prec.instanciate(utils.Utils.reverseParamMap(
						act.mapping()));
				if(current.contains(prec) && i < ex.size()-1) {
					Observation eff = this.getPostcond(act.generalize());
					eff = eff.instanciate(utils.Utils.reverseParamMap(
							act.mapping()));
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
				} else if(current.contains(prec) && i > 0){
					Map<Pair<Symbol, String>,List<Symbol>> res = new HashMap<>();
					List<Symbol> finalPrec = new ArrayList<>(prec.clone().getPredicatesSymbols());
					for(int j = 1; j <= i; j++) {
						List<Symbol> effs = new ArrayList<>();
						Observation eff = this.getPostcond(ex.get(i-1).generalize());
						eff = eff.instanciate(utils.Utils.reverseParamMap(
								ex.get(i-1).mapping()));
						final Integer idx = i;
						eff.getPredicates().forEach((e,v) -> {
							switch(v){
							case TRUE:
							case FALSE:
								if(finalPrec.contains(e)) {
									effs.add(e.generalize(ex.get(idx-1).mapping()));
									finalPrec.remove(e);
								}
							default:
								break;
							}
						});
						res.put(new Pair<>(ex.get(i-j).generalize(),ex.get(i).getName()), effs);
					}
					return res;
				}
			}
		}
		return null;
	}
	
	private Map<Pair<Symbol, String>,List<Symbol>> rejectedPair(
			Observation is, CompressedNegativeExample ex) {
		
		return null;
	}

	/**
	 * Backtrack Effects
	 * @param is The initial state
	 * @param pos Thye positive Sample
	 * @param neg The negative sample
	 */
	public void backtrackEffects(Observation is, Sample pos, Sample neg) {
		while(! this.rejectAll(is, neg)) {
			Pair<Map<Pair<Symbol, String>, List<Symbol>>,
				Map<Pair<Symbol, String>, Integer>> candidates =
												this.rejectedPair(is, pos, neg);
			List<Pair<Symbol, String>> allCandidates =
					new ArrayList<>(candidates.getX().keySet());
			if(allCandidates.isEmpty()) {
				return;
			}
			Pair<Symbol, String> bestCandidate = allCandidates.get(0);
			for(int i = 1; i < allCandidates.size(); i++) {
				if(candidates.getY().get(bestCandidate) <
						candidates.getY().get(allCandidates.get(i))) {
					bestCandidate = allCandidates.get(i);
				}
			}
			Symbol op = bestCandidate.getX();
			for(Symbol effect : candidates.getX().get(bestCandidate)) {
				this.mutation(false, op, effect, Observation.Value.ANY);
			}
		}
	}
	
	/**
	 * Backtrack Effects
	 * @param is The initial state
	 * @param pos Thye positive Sample
	 * @param neg The negative sample
	 */
	public void backtrackEffects(
			Observation is,
			Sample pos, 
			Sample neg,
			List<CompressedNegativeExample> compressed) {
		while(! this.rejectAll(is, neg)) {
			Pair<Map<Pair<Symbol, String>, List<Symbol>>,
				Map<Pair<Symbol, String>, Integer>> candidates =
												this.rejectedPair(
														is, 
														pos, 
														compressed);
			List<Pair<Symbol, String>> allCandidates =
					new ArrayList<>(candidates.getX().keySet());
			if(allCandidates.isEmpty()) {
				return;
			}
			Pair<Symbol, String> bestCandidate = allCandidates.get(0);
			for(int i = 1; i < allCandidates.size(); i++) {
				if(candidates.getY().get(bestCandidate) <
						candidates.getY().get(allCandidates.get(i))) {
					bestCandidate = allCandidates.get(i);
				}
			}
			Symbol op = bestCandidate.getX();
			for(Symbol effect : candidates.getX().get(bestCandidate)) {
				this.mutation(false, op, effect, Observation.Value.ANY);
			}
		}
	}

	/**
	 * Check if the Domain can generate an example
	 * @param is The initial state
	 * @param ex The example
	 * @return True if the example is accepted
	 */
	public boolean accept(Observation is, Example ex) {
		Observation current = is.clone();
		for(int i = 0; i<ex.size(); i++) {
			Symbol act = ex.get(i);
			Observation prec = this.getPrecond(act.generalize());
			prec = prec.instanciate(utils.Utils.reverseParamMap(
					act.mapping()));
			if(current.contains(prec)) {
				Observation eff = this.getPostcond(act.generalize());
				eff = eff.instanciate(utils.Utils.reverseParamMap(
						act.mapping()));
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
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the Domain can generate an example
	 * @param is The initial state
	 * @param ex The example
	 * @return True if the example is accepted
	 */
	public int rejectedIndex(Observation is, Example ex) {
		Observation current = is.clone();
		for(int i = 0; i<ex.size(); i++) {
			Symbol act = ex.get(i);
			Observation prec = this.getPrecond(act.generalize());
			prec = prec.instanciate(utils.Utils.reverseParamMap(
					act.mapping()));
			if(current.contains(prec)) {
				Observation eff = this.getPostcond(act.generalize());
				eff = eff.instanciate(utils.Utils.reverseParamMap(
						act.mapping()));
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
			} else {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Merge two Domains
	 * @param other The second Domain
	 * @return A new Domain
	 */
	public Domain merge(Domain other) {
		try {
			Domain clone = this.clone();
			for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
                                                    other.domain.entrySet()) {
				Symbol op = entry.getKey();
				Observation prec = entry.getValue().getX();
				Observation eff = entry.getValue().getY();
				prec.getPredicates().forEach((k,v) -> {
					switch(v) {
					case TRUE:
					case FALSE:
						clone.mutation(true, op, k, v);
						break;
					default:
						break;
					}
				});
				eff.getPredicates().forEach((k,v) -> {
					switch(v) {
					case TRUE:
					case FALSE:
						clone.mutation(false, op, k, v);
						break;
					default:
						break;
					}
				});
				clone.domain.get(op).getX().getPredicates().forEach((k,v) -> {
					Observation.Value p = clone.domain.get(op).getX().getValue(k);
					Observation.Value e = clone.domain.get(op).getY().getValue(k);
					
					if(p == e) {
						clone.mutation(false, op, k, Observation.Value.ANY);
					}
				});
			}
			return clone;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


	}
	
	/**
	 * Merge two Domains
	 * @param other The second Domain
	 * @return A new Domain
	 */
	public Domain merge2(Domain other) {
		try {
			Domain clone = this.clone();
			for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
                                                    other.domain.entrySet()) {
				Symbol op = entry.getKey();
				Observation prec = entry.getValue().getX();
				Observation eff = entry.getValue().getY();
				prec.getPredicates().forEach((k,v) -> {
					switch(v) {
					case TRUE:
					case FALSE:
						clone.mutation(true, op, k, v);
						break;
					default:
						break;
					}
				});
				eff.getPredicates().forEach((k,v) -> {
					switch(v) {
					case TRUE:
					case FALSE:
						clone.mutation(false, op, k, v);
						break;
					default:
						break;
					}
				});
				clone.domain.get(op).getX().getPredicates().forEach((k,v) -> {
					Observation.Value p = clone.domain.get(op).getX().getValue(k);
					Observation.Value e = clone.domain.get(op).getY().getValue(k);
					
					if(p == e) {
						clone.mutation(false, op, k, Observation.Value.ANY);
					}
				});
			}
			return clone;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


	}

	/**
	 * Apply an action in an observed state
	 * @param state The observed state
	 * @param action The action
	 * @return The new state
	 */
	public Observation apply(Observation state, Symbol action) {
		Observation post = state.clone();
		Symbol op = action.generalize();
		Observation eff = this.getPostcond(op);
		eff = eff.instanciate(utils.Utils.reverseParamMap(action.mapping()));
		post = post.addEffect(eff);
		return post;
	}

	/**
	 * Check if an effect is an effect of a specific operator
	 * @param operator The operator
	 * @param predicate The effect
	 * @return True if is the effect is an effect of the specific operator
	 */
	public boolean isEffectOf(Symbol operator, Symbol predicate) {
		Observation effects = this.domain.get(operator).getY();
		switch(effects.getValue(predicate)) {
		case TRUE:
			return true;
		case FALSE:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Play an example
	 * @param actions The initial sate
	 * @param initial The initial state
	 * @return The final state
	 */
	public Observation play(List<Symbol> actions, Observation initial) {
		Observation current = initial.clone();
		for(Symbol act : actions) {
			current = this.apply(current, act);
		}
		return current;
	}
	
	/**
	 * 
	 * @param is
	 * @param pos
	 * @param neg
	 * @return
	 */
	public float fscore(Observation is, Sample pos, Sample neg) {
		float fscore =0f;
		int truePos=0, falsePos=0;
		for(Trace ex : pos.getExamples()) {
			if(this.accept(is, (Example)ex)) {
				truePos++;
			}
		}
		for(Trace ex : neg.getExamples()) {
			if(this.accept(is, (Example)ex)) {
				falsePos++;
			}
		}
		
		fscore = learning.Measure.FScore(
				learning.Measure.R(truePos, pos.size()), 
				learning.Measure.P(truePos, falsePos));
		return fscore;
	}
	
	/**
	 * Print all accepted sequences
	 * @param is The initial state
	 * @param s The sample
	 */
	public void printAccepted(Observation is, Sample s) {
		for(Trace ex : s.getExamples()) {
			if(this.accept(is, (Example)ex)) {
				System.out.println(ex);
			}
		}
	}
	
	/**
	 * 
	 * @param op
	 * @param propositions
	 */
	public void addOperator(Symbol op, List<Symbol> propositions) {
		Observation p = new Observation();
		Observation e = new Observation();
		propositions.forEach(k -> {
			p.addAnyObservation(k);
			e.addAnyObservation(k);
		});
		this.A.add(op);
		this.domain.put(op, new Pair<>(p,e));
	}
	
	/**
	 * 
	 * @return
	 */
	public String generatePDDL(String name, TypeHierarchy types) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> p =
				this.decode();
		Map<Symbol, Observation> preconditions = p.getX();
		Map<Symbol, Observation> postconditions = p.getY();
		String res = "(define (domain "+name+")\n";
		res += "(:requirements :strips :typing :negative-preconditions)\n";

		//Types
		res += "(:types\n";
		res += types.toString();
		res += ")\n";
		//Predicates
		res += "(:predicates\n";
		List<Symbol> tmp = new ArrayList<>();
		for(Symbol s : this.P) {
			Symbol ss = s.generalize();
			if(! tmp.contains(ss)) {
				res += "\t"+ss.toStringType()+"\n";
				tmp.add(ss);
			}
		}
		res += ")\n";

		//Actions
		tmp = new ArrayList<>();
		for(Symbol action : this.A){
			Symbol actionType = action.generalize();
			if(! tmp.contains(actionType)){
				tmp.add(actionType);
				res += "(:action "+actionType.getName()+"\n";
				//Parameters
				res += "\t:parameters (";
				for(String s : actionType.getListParametersType()) {
					res += s+" ";
				}
				res += ")\n";

				//Precondition
				res += "\t:precondition (and";
				for(Symbol pred : preconditions.get(actionType).
						getPredicatesSymbols()) {
					switch(preconditions.get(actionType).
							getValue(pred)) {
							case TRUE:
								res += "\n\t"+pred;
								break;
							case FALSE:
								res += "\n\t(not"+pred+")";
								break;
							case ANY:
								break;
							default:
								break;
					}
				}
				res += ")\n";

				//Postcondition
				res += "\t:effect (and";
				for(Symbol post : postconditions.get(actionType).
						getPredicatesSymbols()) {
					switch(postconditions.get(actionType).
							getValue(post)) {
							case TRUE:
								res += "\n\t"+post;
								break;
							case FALSE:
								res += "\n\t(not"+post+")";
								break;
							case ANY:
								break;
							default:
								break;
					}
				}
				res += ")\n";

				res += ")\n";
			}
		}

		res +=")\n";
		return res;
	}
	
	/**
	 * 
	 * @param m
	 */
	public void makeMovement(Movement m) {
		this.mutation(
				m.isPrecondition(), 
				m.getOperator(), 
				m.getPredicate(), 
				m.getValue());
	}
	
	/**
	 * 
	 * @param m
	 */
	public void makeMovement(List<Movement> m) {
		m.forEach(mm -> this.makeMovement(mm));
	}
	
	/**
	 * 
	 * @param m
	 */
	public void backtrack(Movement m) {
		this.makeMovement(m.inverse());
	}
	
	/**
	 * 
	 * @param m
	 */
	public void backtrack(List<Movement> m) {
		m.forEach(mm -> this.backtrack(mm));
	}
	
	/***
	 * 
	 * @return
	 */
	public List<Symbol> getOperators() {
		List<Symbol> res = new ArrayList<>();
		this.getA().forEach(a -> {
			Symbol op = a.generalize();
			if(!res.contains(op)) {
				res.add(op);
			}
		});
		return res;
	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	public float errorRatePrecondition(ObservedExample ex) {
		float res = 0f;
		for(int i = 0; i < ex.getActionSequences().size(); i++) {
			Observation obs = ex.ante(i);
			Symbol act = ex.get(i);
			Observation prec = this.getPrecond(act.generalize());
			prec = prec.instanciate(utils.Utils.reverseParamMap(
					act.mapping()));
			int n = 0, error=0;
			for(Symbol p : prec.getPredicatesSymbols()) {
				switch(prec.getValue(p)) {
				case TRUE:
					n++;
					switch(obs.getValue(p)) {
					case TRUE:
						break;
					default:
						error++;
						break;
					}
					break;
				case FALSE:
					n++;
					switch(obs.getValue(p)) {
					case FALSE:
						break;
					default:
						error++;
						break;
					}
					break;
				default:
					break;
				}
			}
			res += n > 0 ? ((float)error/n) : 0;
		}
		return res/ex.getActionSequences().size();
	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	public float errorRateEffect(ObservedExample ex) {
		float res = 0f;
		for(int i = 0; i < ex.getActionSequences().size(); i++) {
			Observation obs = ex.post(i);
			Symbol act = ex.get(i);
			Observation prec = this.getPostcond(act.generalize());
			prec = prec.instanciate(utils.Utils.reverseParamMap(
					act.mapping()));
			int n = 0, error=0;
			for(Symbol p : prec.getPredicatesSymbols()) {
				switch(prec.getValue(p)) {
				case TRUE:
					n++;
					switch(obs.getValue(p)) {
					case TRUE:
						break;
					default:
						error++;
						break;
					}
					break;
				case FALSE:
					n++;
					switch(obs.getValue(p)) {
					case FALSE:
						break;
					default:
						error++;
						break;
					}
					break;
				default:
					break;
				}
			}
			res += n > 0 ? ((float)error/n) : 0;
		}
		return res/ex.getActionSequences().size();
	}
}
