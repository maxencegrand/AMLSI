/**
 * 
 */
package fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxence Grand
 *
 */
public class DecompositionTrace extends Trace {
	/**
	 * 
	 */
	private List<Symbol> actions;
	/**
	 * 
	 */
	Map<Integer, Map<Symbol, Trace>> nonPrimitiveDecomposition;
	
	/**
	 * 
	 * Constructs
	 */
	public DecompositionTrace() {
		this.actions = new ArrayList<>();
		this.nonPrimitiveDecomposition = new HashMap<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param t
	 */
	public DecompositionTrace(RandomTaskTrace t) {
		this();
		t.getTasks().getActionSequences().forEach(a -> this.actions.add(a));
		for(int i = 0; i < t.getBeginIdx().size(); i++) {
			int idxB = t.getBeginIdx().get(i), idxE = t.getEndIdx().get(i);
			List<Symbol> tmp = new ArrayList<>();
			for(int j = idxB; j <= idxE; j++) {
				tmp.add(t.getActionSequences().get(j));
			}
			this.addDecomposition(i, this.actions.get(i), new Example(tmp));
//			System.out.println(this.NonPrimitiveDecomposition.keySet());
		}
	}
	
	/**
	 * 
	 * @param idx
	 * @param t
	 */
	public void addDecomposition(int idx, Symbol op, Trace t) {
		if(!this.nonPrimitiveDecomposition.containsKey(idx)) {
			this.nonPrimitiveDecomposition.put(idx, new HashMap<>());
		} 
		this.nonPrimitiveDecomposition.get(idx).put(op, t);
		
	}
	
	/**
	 * 
	 * @param idx
	 * @return
	 */
	public Trace getDecomposition(int idx, Symbol op) {
		return this.nonPrimitiveDecomposition.get(idx).get(op);
	}
	
	/**
	 * 
	 */
	@Override
	public List<Symbol> getActionSequences() {
		// TODO Auto-generated method stub
		return actions;
	}

	/**
	 * 
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return actions.size();
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < this.size(); i++) {
			Symbol a = this.getActionSequences().get(i);
			if(a instanceof Task) {
				str.append(a)
					.append(" -> ")
					.append(this.getDecomposition(i, a))
					.append("\n");
			} else {
				str.append(a)
					.append("\n");
			}
		}
		return str.toString();
	}

	/**
	 * 
	 */
	public Map<String, String> contants() {
		Map<String, String> res = new HashMap<>();
		this.getActionSequences().forEach(action -> {
			action.getParameters().forEach((k,v) -> {
				res.put(k, v);
			});
		});
		this.nonPrimitiveDecomposition.forEach((k,v) -> {
			v.forEach((vv,tr) -> {
				tr.getActionSequences().forEach(action -> {
					action.getParameters().forEach((p,t) -> {
						res.put(p, t);
					});
				});
			});
		});
		return res;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public Trace prefix(int i) {
		List<Symbol> acts = new ArrayList<>();
		for(int j = 0; j < i; j++) {
			Symbol op = this.getActionSequences().get(j);
			if(op instanceof Task) {
				acts.addAll(this.getDecomposition(j, op).getActionSequences());
			} else {
				acts.add(op);
			}
		}
		return new Example(acts);
	}
}
