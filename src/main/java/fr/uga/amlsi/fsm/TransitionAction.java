/**
 * 
 */
package fr.uga.amlsi.fsm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.Action;
import fr.uga.generator.symbols.Symbol;

/**
 * @author Maxence Grand
 *
 */
public class TransitionAction extends Action{
	/**
	 * 
	 */
	private Symbol action;
	/**
	 * 
	 */
	private int state;
	
	/**
	 * 
	 * Constructs 
	 * @param a
	 * @param q
	 * @param parameters
	 * @param parametersAbs
	 */
	public TransitionAction(
			Symbol a, 
			int q, 
			Map<String, String> parameters,
			Map<String, String> parametersAbs) {
		super(a.getName()+"_"+q, parameters, parametersAbs);
		this.action = a;
		this.state = q;
	}

	/**
	 * Getter of action
	 * @return the action
	 */
	public Symbol getAction() {
		return action;
	}

	/**
	 * Setter action
	 * @param action the action to set
	 */
	public void setAction(Symbol action) {
		this.action = action;
	}

	/**
	 * Getter of state
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * Setter state
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> linked() {
		List<String> res = new ArrayList<>();
//		System.out.println("****"+this.action+" "+this.getListParameters()+" "+this.action.getListParameters());
		for(String p : this.getListParameters()) {
			if(this.getAction().getListParameters().contains(p)) {
				res.add(p);
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> free() {
		List<String> res = new ArrayList<>();
		for(String p : this.getListParameters()) {
			if(!this.getAction().getListParameters().contains(p)) {
				res.add(p);
			}
		}
		return res;
	}
	
	/**
	 * Generalize an Action
	 * 
	 * @return generalized action
	 */
	public Symbol generalize() {
    	Map<String, String> mapping = mapping();
    	Map<String, String> newParam = new LinkedHashMap<>();
    	Map<String, String> newAbsParam = new LinkedHashMap<>();
    	for(Map.Entry<String, String> entry : parameters.entrySet()) {
    	    newParam.put(mapping.get(entry.getKey()), this.abstractParameters.get(entry.getKey()));
    	    newAbsParam.put(mapping.get(entry.getKey()), this.abstractParameters.get(entry.getKey()));
        }
        return new TransitionAction(this.action.generalize(this.mapping()), this.state, newParam, newAbsParam);
    }
	
	/**
	 * Map action's parameters with new parameters
	 *
	 * @param mapping Parameters' mapping
	 * @return The new action
	 */
	@Override
	public Symbol generalize(Map<String, String> mapping) {
		Map<String, String> newParam = new LinkedHashMap<>();
		Map<String, String> newAbsParam = new LinkedHashMap<>();
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			newParam.put(mapping.get(entry.getKey()), entry.getValue());
			newAbsParam.put(mapping.get(entry.getKey()), entry.getValue());
		}
		return new TransitionAction(this.action.generalize(mapping), this.state, newParam,newAbsParam);
	}
	
	/**
     * The abstract generalization ie the generalization where each parameters
     * is replaced by the corresponding abstract parameters ?xi
     *
     * @return A symbol
     */
	@Override
	public Symbol clone() {
		return new TransitionAction(this.action, this.state, this.parameters,this.abstractParameters);
	}

	/**
     * Return an abstract mapping ie map each parameters with the abstract
     * parameter ?xi
     * 
     * @return Parameter's mapping
     */
    public Map<String, String> mapping() {
        Map<String, String> res = new LinkedHashMap<>();
        int i = 1, j = 1;
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
        	if(this.free().contains(entry.getKey())) {
        		res.put(entry.getKey(), "?x"+i);
        		i++;
        	} else {
        		res.put(entry.getKey(), "?x"+i);
        		i++;
        	}
            
        }
        return res;
    }
}
