/**
 * 
 */
package fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class represent a Trace
 * @author Maxence Grand
 *
 */
public abstract class Trace {
	
	/**
	 * @return Action sequence
	 */
	public abstract List<Symbol> getActionSequences();
	
	/**
	 * @return The size of the Trace
	 */
	public abstract int size();
	
	/**
     * Check if two traces are equals
     * @param other the traces to compare
     * @return True of the two traces are equals
     */
	public boolean equals(Trace t) {
		if(this.size() != t.size()) {
			return false;
		}
		if(this.size() == t.size()) {
			for(int i = 0; i<this.size(); i++) {
				if(! this.getActionSequences().get(i).equals(
						t.getActionSequences().get(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> contants() {
		Map<String, String> res = new HashMap<>();
		this.getActionSequences().forEach(action -> {
			action.getParameters().forEach((k,v) -> {
				res.put(k, v);
			});
		});
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<Symbol, List<Symbol>> getPair() {
		Map<Symbol, List<Symbol>> res = new HashMap<>();
		for(int i = 1; i < this.size(); i++) {
			Symbol a1 = this.getActionSequences().get(i-1);
			Symbol a2 = this.getActionSequences().get(i);
			if(!res.containsKey(a1)) {
				res.put(a1, new ArrayList<>());
			}
			if(!res.get(a1).contains(a2)) {
				res.get(a1).add(a2);
			}
		}
		return res;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder build = new StringBuilder();
		build.append("{");
		for(int i =0; i < this.size(); i++) {
			Symbol a = this.getActionSequences().get(i);
			if(a instanceof Method) {
				Method m = (Method)a;
				build.append(m.toStringShort()+(i<this.size()-1 ? " " : ""));
			} else {
				build.append(a+(i<this.size()-1 ? " " : ""));
			}
		}
		build.append("}");
		return build.toString();
	}
}
