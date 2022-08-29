/**
 * 
 */
package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uga.generator.exception.PlanException;
import fr.uga.generator.simulator.StripsSimulator;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.CompressedNegativeExample;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.problem.operator.Effect;
import fr.uga.pddl4j.util.BitVector;
import fr.uga.generator.utils.Pair;
import learning.Domain;

/**
 * @author Maxence Grand
 *
 */
public class DomainSimulator extends StripsSimulator{

	/**
	 * The domain
	 */
	protected Domain domain;
	
	/**
	 * The initial state
	 */
	protected Observation initial;
	/**
	 * All instances
	 */
	protected Map<Symbol, List<Symbol>> instances;
	/**
	 * Predicate index
	 */
	protected Map<Symbol, Integer> predicateIndex;
	/**
	 * All preconditions
	 */
	protected Map<Symbol, Observation> preconditions;
	/**
	 * All effects
	 */
	protected Map<Symbol, Observation> effects;
	
	/**
	 * 
	 * Constructs
	 */
	public DomainSimulator() {
		super();
		this.instances = new HashMap<>();
		this.predicateIndex = new HashMap<>();
		this.preconditions = new HashMap<>();
		this.effects = new HashMap<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param domain
	 * @param predicates
	 * @param positiveStaticPredicates
	 * @param actions
	 */
	public DomainSimulator(
			Domain domain, 
			List<Symbol> predicates,
			List<Symbol> actions,
			Observation initial) {
		this();
		this.setDomain(domain);
		this.setActions(actions);
		this.setPredicates(predicates);
		this.setInitial(initial);
		this.instantiate();
		Pair<BitVector, BitVector> tmp = this.getVector(this.getInitial());
		Condition tmp2 = new Condition(tmp.getX(), tmp.getY());
		super.setInternalInit(new Node(new State(tmp2)));
	}

	/**
	 * Instantiation
	 */
	public void instantiate() {
		int nbFluents = this.getPredicates().size();
		//Create fluents
		for(int i = 0; i < nbFluents; i++) {
			Symbol p = this.getPredicates().get(i);
			Fluent f = new Fluent(i, new int[0]);
			this.getInternalFluents().add(f);
			this.getTableFluents().put(p, f);
			this.predicateIndex.put(p, i);
		}
		//Create Actions
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = this.domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		for(Symbol a : this.getActions()) {
			if(!this.instances.containsKey(a.generalize())) {
				this.instances.put(a.generalize(), new ArrayList<>());
			}
			this.instances.get(a.generalize()).add(a);
			Action action = this.instantiate(a, preconditions, postconditions);
			this.getTableActions().putLeft(a, action);
		}
	}
	
	/**
	 * Make movement
	 * @param movement The movement
	 */
	public void makeMovement(Movement movement) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = this.domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		for(Symbol a : this.instances.get(movement.getOperator())) {
			Observation prec = preconditions.get(a.generalize());
			prec = prec.instanciate(utils.Utils.reverseParamMap(a.mapping()));
			Observation post = postconditions.get(a.generalize());
			post = post.instanciate(utils.Utils.reverseParamMap(a.mapping()));
			Symbol p = movement.getPredicate().generalize(utils.Utils.reverseParamMap(a.mapping()));
			if(movement.isPrecondition()) {
				prec.addObservation(p, movement.getValue());
			} else {
				post.addObservation(p, movement.getValue());
			}
			this.preconditions.put(a, prec);
			this.effects.put(a, post);
			Pair<BitVector, BitVector> condition = this.getVector(prec);
			Pair<BitVector, BitVector> effect = this.getVector(post);
			Action action = new Action(
					a.getName(),
					0,
					new Condition(condition.getX(), condition.getY()),
					new Effect(effect.getX(), effect.getY()));
			this.getTableActions().put(a, action);
		}
	}
	
	/**
	 * 
	 * @param movement
	 */
	public void backtrack(Movement movement) {
//		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
//		pair = this.domain.decode();
//		Map<Symbol, Observation> preconditions = pair.getX();
//		Map<Symbol, Observation> postconditions = pair.getY();
//		for(Symbol a : this.instances.get(movement.getOperator())) {
//			Action action = this.instantiate(a, preconditions, postconditions);
//			this.getTableActions().put(a, action);
//		}
		this.makeMovement(movement.inverse());
	}
	
	/**
	 * Getter of domain
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Setter domain
	 * @param domain the domain to set
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	
	/**
	 * Getter of initial
	 * @return the initial
	 */
	public Observation getInitial() {
		return initial;
	}

	/**
	 * Setter initial
	 * @param initial the initial to set
	 */
	public void setInitial(Observation initial) {
		this.initial = initial;
	}

