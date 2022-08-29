/**
 * 
 */
package simulator.hierarchical;

import java.util.List;
import java.util.Map;

import exception.PlanException;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.operator.Action;
import fsm.Symbol;
import fsm.Trace;
import learning.Observation;
import learning.TypeHierarchy;
import simulator.Simulator;

/**
 * @author Maxence Grand
 *
 */
public abstract class HierarchicalSimulator extends Simulator{
	 
    /**
     * 
     * @return
     */
//    public boolean consistant(
//    		Map<Symbol, Observation> preconditions,
//    		Map<Symbol, Observation> effects) {
//    	for(Symbol act : this.getAllActions()) {
//    		if(! this.consistantPrecondition(act, preconditions.get(act))) {
//    			return false;
//    		}
//    		if(! this.consistantEffect(act, effects.get(act))) {
//    			return false;
//    		}
//    	}
//    	return true;
//    }
    
    /**
     * 
     * @param example
     * @return
     */
    public boolean testable(List<Symbol> example) {
    	List<Symbol> act = this.getActions();
    	for(Symbol a : example) {
    		if(!act.contains(a)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * 
     * @param act
     * @param p
     * @return
     */
//    protected abstract boolean consistantPrecondition(Symbol act, Observation p);
    
    /**
     * 
     * @param act
     * @param e
     * @return
     */
//    protected abstract boolean consistantEffect(Symbol act, Observation e);
    
    /**
     * 
     * @return
     */
    public abstract List<Symbol> getTasks();
    
    /**
	 * 
	 * @param t
	 * @return
	 */
	public abstract boolean test(fsm.Symbol t);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public abstract Trace getDecomposition(fsm.Symbol t);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public abstract boolean testTrace(Trace t);
	
	/**
	 * 
	 * @param trace
	 */
	public abstract void applyTrace(Trace trace);
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public abstract Trace decompose(fsm.Symbol t);
	
	/**
	 * 
	 * @param prefix
	 * @param task
	 * @param decomposition
	 * @return
	 */
	public abstract boolean test(
			Trace prefix, 
			fsm.Symbol task, 
			Trace decomposition);
//    /**
//     * 
//     * @param task
//     * @return
//     */
//    public abstract boolean taskIsDone(Symbol task);
}
