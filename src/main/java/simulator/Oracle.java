package simulator;

import java.util.List;
import java.util.Map;
import fsm.Symbol;
import learning.TypeHierarchy;
import exception.PlanException;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.operator.Action;
/**
 * This class represents an oracle
 * @author Maxence Grand
 *
 */
public abstract class Oracle {
    /**
     * Test action's feasability
     * @param op an action
     * @throws PlanException
     */
    public abstract void testAction(Action op) throws PlanException;

    /**
     * Apply the selected action
     * @return the resulting state
     * @throws PlanException 
     */
    public abstract Node apply() throws PlanException;
    
    /**
     * Apply an action in the current state
     * @param op The action
     * @return An internal state
     */
    public abstract Node apply(Symbol op);
    
    /**
     * Apply an action in the current state
     * @param op The action
     * @return An internal state
     */
    public abstract Node apply(fr.uga.pddl4j.problem.operator.Action op);
    
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
     * Test if an action is feasible in the current state
     * @param op an action
     * @return True the action is feasible
     */
    public abstract boolean isApplicable(Symbol op);
    /**
     * Get all predicate present in the state
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
     * @return True if the example is feasible
     */
    public abstract boolean accept(List<Symbol> example);
    /**
     * Check if an action is feasible in the current state
     * @param example action to test
     * @return True if the action is feasible
     */
    public abstract boolean accept(Symbol example);
    /**
     * Check if the selected action is feasible
     * @return true if the action is feasible
     */
    public abstract boolean availableActions();
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
}