	/**
     * Negative fit score
     * @param compressed An example
     * @return The fit score
     */
    public float fitnessNegative(CompressedNegativeExample compressed) {
    	float res = 0;
    	this.reInit();
    	for(int i = 0; i< compressed.getPrefix().size(); i++) {
    		//Test negative
    		for(Example n : compressed.getNegativeIndex(i)) {
    			for(Symbol act : n.getActionSequences()) {
    				if(! this.testAction(act)) {
    	    			res++;
    	    		}
    			}
    		}
    		//Test Positive
    		if(i < compressed.getPrefix().size()) {
    			if(this.testAction(compressed.getPrefix().get(i))) {
    				try {
    					this.apply(compressed.getPrefix().get(i));
    				} catch(Exception e) {
    					break;
    				}
    			} else {
    				break;
    			}
    		}
    	}
    	return res;
    }
    
    /**
     * Negative fit score
     * @param compressed Examples
     * @return The fit score
     */
    public int fitNegative(List<CompressedNegativeExample> compressed) {
		int res = 0;

		for(CompressedNegativeExample plan : compressed){
			Observation current = this.getInitialState().clone();
			for(int i = 0; i< plan.getPrefix().size(); i++) {
				//Test Negatives
				for(Example n : plan.getNegativeIndex(i)) {
					Observation current2 = current.clone();
					for(Symbol a : n.getActionSequences()) {
						Observation prec = this.preconditions.get(a);
						if(!current2.contains(prec)) {
							res++;
							break;
						} else {
							Observation eff = this.effects.get(a);
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
					Observation prec = getPrecondition(a);
					if(!current.contains(prec)) {
						break;
					}
					//Apply Effect
					Observation eff = this.getEffect(a);
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
     * Negative fit score
     * @param compressed Examples
     * @return The fit score
     */
    public float fitnessNegative(List<CompressedNegativeExample> compressed) {
    	float res = 0;
    	for(CompressedNegativeExample e : compressed) {
    		res += this.fitnessNegative(e);
    	}
    	return res;
    }
    
    /**
     * Positive fit score
     * @param pos Examples
     * @return The fit score
     */
    public float fitnessPositive(Sample pos) {
    	float res = 0f;
    	for(Trace e : pos.getExamples()) {
    		this.reInit();
    		for(Symbol a : e.getActionSequences()) {
    			if(this.testAction(a)) {
    				try {
						this.apply(a);
						res++;
					} catch (PlanException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.exit(1);
					}
    			}
    		}
    	}
    	return res;
    }
    
    /**
     * Precondition fit score
     * @param pos Examples
     * @return The fit score
     */
    public float firnessPrecondition(Sample pos) {
    	float fit = 0f, err = 0f;
    	for(Trace plan : pos.getExamples()) {
			ObservedExample obs = (ObservedExample) plan;
			for(int i = 0; i< obs.size(); i++) {
				Symbol act = obs.get(i);
				Observation ante = obs.ante(i);
				Observation prec = this.getPrecondition(act);
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
     * Effect fit score
     * @param pos Examples
     * @return The fit score
     */
    public float firnessEffect(Sample pos) {
    	int fit=0, err=0;
		for(Trace plan : pos.getExamples()) {
			ObservedExample obs = (ObservedExample) plan;
			for(int i = 0; i< obs.size(); i++) {
				Symbol act = obs.get(i);
				Observation ante = obs.ante(i);
				Observation next = obs.post(i);
				Observation post = this.getEffect(act);
				for(Symbol eff : post.getPredicatesSymbols()) {
					switch(post.getValue(eff)) {
					case TRUE:
						switch(next.getValue(eff)) {
						case TRUE:
							if(ante.getValue(eff)!=post.getValue(eff)) {
								fit++;
							}else {
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
		return fit-err;
    }
    
    /**
     * Fit score
     * @param pos Positive Examples
     * @param neg Negative Examples
     * @return The fit score
     */
    public float fitness(Sample pos, List<CompressedNegativeExample> neg) {
    	return
    			this.firnessPrecondition(pos) +
    			this.firnessEffect(pos) +
    			this.fitnessPositive(pos) +
    			this.fitnessNegative(neg);
    }
    
    /**
	 * Print fitness
     * @param pos Positive Examples
     * @param neg Negative Examples
	 */
	public void printFitnessComponents(
			Sample pos, 
			List<CompressedNegativeExample> neg) {
		float fitPos = this.fitnessPositive(pos);
		float fitNeg = this.fitnessNegative(neg);
		float fitNeg2 = this.fitNegative(neg);
		float fitPrec = this.firnessPrecondition(pos);
		float fitPost = this.firnessEffect(pos);
		System.out.println(fitPos+" "+fitNeg+" "+fitNeg2+" "+fitPrec+" "+fitPost);
	}
	
    /**
     * Instantiate an action
     * @param a The action 
     * @param preconditions Preconditions
     * @param postconditions Effects
     * @return Instantiated action
     */
    private Action instantiate(
    		Symbol a,
    		Map<Symbol, Observation> preconditions,
    		Map<Symbol, Observation> postconditions) {
    	Observation prec = preconditions.get(a.generalize());
    	prec = prec.instanciate(utils.Utils.reverseParamMap(a.mapping()));
		Observation post = postconditions.get(a.generalize());
		post = post.instanciate(utils.Utils.reverseParamMap(a.mapping()));
		this.preconditions.put(a, prec);
		this.effects.put(a, post);
		Pair<BitVector, BitVector> condition = this.getVector(prec);
		Pair<BitVector, BitVector> effect = this.getVector(post);
		Action action = new Action(
				a.getName(),
				0,
				new Condition(condition.getX(), condition.getY()),
				new Effect(effect.getX(), effect.getY()));
		return action;
    }
    
    /**
     * 
     * @param o
     * @return
     */
    private Pair<BitVector, BitVector> getVector(Observation o) {
    	final BitVector positive = new BitVector(this.getInternalFluents().size());
    	final BitVector negative = new BitVector(this.getInternalFluents().size());
    	o.getPredicates().forEach((k,v) -> {
    		Integer idxFluent = this.predicateIndex.get(k);
    		if(idxFluent == null) {
    			idxFluent = this.getPredicates().size();
    			Fluent f = new Fluent(idxFluent, new int[0]);
    			this.getPredicates().add(k);
    			this.getInternalFluents().add(f);
    			this.getTableFluents().put(k, f);
    			this.predicateIndex.put(k, idxFluent);
    			this.getInitial().addFalseObservation(k);
    			Pair<BitVector, BitVector> tmp = this.getVector(this.getInitial());
    			Condition tmp2 = new Condition(tmp.getX(), tmp.getY());
    			super.setInternalInit(new Node(new State(tmp2)));
    		}
    		
    		switch(v) {
			case FALSE:
				negative.set(idxFluent, true);
				break;
			case TRUE:
				positive.set(idxFluent, true);
				break;
			case ANY:
			case MISSED:
			default:
				break;
    		
    		}
    	});
    	return new Pair<>(positive, negative);
    }
    
    /**
     * 
     * @param vector
     * @return
     */
    protected List<fr.uga.generator.symbols.Symbol> getPredicatesVector(BitVector vector) {
		List<Symbol> res = new ArrayList<>();
		for(int i = vector.nextSetBit(0); i >= 0; i = vector.nextSetBit(i+1)) {
			//System.out.println(this.getInternalFluents().get(i));
			//System.out.println(this.getTableFluents().getRight());
			Symbol p = this.getTableFluents().getRight(this.getInternalFluents().get(i));
			res.add(p);
		}
		return res;
	}
    
    /**
     * Get an action precondition
     * @param action An action
     * @return action's preconditions
     */
    public Observation getPrecondition(Symbol action) {
    	return this.preconditions.get(action);
    }
    
    /**
     * Get action effects
     * @param action An action
     * @return action's effects
     */
    public Observation getEffect(Symbol action) {
    	return this.effects.get(action);
    }
    
    /**
     * Compute fscore
     * @param pos Positive examples
     * @param neg Negative examples
     * @return fscore
     */
    public float fscore(Sample pos, Sample neg) {
    	float fscore = 0f, recall=0f, precision=0f;
    	int truePos=0, falsePos=0;
    	//Compute Recall
    	for(Trace t : pos.getExamples()) {
    		this.reInit();
    		if(this.accept(t)) {
    			truePos++;
    		}
    	}
    	recall = learning.Measure.accuracy(truePos, pos.size());
    	//Compute Precision
    	for(Trace t : neg.getExamples()) {
    		this.reInit();
    		if(this.accept(t)) {
    			falsePos++;
    		}
    	}
    	precision = learning.Measure.P(truePos, falsePos);
    	//Compute fscore
    	fscore = learning.Measure.FScore(recall, precision);
    	return fscore;
    }
    
    /*public void printActions() {
    	for(Symbol a : this.preconditions.keySet()) {
    		System.out.println(a+" "+this.getPrecondition(a)+" "+this.getEffect(a));
    	}
    }*/
    
    /*public void printExecution(Trace example) {
    	ObservedExample obs = (ObservedExample) example;
    	Observation current = this.getInitial().clone();
    	for(int i = 0; i< obs.size(); i++) {
			Symbol act = obs.get(i);
			Observation prec = this.getPrecondition(act);
			Observation post = this.getEffect(act);
			System.out.println(current);
			System.out.println("Prec : "+prec);
			System.out.println("Eff : "+post);
			for(Symbol e : post.getPredicatesSymbols()) {
				switch(post.getValue(e)) {
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
    }*/
}
