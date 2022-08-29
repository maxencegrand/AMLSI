/**
 * 
 */
package learning.temporal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsm.Pair;
import fsm.Symbol;
import learning.Domain;
import learning.DomainInstantiation;
import learning.Observation;
import simulator.temporal.TemporalExample;
import simulator.temporal.TemporalOracle;
/**
 * @author Maxence Grand
 *
 */
public class TemporalDomain {
	/**
	 * Start domain
	 */
	private Domain start;
	/** 
	 * Invariant
	 */
	private Domain invariant;
	/**
	 * End domain
	 */
	private Domain end;
	/**
	 * Durations
	 */
	private Map<Symbol, Float> durations;
	/**
	 * 
	 */
	private static Map<Domain, Domain> previousConversion = new HashMap<>();
	/**
	 * 
	 */
	private boolean twoOp;
	/**
	 * 
	 */
	private static Map<Symbol, Boolean> isStart;
	/**
	 * 
	 */
	private static Map<Symbol, Boolean> isEnd;
	/**
	 * 
	 */
	private static Map<Symbol, Boolean> isInv;
	/**
	 * 
	 */
	private static Map<Symbol, Symbol> getStart;
	/**
	 * 
	 */
	private static Map<Symbol, Symbol> getInv;
	/**
	 * 
	 */
	private static Map<Symbol, Symbol> getEnd;
	
	public static void initClassicalSym(Domain i) {
		TemporalDomain.isStart = new HashMap<>();
		TemporalDomain.isEnd = new HashMap<>();
		TemporalDomain.getStart = new HashMap<>();
		TemporalDomain.getEnd = new HashMap<>();
		TemporalDomain.isInv = new HashMap<>();
		TemporalDomain.getInv = new HashMap<>();
		i.getDomain().keySet().forEach(op -> {
			if(isStartOp(op)) {
				isStart.put(op, true);
				isEnd.put(op, false);
				isInv.put(op, false);
				getStart.put(op, op);
				getInv.put(op, getLabeledOp(op,"inv"));
				getEnd.put(op, getLabeledOp(op,"end"));
			} else if(isInvariantOp(op)) {
				isStart.put(op, false);
				isInv.put(op, true);
				isEnd.put(op, false);
				getInv.put(op, op);
				getStart.put(op, getLabeledOp(op,"start"));
				getEnd.put(op, getLabeledOp(op,"end"));
			} else if(isEndOp(op)) {
				isStart.put(op, false);
				isInv.put(op, false);
				isEnd.put(op, true);
				getEnd.put(op, op);
				getInv.put(op, getLabeledOp(op,"inv"));
				getStart.put(op, getLabeledOp(op,"start"));
			}
		});
	}
	
