/**
 * 
 */
package fr.uga.amlsi.learning.temporal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.simulator.temporal.TemporalExample;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.TypeHierarchy;
import fr.uga.generator.symbols.trace.CompressedTemporalNegativeExample;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fr.uga.amlsi.fsm.DFA;
import fr.uga.amlsi.learning.DomainLearning;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.DomainsQueue;
import fr.uga.amlsi.learning.Mapping;
import fr.uga.amlsi.main.Argument;

/**
 * @author Maxence Grand
 *
 */
public class TemporalDomainLearning extends DomainLearning{
	
	/**
	 * The constructor of the object TemporalDomainLearning
	 */
	public TemporalDomainLearning() {
		predicates = new ArrayList<>();
		actions = new ArrayList<>();
		types = new TypeHierarchy();
	}

	/**
	 * The constructor of the object TemporalDomainLearning
	 *
	 * @param predicates The set of predicates
	 * @param actions The set of actions
	 * @param directory The directory where the learned domain is saved
	 * @param name The planning domain's name
	 * @param realDomain The domain to copy
	 * @param initialState The initial state
	 */
	public TemporalDomainLearning(
			List<Symbol> predicates,
			List<Symbol> actions,
			String directory,
			String name,
			String realDomain,
			String initialState,
			Generator generator
			) {
		this();
		this.name=name;
		this.generator = generator;
		for(Symbol s : predicates) {
			this.predicates.add(s);
		}
		for(Symbol s : actions) {
			this.actions.add(s);
		}
		this.initialState = initialState;
		this.directory = directory;
		learnedDomain = directory+"/domain.pddl";
	}
	
	/**
	 * Compute fitness for positive samples
	 *
	 * @param Domain The model
	 * @param data The observations
	 * @return fitness score
	 * @throws IOException 
	 */
	public float fitPositive(TemporalDomain Domain, List<TemporalExample> S, Observation is) {
		return Domain.rateOfAccepted(S, is);
	}

	/**
	 * Compute fitness for negative samples
	 *
	 * @param Domain The model
	 * @param data The observations
	 * @return fitness score
	 * @throws IOException 
	 */
	public float fitNegative(
			TemporalDomain Domain, List<TemporalExample> S, 
			List<CompressedTemporalNegativeExample> compressed, Observation is) {
		return 1-Domain.rateOfAccepted(S, is);
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
			Domain indiv, List<TemporalExample> pos,
			List<TemporalExample> neg,
			List<CompressedTemporalNegativeExample> compressed,
			Map<Symbol,Float> duration, boolean twoOp,
			Observation initial) {

		float fit;

		TemporalDomain tempIndiv = new TemporalDomain(indiv, duration, twoOp, this.predicates, this.actions);
		float fitPos = this.fitPositive(tempIndiv, pos, initial);
		//int scoreMaxPos = 0;
		float fitNeg = this.fitNegative(tempIndiv, neg, compressed, initial);
		//int scoreMaxNeg = 0;
		fit = (fitPos+fitNeg)/2;
		return fit;
	}
	
