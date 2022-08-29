package simulator.temporal;

import java.util.List;
import java.util.Map;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.operator.Action;
import fsm.Symbol;
import learning.TypeHierarchy;

/**
 * @author Maxence Grand
 *
 */
public abstract class TemporalOracle {
	/**
	 * 
	 */
	public static float TOLERANCE_VALUE = 0.1f;
    /**
     * Apply the selected action
     * @return the resulting state
     */
    public abstract Node apply();
    /**
     * Reinitialize current state
     */
    public abstract void reInit();
    /**
     * Get the initial state
     * @return the initial state
     */
    public abstract Node getInitialState();
    /**
     * Get the current state
     * @return the current state
     */
    public abstract Node getCurrentState();
    /**
     * Get the current time
     * @return the current time
     */
    public abstract float getCurrentTime();
    /**
     * GTet the duration of an action op
     * @param op The action
     * @return The duration
     */
    public abstract float getDuration(Symbol op);
    /**
     * Test if an action is feasible in the current state
     * @param op an action
     * @param t the timestamp
     * @return True the action is feasible
     */
    public abstract boolean isApplicable(Symbol op, float t);
    /**
     * Get all predicates present in the state
     * @param state the state
     * @return a set of predicate
     */
    public abstract List<Symbol> getSymbolsState(Node state);
    /**
     * Get all predicates
     * @return all predicates
     */
    public abstract List<Symbol> getAllPredicates();
    /**
     * Return all actions
     * @return all actions
     */
    public abstract List<Symbol> getAllActions();
    /**
     * Get all types
     * @return all types
     */
    public abstract List<String> getTypes();
    /**
     * Get positive static predicates
     * @return positive static predicates
     */
    public abstract List<Symbol> getPositiveStaticPredicate();
    /**
     * Check if an example is feasible starting by the initial state
     * @param example to test
     * @param All timestamps
     * @return True if the example is feasible
     */
    public abstract boolean accept(List<Symbol> example, List<Float> timestamps);
    /**
     * Check if an action is feasible in the current state
     * @param example action to test
     * @param t the timestamp
     * @return True if the action is feasible
     */
    public abstract boolean accept(Symbol example, float t);
    /**
     * Get all parameters with types
     * @return All typed parameters
     */
    public abstract Map<String, String> getParamTypes();
    /**
     * Get the type hierarchy
     * @return The type Hierarchy
     */
    public abstract TypeHierarchy typeHierarchy();
    /**
     * Finish the simulation
     */
    public abstract void endSimulation();
    /**
     * Execute the next end action
     * 
     * @return True if there exist at least one current action
     */
    public abstract boolean executeNextEnd();
    /**
     * 
     * @param s
     * @return
     */
    public abstract boolean acceptAll(List<TemporalExample> s);
    /**
     * 
     * @param s
     * @return
     */
    public abstract boolean rejectAll(List<TemporalExample> s);
    
    /**
     * 
     * @param s
     * @return
     */
    public abstract boolean rejectLast(TemporalExample s);
	
    /**
     * 
     * @param s
     * @return
     */
	public abstract boolean accept(TemporalExample s);
	
	/**
	 * 
	 * @return
	 */
	public abstract Map<Symbol, Action> getSplitTable();
	
	/**
	 * 
	 * @param op
	 * @return
	 */
	public  abstract Node apply_sequential(Symbol op);
	
	/**
	 * 
	 * @return
	 */
	public abstract List<Symbol> getSeqActions();
}
