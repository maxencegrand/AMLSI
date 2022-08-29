package fr.uga.amlsi.learning;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.Task;
import fr.uga.generator.symbols.TypeHierarchy;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;

import java.util.HashMap;
import fr.uga.amlsi.fsm.*;

/**
 * This class implements the action model learning module.
 *
 * @author Maxence Grand
 * @version 1.0
 * @since   10-2019
 */
public class DomainLearning {
	/**
	 * The size of the buffer for tabu search
	 */
	public static final int bufferSize = 10;
	/**
	 * The size of the tabu list
	 */
	public static final int tabouSize = 10;
	/**
	 * The number of epochs for the tabu search
	 */
	public static int tabouEpoch = 200;
	/**
	 * The set of predicates
	 */
	protected List<Symbol> predicates;
	/**
	 * The set of predicates
	 */
	protected List<Symbol> actions;
	/**
	 * The positive test sample
	 */
	protected Sample testPositive;
	/**
	 * The negative test sample
	 */
	protected Sample testNegative;
	/**
	 * The directory where we save logs
	 */
	protected String directory;
	/**
	 * The planning domain's name
	 */
	protected String name;
	/**
	 * The initial state
	 */
	protected String initialState;
	/**
	 * A set of fsa
	 */
	protected DFA As;
	/**
	 * A mapping
	 */
	protected Mapping mappings;
	/**
	 * A mapping
	 */
	protected Mapping mappingPosts;
	/**
	 * The file where the learned domain is saved
	 */
	protected String learnedDomain;

	/**
	 * The Domain learned
	 */
	protected Domain learned;
	/**
	 * The genarator
	 */
	protected Generator generator;

	/**
	 * The type hierarchy
	 */
	protected TypeHierarchy types;
	
	/**
	 * 
	 */
	protected Map<Domain, Float> fitnessScores;

	/**
	 * The type hierarchy
	 * @return The type hierarchy
	 */
	public TypeHierarchy getTypes() {
		return types;
	}

	/**
	 * Step types hierarchy
	 * @param types
	 */
	public void setTypes(TypeHierarchy types) {
		this.types = types;
	}

	/**
	 * The constructor of the object DomainLearning
	 */
	public DomainLearning() {
		predicates = new ArrayList<>();
		actions = new ArrayList<>();
		types = new TypeHierarchy();
		fitnessScores = new ConcurrentHashMap<>();
	}

