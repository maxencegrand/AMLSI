/**
 * 
 */
package simulator.hierarchical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import exception.PlanException;
import fr.uga.pddl4j.parser.PDDLExpression;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.parser.PDDLProblem;
import fr.uga.pddl4j.parser.PDDLTypedSymbol;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.Method;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.HTNProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Task;
import fsm.CFG;
import fsm.Example;
import fsm.Predicate;
import fsm.Symbol;
import fsm.TaskTrace;
import fsm.Trace;
import learning.Observation;
import learning.TypeHierarchy;
import simulator.Pddl4jUtils;
import utils.BiMap;

/**
 * @author Maxence Grand
 *
 */
public class HtnSimulator extends HierarchicalSimulator{
	/**
	 * 
	 */
	private HTNProblem instance;
	/**
	 * 
	 */
	private List<fsm.Symbol> actions;
	/**
	 * 
	 */
	private List<fsm.Symbol> tasks;
	/**
	 * 
	 */
	private BiMap<fsm.Symbol, fr.uga.pddl4j.problem.operator.Action> actionsTable;
	/**
	 * 
	 */
	private BiMap<fsm.Symbol, fr.uga.pddl4j.problem.Task> tasksTable;
	/**
import simulator.BlackBox;
	 * 
	 */
	private List<fsm.Symbol> fluents;
	/**
	 * 
	 */
	private List<fsm.Symbol> positiveStatics;
	/**
	 * 
	 */
	private Map<Fluent, fsm.Symbol> fluentsTable;
	/**
	 * 
	 */
	private Node internalInitial;
	/**
	 * 
	 */
	private Node internalCurrent;
	/**
	 * 
	 */
	private Map<String, String> constantType;
	/**
	 * 
	 */
	private TypeHierarchy hier;
	/**
	 * 
	 */
	private BiMap<fsm.Method, fr.uga.pddl4j.problem.operator.Method> methodsTable;
	/**
	 * 
	 */
	private List<fsm.Symbol> methods;
	/**
	 * 
	 */
	private Map<fsm.Task, List<fsm.Method>> tasksMethods;
	/**
	 * 
	 * Constructs
	 */
	public HtnSimulator() {
		this.positiveStatics = new ArrayList<>();
		this.fluents = new ArrayList<>();
		this.fluentsTable = new HashMap<>();
		this.constantType = new HashMap<>();
		this.actions = new ArrayList<>();
		this.actionsTable = new BiMap<>();
		this.constantType = new HashMap<>();
		this.tasks = new ArrayList<>();
		this.tasksTable = new BiMap<>();
		this.hier = new TypeHierarchy();
		this.methods = new ArrayList<>();
		this.methodsTable = new BiMap<>();
		this.tasksMethods = new HashMap<>();
	}
	/**
	 * 
	 * Constructs 
	 * @param d
	 * @param p
	 */
	public HtnSimulator(String d, String p){
		this();
		PDDLParser parser = new PDDLParser();
		try{
			parser.parse(d, p);
			instance = new HTNProblem(parser.getDomain(),parser.getProblem());
			instance.instantiate();
			
			if(instance == null){
				System.out.println("cp null "+d+" "+p);
				System.exit(1);
			}
			
			//Initialize states
			internalInitial = new Node(new State(instance.getInitialState()));
			internalCurrent = new Node(internalInitial);
			
			//Initialize predicates
			List<Symbol> allSym = new ArrayList<>();
			List<Symbol> allFluents = new ArrayList<>();
			try {
				PDDLProblem pb = parser.getProblem();
				
				for(PDDLTypedSymbol tt : pb.getObjects()) {
					this.constantType.put(tt.getImage(), tt.getTypes().get(0).getImage());
				}
				
				//Fluent Predicate
				for(Fluent fluent : instance.getFluents()) {
					String name = Pddl4jUtils.getPredicateName(instance.toString(fluent));
					Map<String, String> paramMap = new LinkedHashMap<>();
					boolean b = true;
					for(int idx : fluent.getArguments()) {
						String param = instance.getConstantSymbols().get(idx);
						String paramType = this.constantType.get(param);
						b &= (!paramMap.keySet().contains(param));
						paramMap.put(param, paramType);
					}
					if(b) {
						Symbol pred = new Predicate(name, paramMap);
						allSym.add(pred);
						switch(instance.getInertia().get(instance.getPredicateSymbols().indexOf(name))) {
						case INERTIA:
							this.positiveStatics.add(pred);
							break;
						default:
							allFluents.add(pred);
							break;
						}
						this.fluentsTable.put(fluent, pred);
					}		
				}
				
				//Static Predicate
				for(PDDLExpression exp : pb.getInit()) {
					Map<String, String> paramMap = new LinkedHashMap<>();
					String name = exp.getAtom().get(0).toString();
					boolean b = true;
					for(int i = 1; i < exp.getAtom().size(); i++) {
						String param = exp.getAtom().get(i).toString();
						String paramType = this.constantType.get(param);
						b &= (!paramMap.keySet().contains(param));
						paramMap.put(param, paramType);
					}
					if(b) {
						Symbol pred = new Predicate(name, paramMap);
						if(! allSym.contains(pred)) {
							this.positiveStatics.add(pred);
							allSym.add(pred);
						};
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			//initialize Actions
			for (fr.uga.pddl4j.problem.operator.Action  op : instance.getActions()) {
				Map<String, String> params= new LinkedHashMap<>();
				String name = op.getName();
				boolean b = false;
				for (int i = 0; i < op.arity(); i++) {
					int index = op.getValueOfParameter(i);
					int index2 = op.getTypeOfParameters(i);
					String param = instance.getConstantSymbols().get(index);
					String type = instance.getTypes().get(index2);
					if(params.containsKey(param)) {
						b=true;
						continue;
					}
					params.put(param, type);
					constantType.put(param, type);
				}
				if(b) {
					continue;
				}
				fsm.Symbol act = new fsm.Action(name, params);
				this.actions.add(act);
				this.actionsTable.put(act, op);
			}
			
			//Initialize Task
			for (fr.uga.pddl4j.problem.Task task : instance.getTasks()) {
				Map<String, String> params= new LinkedHashMap<>();
				String name = instance.getTaskSymbols().get(task.getSymbol());
				boolean b = false;
				for (int i = 0; i < task.getArguments().length; i++) {
					int index = task.getArguments()[i];
					String param = instance.getConstantSymbols().get(index);
					String type = constantType.get(param);
					if(params.containsKey(param)) {
						b=true;
						continue;
					}
					params.put(param, type);
				}
				if(b) {
					continue;
				}
				fsm.Symbol act = new fsm.Action(name, params);
				if(!this.actions.contains(act)) {
					act = new fsm.Task(name, params);
					this.tasks.add(act);
					this.tasksTable.put(act, task);
				}	
			}
			
			//Initialize Methods
			for(fr.uga.pddl4j.problem.operator.Method method : instance.getMethods()) {
				Map<String, String> params = new LinkedHashMap<>();
				String name = method.getName();
				boolean b = false;
				for (int i = 0; i < method.arity(); i++) {
					int index = method.getValueOfParameter(i);
					int index2 = method.getTypeOfParameters(i);
					String param = instance.getConstantSymbols().get(index);
					String type = instance.getTypes().get(index2);
					if(params.containsKey(param)) {
						b=true;
						continue;
					}
					params.put(param, type);
				}
				if(b) {
					continue;
				}
				Task toDecompose = instance.getTasks().get(method.getTask());
				fsm.Method act = new fsm.Method(
						name, params,
						(fsm.Task)this.tasksTable.getRight(toDecompose));
				this.methods.add(act);
				this.methodsTable.put(act, method);
				List<fsm.Action> sub = new LinkedList<>();
				method.getSubTasks().forEach(t -> {
					fr.uga.pddl4j.problem.Task task = instance.getTasks().get(t);
					if(!task.isPrimtive()) {
						sub.add((fsm.Task)this.tasksTable.getRight(task));
					} else {
						Action a = instance.getActions().get(t);
						sub.add((fsm.Action)this.actionsTable.getRight(a));
					}
				});
				act.setSubtasks(sub);
			}
			
			//Initialize Task -> Methods
			for(fsm.Symbol method : this.methods) {
				fsm.Task task =((fsm.Method) method).getToDecompose();
				if(!this.tasksMethods.containsKey(task)) {
					this.tasksMethods.put(task, new LinkedList<>());
				}
				this.tasksMethods.get(task).add((fsm.Method)method);
			}
			
			/**
			 * Compute type hierarchy
			 */
			hier.infere(allSym, this.getParamTypes());
			this.actions = hier.compute_litterals_operators
					(this.actions, this.getParamTypes());
			this.tasks = hier.compute_litterals_operators
					(this.tasks, this.getParamTypes());
			this.positiveStatics = hier.compute_litterals_operators
					(this.positiveStatics, this.getParamTypes());
			this.fluents = hier.compute_litterals_operators
					(allFluents, this.getParamTypes());
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}

	/**
	 * Getter of hierarchy
	 * @return the hierarchy
	 */
	public TypeHierarchy getHierarchy() {
		return hier;
	}
	
	@Override
	public void reInit() {
		this.internalCurrent = new Node(this.getInternalInitial());
	}


	@Override
	public boolean testAction(Symbol op) {
		fr.uga.pddl4j.problem.operator.Action ops = this.actionsTable.getLeft(op);
		if(ops != null && ops.isApplicable(this.internalCurrent)) {
			return true;
		}
		return false;
	}

	public List<Symbol> getSymbolsState(Node state) {
		List<Symbol> res = new ArrayList<>();
		for (int i = state.nextSetBit(0); i >= 0; i = state.nextSetBit(i + 1)) {
			Symbol p = this.fluentsTable.get(instance.getFluents().get(i));
			res.add(p);
		}
		return res;
	}

	@Override
	public List<Symbol> getPredicates() {
		return this.fluents;
	}

	@Override
	public List<Symbol> getActions() {
		return this.actions;
	}

	@Override
	public List<String> getTypes() {
		List<String> res = new ArrayList<>();
		this.constantType.forEach((s1, s2) ->{
			if(! res.contains(s2)) {
				res.add(s2);
			}
		});
		return res;
	}

	@Override
	public Map<String, String> getParamTypes() {
		return this.constantType;
	}
	
	@Override
	public List<Symbol> getTasks() {
		return this.tasks;
	}

	@Override
	public void apply(Symbol op) throws PlanException {
		// TODO Auto-generated method stub
		Trace t = this.decompose(op);
		if(t == null || !this.testTrace(t)) {
			throw new PlanException();
		}
		this.applyTrace(t);
	}

	/**
	 * Getter of internalInitial
	 * @return the internalInitial
	 */
	public Node getInternalInitial() {
		return internalInitial;
	}

	/**
	 * Setter internalInitial
	 * @param internalInitial the internalInitial to set
	 */
	public void setInternalInitial(Node internalInitial) {
		this.internalInitial = internalInitial;
	}

	/**
	 * Getter of internalCurrent
	 * @return the internalCurrent
	 */
	public Node getInternalCurrent() {
		return internalCurrent;
	}

	/**
	 * Setter internalCurrent
	 * @param internalCurrent the internalCurrent to set
	 */
	public void setInternalCurrent(Node internalCurrent) {
		this.internalCurrent = internalCurrent;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public Trace getDecomposition(fsm.Symbol t) {
		List<Symbol> trace = this.getDecomposition(
				t, 
				new Node(this.getInternalCurrent()));
		if(trace==null) {
			return null;
		}
		return new Example(trace);
	}
	
	private List<Symbol> getDecomposition(fsm.Symbol t, Node n) {
		if(t instanceof fsm.Task) {
			for(fsm.Method m : this.tasksMethods.get(t)) {
				Method met = this.methodsTable.getLeft(m);
				boolean b = true;
				if(met != null && n.satisfy(met.getPrecondition())) {
					List<Symbol> res = new ArrayList<>();
					for(Symbol op : m.getSubtasks()) {
						List<Symbol> tmp = this.getDecomposition(op, n);
						if(tmp == null) {
							b=false;
							break;
						}
						res.addAll(tmp);
					}
					if(b) {
						
					}
				}
			}
			return null;
		} else {
			List<Symbol> res = new ArrayList<>();
			res.add(t);
			n.apply(this.actionsTable.getLeft(t).getUnconditionalEffect());
			return res;
		}
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public Trace decompose(fsm.Symbol t) {
		if(t instanceof fsm.Task) {
			if(!this.tasksMethods.containsKey(t)) {
				return null;
			}
		}
		LinkedList<Trace> fifo = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(t);
		fifo.add(new Example(tmp));
		int bound = 5000;
		while(! fifo.isEmpty() && bound > 0) {
			Trace tr = fifo.poll();
			while(this.isTerminalTrace(tr)) {
				if(this.testTrace(tr)) {
					return tr;
				}
				if(fifo.isEmpty()) {
					return null;
				}
				tr = fifo.poll();
			}
			this.decompose(tr, new Node(this.getInternalCurrent()))
					.forEach(e -> fifo.add(e));
			bound--;
		}
		return null;
	}
	
	/**
	 * 
	 * @param t
	 * @param n
	 * @return
	 */
	private List<Trace> decompose(Trace t, Node n) {
		List<Trace> fifo = new LinkedList<>();
		List<List<Symbol>> allDec = new ArrayList<>();
		allDec.add(new ArrayList<>());
		boolean onlyTerm = true;
		boolean atLeastOne = true;
		for(Symbol a : t.getActionSequences()) {
			if(!atLeastOne) {
				break;
			}
			if(onlyTerm && a instanceof fsm.Task) {
				onlyTerm=false;
				atLeastOne=false;
				List<List<Symbol>> tmp = new ArrayList<>();
//				System.out.println(t+" "+a+" "+this.tasksMethods.get(a));
				for(fsm.Method m : this.tasksMethods.get(a)) {
					Method met = this.methodsTable.getLeft(m);
					if(met == null) {
						continue;
					}
					for(List<Symbol> l : allDec) {
						boolean b =true;
						Node nn = new Node(n);
						for(Symbol s : l) {
							b &= nn.satisfy(this.actionsTable.getLeft(s)
									.getPrecondition());
							nn.apply(this.actionsTable.getLeft(s)
									.getUnconditionalEffect());

						}
						b&=nn.satisfy(met.getPrecondition());
						if(b) {
							atLeastOne=true;
							List<Symbol> tmp2 = new ArrayList<>();
							tmp2.addAll(l);
							for(Symbol st : m.getSubtasks()) {
								tmp2.add(st);
							}
							tmp.add(tmp2);
						}
					}
				}
				allDec.clear();
				allDec.addAll(tmp);
			} else {
				allDec.forEach(l -> l.add(a));
			}
		};
		if(!atLeastOne){
			return new ArrayList<>();
		}
		allDec.forEach(e -> fifo.add(new Example(e)));
		return fifo;
	}
	
	/**
	 * 
	 */
	public boolean test(fsm.Symbol t) {
//		System.out.println("test "+t);
		if(t instanceof fsm.Task) {
			Trace trace = this.decompose(t);
			if(trace == null) {
				return false;
			};
			if(this.testTrace(trace) ) {
				return true;
			} else {
				return false;
			}
		} else {
			return this.testAction(t);
		}
	}
	
	/**
	 * 
	 */
	public boolean testTrace(Trace trace) {
		Node n = new Node(this.getInternalCurrent());
		for(Symbol a : trace.getActionSequences()) {
			if(this.actionsTable.getLeft(a) == null) {
				return false;
			}
			if(n.satisfy(this.actionsTable.getLeft(a).getPrecondition())) {
				n.apply(this.actionsTable.getLeft(a).getUnconditionalEffect());
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 */
	public void applyTrace(Trace trace) {
		Node n = new Node(this.getInternalCurrent());
		for(Symbol a : trace.getActionSequences()) {
			n.apply(this.actionsTable.getLeft(a).
					getUnconditionalEffect());
		}
		this.setInternalCurrent(n);
	}
	
	/**
	 * Get the current state
	 * @return current state
	 */
	public Observation getCurrentState() {
		return this.getObservedState(new State(this.getInternalCurrent()));
	}

	/**
	 * Get the initial state
	 * @return initial state
	 */
	public Observation getInitialState() {
		return this.getObservedState(new State(this.internalInitial));
	}
	
	/**
	 * Get observed state
	 * @param n The Internal state
	 * @return The observed state
	 */
	private Observation getObservedState(State n) {
		Observation res = new Observation(this.getPredicates());
		for(fsm.Symbol p : this.getPositiveStaticPredicates()) {
			res.addTrueObservation(p);
		}
		for (int i = n.nextSetBit(0); i >= 0; i = n.nextSetBit(i + 1)) {
			Symbol p = this.fluentsTable.get(instance.getFluents().get(i));
			if(p != null) {
				res.addTrueObservation(p);
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	private boolean isTerminalTrace(Trace t) {
		for(Symbol s : t.getActionSequences()) {
			if(s instanceof fsm.Task) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param l
	 * @return
	 */
	public List<Trace> allTerminal(List<Trace> l) {
		Iterator<Trace> it = l.iterator();
		List<Trace> res = new ArrayList<>();
		while(it.hasNext()) {
			Trace t = it.next();
			if(this.isTerminalTrace(t)) {
				it.remove();
				res.add(t);
			}
		}
		return res;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean test(Trace prefix, Symbol task, Trace decomposition) {
		// TODO Auto-generated method stub
//		System.out.println("-"+task);
		this.reInit();
		this.applyTrace(prefix);
		if(this.testTrace(decomposition)) {
			return this.correct(task, decomposition);
		}
		return false;
	}
	
	/**
	 * 
	 * @param task
	 * @param decomposition
	 * @return
	 */
	private boolean correct(Symbol task, Trace decomposition) {
//		System.out.println("*"+task);
		boolean b = this.parse(new TaskTrace(
				new Example(decomposition.getActionSequences()), 
				task, 
				new ArrayList<>()));
		return b;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	private boolean parse(TaskTrace t) {
//		System.out.println(t.getTask());
//		System.out.println(this.tasksMethods.get(t.getTask()));
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(t.getTask());
		stack.add(new Example(tmp));
		int round = 0;
		while(!stack.isEmpty() && round < 100) {
			round++;
			Iterator<Trace> it = stack.iterator();
			while(it.hasNext()) {
				Trace tr = it.next();
				if(isTerminalTrace(tr)) {
					//System.out.println(tr.getActionSequences()+" "+tr.equals(new Example( t.getActionSequences())));
					if(tr.equals(new Example( t.getActionSequences()))) {
						return true;
					};
					it.remove();
				}
				
			}
			if(stack.isEmpty()) {
				return false;
			}
			Trace tr = stack.pollLast();
			if(!compatible(t.getActionSequences(), tr.getActionSequences())) {
				continue;
			}
//			System.out.println("+"+tr);
			//Derive
			if(isTerminalTrace(tr)) {
				//System.out.println(tr.getActionSequences()+" "+tr.equals(new Example( t.getActionSequences())));
				if(tr.equals(new Example( t.getActionSequences()))) {
					return true;
				};
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof fsm.Task) {
						if(! this.tasksMethods.containsKey(a)) {
							continue;
						}
						for(fsm.Method m : this.tasksMethods.get(a)) {
							///System.out.println(m);
							if(m.getSubtasks().isEmpty()) {
								List<Symbol> subt = new ArrayList<>();
								subt.addAll(current);
								for(int j = i+1; j < tr.getActionSequences().size(); j++) {
									subt.add(tr.getActionSequences().get(j));
								}
								//System.out.println(subt);
								stack.add(new Example(subt));
							} else {
								List<Symbol> subt = new ArrayList<>();
								subt.addAll(current);
								m.getSubtasks().forEach(s -> subt.add(s));
								for(int j = i+1; j < tr.getActionSequences().size(); j++) {
									subt.add(tr.getActionSequences().get(j));
								}
								//System.out.println(subt);
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
		return false;
	}
	
	public static boolean compatible(List<Symbol> toAccept, List<Symbol> toDerive) {
		int nbPrim = 0;
		int idxPrimitive = 0;
		for(Symbol s : toDerive) {
			if(!(s instanceof fsm.Task)) {
				nbPrim ++;
				if(nbPrim > toAccept.size()) {
					return false;
				}
				boolean b = false;
				for(int i = idxPrimitive; i < toAccept.size(); i++) {
					if(toAccept.get(i).equals(s)) {
						b = true;
						idxPrimitive=i;
					}
				}
				if(!b) {
					return false;
				}
			}
		}
		return nbPrim <= toAccept.size();
	}
	
	/**
	 * Getter of positiveStaticPredicates
	 * @return the positiveStaticPredicates
	 */
	public List<Symbol> getPositiveStaticPredicates() {
		return this.positiveStatics;
	}
}