	/**
	 * Compute the tabu search
	 *
	 * @param indiv initial candidate
	 * @param data the observtions
	 * @param pos I+
	 * @param neg I-
	 * @param is The initial states
	 * @param size The buffer size
	 * @param epoch The number of epochs
	 * @param tabou The tabu list
	 * @return Best candidate
	 */
//	public Domain localSearch(
//			Domain indiv, Sample pos, Sample neg, 
//			List<CompressedNegativeExample> compressed, String is,
//			int size, int epoch,
//			List<Domain> tabou, Map<Symbol, Float> duration, boolean twoOp,
//			List<Symbol> Atemp) {
//		
//		Domain indiv2 = indiv.clone();
//		//System.out.println(indiv2+"\n");
//		indiv2 = (new TemporalDomain(indiv2, duration, twoOp, this.predicates, Atemp)).convert(this.predicates, this.actions);
//		
//		//System.out.println(TemporalDomain.neighbors(indiv2.neighbors(), duration, twoOp, this.predicates, this.actions, Atemp).size());
//		DomainsQueue queue = new DomainsQueue(size, tabou);
//		float fit = this.fitness(indiv2,  pos, neg, compressed,is);
//		queue.add(indiv2, fit);
//	//	List<Domain> dejavu = new ArrayList<>();
//		tabou.remove(indiv2);
//		int i =0;
//		for(i =0; i < epoch; i ++) {
//			System.out.println(i);
//			try {
//				Domain choosen = queue.next();
//				//System.out.println("****"+this.fitness(choosen, pos, neg, compressed,is));
//				List<Domain> neigh = choosen.neighbors();
//				List<Domain> neigh2 = TemporalDomain.neighbors(neigh, duration, twoOp, this.predicates, this.actions, Atemp);
//				for(Domain ind : neigh2) {
//					if(! queue.inTabou(ind)) {
//						fit = this.fitness(ind, pos, neg, compressed,is);
//						queue.add(ind, fit);
//					}
//				}
//			}catch(IllegalArgumentException e){
//				break;
//			}
//		}
//		try {
//			queue.next();
//		}catch(IllegalArgumentException e){
//
//		}
//		Domain res = tabou.get(0);
//		float fitRef = this.fitness(res, pos, neg, compressed,is);
//		for(Domain ind : tabou) {
//			fit = this.fitness(ind, pos, neg, compressed,is);
//			if(fit > fitRef) {
//				res = ind;
//				fitRef = fit;
//			}
//		}
//
//		return res;
//	}
	
//	public Domain localSearchTemporal(
//			Domain indiv,
//			List<TemporalExample> pos, List<TemporalExample> neg, 
//			List<CompressedTemporalNegativeExample> compressed,
//			int size, int epoch,
//			List<Domain> tabou, Map<Symbol, Float> duration, boolean twoOp,
//			Observation initial, List<Symbol> Atemp) {
//		DomainsQueue queue = new DomainsQueue(size, tabou);
//		float fit = this.fitness(indiv, pos, neg, compressed, duration, twoOp, initial);
//		if(fit == 1.0) {
//			return indiv;
//		}
//		queue.add(indiv, fit);
//		List<Domain> dejavu = new ArrayList<>();
//		tabou.remove(indiv);
//		int i =0;
//		for(i =0; i < epoch; i ++) {
//			try {
//				Domain choosen = queue.next();
//				//System.out.println("****"+i+" "+this.fitness(choosen, pos, neg, compressed, duration, twoOp, initial));
//				if(this.fitness(choosen, pos, neg, compressed, duration, twoOp, initial) == 1.0) {
//					return choosen;
//				}
//				dejavu.add(choosen);
//				List<Domain> neigh = choosen.neighbors();
//				List<Domain> neigh2 = TemporalDomain.neighbors(neigh, duration, twoOp, this.predicates, this.actions, Atemp);
//				List<Domain> toCompute = new ArrayList<>();
//				for(Domain ind : neigh2) {
//					if(! queue.inTabou(ind) && !dejavu.contains(ind)) {
//						toCompute.add(ind);
//						dejavu.add(ind);
//					}
//				}
//				for(Domain ind : toCompute) {
//					queue.add(ind, this.fitness(ind, pos, neg, compressed, duration, twoOp, initial));
//				}
//			}catch(IllegalArgumentException e){
//				break;
//			} 
//		}
//		try {
//			queue.next();
//		}catch(IllegalArgumentException e){
//
//		}
//		Domain res = tabou.get(0);
//		float fitRef = this.fitness(res, pos, neg, compressed,
//				duration, twoOp, initial);
//		for(Domain ind : tabou) {
//			fit = this.fitness(ind, pos, neg, compressed,
//					duration, twoOp, initial);
//			if(fit > fitRef) {
//				res = ind;
//				fitRef = fit;
//			}
//		}
//
//		return res;
//	}
	