	/**
	 * The constructor of the object DomainLearning
	 *
	 * @param predicates The set of predicates
	 * @param actions The set of actions
	 * @param directory The directory where the learned domain is saved
	 * @param name The planning domain's name
	 * @param realDomain The domain to copy
	 * @param initialState The initial state
	 */
	public DomainLearning(
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
	 * Set test samples
	 * @param pos The positive test sample
	 * @param neg The negative test sample
	 */
	public void setSamples(Sample pos, Sample neg) {
		this.testPositive = pos;
		this.testNegative = neg;
	}

	/**
	 * Generate PDDL operator
	 * @param A The automaton
	 * @param reduceMapping The mapping ante
	 * @param reduceMappingPost The mapping post
	 * @return The action model
	 * @throws BlocException
	 */
	public  Domain generatePDDLOperator(
			DFA A,
			Mapping reduceMapping,
			Mapping reduceMappingPost) throws BlocException {
		As = A;
		mappingPosts = reduceMappingPost;
		mappings = reduceMapping;

		List<Symbol> symbols = new ArrayList<>();

		//Map actions with preconditions/effects
		Map<Symbol, List<Observation>> preconditions = new HashMap<>();
		for(Symbol s : this.getActionTypes()){
			preconditions.put(s, new ArrayList<>());
		}

		//For each operator
		for(Symbol s : this.getActionTypes()){

			//Learn instanciate
			for(Symbol a : getActionOfType(actions, s)) {
				Observation tmp = learnPrecondition(a,
						mappings.getSet(a),
						As);
//				System.out.println(a+" "+tmp);
				Map<String, String> map = a.mapping();
				//Gen contains all preconditions for action a for each states
				//where a was feasible

				Observation gen =  new Observation();
				for(Symbol pred : tmp.getPredicatesSymbols()) {
					switch(tmp.getValue(pred)) {
					case TRUE:
						if(!symbols.contains(pred.generalize(map))) {
							symbols.add(pred.generalize(map));
						}
						gen.addTrueObservation(pred.generalize(map));
						break;
					case FALSE:
						if(!symbols.contains(pred.generalize(map))) {
							symbols.add(pred.generalize(map));
						}
						gen.addFalseObservation(pred.generalize(map));
						break;
					case MISSED:
						if(!symbols.contains(pred.generalize(map))) {
							symbols.add(pred.generalize(map));
						}
						gen.missingPredicate(pred.generalize(map));
					default:
						break;
					}
				}
				if(! gen.isEmpty()) {
					preconditions.get(s).add(gen);
				}
			}//Instanciate
		}
//		System.out.println(preconditions);
		//Generalize preconditions
		Map<Symbol, Observation> genPreconditions = new HashMap<>();
		for(Symbol s : getActionTypes()) {
			genPreconditions.put(s, new Observation(preconditions.get(s), symbols));
		}

		//Learn postconditions
		Map<Symbol, List<Observation>> postconditions = new HashMap<>();
		for(Symbol s : this.getActionTypes()){
			postconditions.put(s, new ArrayList<>());
		}
		symbols = new ArrayList<>();
		for(Symbol s : this.getActionTypes()){
			//Learn instanciate
			for(Symbol a : getActionOfType(actions, s)) {
				Observation tmp = learnPostcondition(a,
						mappings.getSet(a),
						mappingPosts.getSet(a),
						As);
				Map<String, String> map = a.mapping();
				//Gen contains all postconditions for action a for each states
				//where a was feasible
				Observation gen =  new Observation();
				for(Symbol pred : tmp.getPredicatesSymbols()) {
					switch(tmp.getValue(pred)) {
					case TRUE:
						if(!symbols.contains(pred.generalize(map))) {
							symbols.add(pred.generalize(map));
						}
						gen.addTrueObservation(pred.generalize(map));
						break;
					case FALSE:
						if(!symbols.contains(pred.generalize(map))) {
							symbols.add(pred.generalize(map));
						}
						gen.addFalseObservation(pred.generalize(map));
						break;
					case MISSED:
						if(!symbols.contains(pred.generalize(map))) {
							symbols.add(pred.generalize(map));
						}
						gen.missingPredicate(pred.generalize(map));
					default:
						break;
					}
				}
				if(! gen.isEmpty()) {
					postconditions.get(s).add(gen);
				}
			}//Instanciate
		}//All instanciate

		//Generalize postconditions
		Map<Symbol, Observation> genPostconditions = new HashMap<>();
		for(Symbol s : getActionTypes()) {
			genPostconditions.put(s, new Observation(postconditions.get(s),
					symbols));
		}

		List<Symbol> genAct = new ArrayList<>();
		for(Symbol a : this.actions) {
			if(! genAct.contains(a)) {
				genAct.add(a);
			}

		}
		//System.out.println("*****"+this.predicates);
//		System.out.println(genPreconditions);
		return new Domain(this.predicates, genAct,
				genPreconditions,
				genPostconditions);
	}
	
	
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
			Mapping reduceMappingPost) throws BlocException {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> p = ind.decode();
		Map<Symbol, Observation> preconditions = p.getX();
		Map<Symbol, Observation> postconditions = p.getY();
		int maxIter = 100;
		boolean b = true;
		while(b && maxIter > 0) {
			b = false;
			maxIter--;

			//Refine postcondition
			for(Symbol s : this.getActionTypes()){
				if(s instanceof Method) {
					continue;
				}
				Observation post = postconditions.get(s);
				Observation newPost = refinePostcondition(
						s,reduceMapping,preconditions,
						post.clone(), A);
				newPost.missedToAny();
				postconditions.put(s, newPost);
				b |= !(newPost.equals(post));
			}

			//Refine precondition
			for(Symbol s : this.getActionTypes()){
				if(s instanceof Method) {
					continue;
				}
				Observation prec_ = preconditions.get(s);
				Observation post = postconditions.get(s);
				Observation newPrec = refinePrecondition(
						prec_.clone(), post);
				newPrec.missedToAny();
				preconditions.put(s, newPrec);
				b |= !(newPrec.equals(prec_));
			}

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
	 * compute all operator's parameters
	 *
	 * @return List of operator's parameters
	 */
	public List<Symbol> getActionTypes(){
		List<Symbol> types = new ArrayList<>();
		for(Symbol action : this.actions){
			Symbol a = action.generalize();
			if(! types.contains(a)){
				types.add(a);
			}
		}
		return types;
	}

	/**
	 * compute all predicate's parameters
	 *
	 * @return List of predicate's parameters
	 */
	public List<Symbol> getPredicateTypes(){
		List<Symbol> types = new ArrayList<>();
		for(Symbol pred : this.predicates){
			Symbol a = pred.generalize();
			if(! types.contains(a)){
				types.add(a);
			}
		}
		return types;
	}

	/**
	 * Learn precondition from Mapping
	 *
	 * @param a The action
	 * @param reduceMapping The mapping
	 * @param A fsa
	 * @return a's precondtions
	 * @throws BlocException
	 */
	public Observation learnPrecondition(Symbol a, Map<Integer,
			Observation> reduceMapping,
			DFA A)
					throws BlocException{
		List<Symbol> lp = new ArrayList<>();
		List<Observation> Q = new ArrayList<>();
//		System.out.println("--------------------------------------------------------");
		for(Map.Entry<Integer, Observation> entry : reduceMapping.entrySet()){
			int q = entry.getKey();
			List<Symbol> list = A.getPossibleBlocTransition(q);
//			System.out.println(a+" "+A.getSigma().getSymboles()+" "+A.getSigma().contains(a)+" "+list+" "+list.contains(a));
			if(list.contains(a)){
//				System.out.println(q+" "+reduceMapping.get(q));
				boolean bool = false;
				Observation obs = new Observation();
				for(Symbol predicate : entry.getValue().
						getPredicatesSymbols()){
					if(a.compatible(predicate)){
						bool = true;
						obs.addObservation(predicate,
								entry.getValue().getValue
								(predicate));
						if(!lp.contains(predicate)){
							lp.add(predicate);
						}
					}
				}
				if(bool){
					if(! Q.contains(obs) && !obs.isEmpty()){
						Q.add(obs);
					}
				}
			}
		}
//		System.out.println(Q);
		if(Q.isEmpty()) {
			return new Observation();
		}
		Observation tmp = new Observation(Q, lp);
		return tmp;
	}

	/**
	 *
	 * Learn effects from Mappings
	 *
	 * @param action The action
	 * @param reduceMapping The mapping ante
	 * @param reduceMappingPost The mapping post
	 * @param A fsa
	 * @return a's effects
	 *
	 * @throws BlocException
	 */
	public Observation learnPostcondition(
			Symbol action,
			Map<Integer,Observation>reduceMapping,
			Map<Integer,Observation>reduceMappingPost,
			DFA A)
					throws BlocException{
//		System.out.println(action);
		List<Symbol> lp = new ArrayList<>();
		List<Observation> Q = new ArrayList<>();
		for(Map.Entry<Integer, Observation> entry : reduceMapping.entrySet()){
			int q = entry.getKey();
			List<Symbol> list = A.getPossibleBlocTransition(q);
			if(list.contains(action)){
				boolean bool = false;
				Observation obs = new Observation();
				for(Symbol predicate : entry.getValue().
						getPredicatesSymbols()){
					if(action.compatible(predicate)){
						bool = true;
						obs.addObservation(predicate,
								entry.getValue().getValue
								(predicate));
						if(!lp.contains(predicate)){
							lp.add(predicate);
						}
					}
				}
				if(bool){
					int i = A.getBlocTransition(A.getPartition().
							getBloc(entry.getKey()),action).min();
					Observation o = reduceMappingPost.get(i);
					Observation o2 = obs.diff(o);
//					System.out.println("\t"+obs+" "+o);
					if(! Q.contains(o2) && !o2.isEmpty()){
						Q.add(o2);
					}
				}
			}
		}
		Observation postcondition = new Observation(Q, lp);
		return postcondition;
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
	public Observation refinePostcondition(
			Symbol action,
			Mapping reduceMapping,
			Map<Symbol, Observation>preconditions,
			Observation postcondition,
			DFA A)
					throws BlocException{
		for(int q : A.blocStates()){
			List<Symbol> list = A.getPossibleBlocTransition(q);
			if(getListActionType(list).contains(action)){
				for(Symbol action2 : getActionOfType(list, action)){
					Observation effect = postcondition.
							instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(action2.mapping()));
					Observation prec = preconditions.get(action).
							instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(action2.mapping()));
					Observation currentState = reduceMapping.getStates(q, action2);
//					System.out.println(q);
//					System.out.println(action2);
//					System.out.println(reduceMapping.getSet(action2).keySet());
					int qNext = A.getBlocTransition
							(A.getPartition().getBloc(q), action2).min();
					for(Symbol nextAction : A.getPossibleBlocTransition(qNext)) {
						if(nextAction instanceof Method) {
							continue;
						}
						if(nextAction instanceof Task) {
							continue;
						}
						Observation precNext = preconditions.get(nextAction.generalize()).
								instanciate(fr.uga.amlsi.utils.Utils.reverseParamMap(nextAction.mapping()));
//						System.out.println(currentState);
						//System.out.println(prec);
					try {
							currentState = currentState.addEffect(prec);
							currentState = currentState.addEffect(effect);
							for(Symbol p : precNext.getPredicatesSymbols()) {
								if(action2.compatible(p)) {
									switch(precNext.getValue(p)) {
									case TRUE:
										switch(currentState.getValue(p)) {
										case FALSE:
											postcondition.addTrueObservation(p.generalize(
													action2.mapping()));
//											System.out.println(
//													"-------------------STATE "+qNext+"-------------------\n"
//													+action2+"\n"
//													+nextAction+"\n"
//													+p+" TRUE");
											break;
										default:
											break;
										}
										break;
									case FALSE:
										switch(currentState.getValue(p)) {
										case TRUE:
											postcondition.addFalseObservation(p.generalize(
													action2.mapping()));
//											System.out.println(
//													"-------------------STATE "+qNext+"-------------------\n"
//													+action2+"\n"
//													+nextAction+"\n"
//													+p+" FALSE");
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
						} catch(NullPointerException e) {
							if(currentState != null) {
								e.printStackTrace();
								System.exit(1);
							}
						}
					}
				}
			}
		}
		return postcondition;
	}

	/**
	 * Preconditions refinement
	 *
	 * @param precondition A precondition
	 * @param postcondition An effect

	 * @return Refined preconditions
	 * @throws BlocException
	 */
	public Observation refinePrecondition(
			Observation precondition,
			Observation postcondition)
					throws BlocException{
		Observation obs = new Observation(precondition);

		for(Symbol p : postcondition.getPredicatesSymbols()) {
			switch(postcondition.getValue(p)) {
			case FALSE:
				obs.addTrueObservation(p);
				break;
			default:
				break;
			}
		}

		return obs;
	}

	/**
	 * Return the set of operators
	 * @param actions Operators
	 * @return  All actions
	 */
	public List<Symbol> getListActionType(List<Symbol> actions) {
		List<Symbol> res = new ArrayList<>();

		for(Symbol action : actions) {
			Symbol type = action.generalize();
			if(! res.contains(type)) {
				res.add(type);
			}
		}

		return res;
	}

	/**
	 * Return all actions instanciating type
	 * @param actions set of actions
	 * @param type the operators
	 * @return A set of actions
	 */
	public List<Symbol> getActionOfType(List<Symbol> actions, Symbol type) {
		List<Symbol> res = new ArrayList<>();

		for(Symbol action : actions) {
			Symbol actionType = action.generalize();
			if(type.equals(actionType)) {
				res.add(action);
			}
		}

		return res;
	}

	/**
	 * Return the key pointing to the value
	 * @param map The map
	 * @param value The value
	 * @return The key
	 */
	public String getKey(Map<String, String> map, String value) {
		for(Map.Entry<String, String> entry : map.entrySet()) {
			if(entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Generate the content of the pddl action model file
	 * @param preconditions All preconditions
	 * @param postconditions All effects
	 * @return Pdd action model
	 */
	public String generation(Map<Symbol, Observation> preconditions,
			Map<Symbol, Observation> postconditions) {

		String res = "(define (domain "+this.name+")\n";
		res += "(:requirements :strips :typing :negative-preconditions)\n";

		//Types
		res += "(:types\n";
		res += this.types.toString();
		res += ")\n";
		//Predicates
		res += "(:predicates\n";
		List<Symbol> tmp = new ArrayList<>();
		for(Symbol s : this.predicates) {
			Symbol ss = s.generalize();
			if(! tmp.contains(ss)) {
				res += "\t"+ss.toStringType()+"\n";
				tmp.add(ss);
			}
		}
		res += ")\n";

		//Actions
		tmp = new ArrayList<>();
		for(Symbol action : this.actions){
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
	 * @param S
	 * @return
	 */
	public static List<Symbol> getAllActions(Sample S) {
		List<Symbol> l = new ArrayList<>();
		S.getExamples().forEach(ex -> {
			ex.getActionSequences().forEach(s -> {
				if(!l.contains(s)) {
					l.add(s);
				}
			});
		});
		return l;
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
			DFA A,
			Sample pos)	throws BlocException{
		for(Trace t : pos.getExamples()) {
			Example example = (Example)t;
			int q = A.getQ0();
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
								postcondition.addTrueObservation(p.generalize(
										action2.mapping()));
//								System.out.println("Add "+action2+" "+nextAction+" "+p);//+" "+example);
								break;
							default:
								break;
							}
							break;
						case FALSE:
							switch(currentState.getValue(p)) {
							case TRUE:
								postcondition.addFalseObservation(p.generalize(
										action2.mapping()));
//								System.out.println("Add "+action2+" "+nextAction+" not "+p);//+" "+example);
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
}
