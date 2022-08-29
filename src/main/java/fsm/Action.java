package fsm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import learning.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent an Action
 * @author Maxence Grand
 */
public class Action extends Symbol {

	/**
	 * Constructs an Action
	 */
	public Action() {
		super();
	}

	/**
	 * Constructs an Action
	 *
	 * @param name the actions's name
	 */
	public Action (String name){
		super(name);
	}

	/**
	 * Constructs an Action
	 *
	 * @param name the action's name
	 * @param parameters all parameters with types
	 */
	public Action(String name, Map<String, String> parameters){
		super(name,parameters);
	}

	/**
	 * Constructs
	 * @param name Action name
	 * @param parameters Action parameters
	 * @param parametersAbs Action abstract parameters
	 */
	public Action(String name, Map<String, String> parameters,Map<String, String> parametersAbs){
		super(name,parameters);
		this.setAbstractParameters(parametersAbs);
	}

	/**
	 * String representation of the symbol
	 *
	 * @return String representation of the symbol
	 */
	@Override
	public String toStringType() {
		String res = this.name+"(";

		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			res += " "+entry.getKey()+" - "+entry.getValue();
		}

		res += ")";
		return res;
	}

	/**
	 * String representation of the symbol
	 *
	 * @return String representation of the symbol
	 */
	@Override
	public String toString() {
		String res = this.name+"(";

		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			res += " "+entry.getKey();
		}

		res += ")";
		return res;
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
		return new Action(name, newParam,newAbsParam);
	}

	/**
	 * Get all predicates' permutations compatible with the actions
	 *
	 * @param P A list of predicates
	 * @return All compatible predicates
	 */
	public List<Symbol> getAllPredicates(List<Symbol> P, TypeHierarchy t) {
		List<Symbol> res = new ArrayList<>();
		for(Symbol p : P){
			if(this.compatibleType(p,t)) {
				//All permutations
				List<String> param = p.getListType();
				if(param.size() == 0) {//proposition without param like (handempty)
					res.add(p);
					break;
				}

				//Compute permute : map each index of predicate with all
				//compatible parameters of the action
				Map<Integer, List<String>> permut = new HashMap<>();
				List<Integer> sizes = new ArrayList<>();
				List<Integer> idxs = new ArrayList<>();
				for(int i=0; i<param.size(); i++) {
					permut.put(i, new ArrayList<>());
					for(Map.Entry<String, String> entry : parameters.entrySet()) {
						if(t.descendants(param.get(i)).contains(entry.getValue())) {
							permut.get(i).add(entry.getKey());
						}
					}
					idxs.add(0);
					sizes.add(permut.get(i).size());
				}

				//Compute allPermut : list of all parameters permuations possible
				List<List<String>> allPermut = new ArrayList<>();
				int i = param.size()-1;
				while(idxs.get(0) < sizes.get(0)){
					List<String> tmp = new ArrayList<>();
					for(int k = 0; k < permut.size(); k++) {
						tmp.add(permut.get(k).get(idxs.get(k)));
					}
					allPermut.add(tmp);
					boolean b = true;
					while(b) {
						idxs.set(i, idxs.get(i)+1);
						if(idxs.get(i) >= sizes.get(i) && i > 0){
							idxs.set(i,0);
							i--;
						}else{
							b = false;
							i = param.size()-1;
						}
					}
				}

				for(List<String> permutation : allPermut) {
					if(! allDifferent(permutation)) {
						continue;
					}
					Map<String, String> tmp = new HashMap<>();
					for(int k =0; k<param.size(); k++){
						tmp.put(p.getListParameters().get(k),permutation.get(k));
					}
					res.add(p.generalize(tmp));
				}
			}
		}
		return res;
	}

	/**
	 * Check of all element of a string list are different
	 * @param l a list of string
	 * @return True if all elements are different
	 */
	private boolean allDifferent(List<String> l) {
		boolean b = true;
		List<String> tabou = new ArrayList<>();
		for(String str : l) {
			b &= (! tabou.contains(str));
			tabou.add(str);
		}

		return b;
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
		return new Action(this.name, this.parameters,this.abstractParameters);
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
        return new Action(this.name, newParam, newAbsParam);
    }
}