	/**
	 * Refine operator
	 * @param ind The action model to refine
	 * @param A The automaton
	 * @param reduceMapping The mapping ante
	 * @param reduceMappingPost The mapping post
	 * @return The refined action model
	 * @throws BlocException
	 */
	public Domain refineOperator(
			Domain ind,
			DFA A,
			Mapping reduceMapping,
			Mapping reduceMappingPost,
			Sample pos) throws BlocException {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> p = ind.decode();
		Map<Symbol, Observation> preconditions = p.getX();
		Map<Symbol, Observation> postconditions = p.getY();
		int maxIter = 100;
		boolean b = true;
		while(b && maxIter > 0) {
			b = false;
			maxIter--;

			//Refine postcondition
			postconditions = refinePostcondition(reduceMapping,preconditions,
					postconditions, A, pos);

			//Refine precondition
			for(Symbol s : this.getActionTypes()){
				Observation prec_ = preconditions.get(s);
				Observation post = postconditions.get(s);
				Observation newPrec = refinePrecondition(
						prec_.clone(), post);
				newPrec.missedToAny();
				preconditions.put(s, newPrec);
				b |= !(newPrec.equals(prec_));
			}
			
			//System.exit(1);
		}

		List<Symbol> genAct = new ArrayList<>();
		for(Symbol a : this.actions) {
			if(! genAct.contains(a)) {
				genAct.add(a);
			}

		}

		return new Domain(
				this.predicates,
				genAct,
				preconditions,
				postconditions);
	}
	
