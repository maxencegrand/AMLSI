/**
 * 
 */
package learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsm.Example;
import fsm.Sample;
import fsm.Symbol;
import fsm.Trace;

/**
 * @author Maxence Grand
 *
 */
public class CompressedNegativeExample {
	/**
	 * The positive prefix
	 */
	protected Example prefix;
	/**
	 * All negative suffixes
	 */
	protected Map<Integer, List<Example>> negatives;
	
	public CompressedNegativeExample() {
		this.negatives = new HashMap<>();
	}
	/**
	 * Constructs 
	 * @param prefix
	 * @param negatives
	 */
	public CompressedNegativeExample(Trace prefix, List<Trace> negatives) {
		this();
		this.prefix = new Example(prefix.getActionSequences());
		for(int i = 0; i <= prefix.size(); i++) {
			this.negatives.put(i, new ArrayList<>());
		}
		negatives.forEach( n -> {
			Example neg = (Example) n;
			int suff = CompressedNegativeExample.sizeCommonPrefix(prefix, neg);
			if(! this.negatives.containsKey(suff)) {
				this.negatives.put(suff, new ArrayList<>());
			}
			this.negatives.get(suff).add(neg.getSuffix(suff));
		});
	}

	/**
	 * Getter of prefix
	 * @return the prefix
	 */
	public List<Symbol> getPrefix() {
		return prefix.getActionSequences();
	}

	/**
	 * Setter prefix
	 * @param prefix the prefix to set
	 */
	public void setPrefix(List<Symbol> prefix) {
		this.prefix = new Example(prefix);
	}

	/**
	 * Setter negatives
	 * @param negatives the negatives to set
	 */
	public void setNegatives(Map<Integer, List<Example>> negatives) {
		this.negatives = negatives;
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
	 * All negative examples from a given index
	 * @param i The index
	 * @return Negative Examples
	 */
	public List<Example> getNegativeIndex(int i) {
		if(!this.negatives.containsKey(i)) {
			return new ArrayList<>();
		}
		return this.negatives.get(i);
	}

	/**
	 * Getter of negatives
	 * @return the negatives
	 */
	public Map<Integer, List<Example>> getNegatives() {
		return negatives;
	}

	/**
	 * Setter prefix
	 * @param prefix the prefix to set
	 */
	public void setPrefix(Example prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * 
	 * @param pos
	 * @param neg
	 * @return
	 */
	public static List<CompressedNegativeExample> getExamplesLast(
			Sample pos, 
			Sample neg) {
		List<CompressedNegativeExample> res = new ArrayList<>();
		pos.getExamples().forEach(ex -> {
			List<Trace> n = new ArrayList<>();
			neg.getExamples().forEach(t -> {
				n.add(t);
			});
			res.add(onlyLast(ex,n));
		});
		return res;
	}
	
	public String toString() {
		String res = "";
		res += this.prefix+"\n";
		for(int i = 0; i<=this.prefix.size(); i++) {
			if(!this.negatives.containsKey(i)) {
				continue;
			}
			res += "Index : "+i;
			for(Trace t : this.negatives.get(i)) {
				res += "\n\t"+t;
			}
			res += "\n";
		}
		return res;
	}
	
	public static CompressedNegativeExample onlyLast(Trace prefix, List<Trace> negatives) {
		CompressedNegativeExample res = new CompressedNegativeExample(prefix, negatives);
		Map<Integer, List<Example>> newNeg = new HashMap<>();
//		System.out.println(res.negatives);
		for(int i = 0; i<=res.prefix.size(); i++) {
			newNeg.put(i, new ArrayList<>());
//			System.out.println(i);
			for(Example t : res.negatives.get(i)) {
				if(t.getActionSequences().size() == 1) {
					newNeg.get(i).add(t);
				}
			}
		}
		res.negatives = newNeg;
		return res;
	}
}
