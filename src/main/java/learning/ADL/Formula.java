/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fsm.Symbol;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public abstract class Formula {

	/**
	 * 
	 */
	public abstract String toString();
	
	/**
	 * 
	 * @param act
	 * @return
	 */
	public abstract Formula checkLinkedVar(Symbol act);
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public abstract boolean equivalence(Formula other);
	
	/**
	 * 
	 * @param mapping
	 * @return
	 */
	public abstract Formula generalize(Map<String, String> mapping);
	
	/**
	 * 
	 * @return
	 */
	public abstract Map<String, String> getVar();
	
	/**
	 * 
	 * @param pred
	 * @param others
	 * @return
	 */
	protected static List<Symbol> dependent(Symbol pred, List<Symbol> others) {
		List<Symbol> res = new ArrayList<>();
		List<String> params = new ArrayList<>();
		res.add(pred);
		others.forEach(p -> {
			if(Formula.containsOneOf(
					pred.getListParameters(), 
					p.getListParameters())) {
				res.add(p);
			}
		});
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	private static boolean containsOneOf(List<String> l1, List<String> l2) {
		boolean b = false;
		for(String str : l2) {
			b |= l1.contains(str);
		}
		return b;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isCoherent(Formula F) {
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public Formula merge(Formula F) {
		Conjonction c = new Conjonction();
		c.add(this);
		c.add(F);
		return c;
	}
	
	/**
	 * 
	 * @return
	 */
	public Formula lift(Symbol act) {
		return this;
	}
	
	/**
	 * 
	 */
	public abstract boolean equals(Object o);
	
	public boolean produce(Formula F) {return false;} ;
	
	public boolean equivalent(Formula F) {
		return this.produce(F) && F.produce(this); 
	}
	
	public Formula simplify() {
		return this;
	}
	
	public abstract boolean check(Observation o, Symbol a);
	
    public static List<Map<String, String>> allInstances (Map<String, String> p1, Map<String, String> p) {
    	List<Map<String, String>> res = new ArrayList<>();
    	List<List<String>> allParams = new ArrayList<>();
    	List<String> first = new ArrayList<>(p1.keySet());
    	p1.forEach((k,v) -> {
    		List<String> tmp = new ArrayList<>();
    		p.forEach((kk,vv) -> {
    			if(v.equals(vv)) {
    				tmp.add(kk);
    			}
    		});
    		allParams.add(tmp);
    	});
//    	System.out.println("*"+allParams);
    	int[] idxs = new int[allParams.size()];
    	for(int i = 0; i < idxs.length; i++) {
    		idxs[i]=0;
    	}
    	
    	for(List<String> tmp : allParams) {
    		if(tmp.isEmpty()) {
    			return new ArrayList<>();
    		}
    	}
    	while(idxs[0] < allParams.get(0).size()) {
    		Map<String, String> newParams = new LinkedHashMap<>();
    		boolean b = true;
    		for(int i =0; i < idxs.length && b; i++) {
    			if(newParams.containsKey(allParams.get(i).get(idxs[i]))) {
    				b = false;
    			} else {
    				String str = allParams.get(i).get(idxs[i]);
    				newParams.put(first.get(i), str);
    			}
    		}
    		if(b) {
    			res.add(newParams);
    		}
    		for(int i = idxs.length-1; i >=0; i--) {
    			idxs[i]++;
    			if(idxs[i] >= allParams.get(i).size() && i>0) {
    				idxs[i] = 0;
    			} else {
    				break;
    			}
    		}
    	}
    	return res;
    }
    
    public abstract boolean only(List<String> p);
}
