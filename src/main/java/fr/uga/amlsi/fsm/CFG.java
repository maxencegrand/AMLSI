/**
 * 
 */
package fr.uga.amlsi.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.TaskTrace;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.symbols.Task;



/**
 * @author Maxence Grand
 *
 */
public class CFG {
	/**
	 * The initial method
	 */
	private static final Method INIT = new Method("S"); 
	/**
	 * Non Terminal symbols
	 */
	private List<Symbol> nonTerminals;
	/**
	 * Terminal symbols
	 */
	private List<Symbol> terminals;
	/**
	 * All language rules
	 */
	private Map<Symbol, List<CfgRule>> rules;
	/**
	 * All methods name
	 */
	private Map<String, Symbol> methodsName;
	/**
	 * All instantiations
	 */
	private Map<Task, List<Method>> instantiation;
	/**
	 * Accepted traces
	 */
	private List<TaskTrace> accepted;
	/**
	 * Constructs
	 */
	public CFG() {
		this.nonTerminals = new ArrayList<>();
		this.terminals = new ArrayList<>();
		this.rules = new LinkedHashMap<>();
		this.methodsName = new HashMap<>();
		this.instantiation = new HashMap<>();
		this.accepted = new ArrayList<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param m
	 */
	public CFG(List<Method> m) {
		this();
		m.forEach(met -> {
			Symbol t = met.getToDecompose();
			if(!this.nonTerminals.contains(t)) {
				this.nonTerminals.add(t);	
			}
			if(!this.rules.containsKey(t)) {
				List<Symbol> tmp = new ArrayList<>();
				tmp.add(t);
				this.rules.put(t, new ArrayList<>());
				this.methodsName.put(t.getName(), t);
			}
			List<Symbol> tmp = new ArrayList<>();
			met.getSubtasks().forEach(st -> {
				if(st instanceof Task) {
					if(!this.nonTerminals.contains(st)) {
						this.nonTerminals.add(st);
					}
				} else {
					if(!this.terminals.contains(st)) {
						this.terminals.add(st);
					}
				}
				tmp.add(st);
			});
			if(!this.rules.get(t).contains(new CfgRule(met, tmp))) {
				this.rules.get(t).add(new CfgRule(met, tmp));
			}
		});
	}
	
	/**
	 * Clone
	 * @return Clone
	 */
	public CFG clone() {
		CFG clone = new CFG();
		this.allRules().forEach(r -> clone.add(r));
		this.accepted.forEach(t -> clone.accepted.add(t));
		return clone;
	}
	
	/**
	 * Add a new rule
	 * @param r Rule
	 */
	public void add(CfgRule r) {
		Method met = (Method) r.getLeft();
		Symbol t = met.getToDecompose();
		if(!this.nonTerminals.contains(t)) {
			this.nonTerminals.add(t);	
		}
		if(!this.rules.containsKey(t)) {
			List<Symbol> tmp = new ArrayList<>();
			tmp.add(t);
			this.rules.put(t, new ArrayList<>());
			this.methodsName.put(t.getName(), t);
		}
		met.getSubtasks().forEach(st -> {
			if(st instanceof Task) {
				if(!this.nonTerminals.contains(st)) {
					this.nonTerminals.add(st);
				}
			} else {
				if(!this.terminals.contains(st)) {
					this.terminals.add(st);
				}
			}
		});
		if(!this.rules.get(t).contains(r)) {
			this.rules.get(t).add(r);
		}
	}
	
	/**
	 * to string method
	 * @return a string
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Non Terminals : (");
		this.nonTerminals.forEach(s -> str.append(" ").append(s.getName()));
		str.append(")\n");
		str.append("Terminals : (");
		this.terminals.forEach(s -> str.append(" ").append(s.getName()));
		str.append(")\n");
		this.rules.forEach((w,t) -> {
			str.append(w).append(" -> ");
			t.forEach(l -> {
				str.append("( ");
				l.getRight().forEach(s -> {
					str.append(s.getName()).append(" ");
				});
				str.append(") ");
			});
			str.append("\n");
		});
		return str.toString();
	}
	
	
	/**
	 * Methods getter
	 * @return methods
	 */
	public List<Method> getMethods() {
		List<Method> res = new ArrayList<>();
		for(Symbol task : this.rules.keySet()) {
			if(task.equals(INIT)) {
				continue;
			}
			for(int i = 0; i < this.rules.get(task).size(); i++) {
				Method m = (Method) this.rules.get(task).get(i).getLeft();
				m.setName(m.getName()+"_"+i);
				res.add(m);
			}
		}
		return res;
	}
	
	/**
	 * Simplify the methods set using the greedy approximation
	 * @param traces all traces
	 * @return The methods
	 */
	public CFG simplificationGreedy(List<TaskTrace> traces) {
		CFG res = new CFG();
		List<CfgRule> allRules = this.allRules();
		Iterator<CfgRule> it = allRules.iterator();
		while(it.hasNext()) {
			CfgRule r = it.next();
			if(r.getRight().isEmpty()) {
				it.remove();
				res.add(r);
			}
		}
		int iterMax = allRules.size();
		do {
			CFG bestClone = res.clone();
			for(CfgRule r : allRules) {
				CFG tmp = res.clone();
				tmp.add(r);
				if(!tmp.isConsistant()) {
					continue;
				}
				if(tmp.score(traces) > bestClone.score(traces)) {
					bestClone = tmp;
				} else if (tmp.score(traces) == bestClone.score(traces)
						&& tmp.nbPrimitive() < bestClone.nbPrimitive()){
					bestClone=tmp;
					
				}
			}
			res=bestClone;
			iterMax--;
		}while(res.score(traces) < traces.size() && iterMax > 0);
		return res;
	}
	
	/**
	 * Simplify the methods set using the heuristic approximation
	 * @param traces all traces
	 * @return The methods
	 */
	public static CFG simplificationHeuristic(
			Map<Task, List<TaskTrace>> traces,
			List<TaskTrace> tasksTraces,
			List<Task> toRecompose) {
		CFG res = new CFG();
		List<Task> computed = new ArrayList<>();
		Map<Task, CFG> currents = new HashMap<>();
		while(!toRecompose.isEmpty()) {
			Map<Task, List<Method>> mapMeth = new HashMap<>();
			for(Task t : toRecompose) {
				List<Method> methods = new ArrayList<>();
				for(TaskTrace tr : traces.get(t)) {
					List<TaskTrace> rec = tr.getAllRecomposition(
							tasksTraces,
							computed);
					for(TaskTrace tt : rec) {
							Method m = (Method)tt.getMethod().generalize();
							if(!methods.contains(m)) {
								methods.add(m);
							}
					}
				}
				mapMeth.put(t, methods);
			}
			for(Task t : toRecompose) {
				CFG tmp = new CFG(mapMeth.get(t));
				int maxIter = currents.containsKey(t) ? currents.get(t).allRules().size() : tmp.allRules().size();
				CFG newCFG = res.greedy(traces.get(t), tmp.allRules(), maxIter);
				if(currents.containsKey(t)) {
					if(newCFG.allRules(t).size() < currents.get(t).allRules(t).size()) {
						currents.put(t, newCFG);
					}
				} else {
					currents.put(t, newCFG);
				}
			}
			Task minimals = null;
			for(Task t : currents.keySet()) {
				CFG cfg = currents.get(t);
				if(minimals == null) {
					minimals = t;
				} else {
					if(cfg.allRules().size() 
							< currents.get(minimals).allRules().size()) {
						minimals = t;
					} else if (cfg.allRules().size() 
							== currents.get(minimals).allRules().size()) {
						if(cfg.nbPrimitive() 
								< currents.get(minimals).nbPrimitive()) {
							minimals = t;
						}
					}
				}
			}
			toRecompose.remove(minimals);
			computed.add(minimals);
			for(CfgRule r : currents.get(minimals).allRules()) {
				res.add(r);
			}
			currents.remove(minimals);
		}
		return res;
	}
	
	/**
	 * The greedy approximations
	 * @param traces2 all traces
	 * @param all rules
	 * @return Methods set decompositions
	 */
	private CFG greedy(
			List<TaskTrace> traces2, 
			List<CfgRule> rules,
			int maxIter) {
		List<TaskTrace> traces = new ArrayList<>();
		traces.addAll(traces2);
		int iter = 0;
		CFG current = this.clone();
		Iterator<CfgRule> it = rules.iterator();
		boolean empty = false;
		while(it.hasNext()) {
			CfgRule r = it.next();
			if(r.getRight().isEmpty()) {
				it.remove();
				current.add(r);
				empty=true;
			}
		}
		if(empty) {
			Iterator<TaskTrace> it2 = traces.iterator();
			while(it2.hasNext()) {
				TaskTrace tr = it2.next();
				if(tr.getStates().size() <= 1) {
					it2.remove();
				}
			}
		}
		int maxScore = current.score(traces);
		do {
			CFG bestClone = current.clone();
			for(CfgRule r : rules) {
				CFG tmp = current.clone();
				tmp.add(r);
				int tmpScore = tmp.score(traces);
				if(tmpScore > maxScore) {
					bestClone = tmp;
					maxScore = tmpScore;
				} else if (tmpScore == maxScore
						&& tmp.nbPrimitive() < bestClone.nbPrimitive()){
					bestClone=tmp;
					maxScore = tmpScore;
				} else if (tmpScore == maxScore
						&& tmp.nbPrimitive() == bestClone.nbPrimitive()
						&& tmp.nbNonPrimitive() < bestClone.nbNonPrimitive()){
					bestClone=tmp;
					maxScore = tmpScore;
				}
			}
			current=bestClone;
			iter++;
		}while(maxScore < traces.size() && iter < maxIter);
		if(maxScore == 0) {
			for(CfgRule r : rules) {
				current.add(r);
			}
		}
		System.gc();
		return current;
	}
	

	
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public boolean accept(TaskTrace t) {
		if(this.accepted.contains(t)) {
			return true;
		}
		this.instantiate(t.contants());
		LinkedList<List<Symbol>> fifo = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(t.getTask());
		fifo.add(tmp);
		
		fifo.add(new ArrayList<>());
		boolean b = this.parse(t);
		return b;
	}
	
	
	/**
	 * Try to parse traces
	 * @param t trace
	 * @return true if the trace is parsed
	 */
	public boolean parse(TaskTrace t) {
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(t.getTask());
		stack.add(new Example(tmp));
		int round = 0;
		while(!stack.isEmpty() && round < 100) {
			round++;
			Trace tr = stack.pollLast();
			//Test Compatibility
			if(!compatible(t.getActionSequences(), tr.getActionSequences())) {
				continue;
			}
			//Derive
			if(isOnlyTerminal(tr.getActionSequences())) {
				if(tr.equals(new Example( t.getActionSequences()))) {
					return true;
				};
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof Task) {
						if(! this.instantiation.containsKey(a)) {
							continue;
						}
						for(Method m : this.instantiation.get(a)) {
							List<Symbol> subt = new ArrayList<>();
							subt.addAll(current);
							m.getSubtasks().forEach(s -> subt.add(s));
							for(int j = i+1; j < tr.getActionSequences().size(); j++) {
								subt.add(tr.getActionSequences().get(j));
							}
							stack.add(new Example(subt));
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
	

	/**
	 * Try to parse traces
	 * @param t trace
	 * @param met All methods
	 * @return true if the trace is parsed
	 */
	public boolean parse(TaskTrace t, List<Symbol> met) {
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(t.getTask());
		stack.add(new Example(met));
		int round = 0;
		while(!stack.isEmpty() && round < 100) {
			round++;
			Trace tr = stack.pollLast();
			//Test Compatibility
			if(!compatible(t.getActionSequences(), tr.getActionSequences())) {
				continue;
			}
			//Derive
			if(isOnlyTerminal(tr.getActionSequences())) {
				if(tr.equals(new Example( t.getActionSequences()))) {
					return true;
				};
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof Task) {
						if(! this.instantiation.containsKey(a)) {
							continue;
						}
						for(Method m : this.instantiation.get(a)) {
							List<Symbol> subt = new ArrayList<>();
							subt.addAll(current);
							m.getSubtasks().forEach(s -> subt.add(s));
							for(int j = i+1; j < tr.getActionSequences().size(); j++) {
								subt.add(tr.getActionSequences().get(j));
							}
							stack.add(new Example(subt));
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
	
	private boolean parse2(TaskTrace t, List<Symbol> met) {
		LinkedList<Trace> stack = new LinkedList<>();
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(t.getTask());
		stack.add(new Example(met));
		int round = 0;
		while(!stack.isEmpty() && round < 100) {
			round++;
			Trace tr = stack.pollLast();
			System.err.println("---"+tr);
			//Test Compatibility
			if(!compatible(t.getActionSequences(), tr.getActionSequences())) {
				System.err.println("Not COmpatible");
				continue;
			}
			//Derive
			if(isOnlyTerminal(tr.getActionSequences())) {
				System.err.println("coucou");
				if(tr.equals(new Example( t.getActionSequences()))) {
					return true;
				};
			} else {
				List<Symbol> current = new ArrayList<>();
				for(int i = 0; i< tr.getActionSequences().size(); i++) {
					Symbol a = tr.getActionSequences().get(i);
					if(a instanceof Task) {
						if(! this.instantiation.containsKey(a)) {
							continue;
						}
						for(Method m : this.instantiation.get(a)) {
							List<Symbol> subt = new ArrayList<>();
							subt.addAll(current);
							m.getSubtasks().forEach(s -> subt.add(s));
							for(int j = i+1; j < tr.getActionSequences().size(); j++) {
								subt.add(tr.getActionSequences().get(j));
							}
							stack.add(new Example(subt));
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
	
	/**
	 * Check if all symbols are terminals
	 * @param l list of symbols
	 * @return boolean
	 */
	public boolean isOnlyTerminal(List<Symbol> l) {
		for(Symbol s: l) {
			if(s instanceof Task) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Compute the score
	 * @param traces all traces
	 * @return score
	 */
	public int score(List<TaskTrace> traces) {
		int res = 0;
		for(TaskTrace trace : traces) {
			if(this.accept(trace)) {
				res++;
				if(!this.accepted.contains(trace)) {
					this.accepted.add(trace);
				}
			}
		}
		return res;
	}

	/**
	 * Check if all traces are accepted
	 * @param traces traces
	 * @return true if all traces are accepted
	 */
	public boolean acceptAll(List<TaskTrace> traces) {
		for(TaskTrace trace : traces) {
			if(this.accept(trace)) {
				if(!this.accepted.contains(trace)) {
					this.accepted.add(trace);
				}
			} else {
				return false;
			}
		}
		return true;
	}

	
	/**
	 * Get all rules
	 * @return rules
	 */
	public List<CfgRule> allRules() {
		List<CfgRule> res = new ArrayList<>();
		this.rules.forEach((k,v) -> res.addAll(v));
		return res;
	}
	
	/**
	 * Get all rules for a task t
	 * @param t the task
	 * @return rules
	 */
	public List<CfgRule> allRules(Task t) {
		List<CfgRule> res = new ArrayList<>();
		this.rules.forEach((k,v) -> {
			v.forEach(r -> {
				if(r.getLeft().getToDecompose().equals(t)) {
					res.add(r);
				}
			});
		});
		return res;
	}
	
	/**
	 * 
	 * @param constant
	 */
	private void instantiate(Map<String, String> constant) {
		this.instantiation = new HashMap<>();
		this.rules.forEach((k,v) -> {
			v.forEach(r -> {
				r.getLeft().allInstances(constant).forEach(s -> {
					Method m = (Method)s;
					if(!this.instantiation.containsKey(m.getToDecompose())) {
						this.instantiation.put(
								(Task)m.getToDecompose(), 
								new ArrayList<>());
						m.getToDecompose().generalize();
					}
					this.instantiation.get((Task)m.getToDecompose().clone()).add(m);
				});
			});
		});
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isConsistant() {
		for(Symbol s : this.rules.keySet()) {
			for(CfgRule r : this.rules.get(s)) {
				for(Symbol a : r.getRight()) {
					if(a instanceof Task) {
						if(!this.rules.containsKey(a.generalize())) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	private int nbPrimitive() {
		int res = 0;
		for(CfgRule r : this.allRules()) {
			for(Symbol s : r.getRight()) {
				if(!(s instanceof Task)) {
					res++;
				}
			}
		}
		return res;
	}
	
	private int nbNonPrimitive() {
		int res = 0;
		for(CfgRule r : this.allRules()) {
			for(Symbol s : r.getRight()) {
				if(s instanceof Task) {
					res++;
				}
			}
		}
		return res;
	}
	
	/**
	 * 
	 */
	public boolean equals(Object other) {
		if(!(other instanceof CFG)) {
			return false;
		}
		CFG o = (CFG) other;
		List<CfgRule> l1 = this.allRules();
		List<CfgRule> l2 = o.allRules();
		if(l1.size() != l2.size()) {
			return false;
		}
		for(CfgRule r : l1) {
			if(!l2.contains(r)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 */
	public int hashCode() {
		int hash = 0;
		for(CfgRule r : this.allRules()) {
			hash += r.hashCode();
		}
		return hash;
	}
	
	public int score() {
		return this.accepted.size();
	}
	
	/**
	 * 
	 * @param toAccept
	 * @param toDerive
	 * @return
	 */
	private static boolean compatible(List<Symbol> toAccept, List<Symbol> toDerive) {
		int nbPrim = 0;
		int idxPrimitive = 0;
		if(toAccept.isEmpty()) {
			return toDerive.isEmpty();
		}
		if(toDerive.isEmpty()) {
			return toAccept.isEmpty();
		}
		if(!(toDerive.get(toDerive.size()-1) instanceof Task)) {
			if(!toDerive.get(toDerive.size()-1).equals(toAccept.get(toAccept.size()-1))) {
				return false;
			}
		}
		if(!(toDerive.get(0) instanceof Task)) {
			if(!toDerive.get(0).equals(toAccept.get(0))) {
				return false;
			}
		}
		for(Symbol s : toDerive) {
			if(!(s instanceof Task)) {
				nbPrim ++;
				if(nbPrim > toAccept.size()) {
					return false;
				}
				boolean b = false;
				for(int i = idxPrimitive; i < toAccept.size(); i++) {
					if(toAccept.get(i).equals(s)) {
						b = true;
						idxPrimitive=i;
						break;
					}
				}
				if(!b) {
					return false;
				}
			} else {
			}
		}
		return true;
	}
	
	/**
	 * Map all traces with methods
	 * @param t trace
	 * @return methods
	 */
	public Map<TaskTrace, Method> mapTraceMethod(List<TaskTrace> t) {
		Map<TaskTrace, Method> res = new HashMap<>();
		t.forEach(tr -> {
			this.instantiate(tr.contants());
			for(Method m : this.instantiation.get(tr.getTask())) {
				List<Symbol> tmp = new ArrayList<>();
				tmp.addAll(m.getSubtasks());
				if(this.parse(tr, tmp)) {
					res.put(tr, m);
					break;
				}
			}
		});
		return res;
	}
}
