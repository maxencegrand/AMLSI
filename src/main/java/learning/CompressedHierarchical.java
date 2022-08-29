/**
 * 
 */
package learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsm.Example;
import fsm.Method;
import fsm.RandomTaskTrace;
import fsm.Sample;
import fsm.Symbol;
import fsm.Trace;

/**
 * @author Maxence Grand
 *
 */
public class CompressedHierarchical extends CompressedNegativeExample{
	/**
	 * All negative suffixes
	 */
	private Map<Integer, Example> decomposition;
	
	/**
	 * 
	 * Constructs
	 */
	public CompressedHierarchical() {
		this.negatives = new HashMap<>();
		this.decomposition = new HashMap<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param rt
	 */
	public CompressedHierarchical(RandomTaskTrace rt) {
		this();
		this.prefix = rt.getTasks();
		for(int i = 0; i < this.prefix.size(); i++) {
			List<Symbol> l = new ArrayList<>();
			for(int j = rt.getBeginIdx().get(i); 
					j <= rt.getEndIdx().get(i); j++) {
				l.add(rt.getActionSequences().get(j));
			}
			this.decomposition.put(i, new Example(l));
		}
	}
	
	/**
	 * 
	 * Constructs 
	 * @param rt
	 * @param m
	 */
	public CompressedHierarchical(RandomTaskTrace rt, List<Symbol> m) {
		this(rt);
		for(int i = 0; i < this.prefix.size(); i++) {
			if(!this.negatives.containsKey(i)) {
				this.negatives.put(i, new ArrayList<>());
			}
			Symbol op = this.prefix.get(i);
			if(op instanceof Method) {
				Method m1 = (Method)op;
				for(Symbol op2 : m) {
					Method m2 = (Method)op2;
					if(m1.equals(m2)) {
						continue;
					}
					if(m1.getToDecompose().equals(m2.getToDecompose())) {
						List<Symbol> tmp = new ArrayList<>();
						tmp.add(m2);
						this.negatives.get(i).add(new Example(tmp));
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * Constructs 
	 * @param rt
	 * @param m
	 * @param neg
	 */
	public CompressedHierarchical(
			RandomTaskTrace rt, 
			List<Symbol> m, 
			Sample neg) {
		this(rt, m);
		for(Trace t : neg.getExamples()) {
			int s = sizeCommonPrefix(this.prefix, t);
			//System.out.println(t+" "+prefix+" "+s);
			if(s == t.size()-1) {
				List<Symbol> tmp = new ArrayList<>();
				tmp.add(t.getActionSequences().get(s));
				this.negatives.get(s).add(new Example(tmp));
			}
		}
	}
	
	/**
	 * The size of common prefix between two traces
	 * @param a A Trace
	 * @param b A Trace
	 * @return The size of the common prefix
	 */
	private static int sizeCommonPrefix(Trace a, Trace b) {
		int res = 0;
		for(int i = 0; i < a.size() && i < b.size(); i++) {
			if(a.getActionSequences().get(i).equals(b.getActionSequences().get(i))) {
				res ++;
			}
		}
		return res;
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.prefix.getActionSequences()).append("\n");
		str.append("Decomposition:").append("\n");
		for(int i = 0; i < this.prefix.size(); i++) {
			str.append("\t[")
				.append(i)
				.append("] ")
				.append(this.decomposition.get(i))
				.append("\n");
		}
		str.append("Negatives:").append("\n");
		for(int i = 0; i < this.prefix.size(); i++) {
			str.append("\t[")
				.append(i)
				.append("] ")
				.append(this.negatives.get(i))
				.append("\n");
		}
		return str.toString();
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public Example getDecomposition(int i) {
		return this.decomposition.get(i);
	}
}
