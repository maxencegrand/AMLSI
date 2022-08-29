/**
 * 
 */
package learning.hierarchical;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.uga.pddl4j.parser.PDDLDomain;
import fr.uga.pddl4j.parser.PDDLExpression;
import fr.uga.pddl4j.parser.PDDLParser;
import fsm.DecompositionTrace;
import fsm.Example;
import fsm.Method;
import fsm.Pair;
import fsm.Predicate;
import fsm.Sample;
import fsm.Symbol;
import fsm.Task;
import fsm.Trace;
import learning.Domain;
import learning.Observation;
import learning.TypeHierarchy;
import simulator.hierarchical.HierarchicalSimulator;
import simulator.hierarchical.HtnSimulator;
import utils.Utils;

/**
 * @author Maxence Grand
 *
 */
public class HtnDomain extends Domain{

	/**
	 * Constructs 
	 */
	public HtnDomain() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs 
	 * @param P
	 * @param actions
	 * @param prePos
	 * @param preNeg
	 * @param addList
	 * @param delList
	 */
	public HtnDomain(
			List<? extends Symbol> P, 
			List<? extends Symbol> actions, 
			Map<Symbol, List<String>> prePos,
			Map<Symbol, List<String>> preNeg, 
			Map<Symbol, List<String>> addList,
			Map<Symbol, List<String>> delList) {
		super(P, actions, prePos, preNeg, addList, delList);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs 
	 * @param P
	 * @param actions
	 * @param preconditions
	 * @param postconditions
	 */
	public HtnDomain(
			List<? extends Symbol> P,
			List<? extends Symbol> actions, 
			Map<Symbol, Observation> preconditions,
			Map<Symbol, Observation> postconditions) {
		super(P, actions, preconditions, postconditions);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs 
	 * @param P
	 * @param domain
	 */
	public HtnDomain(
			List<? extends Symbol> P, 
			Map<Symbol, Pair<Observation, Observation>> domain) {
		super(P, domain);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs 
	 * @param P
	 */
	public HtnDomain(List<? extends Symbol> P) {
		super(P);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public Domain clone() {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> tmp =
				decode();
		return new HtnDomain(this.getP(), this.getA(), tmp.getX(), tmp.getY());
	}

	/**
	 * 
	 * @param domain
	 */
	public void extractPreconditionsEffects(String domain) {
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(domain);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		PDDLDomain htn = parser.getDomain();

		htn.getActions().forEach(t -> {
			Symbol action = null;
			Map<String, String> params = new HashMap<>();
			for(Symbol a : this.getA()){
				if(a.generalize().getName().equals(t.getName().toString())) {
					action = a.generalize();
					break;
				}

			};
			for(int i = 0; i < t.getParameters().size(); i++) {
				params.put(
						t.getParameters().get(i).getImage().toString(),
						action.getListParameters().get(i));
			}

			//Extract precondition

			//System.out.println(action+" "+t.getPreconditions().getAtom());
			if(t.getPreconditions().getChildren().isEmpty()) {
				PDDLExpression exp = t.getPreconditions();
				String name = exp.getAtom().get(0).getImage();
				Map<String, String> p = new LinkedHashMap<>();
				Symbol f = null;
				for(int i = 1; i < exp.getAtom().size(); i++) {
					String param = params.get(exp.getAtom().get(i).getImage());
					String type = action.getParameters().get(param);
					p.put(param, type);
				}
				f = new Predicate(name, p, action.getAbstractParameters());
				this.mutation(true, action, f, Observation.Value.TRUE);
			}
			for(PDDLExpression exp : t.getPreconditions().getChildren()) {
				String name ="";
				Map<String, String> p = new LinkedHashMap<>();
				Symbol f = null;
				switch(exp.getConnective()) {
				case ATOM:
					name = exp.getAtom().get(0).getImage();
					for(int i = 1; i < exp.getAtom().size(); i++) {
						String param = params.get(exp.getAtom().get(i).getImage());
						String type = action.getParameters().get(param);
						p.put(param, type);
					}
					f = new Predicate(name, p, action.getAbstractParameters());
					this.mutation(true, action, f, Observation.Value.TRUE);
					break;
				case NOT:
					name = exp.getChildren().get(0).getAtom().get(0).getImage();
					for(int i = 1; i < exp.getChildren().get(0).getAtom().size(); i++) {
						String param = params.get(exp.getChildren().get(0).getAtom().get(i).getImage());
						String type = action.getParameters().get(param);
						p.put(param, type);
					}
					f = new Predicate(name, p, action.getAbstractParameters());
					this.mutation(true, action, f, Observation.Value.FALSE);
					break;
				default:
					break;
				}
			}

			//Extract effects
			for(PDDLExpression exp : t.getEffects().getChildren()) {
				String name ="";
				Map<String, String> p = new LinkedHashMap<>();
				Symbol f = null;
				switch(exp.getConnective()) {
				case ATOM:
					name = exp.getAtom().get(0).getImage();
					for(int i = 1; i < exp.getAtom().size(); i++) {
						String param = params.get(exp.getAtom().get(i).getImage());
						String type = action.getParameters().get(param);
						p.put(param, type);
					}
					f = new Predicate(name, p, action.getAbstractParameters());
					this.mutation(false, action, f, Observation.Value.TRUE);
					break;
				case NOT:
					name = exp.getChildren().get(0).getAtom().get(0).getImage();
					for(int i = 1; i < exp.getChildren().get(0).getAtom().size(); i++) {
						String param = params.get(exp.getChildren().get(0).getAtom().get(i).getImage());
						String type = action.getParameters().get(param);
						p.put(param, type);
					}
					f = new Predicate(name, p, action.getAbstractParameters());
					this.mutation(false, action, f, Observation.Value.FALSE);
					break;
				default:
					break;
				}
			}
		});
		//System.exit(1);
	}

	public List<Method> extractMethods(String domain, List<Symbol> tasks) {
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(domain);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		PDDLDomain htn = parser.getDomain();
		List<Method> methods = new ArrayList<>();
		htn.getMethods().forEach(m -> {
			fsm.Method method = null;
			//Extract Name
			String name = m.getName().getImage();
			//Extract Task to Decompose
			PDDLExpression task = m.getTask();
			Symbol task_ = null;
			for(Symbol t : tasks) {
				if(t.getName().equals(task.getAtom().get(0).getImage())) {
					if(task.getAtom().size() == t.getListParameters().size()+1) {
						task_ = t.generalize(mapping(t,task));
					}
				}
			}
			//Extract Subtasks
			PDDLExpression subtasks = m.getSubTasks();
			List<Symbol> subtasks_ = new ArrayList<>();
			for(int i = 0; i < subtasks.getChildren().size(); i++) {
				task = subtasks.getChildren().get(i);
				Symbol task_2 = null;
				for(Symbol t : tasks) {
					if(t.getName().equals(task.getAtom().get(0).getImage())) {
						if(task.getAtom().size() == t.getListParameters().size()+1) {
							task_2 = t.generalize(mapping(t,task));
						}
					}
				}
				if(task_2 == null) {
					for(Symbol t : super.getA()) {
						if(t.getName().equals(task.getAtom().get(0).getImage())) {
							if(task.getAtom().size() == t.getListParameters().size()+1) {
								task_2 = t.generalize(mapping(t,task));
							}
						}
					}
				}
				subtasks_.add(task_2);
			}
			//Extract parameters
			Map<String, String> param = new LinkedHashMap<>();
			m.getParameters().forEach(p -> {
				String par = p.getImage();
				String type = p.getTypes().get(0).getImage();
				param.put(par, type);
			});
			//Extract precondition
			Observation preconditions = new Observation();
			for(int i = 0; i < m.getPreconditions().getChildren().size(); i++) {
				PDDLExpression exp = m.getPreconditions().getChildren().get(i);
				switch(exp.getConnective()) {
					case ATOM:
						Symbol p1 = getPredicate(super.getP(), exp);
						if(p1 != null) {
							preconditions.addTrueObservation(p1);
						}
						break;
					case NOT:
						Symbol p2 = getPredicate(super.getP(), exp.getChildren().get(0));
						if(p2 != null) {
							preconditions.addFalseObservation(p2);
						}
						break;
					default:
						break;
				}
			}
			//Generate Method
			method = new fsm.Method(name, param, (Task) task_);
			for(Symbol t : subtasks_) {
				method.add((fsm.Action)t);
			}
			this.getDomain().put(method, new Pair<>(preconditions, new Observation()));
			if(method != null) {
				methods.add(method);
			}
		});
		return methods;
	}
	/**
	 * 
	 * @return
	 */
	public String generateHDDL(
			String name, 
			TypeHierarchy types, 
			List<Method> methods, 
			List<Symbol> tasks) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> p =
				this.decode();
		Map<Symbol, Observation> preconditions = p.getX();
		Map<Symbol, Observation> postconditions = p.getY();
		//		this.getA().forEach(a -> {
		//			if(a instanceof Method) {
		//			}
		//			System.out.println(a.getListParameters());
		//		});
		String res = "(define (domain "+name+")\n";
		res += "(:requirements :strips :typing :negative-preconditions :hierarchy :method-preconditions)\n";

		//Types
		res += "(:types\n";
		res += types.toString();
		res += ")\n";
		//Predicates
		res += "(:predicates\n";
		List<Symbol> tmp = new ArrayList<>();
		for(Symbol s : this.getP()) {
			Symbol ss = s.generalize();
			if(! tmp.contains(ss)) {
				res += "\t"+ss.toStringType()+"\n";
				tmp.add(ss);
			}
		}
		res += ")\n";
		//Tasks
		tmp = new ArrayList<>();
		for(Symbol action : tasks){
				action = action.generalize();
				
				if(tmp.contains(action)) {
					continue;
				} else {
					tmp.add(action);
				}
				res += "(:task "+action.getName()+"\n";
				res += "\t:parameters (";
				for(String s : action.getListParametersType()) {
					res += s+" ";
				}
				res += "))\n";
				res+= "\n";
		}
		//Actions and methods
		tmp = new ArrayList<>();
		for(Symbol action : methods){
			Symbol actionType = action;
			if(! tmp.contains(actionType)){
				tmp.add(actionType);
				res += "(:method "+actionType.getName()+"\n";
				//Parameters
				res += "\t:parameters (";
				for(String s : actionType.getListParametersType()) {
					res += s+" ";
				}
				res += ")\n";
				//Task
				res += "\t:task "+((Method)actionType).getToDecompose()+"\n";
				//Preconditions
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
				//Subtasks
				res += "\t:ordered-subtasks (and\n";
				for(Symbol sub : ((Method)actionType).getSubtasks()) {
					sub = new Predicate(sub.getName(), sub.getParameters(), sub.getAbstractParameters());
					res += "\t\t"+sub+"\n";
				}
				res += "\t)\n";
				res += ")\n";
			}
		}
		tmp = new ArrayList<>();
		for(Symbol action : this.getA()){
			Symbol actionType = action;
			if(actionType instanceof Method) {
				continue;
			} else {
				actionType = action.generalize();
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
		}

		res +=")\n";
		return res;
	}

	/**
	 * 
	 * @param d
	 */
	public void mergeMethods(Domain d) {
		d.getDomain().forEach((k,v)->{
			if(k instanceof Method) {
				if(! this.getDomain().containsKey(k)) {
					this.getDomain().put(k, new Pair<>(new Observation(), new Observation()));
				}
				v.getX().getPredicates().forEach((pred, value) -> {
					this.mutation(true, k, pred, value);
				});
				v.getY().getPredicates().forEach((pred, value) -> {
					this.mutation(false, k, pred, value);
				});
			}
		});
	}
	
	/**
	 * 
	 * @param d
	 */
	public void mergeActions(Domain d) {
		d.getDomain().forEach((k,v)->{
			if(!(k instanceof Method)) {
				v.getX().getPredicates().forEach((pred, value) -> {
					this.mutation(true, k, pred, value);
				});
				v.getY().getPredicates().forEach((pred, value) -> {
					this.mutation(false, k, pred, value);
				});
			}
		});
	}
	
	/**
	 * 
	 * @param S
	 * @return
	 */
	public Pair<Float, Float> test(
			List<DecompositionTrace> S, 
			Observation initial,
			HierarchicalSimulator original) {
		List<Float> succes = new ArrayList<>(), correct= new ArrayList<>();
		S.forEach(t -> {
			Pair<Float, Float> p = this.test(t, initial, original);
			succes.add(p.getX());
			correct.add(p.getY());
		});
		return new Pair<>(utils.Utils.mean(succes), utils.Utils.mean(correct));
	}
	
	public float testNegative(
			Sample S, 
			Observation initial) {
		List<Float> precision = new ArrayList<>();
		S.getExamples().forEach(t -> {
			if(t.getActionSequences().get(t.size()-1) instanceof Task) {
				float p = this.testNegative(t, initial);
				precision.add(p);
			}
		});
		return utils.Utils.mean(precision);
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public float testNegative(
			Trace t,
			Observation initial) {
		float s=0f, c=0f;
		Map<Symbol, Pair<Observation, Observation>> instance = 
				this.instantiation(t.contants());
		Map<Symbol, List<Method>> taskMap = new HashMap<>();
		instance.keySet().forEach(a -> {
			if(a instanceof Method) {
				Method m = (Method)a;
				if(!taskMap.containsKey(m.getToDecompose())) {
					taskMap.put(m.getToDecompose(), new ArrayList<>());
				}
				taskMap.get(m.getToDecompose()).add(m);
			}
		});
		Observation current = initial.clone();
		for(int i = 0; i < t.size(); i++) {
			Symbol a = t.getActionSequences().get(i);
			if(a instanceof Task) {
				Trace dec = this.decompose(a, current.clone(), instance, taskMap);
				if(dec == null) {
					if(i == t.size()-1) {
						return 1;
					} else {
						return 0;
					}
				}			
				current = this.apply(dec, current, instance);
			} else {
				if(current.contains(instance.get(a).getX())) {
					current = this.apply(a, current, instance);
				} else {
					return 0;
				}
			}
		}
		return 0f;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public Pair<Float, Float> test(
			DecompositionTrace t,
			Observation initial,
			HierarchicalSimulator original) {
		float s=0f, c=0f;
		Map<Symbol, Pair<Observation, Observation>> instance = 
				this.instantiation(t.contants());
		Map<Symbol, List<Method>> taskMap = new HashMap<>();
		instance.keySet().forEach(a -> {
			if(a instanceof Method) {
				Method m = (Method)a;
				if(!taskMap.containsKey(m.getToDecompose())) {
					taskMap.put(m.getToDecompose(), new ArrayList<>());
				}
				taskMap.get(m.getToDecompose()).add(m);
			}
		});
		Observation current = initial.clone();
		for(int i = 0; i < t.size(); i++) {
			Symbol a = t.getActionSequences().get(i);
			if(a instanceof Task) {
				Trace dec = this.decompose(a, current.clone(), instance, taskMap);
				
				if(dec == null) {
					System.out.println(a+" "+t.getDecomposition(i, a));
					this.decomposePrint(a, current.clone(), instance, taskMap);
					current = this.apply(t.getDecomposition(i, a), current, instance);
					continue;
				}
				s++;
				if(dec.size() == 0 && t.getDecomposition(i, a).size()==0) {
					c++;
				}else if(original.test(t.prefix(i), a, dec)) {
					c++;
				} else {
//					System.out.println(t.prefix(i)+" "+a+" "+dec);
				}
				current = this.apply(t.getDecomposition(i, a), current, instance);
			} else {
				if(current.contains(instance.get(a).getX())) {
					current = this.apply(a, current, instance);
				} else {
					break;
				}
			}
		}
		if(nbNonPrimitive(t) == 0) {
			return new Pair<>(1f, 1f);
		}
		if(c == 0) {
			return new Pair<>((float)s/nbNonPrimitive(t), 1f);
		}
		return new Pair<>((float)s/nbNonPrimitive(t), (float)c/s);
	}
	
	/**
	 * 
	 * @param contants
	 * @return
	 */
	private Map<Symbol, Pair<Observation, Observation>> instantiation(
			Map<String, String> contants) {
		Map<Symbol, Pair<Observation, Observation>> res = new HashMap<>();
		//instantiate primitive tasks
		for(Symbol op : this.getDomain().keySet()) {
//			System.out.println(op+" "+op.getParameters());
			Observation prec = this.getPrecond(op);
			Observation eff = this.getPostcond(op);
			for(Symbol a : op.allInstances(contants)) {
				Map<String, String> m = Utils.reverseParamMap(a.mapping());
				Observation p = prec.instanciate(m);
				Observation e = eff.instanciate(m);
				res.put(a, new Pair<>(p,e));
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param a
	 * @param state
	 * @param instance
	 * @return
	 */
	private Observation apply(
			Symbol a,
			Observation state,
			Map<Symbol, Pair<Observation, Observation>> instance) {
		Observation clone = state.clone();
		for(Symbol e : instance.get(a).getY().getPredicatesSymbols()) {
			switch(instance.get(a).getY().getValue(e)) {
			case TRUE:
				clone.addTrueObservation(e);
				break;
			case FALSE:
				clone.addFalseObservation(e);
				break;
			default:
				break;
			}
		}
		return clone;
	}
	
	/**
	 * 
	 * @param t
	 * @param state
	 * @param instance
	 * @return
	 */
	private Observation apply(
			Trace t,
			Observation state,
			Map<Symbol, Pair<Observation, Observation>> instance) {
		Observation clone = state.clone();
		for(Symbol a : t.getActionSequences()) {
			clone = this.apply(clone, a);
		}
		return clone;
	}
	
	/**
	 * 
	 * @param t
	 * @param state
	 * @param instance
	 * @return
	 */
	private boolean test(
			Trace t,
			Observation state,
			Map<Symbol, Pair<Observation, Observation>> instance) {
		Observation clone = state.clone();
		for(Symbol a : t.getActionSequences()) {
			if(clone.contains(instance.get(a).getX())) {
				clone = this.apply(clone, a);
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param task
	 * @param state
	 * @param instance
	 * @param taskMap
	 * @return
	 */
	public Trace decompose(
			Symbol task,
			Observation state,
			Map<Symbol, Pair<Observation, Observation>> instance,
			Map<Symbol, List<Method>> taskMap) {
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(task);
		stack.add(new Example(tmp));
		int round = 0;
		while(!stack.isEmpty() && round < 5000) {
			round++;
			Trace tr = stack.pollLast();
			if(onlyPrimitive(tr)) {
				if(this.test(tr, state, instance)) {
					return tr;
				}
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof Task) {
						if(! taskMap.containsKey(a)) {
							continue;
						}
						List<Symbol> tmp2 = new ArrayList<>();
						for(int j = 0; j < i; j++) {
							tmp2.add(tr.getActionSequences().get(j));
						}
						Observation stateTmp = this.apply(new Example(tmp2),state, instance);
						for(Method m : taskMap.get(a)) {
							if(stateTmp.contains(instance.get(m).getX())) {
								List<Symbol> subt = new ArrayList<>();
								subt.addAll(current);
								m.getSubtasks().forEach(s -> subt.add(s));
								for(int j = i+1; 
										j < tr.getActionSequences().size(); 
										j++) {
									subt.add(tr.getActionSequences().get(j));
								}
								stack.add(new Example(subt));
							}
						}
						break;
					} else {
						current.add(a);
					}
				}
			}
		}
		return null;
	}
	
	public Trace decomposePrint(
			Symbol task,
			Observation state,
			Map<Symbol, Pair<Observation, Observation>> instance,
			Map<Symbol, List<Method>> taskMap) {
		System.out.println(taskMap.get(task));
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(task);
		stack.add(new Example(tmp));
		int round = 0;
		while(!stack.isEmpty() && round < 5) {
			round++;
//			System.out.println(stack);
			Trace tr = stack.pollLast();
			if(onlyPrimitive(tr)) {
				if(this.test(tr, state, instance)) {
					return tr;
				}
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof Task) {
						if(! taskMap.containsKey(a)) {
							continue;
						}
						List<Symbol> tmp2 = new ArrayList<>();
						for(int j = 0; j < i; j++) {
							tmp2.add(tr.getActionSequences().get(j));
						}
						Observation stateTmp = this.apply(new Example(tmp2),state, instance);
						for(Method m : taskMap.get(a)) {
							if(stateTmp.contains(instance.get(m).getX())) {
								List<Symbol> subt = new ArrayList<>();
								subt.addAll(current);
								m.getSubtasks().forEach(s -> subt.add(s));
								for(int j = i+1; 
										j < tr.getActionSequences().size(); 
										j++) {
									subt.add(tr.getActionSequences().get(j));
								}
								stack.add(new Example(subt));
							}
						}
						break;
					} else {
						current.add(a);
					}
				}
			}
		}
		return null;
	}
	
	public Trace decompose2(
			Symbol task,
			Observation state,
			Map<Symbol, Pair<Observation, Observation>> instance,
			Map<Symbol, List<Method>> taskMap) {
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(task);
		stack.add(new Example(tmp));
		int round = 0;
		while(!stack.isEmpty() && round < 1000000) {
			System.out.println(stack);
			round++;
			Trace tr = stack.pollLast();
			if(onlyPrimitive(tr)) {
				if(this.test(tr, state, instance)) {
					return tr;
				}
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof Task) {
						if(! taskMap.containsKey(a)) {
							continue;
						}
						List<Symbol> tmp2 = new ArrayList<>();
						for(int j = 0; j < i; j++) {
							tmp2.add(tr.getActionSequences().get(j));
						}
						Observation stateTmp = this.apply(new Example(tmp2),state, instance);
						for(Method m : taskMap.get(a)) {
							if(stateTmp.contains(instance.get(m).getX())) {
								List<Symbol> subt = new ArrayList<>();
								subt.addAll(current);
								m.getSubtasks().forEach(s -> subt.add(s));
								for(int j = i+1; 
										j < tr.getActionSequences().size(); 
										j++) {
									subt.add(tr.getActionSequences().get(j));
								}
								stack.add(new Example(subt));
							}
						}
						break;
					} else {
						current.add(a);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	private static boolean onlyPrimitive(Trace t) {
		for(Symbol a : t.getActionSequences()) {
			if(a instanceof Task) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	private static int nbNonPrimitive(Trace t) {
		int res = 0;
		for(Symbol a : t.getActionSequences()) {
			if(a instanceof Task) {
				res++;
			}
		}
		return res;
	}
	
	public boolean isConsistant() {
//		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
//			this.getDomain().entrySet()) {
//			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
//				switch(entry.getValue().getX().getValue(s)) {
//	            case TRUE:
//	                switch(entry.getValue().getY().getValue(s)) {
//	                case TRUE:
//	                    return false;
//	                case FALSE:
//	                    break;
//	                case ANY:
//	                    break;
//	                default:
//	                    break;
//	                }
//	                break;
//	            case FALSE:
//	                switch(entry.getValue().getY().getValue(s)) {
//	                case TRUE:
//	                    break;
//	                case FALSE:
//	                    return false;
//	                case ANY:
//	                    break;
//	                default:
//	                    break;
//	                }
//	                break;
//	            default:
//	                switch(entry.getValue().getY().getValue(s)) {
//	                case TRUE:
//	                    break;
//	                case FALSE:
//	                    break;
//	                case ANY:
//	                    break;
//	                default:
//	                    break;
//	                }
//	                break;
//	            }
//			}
//		}
		return true;
	}

	/**
	 * 
	 * @param t
	 * @param task
	 * @return
	 */
	private static Map<String, String> mapping(Symbol t, PDDLExpression task) {
		Map<String, String> mapping = new LinkedHashMap<>();
		for(int i = 0; i < t.getListParameters().size(); i++) {
			mapping.put(
					t.getListParameters().get(i), 
					task.getAtom().get(i+1).getImage());
		}
		return mapping;
	}
	
	/**
	 * 
	 * @param l
	 * @param exp
	 * @return
	 */
	private static Symbol getPredicate(List<Symbol> l, PDDLExpression exp) {
		Symbol res = null;
		for(Symbol p : l) {
			if(p.getName().equals(exp.getAtom().get(0).getImage())) {
				res = p.generalize(mapping(p, exp));
			}
		}
		return res;
	}
}