	/**
	 * Effects refinement from fsa
	 * @param action the operator
	 * @param reduceMapping Mapping post
	 * @param preconditions all preconditions
	 * @param postcondition action's effects
	 * @param A fsa
	 * @return Refined action's effect
	 * @throws BlocException
	 */
	public Map<Symbol, Observation> refinePostcondition(
			Mapping reduceMapping,
			Map<Symbol, Observation> preconditions,
			Map<Symbol, Observation> postconditions,
			DFA A, Sample pos)
					throws BlocException{
		for(Trace trace : pos.getExamples()) {
			Example example = (Example) trace;
			int q = A.getQ0();
			for(int i = 0; i<example.size(); i++) {
				Symbol action = example.get(i).generalize();
				Symbol action2 = example.get(i);
				int qNext = A.getBlocTransition(A.getPartition().getBloc(q),
						action2).min();
				if(isStart(action2)) {
					Symbol nextAction = getEndAction(action2);
					Observation postcondition = postconditions.get(action);
					Observation effect = postcondition.
							instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(action2.mapping()));
					Observation prec = preconditions.get(action).
							instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(action2.mapping()));
					Observation currentState = new Observation();//reduceMapping.getStates(q, action2);
					currentState = currentState.addEffect(prec);
					currentState = currentState.addEffect(effect);
					Observation precNext = preconditions.get(nextAction.generalize()).
							instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(nextAction.mapping()));
					for(Symbol p : precNext.getPredicatesSymbols()) {
						if(action2.compatible(p)) {
							switch(precNext.getValue(p)) {
							case TRUE:
								switch(currentState.getValue(p)) {
								case FALSE:
									//System.out.println(action2+" "+nextAction+" pos "+p);
									postcondition.addTrueObservation(p.generalize(
											action2.mapping()));
									break;
								default:
									break;
								}
								break;
							case FALSE:
								switch(currentState.getValue(p)) {
								case TRUE:
									//System.out.println(action2+" "+nextAction+" neg "+p);
									postcondition.addFalseObservation(p.generalize(
											action2.mapping()));
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
					//System.out.println(postcondition);
					postconditions.put(action, postcondition);
				}
				q=qNext;
			}
		}
		for(Trace trace : pos.getExamples()) {
			Example example = (Example) trace;
			int q = A.getQ0();
			//System.out.println(example);
			for(int i = 0; i<example.size()-1; i++) {
				Symbol action = example.get(i).generalize();
				Symbol action2 = example.get(i);
				Symbol nextAction = example.get(i+1);
				int qNext = A.getBlocTransition(A.getPartition().getBloc(q),
						action2).min();
				Observation postcondition = postconditions.get(action);
				Observation effect = postcondition.
						instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(action2.mapping()));
				Observation prec = preconditions.get(action).
						instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(action2.mapping()));
				Observation currentState = reduceMapping.getStates(q, action2);
				Observation temp = currentState.clone();
				currentState = currentState.addEffect(prec);
				currentState = currentState.addEffect(effect);
				Observation precNext = preconditions.get(nextAction.generalize()).
						instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(nextAction.mapping()));
				for(Symbol p : precNext.getPredicatesSymbols()) {
					if(action2.compatible(p)) {
						switch(precNext.getValue(p)) {
						case TRUE:
							switch(currentState.getValue(p)) {
							case FALSE:
								//System.out.println(temp.getValue(p)+" "+action2+" "+nextAction+" pos "+p);
								postcondition.addTrueObservation(p.generalize(
										action2.mapping()));
								break;
							default:
								break;
							}
							break;
						case FALSE:
							switch(currentState.getValue(p)) {
							case TRUE:
								//temp.getValue(p);
								/*reduceMapping.getSetStates(q, action2).forEach(o -> {
									System.out.println(o.getValue(p));
								});
								System.out.println(temp.getValue(p)+" "+action2+" "+nextAction+" neg "+p);*/
								postcondition.addFalseObservation(p.generalize(
										action2.mapping()));
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
				postconditions.put(action, postcondition);
				q=qNext;
			}
		}
		return postconditions;
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public boolean isStart(Symbol a) {
		return getTimeLabel(a).equals("start");
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public String getTimeLabel(Symbol a) {
		String[] tab = a.getName().split("-");
		String label = tab[tab.length-1];
		return label;
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public String getNameWithoutTimeLabel(Symbol a) {
		String[] tab = a.getName().split("-");
		String name = a.getName();
		String label = tab[tab.length-1];
		name = name.substring(0, name.length()-1-label.length());
		return name;
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public Symbol getEndAction(Symbol a) {
		String name = getNameWithoutTimeLabel(a);
		name = name+"-end";
		Symbol aEnd = a.clone();
		aEnd.setName(name);
		return aEnd;
	}
	
//	public Domain localSearch(
//			Domain indiv, 
//			Sample pos, 
//			Sample neg, 
//			List<CompressedNegativeExample> compressed, 
//			String is,
//			int size, 
//			int epoch,
//			List<Domain> tabou, Map<Symbol, Float> durations, 
//			List<Symbol>  tempActions) {
//		DomainsQueue queue = new DomainsQueue(size, tabou);
//		float fit = this.fitness(indiv, pos, neg, compressed, is);
//		//if(!TemporalDomain.isConsistant(indiv, Argument.isTwoOp())) {
//		/*indiv = TemporalDomain.convert2(
//				indiv, 
//				durations, 
//				Argument.isTwoOp(), 
//				this.predicates, 
//				this.actions, 
//				tempActions);*/
//		//}
//		//System.out.println(TemporalDomain.isConsistant(indiv, Argument.isTwoOp()));
//		queue.add(indiv, fit);
//		tabou.remove(indiv);
//		int i =0;
//		for(i =0; i < epoch; i ++) {
//			try {
//				Domain choosen = queue.next();
//				List<Domain> neigh = TemporalDomain.neighbors(
//						choosen, Argument.isTwoOp());
//				System.err.println(i+" "+neigh.size());
//				/*List<Domain> neigh2 = TemporalDomain.neighbors(
//						neigh, 
//						durations, 
//						Argument.isTwoOp(), 
//						this.predicates, 
//						this.actions, 
//						tempActions);*/
//				for(Domain ind : neigh) {
//					if(! queue.inTabou(ind)) {
//						if(super.fitnessScores.containsKey(ind)) {
//							queue.add(ind, super.fitnessScores.get(ind));
//						} else {
//							fit = this.fitness(ind, pos, neg, compressed, is);
//							super.fitnessScores.put(ind, fit);
//							long a1 = System.currentTimeMillis();
//							fit = this.fitness(ind, pos, neg, compressed, is);
//							queue.add(ind, fit);
//							long a2 = System.currentTimeMillis();
//							//System.out.println((a2-a1));
//						}
//					}
//				}
//			}catch(IllegalArgumentException e){
//				break;
//			}
//		}
//		try {
//			queue.next();
//		}catch(IllegalArgumentException e){
//
//		}
//		Domain res = tabou.get(0);
//		float fitRef = this.fitness(res, pos, neg, compressed, is);
//		for(Domain ind : tabou) {
//			fit = this.fitness(ind, pos, neg, compressed, is);
//			if(fit > fitRef) {
//				res = ind;
//				fitRef = fit;
//			}
//		}
//
//		return res;
//	}
}
