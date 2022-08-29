/**
 * 
 */
package fr.uga.amlsi.learning.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.Task;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.RandomTaskTrace;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.TaskTrace;
import fr.uga.generator.utils.Pair;
import fr.uga.amlsi.fsm.CFG;
import fr.uga.amlsi.fsm.DFA;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.DomainLearning;
import fr.uga.amlsi.learning.Mapping;
import fr.uga.amlsi.main.Argument;
import fr.uga.amlsi.utils.Utils;

/**
 * @author Maxence Grand
 *
 */
public class HtnLearner extends DomainLearning{
	/**
	 * 
	 */
	private List<Method> methods;
	
	/**
	 * Constructs 
	 */
	public HtnLearner() {
		super();
		methods = new ArrayList<>();
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
	public HtnLearner(
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
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Getter of methods
	 * @return the methods
	 */
	public List<Method> getMethods() {
		return methods;
	}

	/**
	 * Setter methods
	 * @param methods the methods to set
	 */
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	/**
	 * 
	 * @param tasksTraces
	 * @return
	 */
	public List<Method> recompositionHeuristic(List<TaskTrace> tasksTraces) {
		Map<Task, List<TaskTrace>> traces = new HashMap<>();
		for(TaskTrace t : tasksTraces) {
			Task task = (Task)t.getTask().generalize();
			if(!traces.containsKey(task)) {
				traces.put(task, new ArrayList<>());
			}
			if(!traces.get(task).contains(t)) {
				traces.get(task).add(t);
			}
		}
		CFG cfg  = CFG.simplificationHeuristic(
				traces,
				tasksTraces,
				new ArrayList<>(traces.keySet()));
		List<Method> methods = cfg.getMethods();
		return methods;
	}
	
	/**
	 * 
	 * @param tasksTraces
	 * @return
	 */
	public List<Method> recompositionGreedy(List<TaskTrace> tasksTraces) {
		List<TaskTrace> tasksTracesRec= new ArrayList<>();
		Map<Method, List<TaskTrace>> methodsTrace = new HashMap<>();
		for(TaskTrace t : tasksTraces) {
			List<TaskTrace> rec = t.getAllRecomposition(tasksTraces);
			for(TaskTrace tt : rec) {
				Method m = (Method)tt.getMethod().generalize();
				if(!methodsTrace.containsKey(m)) {
					methodsTrace.put(m, new ArrayList<>());
				}
				if(!methodsTrace.get(m).contains(t)){
					methodsTrace.get(m).add(t);
				}
				tasksTracesRec.add(tt);
			}
		};
		CFG cfg = new CFG(new ArrayList<>(methodsTrace.keySet()));
		CFG simplifiedCfg = cfg.simplificationGreedy(tasksTraces);
		List<Method> methods = simplifiedCfg.getMethods();
		return methods;
	}
	
	/**
	 * 
	 * @param pos
	 * @param neg
	 * @param randomTaskTraces
	 * @return
	 * @throws BlocException
	 */
	public  List<Method> methodLearning(
			Sample pos, 
			Sample neg,
			List<RandomTaskTrace> randomTaskTraces,
			DFA A,
			Argument.RECOMPOSITION_TYPE r) throws BlocException {
		
		List<TaskTrace> tasksTraces= new ArrayList<>();
		for(RandomTaskTrace t : randomTaskTraces) {
			//System.out.println(t+"\n");
			for(TaskTrace tt : A.addTaskTransition(t)) {
				if(!tasksTraces.contains(tt)) {
					tasksTraces.add(tt);
				}
			}
		}
		A.writeDotFile("task.dot");
		
//		DFA clone = A.clone();
//		List<TaskTrace> tmp = A.addNonObservedTaskTransition(tasksTraces);
		List<TaskTrace> tasksTraces2 = new ArrayList<>();
		tasksTraces2.addAll(tasksTraces);
//		tasksTraces2.addAll(tmp);

		//Methods Generations
		List<Method> methods = new ArrayList<>();
		switch(r) {
		case GREEDY:
			methods = this.recompositionGreedy(tasksTraces2);
			break;
		case HEURISTIC:
			methods = this.recompositionHeuristic(tasksTraces2);
			break;
		default:
			break;
		
		}
		
		//Map task trace to methods
		CFG cfg = new CFG(methods);
//		System.err.println(cfg);
		Map<TaskTrace, Method> methodsTrace = cfg.mapTraceMethod(tasksTraces);
		
		//Add methods transitions
		methodsTrace.forEach((k,v) -> {
			//System.err.println(k+"\n"+v);
			A.addMethodTransition(k, v);
		});
		return methods;
	}
	
	/**
	 * 
	 * @param A
	 * @param ramdomTaskTraces
	 * @return
	 */
	public List<RandomTaskTrace> getMethodTrace(
			DFA A,
			List<RandomTaskTrace> ramdomTaskTraces) {
		List<RandomTaskTrace> res = new ArrayList<>();
//		try {
//			A.writeDotFile("fff.dot");
//		} catch (BlocException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		for(RandomTaskTrace t : ramdomTaskTraces) {
			Example tr = (Example)A.getMethodTrace(t);		
			//System.out.println(tr.size()+" "+t.getTasks().size());
			RandomTaskTrace tmp = new RandomTaskTrace(
					t.getExample(), 
					tr,
					t.getEndIdx(), 
					t.getBeginIdx());
			res.add(tmp);
		}
		return res;
	}
	
	/**
	 * Generate PDDL operator
	 * @param A The automaton
	 * @param ante The mapping ante
	 * @param post The mapping post
	 * @return The action model
	 * @throws BlocException
	 */
	public  Domain generateHDDLOperator(
			DFA A,
			Mapping ante,
			Mapping post) throws BlocException {
		
		List<Symbol> symbols = new ArrayList<>();

		//Map actions with preconditions/effects
		Map<Symbol, List<Observation>> preconditions = new HashMap<>();
		for(Symbol s : this.getMethods()){
			preconditions.put(s, new ArrayList<>());
		}

		//For each operator
		for(Symbol s : this.getMethods()){

			//Learn instanciate
			for(Symbol a : getActionOfType(A.getMethods(), s)) {
				Observation tmp = learnPrecondition(a,ante.getSet(a),A);
				//System.out.println(a+" "+tmp);
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

		//Generalize preconditions
		Map<Symbol, Observation> genPreconditions = new HashMap<>();
		for(Symbol s : this.getMethods()) {
			genPreconditions.put(s, new Observation(preconditions.get(s), symbols));
		}
		
		//Generalize postconditions
		Map<Symbol, Observation> genPostconditions = new HashMap<>();
		for(Symbol s : this.getMethods()) {
			genPostconditions.put(s, new Observation());
		}

		List<Symbol> genAct = new ArrayList<>();
		for(Symbol a : A.getMethods()) {
			if(! genAct.contains(a)) {
				genAct.add(a);
			}

		}
		for(Symbol a : actions) {
			if(! genAct.contains(a)) {
				genAct.add(a);
			}

		}
		return new HtnDomain(
				this.predicates,
				genAct,
				genPreconditions,
				genPostconditions);
	}
	
//	public Observation refineMethod() {
//		
//	}
	
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
			postconditions = refinePostcondition(
					reduceMapping,
					preconditions,
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
		for(Symbol a : A.getMethods()) {
			if(! genAct.contains(a)) {
				genAct.add(a);
			}

		}

		Domain res =  new HtnDomain(
				this.predicates,
				genAct,
				preconditions,
				postconditions);
//		this.refineDependancy(res);
		return res;
	}
	
	/**
	 * 
	 * @param ind
	 * @return
	 */
	public Domain refineDependancy(Domain ind) {
		Dependancy dep = new Dependancy(ind.getOperators());
		List<String> toRefine = dep.toRefine();
//		System.out.println(toRefine);
//		System.out.println(ind.getOperators());
		Map<String, Symbol> taskName = new HashMap<>();
		for(Symbol op : ind.getOperators()) {
			for(String str : toRefine) {
				if(op instanceof Method) {
					if(str.equals(((Method)op).getToDecompose().getName())) {
						taskName.put(str, ((Method)op).getToDecompose());
						break;
					}
				} else {
					if(str.equals(op.getName())) {
						taskName.put(str, op);
						break;
					}
				}
			}
		}
//		System.out.println(taskName);
		Map<Symbol, Space> spaces = this.getSpace(dep, ind, taskName);
		while(this.removeIncompatible(ind, dep, spaces, taskName)) {
			spaces = this.getSpace(dep, ind, taskName);
		}
		return ind;
	}
	
	/**
	 * 
	 * @param domain
	 * @param dep
	 * @param spaces
	 * @param taskNames
	 * @return
	 */
	private boolean removeIncompatible(
			Domain domain, 
			Dependancy dep,
			Map<Symbol, Space> spaces,
			Map<String, Symbol> taskNames) {
		boolean b = false;
		for(String str : dep.toRefine()) {
			Symbol op = taskNames.get(str);
//			System.out.println(op);
			if(!(op instanceof Task)) {
				continue;
			}
			Task t = (Task)op;
			for(Symbol op2 : domain.getOperators()) {
				if(!(op2 instanceof Method)) {
					continue;
				}
				Method m = (Method)op2;
				if(!m.getToDecompose().equals(t)) {
					continue;
				}
//				System.out.println("->"+m);
				for(int i = 0; i < m.getSubtasks().size()-1; i++) {
					Symbol ti = m.getSubtasks().get(i);
					Symbol ti1 = m.getSubtasks().get(i+1);
					Observation prec = spaces.get(ti1.generalize()).getNecessary(true);
					prec = prec.instanciate(Utils.reverseParamMap(ti1.mapping()));
					Observation eff = spaces.get(ti.generalize()).getNecessary(false);
					eff = eff.instanciate(Utils.reverseParamMap(ti.mapping()));
//					System.out.println(ti+" "+eff);
//					System.out.println(ti1+" "+prec);
					for(Symbol p : eff.getPredicatesSymbols()) {
						if(!prec.getPredicatesSymbols().contains(p)) {
							continue;
						}
						switch(eff.getValue(p)) {
						case TRUE:
							switch(prec.getValue(p)) {
							case FALSE:
								domain.mutation(
										true, 
										ti1.generalize(),
										p.generalize(ti1.mapping()),
										Observation.Value.ANY);
								domain.mutation(
										false, 
										ti.generalize(),
										p.generalize(ti.mapping()),
										Observation.Value.ANY);
							default:
								break;
							}
							break;
						case FALSE:
							switch(prec.getValue(p)) {
							case TRUE:
								domain.mutation(
										true, 
										ti1.generalize(),
										p.generalize(ti1.mapping()),
										Observation.Value.ANY);
								domain.mutation(
										false, 
										ti.generalize(),
										p.generalize(ti.mapping()),
										Observation.Value.ANY);
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
		}
		return b;
	}
	/**
	 * 
	 * @param dep
	 * @param ind
	 * @param taskName
	 * @return
	 */
	private Map<Symbol, Space> getSpace(
			Dependancy dep,
			Domain ind,
			Map<String, Symbol> taskName) {
		Map<Symbol, Space> res = new HashMap<>();
		
		for(String name : dep.toRefine()) {
			Symbol op = taskName.get(name);
			Space space = new Space(op);
			if(op instanceof Task) {
				List<Observation> prec = new ArrayList<>(), 
						eff= new ArrayList<>();
				List<Symbol> allSymbols = new ArrayList<>();
				for(Symbol m : ind.getOperators()) {
					if(m instanceof Method) {
						if(((Method)m).getToDecompose().equals(op)) {
							prec.add(ind.getPrecond(m));
							allSymbols.addAll(ind.getPrecond(m).getPredicatesSymbols());
							eff.add(ind.getPostcond(m));
							allSymbols.addAll(ind.getPrecond(m).getPredicatesSymbols());
						}
					}
				}
				space.addNecessary(new Observation(prec, allSymbols), true);
				space.addNecessary(new Observation(eff, allSymbols), false);
				space.addPossible(getPossible(prec, space.getNecessary(true), allSymbols), true);
				space.addPossible(getPossible(eff, space.getNecessary(false), allSymbols), false);
				space.addImpossible(getImossible(prec, allSymbols, space.getNecessary(true)), true);
				space.addImpossible(getImossible(eff, allSymbols, space.getNecessary(false)), false);
			} else {
				space.addNecessary(ind.getPrecond(op), true);
				space.addNecessary(ind.getPostcond(op), false);
				space.addPossible(new Observation(), true);
				space.addPossible(new Observation(), false);
				space.addImpossible(new Observation(), true);
				space.addImpossible(new Observation(), false);
			}
			res.put(op, space);
//			System.out.println(space);
		}
		
		return res;
	}
	
	private static Observation getPossible(
			List<Observation> Q, 
			Observation n,
			List<Symbol> sym) {
		List<Observation> Q2 = new ArrayList<>();
        for(Observation obs : Q){
            if(!obs.isEmpty()){
                Q2.add(obs);
            }
        }
        Observation res = new Observation();
        if(Q2.size() == 1) {
        	
        	sym.forEach(p -> {
        		switch(Q2.get(0).getValue(p)) {
        		case TRUE:
        			switch(n.getValue(p)) {
        			case TRUE:
        			case FALSE:
        				break;
        			default:
        				res.addTrueObservation(p);
        				break;
        			}
        			break;
        		case FALSE:
        			switch(n.getValue(p)) {
        			case TRUE:
        			case FALSE:
        				break;
        			default:
        				res.addTrueObservation(p);
        				break;
        			}
        			break;
        		default:
					break;
        			
        		}
        	});
        }else if(Q2.size() > 1) {
            Map<Symbol, Pair<Integer, Integer>> map = new HashMap<>();
            for(Symbol predicate : sym){
                map.put(predicate, new Pair<>(0,0));
            }
            List<Symbol> anyList = new ArrayList<>();
            List<Symbol> missedList = new ArrayList<>();
            for(int i=0; i<Q2.size(); i++){
                Map<Symbol, Observation.Value> m = Q2.get(i).getPredicates();
                for(Symbol predicate : sym){
                    Pair<Integer, Integer> pair = map.get(predicate);
                    if(! m.containsKey(predicate)){
                        anyList.add(predicate);
                    } else {
                        switch (m.get(predicate)) {
                        case TRUE:
                            map.put(predicate, new Pair<>(pair.getX()+1,
                                                          pair.getY()));
                            break;
                        case FALSE:
                            map.put(predicate, new Pair<>(pair.getX(),
                                                          pair.getY()+1));
                            break;
                        case ANY:
                            anyList.add(predicate);
                            break;
                        case MISSED:
                            missedList.add(predicate);
                            break;
                        }
                    }
                }
            }
        	sym.forEach(p -> {
        		switch(n.getValue(p)) {
    			case TRUE:
    			case FALSE:
    				break;
    			default:
    				if(map.get(p).getX() > 0 && map.get(p).getY() <= 0) {
    					res.addTrueObservation(p);
    				}
    				if(map.get(p).getY() > 0 && map.get(p).getX() <= 0) {
    					res.addFalseObservation(p);
    				}
    				break;
    			}
        	});
        	
        }
        res.removeAny();
        return res;
	}
	
	private static Observation getImossible(
			List<Observation> Q,
			List<Symbol> sym,
			Observation n) {
		List<Observation> Q2 = new ArrayList<>();
        for(Observation obs : Q){
            if(!obs.isEmpty()){
                Q2.add(obs);
            }
        }
        Observation res = new Observation();

        Map<Symbol, Pair<Integer, Integer>> map = new HashMap<>();
        for(Symbol predicate : sym){
        	map.put(predicate, new Pair<>(0,0));
        }
        List<Symbol> anyList = new ArrayList<>();
        List<Symbol> missedList = new ArrayList<>();
        for(int i=0; i<Q2.size(); i++){
        	Map<Symbol, Observation.Value> m = Q2.get(i).getPredicates();
        	for(Symbol predicate : sym){
        		Pair<Integer, Integer> pair = map.get(predicate);
        		if(! m.containsKey(predicate)){
        			anyList.add(predicate);
        		} else {
        			switch (m.get(predicate)) {
        			case TRUE:
        				map.put(predicate, new Pair<>(pair.getX()+1,
        						pair.getY()));
        				break;
        			case FALSE:
        				map.put(predicate, new Pair<>(pair.getX(),
        						pair.getY()+1));
        				break;
        			case ANY:
        				anyList.add(predicate);
        				break;
        			case MISSED:
        				missedList.add(predicate);
        				break;
        			}
        		}
        	}
        }
        sym.forEach(p -> {
        	switch(n.getValue(p)) {
        	case TRUE:
        	case FALSE:
        		break;
        	default:
        		if(map.get(p).getX() > 0 && map.get(p).getY() > 0) {
					res.addTrueObservation(p);
				}
        		break;
        	}
        });


        res.removeAny();
        return res;
	}
}
