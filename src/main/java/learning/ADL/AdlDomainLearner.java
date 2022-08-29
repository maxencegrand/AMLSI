/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exception.BlocException;
import fsm.DFA;
import fsm.Example;
import fsm.Pair;
import fsm.Sample;
import fsm.Symbol;
import fsm.Trace;
import fsm.TransitionAction;
import learning.Domain;
import learning.DomainLearning;
import learning.Generator;
import learning.Mapping;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public class AdlDomainLearner extends DomainLearning{
	/**
	 * 
	 */
	private Map<Symbol, Map<Integer, Symbol>> actionsMap;
	/**
	 * Constructs 
	 */
	public AdlDomainLearner() {
		super();
		this.actionsMap =  new HashMap<>();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs 
	 * @param predicates
	 * @param actions
	 * @param directory
	 * @param name
	 * @param realDomain
	 * @param initialState
	 * @param generator
	 */
	public AdlDomainLearner(
			List<Symbol> predicates, 
			List<Symbol> actions, 
			String directory, 
			String name, 
			String realDomain,
			String initialState, 
			Generator generator) {
		super(
				predicates, 
				actions, 
				directory, 
				name, 
				realDomain, 
				initialState, 
				generator);
		this.actionsMap =  new HashMap<>();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param dfa
	 * @param actions
	 * @return
	 */
	public DFA convert(DFA dfa, List<Symbol> actions, List<Symbol> predicates) {
		System.out.println(actions);
		try {
			dfa.writeDotFile("tmp1.dot");
		} catch (BlocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DFA res = new DFA();
		res.setF(dfa.getF());
		res.setQ(dfa.getQ());
		res.setQ0(dfa.getQ0());
		res.setPartition(dfa.getPartition());
		res.setSigma(dfa.getSigma());

		Map<String, String> param = new LinkedHashMap<>();
		Map<String, String> paramAbs = new LinkedHashMap<>();
		actions.forEach(a -> a.getParameters().forEach((k,v) -> param.put(k, v)));
		predicates.forEach(a -> a.getParameters().forEach((k,v) -> param.put(k, v)));
		actions.forEach(a -> a.getAbstractParameters().forEach((k,v) -> paramAbs.put(k, v)));
		predicates.forEach(a -> a.getAbstractParameters().forEach((k,v) -> paramAbs.put(k, v)));
		actions.forEach(a -> this.actionsMap.put(a, new HashMap<>()));
		dfa.getDelta().forEach((q, p) -> {
			
			p.forEach((act, l) -> {
				
				l.forEach(qPrime -> {
					try {
						Map<String, String> paramBis = new LinkedHashMap<>();
						Map<String, String> paramAbsBis = new LinkedHashMap<>();
						paramBis.putAll(act.getParameters());
						paramAbsBis.putAll(act.getAbstractParameters());
						param.forEach((k,v) -> {
							if(!paramBis.containsKey(k)) {
								paramBis.put(k, v);
							}
						});
						paramAbs.forEach((k,v) -> {
							if(!paramAbsBis.containsKey(k)) {
								paramAbsBis.put(k, v);
							}
						});
						int qAct = res.getPartition().getBloc(q).min();
						TransitionAction newAct = new TransitionAction(act,qAct,paramBis, paramAbsBis);
						this.actionsMap.get(act).put(qAct, newAct);
						res.addTransition(new Pair<>(q, newAct), qPrime);
					} catch (BlocException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}
				});
			});
			
		});
		try {
			res.writeDotFile("res.dot");
		} catch (BlocException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.getSigma().getSymboles().addAll(this.getNewActions());
		return res;
	}
	
	/**
	 * 
	 * @param S
	 * @param dfa
	 * @param split
	 * @return
	 */
	public Sample convertPostive(Sample S, DFA dfa, DFA split) {
		System.out.println("Action MAPPING");
		this.actionsMap.forEach((k,v) -> {
			System.out.println("+"+k);
			v.forEach((kk,vv) -> {
				System.out.println("\t"+kk+" "+vv);
			});
		});
		Sample res = new Sample();
		for(Trace t : S.getExamples()) {
			List<Integer> states = dfa.getStates(t, dfa.getQ0());
			List<Symbol> tmp = new ArrayList<>();
			try {
				for(int i = 0; i < t.size(); i++) {
					Symbol previous = t.getActionSequences().get(i);
					tmp.add(this.actionsMap.get(previous).get(states.get(i)));
				}
				Trace newT = new Example(tmp);
				res.addExample(newT);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
	public List<Symbol> getNewActions() {
		List<Symbol> res = new ArrayList<>();
		this.actionsMap.forEach((k,v) -> v.forEach((kk,vv) -> res.add(vv)));
		return res;
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
			for(Symbol a : getActionOfType(this.getNewActions(), s)) {
				Observation tmp = learnPrecondition(a,
						mappings.getSet(a),
						As);
				
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
			for(Symbol a : getActionOfType(this.getNewActions(), s)) {
				Observation tmp = learnPostcondition(a,
						mappings.getSet(a),
						mappingPosts.getSet(a),
						As);
//				System.out.println(a+" "+tmp);
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
		for(Symbol a : this.getNewActions()) {
			if(! genAct.contains(a)) {
				genAct.add(a);
			}

		}
		
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
		for(Symbol a : this.getNewActions()) {
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
		for(Symbol action : this.getNewActions()){
			Symbol a = action.generalize();
			if(! types.contains(a)){
				types.add(a);
			}
		}
		return types;
	}

	/**
	 * Getter of actionsMap
	 * @return the actionsMap
	 */
	public Map<Symbol, Map<Integer, Symbol>> getActionsMap() {
		return actionsMap;
	}
	
	
}
