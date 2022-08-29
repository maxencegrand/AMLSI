/**
 * 
 */
package main.experiment.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fsm.Symbol;
import learning.hierarchical.HtnDomain;
import main.Argument;
import simulator.hierarchical.HtnSimulator;

/**
 * @author Maxence Grand
 *
 */
public class Test {
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String domain = "/home/maxence/Documents/amlsi/benchmarks/htn/blocksworld/domain.hddl";
		String is = "/home/maxence/Documents/amlsi/benchmarks/htn/blocksworld/initial_states/initial1.hddl";
		HtnSimulator sim = new HtnSimulator(domain, is);
		List<Symbol> actions = sim.getActions();
		List<Symbol> pred = new ArrayList<>();
		for(Symbol a : sim.getPredicates()){
			pred.add(a);
		}
		for(Symbol a : sim.getPositiveStaticPredicates()){
			pred.add(a);
		}
		System.out.println(actions);
		System.out.println(pred);
		HtnDomain methods_ = new HtnDomain(
				pred, actions, 
				new HashMap<>(), 
				new HashMap<>());
		methods_.extractPreconditionsEffects(domain);
		List<fsm.Method> methods = methods_.extractMethods(domain, sim.getTasks());
		System.out.println(methods_.generateHDDL(
				Argument.getName(), 
				sim.getHierarchy(), 
				methods,
				sim.getTasks()));
	}
}
