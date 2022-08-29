/**
 * 
 */
package fsm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thiçs class represent a Task
 * @author Maxence Grand
 *
 */
public class Task extends Action {

	/**
	 * 
	 * Constructs 
	 * @param name
	 * @param params
	 */
	public Task(String name, Map<String, String> params) {
		super(name, params);
	}

	/**
	 * Constructs
	 * @param name Action name
	 * @param parameters Action parameters
	 * @param parametersAbs Action abstract parameters
	 */
	public Task(
			String name, 
			Map<String, String> parameters,
			Map<String, String> parametersAbs){
		super(name,parameters, parametersAbs);
	}

	/**
	 * 
	 * Constructs
	 */
	public Task() {
		super();
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
			newParam.put(
					mapping.get(entry.getKey()), 
					this.abstractParameters.get(entry.getKey()));
			newAbsParam.put(
					mapping.get(entry.getKey()), 
					this.abstractParameters.get(entry.getKey()));
			if(this.abstractParameters.get(entry.getKey()) == null) {
				throw new IllegalArgumentException();
			}
		}
		return new Task(this.name, newParam, newAbsParam);
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
			newAbsParam.put(
					mapping.get(entry.getKey()), 
					this.getAbstractParameters().get(entry.getKey()));
		}
		return new Task(name, newParam,newAbsParam);
	}

	/**
	 *
	 * Equality test
	 *
	 * @return true if this is equal to other, false otherwise
	 */
	@Override
	public boolean equals(Object other){
		if(other instanceof Task){
			Task o = (Task) other;
			boolean b = o.name.equals(name);
			if(o.parameters.size() != parameters.size()) {
				return false;
			}
			for(int i = 0; i < parameters.size(); i++) {
				b &= getListParameters().get(i).equals
						(o.getListParameters().get(i));
			}
			return b;
		}
		return false;
	}
	
	/**
     * String representation of the predicate
     * Contains parameters' type
     *
     * @return String representation of the symbol
     */
    @Override
    public String toStringType() {
        String res = "("+this.name;

        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            res += " "+entry.getKey()+" - "+entry.getValue();
        }

        res += ")";
        return res;
    }

    /**
     * String representation of the predicate
     *
     * @return String representation of the symbol
     */
    @Override
    public String toString() {
        String res = "("+this.name;

        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            res += " "+entry.getKey();
        }

        res += ")";
        return res;
    }
    
    /**
     * The abstract generalization ie the generalization where each parameters
     * is replaced by the corresponding abstract parameters ?xi
     *
     * @return A symbol
     * @see fsm.Symbol#mapping()
     */
	@Override
	public Symbol clone() {
		return new Task(this.name, this.parameters,this.abstractParameters);
	}
}
