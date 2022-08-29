/**
 * 
 */
package simulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import exception.PlanException;
import fsm.Example;
import fsm.Symbol;
import fsm.Trace;
import learning.CompressedNegativeExample;
import learning.Observation;
import learning.TypeHierarchy;

/**
 * @author Maxence Grand
 *
 */
public abstract class Simulator {
	
	/**
	 * 
	 */
	private Observation initialState;
	/**
	 * 
	 */
	private Observation currentState;
	/**
	 * 
	 */
	private List<String> types;
	/**
	 * 
	 */
	private List<Symbol> actions;
	/**
	 * 
	 */
	private List<Symbol> predicates;
	/**
	 * 
	 */
	private List<Symbol> positiveStaticPredicates;
	/**
	 * 
	 */
	private Map<String, String> paramTypes;
	/**
	 * 
	 */
	private TypeHierarchy hierarchy;

	/**
	 * 
	 * Constructs
	 */
	public Simulator() {
		this.initialState = new Observation();
		this.currentState = new Observation();
		this.types = new LinkedList<>();
		this.actions = new LinkedList<>();
		this.predicates = new LinkedList<>();
		this.positiveStaticPredicates = new LinkedList<>();
		this.paramTypes = new HashMap<>();
		this.hierarchy = new TypeHierarchy();
	}
		
	/**
	 * Getter of initialState
	 * @return the initialState
	 */
	public Observation getInitialState() {
		return initialState;
	}

	/**
	 * Setter initialState
	 * @param initialState the initialState to set
	 */
	public void setInitialState(Observation initialState) {
		this.initialState = initialState;
	}

	/**
	 * Getter of currentState
	 * @return the currentState
	 */
	public Observation getCurrentState() {
		return currentState;
	}

	/**
	 * Setter currentState
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(Observation currentState) {
		this.currentState = currentState;
	}

	/**
	 * Getter of types
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}

	/**
	 * Setter types
	 * @param types the types to set
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}

	/**
	 * Getter of actions
	 * @return the actions
	 */
	public List<Symbol> getActions() {
		return actions;
	}

	/**
	 * Setter actions
	 * @param actions the actions to set
	 */
	public void setActions(List<Symbol> actions) {
		//this.actions = actions;
		for(Symbol a : actions) {
			this.actions.add(a);
		}
	}

	/**
	 * Getter of predicates
	 * @return the predicates
	 */
	public List<Symbol> getPredicates() {
		return predicates;
	}

	/**
	 * Setter predicates
	 * @param predicates the predicates to set
	 */
	public void setPredicates(List<Symbol> predicates) {
		for(Symbol p : predicates) {
			this.predicates.add(p);
		}
	}

	/**
	 * Getter of positiveStaticPredicates
	 * @return the positiveStaticPredicates
	 */
	public List<Symbol> getPositiveStaticPredicates() {
		return positiveStaticPredicates;
	}

	/**
	 * Setter positiveStaticPredicates
	 * @param positiveStaticPredicates the positiveStaticPredicates to set
	 */
	public void setPositiveStaticPredicates(List<Symbol> positiveStaticPredicates) {
		for(Symbol p : positiveStaticPredicates) {
			this.positiveStaticPredicates.add(p);
		}
	}

	/**
	 * Getter of paramTypes
	 * @return the paramTypes
	 */
	public Map<String, String> getParamTypes() {
		return paramTypes;
	}



	/**
	 * Setter paramTypes
	 * @param paramTypes the paramTypes to set
	 */
	public void setParamTypes(Map<String, String> paramTypes) {
		this.paramTypes = paramTypes;
	}



	/**
	 * Getter of hierarchy
	 * @return the hierarchy
	 */
	public TypeHierarchy getHierarchy() {
		return hierarchy;
	}



	/**
	 * Setter hierarchy
	 * @param hierarchy the hierarchy to set
	 */
	public void setHierarchy(TypeHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
    
    /**
     * Check if an example is feasible starting by the initial state
     * @param example to test
     * @return True if the example is feasible
     */
    public boolean accept(Trace example) {
    	this.reInit();
    	for(Symbol a : example.getActionSequences()) {
    		if(!this.accept(a)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Check if an action is feasible in the current state
     * @param example action to test
     * @return True if the action is feasible
     */
    public boolean accept(Symbol example) {
    	if(this.testAction(example)) {
    		try {
				this.apply(example);
			} catch (PlanException e) {
				return false;
			}
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * 
     * @param compressed
     * @return
     */
    public float fitnessNegative(CompressedNegativeExample compressed) {
    	float res = 0;
    	this.reInit();
    	for(int i = 0; i< compressed.getPrefix().size(); i++) {
    		//Test negative
    		for(Example n : compressed.getNegativeIndex(i)) {
    			for(Symbol act : n.getActionSequences()) {
    				if(! this.testAction(act)) {
    	    			res++;
    	    		}
    			}
    		}
    		//Test Positive
    		if(i < compressed.getPrefix().size()) {
    			if(this.testAction(compressed.getPrefix().get(i))) {
    				try {
    					this.apply(compressed.getPrefix().get(i));
    				} catch(Exception e) {
    					break;
    				}
    			} else {
    				break;
    			}
    		}
    	}
    	return res;
    }
    
    /**
     * Test action's feasability
     * @param op an action
     * @throws PlanException
     */
    public abstract boolean testAction(Symbol action);
    
    /**
     * 
     * @param op
     * @return
     */
    public abstract void apply(Symbol op) throws PlanException ;
    
    /**
     * Reinitialize current state
     */
    public abstract void reInit();
}
