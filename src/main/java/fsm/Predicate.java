package fsm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represent a predicate
 * @author Maxence Grand
 */
public class Predicate extends Symbol {

    /**
     * Constructs the predicate
     */
    public Predicate() {
        super();
    }

    /**
     * Constructs the predicate with a given name
     *
     * @param name The name of the predicate
     */
    public Predicate (String name){
        super(name);
    }

    /**
     * Constructs the predicate with a given name and given parameters
     * @param name The predicate's name
     * @param parameters The predicate's parameters with types
     */
    public Predicate(String name, Map<String, String> parameters){
        super(name,parameters);
    }

    /**
	 *
	 * @param name
	 * @param parameters
	 * @param parametersAbs
	 */
	public Predicate(String name, Map<String, String> parameters,
                                    Map<String, String> parametersAbs){
		super(name,parameters);
		this.setAbstractParameters(parametersAbs);
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
     * Generalize the predicate
     * @param mapping parameter's mapping
     * @return A predicate
     */
    @Override
    public Symbol generalize(Map<String, String> mapping) {
        Map<String, String> newParam = new LinkedHashMap<>();
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            newParam.put(mapping.get(entry.getKey()), entry.getValue());
        }
        return new Predicate(name, newParam,this.abstractParameters);
    }

    /**
     * The abstract generalization ie the generalization where each parameters
     * is replaced by the corresponding abstract parameters ?xi
     *
     * @return A symbol
     * @see fsm.Symbol#mapping()
     */
	public Symbol generalize() {
    	Map<String, String> mapping = mapping();
    	Map<String, String> newParam = new LinkedHashMap<>();
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            newParam.put(mapping.get(entry.getKey()), this.abstractParameters.get(entry.getKey()));
        }
        return new Predicate(this.name, newParam,this.abstractParameters);
    }

    /**
	 * Clone the action
	 * @return an Action
	 */
    @Override
    public Symbol clone() {
	return new Predicate(this.name, this.parameters, this.abstractParameters);
    }
}