	/**
	 * 
	 * @param op
	 * @return
	 */
	public static boolean isStartOp(Symbol op) {
		String name = op.getName();
		String[] t = name.split("-");
		switch(t[t.length-1]) {
		case "start":
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * 
	 * @param op
	 * @return
	 */
	public static boolean isEndOp(Symbol op) {
		String name = op.getName();
		String[] t = name.split("-");
		switch(t[t.length-1]) {
		case "end":
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * 
	 * @param op
	 * @return
	 */
	public static boolean isInvariantOp(Symbol op) {
		String name = op.getName();
		String[] t = name.split("-");
		switch(t[t.length-1]) {
		case "inv":
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * 
	 * @param op
	 * @return
	 */
	public static Symbol getLabeledOp(Symbol op, String label) {
		String name = op.getName();
		String[] t = name.split("-");
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < t.length-1; i++) {
			builder.append(t[i]);
			builder.append("-");
		}
		builder.append(label);
		Symbol res = op.clone();
		res.setName(builder.toString());
		return res;
	}
	/**
	 * 
	 * Constructs 
	 * @param classical
	 * @param durations
	 */
	public TemporalDomain(Domain classical, Map<Symbol, Float> durations, boolean twoOp, List<Symbol> P, List<Symbol> A) {
		this(durations);
		this.twoOp = twoOp;
		List<Symbol> operators = new ArrayList<>(this.durations.keySet());
		if(twoOp) {
			operators.forEach(operator -> {
				//System.out.println(operator+" "+classical.getDomain().keySet());
				Symbol s = operator.clone();
				s.setName(s.getName()+"-start");
				Symbol e = operator.clone();
				e.setName(e.getName()+"-end");
				Observation startClassicalPrecondition = classical.getPrecond(s);
				Observation startClassicalEffect = classical.getPostcond(s);
				Observation endClassicalPrecondition = classical.getPrecond(e);
				Observation endClassicalEffect = classical.getPostcond(e);
				List<Symbol> propositions = startClassicalPrecondition.getPredicatesSymbols();
				this.start.addOperator(operator, propositions);
				this.invariant.addOperator(operator, propositions);
				this.end.addOperator(operator, propositions);

				//Ad start effect
				startClassicalEffect.getPredicates().forEach((k,v) -> {
					this.start.getPostcond(operator).addObservation(k, v);
				});
				//Ad end effect
				endClassicalEffect.getPredicates().forEach((k,v) -> {
					this.end.getPostcond(operator).addObservation(k, v);
				});
				List<Symbol> inv = new ArrayList<>();
				//System.out.println(classical.getDomain());
				//System.out.println(operator+" "+s+" "+startClassicalPrecondition);
				startClassicalPrecondition.getPredicates().forEach((k,v) -> {
					switch(v) {
					case ANY:
						break;
					case MISSED:
						break;
					default:
						if(endClassicalPrecondition.getValue(k) == v) {
							inv.add(k);
							this.invariant.getPrecond(operator).addObservation(k, v);
						} else {
							this.start.getPrecond(operator).addObservation(k, v);
						}
						break;
					}
				});
				endClassicalPrecondition.getPredicates().forEach((k,v) -> {
					if(!inv.contains(k)) {
						/*if(v == Observation.Value.TRUE) {
							this.end.getPrecond(operator).addObservation(k, v);
						} else if(v == Observation.Value.FALSE && !startClassicalPrecondition.containsPredicate(k)) {
							this.end.getPrecond(operator).addObservation(k, v);
						} else if(v == Observation.Value.FALSE && startClassicalEffect.getValue(k)!=v) {
							this.end.getPrecond(operator).addObservation(k, v);
						} */
						this.end.getPrecond(operator).addObservation(k, v);
					}
				});
			});
		} else {
			operators.forEach(operator -> {
				Symbol s = operator.clone();
				s.setName(s.getName()+"-start");
				Symbol e = operator.clone();
				e.setName(e.getName()+"-end");
				Symbol i = operator.clone();
				i.setName(i.getName()+"-inv");
				Observation startClassicalPrecondition = classical.getPrecond(s);
				Observation startClassicalEffect = classical.getPostcond(s);
				Observation endClassicalPrecondition = classical.getPrecond(e);
				Observation endClassicalEffect = classical.getPostcond(e);
				Observation invClassicalPrecondition = classical.getPrecond(i);
				Observation invClassicalEffect = classical.getPostcond(i);
				List<Symbol> propositions = startClassicalPrecondition.getPredicatesSymbols();
				this.start.addOperator(operator, propositions);
				this.invariant.addOperator(operator, propositions);
				this.end.addOperator(operator, propositions);

				//Add start effect
				startClassicalEffect.getPredicates().forEach((k,v) -> {
					if(! isDumpy(operator,k)) {
						this.start.getPostcond(operator).addObservation(k, v);
					}
				});
				//Add end effect
				endClassicalEffect.getPredicates().forEach((k,v) -> {
					if(! isDumpy(operator,k)) {
						this.end.getPostcond(operator).addObservation(k, v);
					}
				});

				//Add start precondition
				startClassicalPrecondition.getPredicates().forEach((k,v) -> {
					if(! isDumpy(operator,k)) {
						this.start.getPrecond(operator).addObservation(k, v);
					}
				});
				//Add end precondition
				endClassicalPrecondition.getPredicates().forEach((k,v) -> {
					if(! isDumpy(operator,k)) {
						this.end.getPrecond(operator).addObservation(k, v);
					}
				});
				//Add end precondition
				invClassicalPrecondition.getPredicates().forEach((k,v) -> {
					if(! isDumpy(operator,k)) {
						this.invariant.getPrecond(operator).addObservation(k, v);
					}
				});
			});
		}
		this.start.getDomain().keySet().forEach(operator -> {
			this.start.getPostcond(operator).getPredicates().forEach((k,v) -> {
				switch(v) {
				case ANY:
					break;
				case FALSE:
					switch(this.invariant.getPrecond(operator).getValue(k)) {
					case FALSE:
						this.invariant.mutation(true, operator, k, 
								Observation.Value.ANY);
						break;
					case TRUE:
						this.start.mutation(false, operator, k, 
								Observation.Value.ANY);
						break;
					default:
						break;
					}
					break;
				case MISSED:
					break;
				case TRUE:
					switch(this.invariant.getPrecond(operator).getValue(k)) {
					case TRUE:
						this.invariant.mutation(true, operator, k, 
								Observation.Value.ANY);
						break;
					case FALSE:
						this.start.mutation(false, operator, k, 
								Observation.Value.ANY);
						break;
					default:
						break;
					}
					break;
				default:
					break;
				
				}
			});
		});
	}

	/**
	 * 
	 * Constructs 
	 * @param classical
	 * @param durations
	 */
	public TemporalDomain(Map<Symbol, Float> durations) {
		this.durations = durations;
		this.start = new Domain();
		this.invariant = new Domain();
		this.end = new Domain();
	}

	/**
	 * 
	 * @param example
	 * @param is
	 * @return
	 */
	public boolean accept(TemporalExample example, Observation is,
			DomainInstantiation startI, DomainInstantiation invI,
			DomainInstantiation endI) {
		Map<Float, Symbol> seq = example.getTemporalSequence();
		Map<Float, Symbol> nextEnd = new HashMap<>();
		List<Float> startTime = new ArrayList<>(seq.keySet());
		Collections.sort(startTime);
		Observation current = is.clone();
		for(float f : startTime) {
			//System.out.println(f+" "+startTime);
			//Apply effect of previous end
			List<Float> toRemove = new ArrayList<>();
			for(float f1 : beforeStart(nextEnd.keySet(), f)) {
				current = endI.apply(nextEnd.get(f1), current);
				toRemove.add(f1);
			}
			for(float f1 : toRemove) {
				nextEnd.remove(f1);
			}

			Symbol newAct = seq.get(f);
			float endNewAction = f+this.durations.get(newAct.generalize());
			List<Float> currentActions = beforeStart(nextEnd.keySet(),
					endNewAction);

			if(isApplicable(startI, invI, endI, newAct, current, nextEnd,
					endNewAction, currentActions)) {
				current = startI.apply(newAct, current);
				nextEnd.put(endNewAction, newAct);
			} else {
				///System.out.println(newAct);
				return false;
			}

		}
		return true;
	}

	/**
	 * 
	 * @param startI
	 * @param invI
	 * @param endI
	 * @param newAct
	 * @param current
	 * @param nextEnd
	 * @param endNewAction
	 * @param currentActions
	 * @return
	 */
	public boolean isApplicable(DomainInstantiation startI,
			DomainInstantiation invI, DomainInstantiation endI,
			Symbol newAct, Observation current, Map<Float, Symbol> nextEnd,
			float endNewAction, List<Float> currentActions) {
		//Check at start precondition for the new action
		if(!current.contains(startI.getPrecond(newAct)) ||
				!current.contains(invI.getPrecond(newAct))) {
			return false;
		}

		Observation clone = current.clone();
		clone = startI.apply(newAct, clone);
		boolean toEnd = true;
		List<Float> time = new ArrayList<>(nextEnd.keySet());
		Collections.sort(time);
		for(float f1 : time) {
			if(f1 < endNewAction || !toEnd) {
				if(!clone.contains(endI.getPrecond(nextEnd.get(f1))) ||
						!clone.contains(invI.getPrecond(nextEnd.get(f1)))) {
					//System.out.println(nextEnd.keySet());
					//System.out.println("End ?"+clone.contains(endI.getPrecond(nextEnd.get(f1)))+clone);
					return false;
				}
				clone = endI.apply(nextEnd.get(f1), clone);
			} else {
				toEnd=false;
				if(!clone.contains(endI.getPrecond(newAct)) ||
						!clone.contains(invI.getPrecond(newAct))) {
					return false;
				}
				clone = endI.apply(newAct, clone);
			}
			if(toEnd) {
				if(!clone.contains(invI.getPrecond(newAct))) {
					return false;
				}
			}
			
			for(float f2 : time) {
				if(f2 > f1) {
					if(!clone.contains(invI.getPrecond(nextEnd.get(f2)))) {
						return false;
					}
				}
			}
		}

		return true;
	}
	
	/**
	 * 
	 * @param temp
	 * @param is
	 * @return
	 */
	public boolean acceptAll(List<TemporalExample> temp, Observation is) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		//System.out.println(startI);
		int i =0;
		for(TemporalExample ex : temp) {
			i++;
			if(!this.accept(ex,is,startI,invI,endI)) {
				return false;
			}
		}
		return true;
	}

	public float rateOfAccepted(List<TemporalExample> temp, Observation is) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		//System.out.println(startI);
		float N=0, acc=0;
		for(TemporalExample ex : temp) {
			N++;
			if(this.accept(ex,is,startI,invI,endI)) {
				acc++;
			}
		}
		return acc/N;
	}
	
	public void printAccepted(List<TemporalExample> temp, Observation is) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		for(TemporalExample ex : temp) {
			if(this.accept(ex,is,startI,invI,endI)) {
				System.out.println(ex);;
			}
		}
	}
	
	public void printRejected(List<TemporalExample> temp, Observation is) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		for(TemporalExample ex : temp) {
			if(!this.accept(ex,is,startI,invI,endI)) {
				System.out.println(ex);;
			}
		}
	}
	/**
	 * 
	 * @param temp
	 * @param is
	 * @return
	 */
	public boolean rejectAll(List<TemporalExample> temp, Observation is) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		int i = 0;
		for(TemporalExample ex : temp) {
			i++;
			if(this.accept(ex,is,startI,invI,endI)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param temp
	 * @param is
	 * @return
	 */
	public float scorePos(List<TemporalExample> temp, Observation is) {
		float res = 0f;
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		for(TemporalExample ex : temp) {
			if(this.accept(ex,is,startI,invI,endI)) {
				res += ex.getTemporalSequence().size();
			}
		}
		return res;
	}

	/**
	 * 
	 * @param compressed
	 * @param temp
	 * @param is
	 * @return
	 */
	public float scoreNeg(List<CompressedTemporalNegativeExample> compressed, 
			List<TemporalExample> temp, Observation is) {
		float res = 0f;
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(temp));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(temp));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(temp));
		for(CompressedTemporalNegativeExample ex : compressed) {
			Map<Float, Symbol> seq = ex.getPrefixe();
			Map<Float, List<Symbol>> neg = ex.getNegatives();
			Map<Float, Symbol> nextEnd = new HashMap<>();
			List<Float> startTime = new ArrayList<>(seq.keySet());
			Collections.sort(startTime);
			Observation current = is.clone();
			for(float f : startTime) {
				List<Float> negTime = new ArrayList<>(neg.keySet());
				Collections.sort(negTime);
				int itNeg=0;
				while(itNeg < negTime.size() && negTime.get(itNeg) <= f) {
					List<Float> toRemove = new ArrayList<>();
					for(float f1 : beforeStart(nextEnd.keySet(), f)) {
						current = endI.apply(nextEnd.get(f1), current);
						toRemove.add(f1);
					}
					for(float f1 : toRemove) {
						nextEnd.remove(f1);
					}

					for(Symbol newAct : neg.get(negTime.get(itNeg))) {
						float endNewAction = f+this.durations.get(newAct.generalize());
						List<Float> currentActions = beforeStart(nextEnd.keySet(),
								endNewAction);

						if(!isApplicable(startI, invI, endI, newAct, current, nextEnd,
								endNewAction, currentActions)) {
							res++;
						}
					}
					itNeg++;
				}
				List<Float> toRemove = new ArrayList<>();
				for(float f1 : beforeStart(nextEnd.keySet(), f)) {
					current = endI.apply(nextEnd.get(f1), current);
					toRemove.add(f1);
				}
				for(float f1 : toRemove) {
					nextEnd.remove(f1);
				}

				Symbol newAct = seq.get(f);
				float endNewAction = f+this.durations.get(newAct.generalize());
				List<Float> currentActions = beforeStart(nextEnd.keySet(),
						endNewAction);

				if(isApplicable(startI, invI, endI, newAct, current, nextEnd,
						endNewAction, currentActions)) {
					current = startI.apply(newAct, current);
					nextEnd.put(endNewAction, newAct);
				} else {
					break;
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param s1
	 * @param s2
	 */
	private static boolean isDumpy(Symbol s1, Symbol s2) {
		String opName = s1.getName();
		String propName = s2.getName();
		if(propName.equals(opName+"-inv")) {
			return true;
		}
		if(propName.equals("i"+opName+"-inv")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param l
	 * @param f
	 * @return
	 */
	private static List<Float> beforeStart(Collection<Float> l, float f) {
		List<Float> r = new ArrayList<>();
		for(float x : l) {
			if(x <= f) {
				r.add(x);
			}
		}
		return r;
	}

	/**
	 * 
	 * @param temp
	 * @return
	 */
	private List<Symbol> getAllActions(List<TemporalExample> temp) {
		List<Symbol> actions = new ArrayList<>();
		for(TemporalExample ex : temp) {
			for(Symbol a : ex.getTemporalSequence().values()) {
				if(!actions.contains(a)) {
					actions.add(a);
				}
			}
		}
		return actions;
	}

	/**
	 * 
	 * @param l
	 * @return
	 */
	private static float getLast(Collection<Float> l) {
		float f = -1f;
		for(float ff : l) {
			if(ff > f) {
				f = ff;
			}
		}
		return f;
	}

	/**
	 * 
	 */
	public String toString() {
		return "At Start\n"+this.start+"\nOverAll\n"+this.invariant+"\nAt End\n"+this.end+"\n"+this.durations;
	}

	/**
	 * 
	 * @param temp
	 * @param is
	 * @return
	 */
	public float fscore(List<TemporalExample> pos,List<TemporalExample> neg, Observation is) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(pos));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(pos));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(pos));
		//System.out.println(this.invariant);
		//System.out.println(invI);
		int iPos =0;
		for(TemporalExample ex : pos) {
			if(this.accept(ex,is,startI,invI,endI)) {
				iPos++;
			} else {
			}
		}
		startI = new DomainInstantiation(this.start, getAllActions(neg));
		invI = new DomainInstantiation(this.invariant, getAllActions(neg));
		endI = new DomainInstantiation(this.end, getAllActions(neg));
		int iNeg =0;
		for(TemporalExample ex : neg) {
			if(this.accept(ex,is,startI,invI,endI)) {
				iNeg++;
			}
		}
		float R = learning.Measure.R(iPos, pos.size());
		float P = learning.Measure.P(iPos, iNeg);
	//	System.out.println(iPos+" "+iNeg);
		return learning.Measure.FScore(R, P);
	}

	public float fscore(List<TemporalExample> pos,List<TemporalExample> neg, Observation is, TemporalOracle oracle) {
		DomainInstantiation startI = 
				new DomainInstantiation(this.start, getAllActions(pos));
		DomainInstantiation invI = 
				new DomainInstantiation(this.invariant, getAllActions(pos));
		DomainInstantiation endI = 
				new DomainInstantiation(this.end, getAllActions(pos));
		//System.out.println(this.invariant);
		//System.out.println(invI);
		int iPos =0;
		for(TemporalExample ex : pos) {
			if(this.accept(ex,is,startI,invI,endI)) {
				iPos++;
			} else {
				System.out.println(ex);
				System.out.println(ex.convertIntoSequential3Op(oracle));
			}
		}
		startI = new DomainInstantiation(this.start, getAllActions(neg));
		invI = new DomainInstantiation(this.invariant, getAllActions(neg));
		endI = new DomainInstantiation(this.end, getAllActions(neg));
		int iNeg =0;
		/*for(TemporalExample ex : neg) {
			if(this.accept(ex,is,startI,invI,endI)) {
				iNeg++;
			}
		}*/
		float R = learning.Measure.R(iPos, pos.size());
		float P = learning.Measure.P(iPos, iNeg);
		System.out.println(iPos+" "+iNeg);
		return learning.Measure.FScore(R, P);
	}

	/**
	 * 
	 * @param l
	 * @param durations
	 * @param twoOp
	 * @return
	 */
	public static List<Domain> neighbors(List<Domain> l, 
			Map<Symbol, Float> durations, boolean twoOp, List<Symbol> P, 
			List<Symbol> A, List<Symbol> Atemp) {
		List<Domain> res = new ArrayList<>();
		l.forEach(indiv -> {
			Domain indiv2 = 
					TemporalDomain.previousConversion.containsKey(indiv) ?
							TemporalDomain.previousConversion.get(indiv)
							: TemporalDomain.convert(indiv, durations, twoOp, P, A, Atemp);
			if(indiv2!=null && !res.contains(indiv)) {
				res.add(indiv);
			}
		});
		return res;
	}

	/**
	 * 
	 * @return
	 */
	public Domain convert(List<Symbol> P, List<Symbol> A) {
		Domain indiv = new Domain();
		indiv.setP(P);
		indiv.setA(A);
		Map<Symbol, Pair<Observation, Observation>> domainStart = 
				this.start.getDomain();
		Map<Symbol, Pair<Observation, Observation>> domainEnd = 
				this.end.getDomain();
		Map<Symbol, Pair<Observation, Observation>> domainInv = 
				this.invariant.getDomain();
		List<Symbol> operators = new ArrayList<>(this.durations.keySet());
		operators.forEach(op -> {
			Symbol opStart = op.clone();
			opStart.setName(opStart.getName()+"-start");
			Symbol opEnd = op.clone();
			opEnd.setName(opEnd.getName()+"-end");
			indiv.addOperator(opStart, this.start.getP());
			indiv.addOperator(opEnd, this.start.getP());
			//Start
			domainStart.get(op).getX().getPredicates().forEach((k,v) -> {
				//System.out.println(opStart+" "+k+" "+v);
				indiv.mutation(true, opStart, k, v);
			});
			domainStart.get(op).getY().getPredicates().forEach((k,v) -> {
				indiv.mutation(false, opStart, k, v);
			});
			domainInv.get(op).getX().getPredicates().forEach((k,v) -> {
				if(v == Observation.Value.TRUE || v == Observation.Value.FALSE) {
					indiv.mutation(true, opStart, k, v);
				}
			});
			//End
			domainEnd.get(op).getX().getPredicates().forEach((k,v) -> {
				indiv.mutation(true, opEnd, k, v);
			});
			domainEnd.get(op).getY().getPredicates().forEach((k,v) -> {
				indiv.mutation(false, opEnd, k, v);
			});
			domainInv.get(op).getX().getPredicates().forEach((k,v) -> {
				if(v == Observation.Value.TRUE || v == Observation.Value.FALSE) {
					indiv.mutation(true, opEnd, k, v);
				}
			});
			
			//Remove identical effect
			indiv.getDomain().get(opStart).getY().getPredicates().forEach((k,v) -> {
				if(indiv.getDomain().get(opEnd).getY().getValue(k) == v) {
					indiv.mutation(false, opStart, k, Observation.Value.ANY);
					indiv.mutation(false, opEnd, k, Observation.Value.ANY);
				}
			});
			
			//Remove inconsistant effect p in s and !p in end or inv
			indiv.getDomain().get(opStart).getY().getPredicates().forEach((k,v) -> {
				switch(v) {
				case FALSE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case TRUE:
						indiv.mutation(false, opStart, k, Observation.Value.ANY);
						indiv.mutation(true, opEnd, k, Observation.Value.ANY);
						break;
					default:
						break;
					}
					break;
				case TRUE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case FALSE:
						indiv.mutation(false, opStart, k, Observation.Value.ANY);
						indiv.mutation(true, opEnd, k, Observation.Value.ANY);
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			});
			
			//If end effect in overall change overall to start preconditions
			indiv.getDomain().get(opStart).getX().getPredicates().forEach((k,v) -> {
				switch(v) {
				case FALSE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case FALSE:
						switch(indiv.getDomain().get(opEnd).getY().getValue(k)) {
						case TRUE:
							indiv.mutation(true, opEnd, k, Observation.Value.ANY);
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
					break;
				case TRUE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case TRUE:
						switch(indiv.getDomain().get(opEnd).getY().getValue(k)) {
						case FALSE:
							indiv.mutation(true, opEnd, k, Observation.Value.ANY);
							break;
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
			});
		});
		return indiv;
	}

	/**
	 * 
	 * @param P
	 * @param A
	 * @return
	 */
	public Domain convert3op(List<Symbol> P, List<Symbol> A) {
		Domain indiv = new Domain();
		indiv.setP(P);
		indiv.setA(A);
		Map<Symbol, Pair<Observation, Observation>> domainStart = 
				this.start.getDomain();
		Map<Symbol, Pair<Observation, Observation>> domainEnd = 
				this.end.getDomain();
		Map<Symbol, Pair<Observation, Observation>> domainInv = 
				this.invariant.getDomain();
		List<Symbol> operators = new ArrayList<>(this.durations.keySet());
		operators.forEach(op -> {
			Symbol opStart = op.clone();
			opStart.setName(opStart.getName()+"-start");
			Symbol opEnd = op.clone();
			opEnd.setName(opEnd.getName()+"-end");
			Symbol opInv = op.clone();
			opInv.setName(opInv.getName()+"-inv");
			indiv.addOperator(opStart, this.start.getP());
			indiv.addOperator(opInv, this.start.getP());
			indiv.addOperator(opEnd, this.start.getP());
			//Start
			domainStart.get(op).getX().getPredicates().forEach((k,v) -> {
				//System.out.println(opStart+" "+k+" "+v);
				indiv.mutation(true, opStart, k, v);
			});
			domainStart.get(op).getY().getPredicates().forEach((k,v) -> {
				indiv.mutation(false, opStart, k, v);
			});
			//End
			domainEnd.get(op).getX().getPredicates().forEach((k,v) -> {
				if(v != Observation.Value.FALSE) {
					indiv.mutation(true, opEnd, k, v);
				}
			});
			domainEnd.get(op).getY().getPredicates().forEach((k,v) -> {
				indiv.mutation(false, opEnd, k, v);
			});
			//Inv
			domainInv.get(op).getX().getPredicates().forEach((k,v) -> {
				indiv.mutation(true, opInv, k, v);
			});
			
			//Remove identical effect
			indiv.getDomain().get(opStart).getY().getPredicates().forEach((k,v) -> {
				if(indiv.getDomain().get(opEnd).getY().getValue(k) == v) {
					indiv.mutation(false, opStart, k, Observation.Value.ANY);
					indiv.mutation(false, opEnd, k, Observation.Value.ANY);
				}
			});
			
			//Remove identical preconditions
			indiv.getPrecond(opInv).getPredicates().forEach((k,v)-> {
				if(v == Observation.Value.TRUE || v == Observation.Value.FALSE) {
					if(indiv.getPrecond(opStart).getValue(k) == v) {
						indiv.mutation(true, opStart, k, Observation.Value.ANY);
					}
					if(indiv.getPrecond(opEnd).getValue(k) == v) {
						indiv.mutation(true, opEnd, k, Observation.Value.ANY);
					}
				}
			});
			
			//Remove inconsistant effect p in s and !p in end or inv
			indiv.getDomain().get(opStart).getY().getPredicates().forEach((k,v) -> {
				switch(v) {
				case FALSE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case TRUE:
						indiv.mutation(false, opStart, k, Observation.Value.ANY);
						indiv.mutation(true, opEnd, k, Observation.Value.ANY);
						break;
					default:
						break;
					}
					switch(indiv.getDomain().get(opInv).getX().getValue(k)) {
					case TRUE:
						indiv.mutation(false, opStart, k, Observation.Value.ANY);
						indiv.mutation(true, opInv, k, Observation.Value.ANY);
						break;
					default:
						break;
					}
					break;
				case TRUE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case FALSE:
						indiv.mutation(false, opStart, k, Observation.Value.ANY);
						indiv.mutation(true, opEnd, k, Observation.Value.ANY);
						break;
					default:
						break;
					}
					switch(indiv.getDomain().get(opInv).getX().getValue(k)) {
					case FALSE:
						indiv.mutation(false, opStart, k, Observation.Value.ANY);
						indiv.mutation(true, opInv, k, Observation.Value.ANY);
						break;
					default:
						break;
					}
					break;
				default:
					break;

				}
			});
			
			//If end effect in overall change overall to start preconditions
			indiv.getDomain().get(opInv).getX().getPredicates().forEach((k,v) -> {
				switch(v) {
				case FALSE:
					switch(indiv.getDomain().get(opEnd).getY().getValue(k)) {
					case TRUE:
						indiv.mutation(true, opInv, k, Observation.Value.ANY);
						indiv.mutation(true, opStart, k, Observation.Value.FALSE);
						break;
					default:
						break;
					}
					break;
				case TRUE:
					switch(indiv.getDomain().get(opEnd).getY().getValue(k)) {
					case FALSE:
						indiv.mutation(true, opInv, k, Observation.Value.ANY);
						indiv.mutation(true, opStart, k, Observation.Value.TRUE);
						break;
					default:
						break;
					}
					break;
				default:
					break;

				}
			});
		});
		return indiv;
	}
	public boolean isConsistant() {
		//Check Identical Effect
		for(Symbol op : this.start.getDomain().keySet()) {
			Observation startEffects = this.start.getDomain().get(op).getY();
			Observation endEffects = this.end.getDomain().get(op).getY();
			for(Symbol e : startEffects.getPredicatesSymbols()) {
				switch(startEffects.getValue(e)) {
				case TRUE:
					switch(endEffects.getValue(e)) {
					case TRUE:
						return false;
					default:
						break;
					}
					break;
				case FALSE:
					switch(endEffects.getValue(e)) {
					case FALSE:
						return false;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		}
		
		//Check incompatible overall/end preconditions
		for(Symbol op : this.end.getDomain().keySet()) {
			Observation endPreconditions = this.end.getDomain().get(op).getX();
			Observation overAllPreconditions = this.invariant.getDomain().get(op).getX();
			for(Symbol e : endPreconditions.getPredicatesSymbols()) {
				switch(endPreconditions.getValue(e)) {
				case TRUE:
					switch(overAllPreconditions.getValue(e)) {
					case FALSE:
						return false;
					default:
						break;
					}
					break;
				case FALSE:
					switch(overAllPreconditions.getValue(e)) {
					case TRUE:
						return false;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		}
		
		//Check inconsistant start effect
		for(Symbol op : this.start.getDomain().keySet()) {
			Observation startEffects = this.start.getDomain().get(op).getY();
			Observation endPreconditions = this.end.getDomain().get(op).getX();
			Observation overAllPreconditions = this.invariant.getDomain().get(op).getX();
			for(Symbol e : endPreconditions.getPredicatesSymbols()) {
				switch(startEffects.getValue(e)) {
				case TRUE:
					switch(overAllPreconditions.getValue(e)) {
					case FALSE:
						return false;
					default:
						break;
					}
					switch(endPreconditions.getValue(e)) {
					case FALSE:
						return false;
					default:
						break;
					}
					break;
				case FALSE:
					switch(overAllPreconditions.getValue(e)) {
					case TRUE:
						return false;
					default:
						break;
					}
					switch(endPreconditions.getValue(e)) {
					case TRUE:
						return false;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		}
		return true;
	}
	
	public boolean isConsistant3Op() {
		Domain indiv = new Domain();
		//indiv.setConsistant(true);
		Map<Symbol, Pair<Observation, Observation>> domainStart = 
				this.start.getDomain();
		Map<Symbol, Pair<Observation, Observation>> domainEnd = 
				this.end.getDomain();
		Map<Symbol, Pair<Observation, Observation>> domainInv = 
				this.invariant.getDomain();
		List<Symbol> operators = new ArrayList<>(this.durations.keySet());
		operators.forEach(op -> {
			Symbol opStart = op.clone();
			opStart.setName(opStart.getName()+"-start");
			Symbol opEnd = op.clone();
			opEnd.setName(opEnd.getName()+"-end");
			Symbol opInv = op.clone();
			opInv.setName(opInv.getName()+"-inv");
			indiv.addOperator(opStart, this.start.getP());
			indiv.addOperator(opEnd, this.start.getP());
			indiv.addOperator(opInv, this.invariant.getP());
			
			//Start
			domainStart.get(op).getX().getPredicates().forEach((k,v) -> {
				indiv.mutation(true, opStart, k, v);
			});
			domainStart.get(op).getY().getPredicates().forEach((k,v) -> {
				indiv.mutation(false, opStart, k, v);
			});
			domainInv.get(op).getX().getPredicates().forEach((k,v) -> {
				indiv.mutation(true, opInv, k, v);
			});
			//End
			domainEnd.get(op).getX().getPredicates().forEach((k,v) -> {
				indiv.mutation(true, opEnd, k, v);
			});
			domainEnd.get(op).getY().getPredicates().forEach((k,v) -> {
				indiv.mutation(false, opEnd, k, v);
			});
			/*domainInv.get(op).getX().getPredicates().forEach((k,v) -> {
				if(v == Observation.Value.TRUE || v == Observation.Value.FALSE) {
					indiv.mutation(true, opEnd, k, v);
				}
			});*/

			//Remove identical effect
			indiv.getDomain().get(opStart).getY().getPredicates().forEach((k,v) -> {
				if(indiv.getDomain().get(opEnd).getY().getValue(k) == v && v != Observation.Value.ANY) {
					//indiv.setConsistant(false);
				}
			});
			
			//Remove inconsistant effect p in s and !p in end or inv
			indiv.getDomain().get(opStart).getY().getPredicates().forEach((k,v) -> {
				switch(v) {
				case FALSE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case TRUE:
						//indiv.setConsistant(false);
						break;
					default:
						break;
					}
					switch(indiv.getDomain().get(opInv).getX().getValue(k)) {
					case TRUE:
						//indiv.setConsistant(false);
						break;
					default:
						break;
					}
					break;
				case TRUE:
					switch(indiv.getDomain().get(opEnd).getX().getValue(k)) {
					case FALSE:
						//indiv.setConsistant(false);
						break;
					default:
						break;
					}
					switch(indiv.getDomain().get(opInv).getX().getValue(k)) {
					case FALSE:
						//indiv.setConsistant(false);
						break;
					default:
						break;
					}
					break;
				default:
					break;

				}
			});
		});
		return indiv.isConsistant();
	}
	/**
	 * 
	 * @param indiv
	 * @param durations
	 * @param twoOp
	 * @return
	 */
	public static Domain convert(Domain indiv, 
			Map<Symbol, Float> durations, boolean twoOp, List<Symbol> P,
			List<Symbol> A, List<Symbol> Atemp) {
		/*if(TemporalDomain.previousConversion.containsKey(indiv)) {
			return TemporalDomain.previousConversion.get(indiv);
		}*/
		TemporalDomain temp = new TemporalDomain(indiv, durations, twoOp, P, Atemp);
		if(twoOp) {
			if(!temp.isConsistant()) {
				return null;
			}
		} else {
			if(!temp.isConsistant3Op()) {
				return null;
			}
		}
		//System.out.println(temp);
		Domain res = twoOp ? temp.convert(P, A) : temp.convert3op(P, A);
		TemporalDomain.previousConversion.put(indiv, res);
		return res;
	}
	
	public static Domain convert2(Domain indiv,  
			Map<Symbol, Float> durations, boolean twoOp, List<Symbol> P,
			List<Symbol> A, List<Symbol> Atemp) {
		TemporalDomain temp = 
				new TemporalDomain(indiv, durations, twoOp, P, Atemp);
		Domain res = twoOp ? temp.convert(P, A) : temp.convert3op(P, A);
		TemporalDomain.previousConversion.put(indiv, res);
		return res;
	}
	
	/**
	 * 
	 * @param i
	 * @param twoOp
	 * @return
	 */
	public static boolean isConsistant(Domain i, boolean twoOp) {
		if(!i.isConsistant()) {
			return false;
		}
		if(twoOp) {
			for(Symbol op : i.getDomain().keySet()) {
				if(isStart.get(op)) {
					if(!i.getPostcond(op).noInverse(i.getPrecond(
							getEnd.get(op)))) {
						return false;
					}
					if(!i.getPostcond(op).noIdentical(i.getPostcond(
							getEnd.get(op)))) {
						return false;
					}
				}
			};
		} else {
			for(Symbol op : i.getDomain().keySet()) {
				if(isStart.get(op)) {
					if(!i.getPostcond(op).noInverse(i.getPrecond(
							getEnd.get(op)))) {
						return false;
					}
					if(!i.getPostcond(op).noInverse(i.getPrecond(
							getInv.get(op)))) {
						return false;
					}
					if(!i.getPostcond(op).noIdentical(i.getPostcond(
							getEnd.get(op)))) {
						return false;
					}
					if(!i.getPostcond(op).noIdentical(i.getPostcond(
							getInv.get(op)))) {
						return false;
					}
				}
				if(isInv.get(op)) {
					if(!i.getPrecond(op).noInverse(i.getPostcond(
							getEnd.get(op)))) {
						return false;
					}
					if(!i.getPrecond(op).noIdentical(i.getPostcond(
							getEnd.get(op)))) {
						return false;
					}
					if(!i.getPrecond(op).noIdentical(i.getPrecond(
							getEnd.get(op)))) {
						//System.out.println(op);
						return false;
					}
					
					if(!i.getPrecond(op).noInverse(i.getPrecond(
							getStart.get(op)))) {
						return false;
					}
					if(!i.getPrecond(op).noIdentical(i.getPrecond(
							getStart.get(op)))) {
						return false;
					}
				}
			};
		}
		return true;
	}
	
	public static List<Domain> neighbors(Domain indiv, boolean twoOp) {
		List<Domain> res = new ArrayList<>();
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			indiv.getDomain().entrySet()) {
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				try {
					Domain clone1 = indiv.clone();
					Domain clone2 = indiv.clone();
					switch(entry.getValue().getX().getValue(s)) {
					case TRUE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(isConsistant(clone1, twoOp)) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(isConsistant(clone1, twoOp)) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(true, entry.getKey(), s, Observation.Value.TRUE);
						if(isConsistant(clone1, twoOp)) {
							res.add(clone1);
						}
						if(isConsistant(clone2, twoOp)) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
			for(Symbol s : entry.getValue().getY().getPredicatesSymbols()) {
				try {
					if(twoOp || !isInv.get(entry.getKey())) {
						Domain clone1 = indiv.clone();
						Domain clone2 = indiv.clone();
						switch(entry.getValue().getY().getValue(s)) {
						case TRUE:
							clone1.mutation(
									false, 
									entry.getKey(), 
									s, 
									Observation.Value.ANY);
							if(isConsistant(clone1, twoOp)) {
								res.add(clone1);
							}
							break;
						case FALSE:
							clone1.mutation(
									false, 
									entry.getKey(), 
									s, 
									Observation.Value.ANY);
							if(isConsistant(clone1, twoOp)) {
								res.add(clone1);
							}
							break;
						case ANY:
							clone1.mutation(
									false, 
									entry.getKey(), 
									s, 
									Observation.Value.FALSE);
							clone2.mutation(
									false, 
									entry.getKey(), 
									s, 
									Observation.Value.TRUE);
							if(isConsistant(clone1, twoOp)) {
								res.add(clone1);
							}
							if(isConsistant(clone2, twoOp)) {
								res.add(clone2);
							}
							break;
						default:
							break;
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public static Domain convertInto2Op(Domain i) {
		Domain res = i.clone();
		for(Symbol op : i.getDomain().keySet()) {
			if(isInv.get(op)) {
				Symbol opStart = getStart.get(op);
				Symbol opEnd = getEnd.get(op);
				i.getPrecond(op).getPredicates().forEach((k,v) -> {
					switch(v) {
					case TRUE:
					case FALSE:
						res.mutation(true, opStart, k, v);
						res.mutation(true, opEnd, k, v);
						break;
					default:
						break;
					}
				});
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public static Domain convertInto3Op(Domain i) {
		Domain res = i.clone();
		for(Symbol op : i.getDomain().keySet()) {
			if(isStart.get(op)) {
				Symbol opInv = getInv.get(op);
				Symbol opEnd = getEnd.get(op);
				i.getPrecond(op).getPredicates().forEach((k,v) -> {
					switch(v) {
					case TRUE:
						switch(i.getPrecond(opEnd).getValue(k)) {
						case TRUE:
							res.mutation(true, op, k, Observation.Value.ANY);
							res.mutation(true, opEnd, k, Observation.Value.ANY);
							res.mutation(true, opInv, k, v);
							break;
						default:
							break;
						}
					case FALSE:
						switch(i.getPrecond(opEnd).getValue(k)) {
						case TRUE:
							res.mutation(true, op, k, Observation.Value.ANY);
							res.mutation(true, opEnd, k, Observation.Value.ANY);
							res.mutation(true, opInv, k, v);
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
				});
			}
		}
		return res;
	}
}
