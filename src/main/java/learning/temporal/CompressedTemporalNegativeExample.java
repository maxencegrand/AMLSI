/**
 * 
 */
package learning.temporal;

import java.util.List;
import java.util.Map;

import fsm.Symbol;

/**
 * @author Maxence Grand
 *
 */
public class CompressedTemporalNegativeExample {
	/**
	 * All prefixes
	 */
	Map<Float, Symbol> prefixe;
	/**
	 * All negatives examples
	 */
	Map<Float, List<Symbol>> negatives;
	
	/**
	 * Constructs 
	 * @param prefixe
	 * @param negatives
	 */
	public CompressedTemporalNegativeExample(Map<Float, Symbol> prefixe, 
			Map<Float, List<Symbol>> negatives) {
		this.prefixe = prefixe;
		this.negatives = negatives;
	}

	/**
	 * Getter of prefixe
	 * @return the prefixe
	 */
	public Map<Float, Symbol> getPrefixe() {
		return prefixe;
	}

	/**
	 * Setter prefixe
	 * @param prefixe the prefixe to set
	 */
	public void setPrefixe(Map<Float, Symbol> prefixe) {
		this.prefixe = prefixe;
	}

	/**
	 * Getter of negatives
	 * @return the negatives
	 */
	public Map<Float, List<Symbol>> getNegatives() {
		return negatives;
	}

	/**
	 * Setter negatives
	 * @param negatives the negatives to set
	 */
	public void setNegatives(Map<Float, List<Symbol>> negatives) {
		this.negatives = negatives;
	}
	
	/**
	 * Find an example with the same prefixe
	 * @param examples Examples
	 * @return An example with the same preefix
	 */
	public CompressedTemporalNegativeExample hasSamePrefixe(
			List<CompressedTemporalNegativeExample> examples) {
		for(CompressedTemporalNegativeExample e : examples) {
			if(this.samePrefixe(e)) {
				return e;
			}
		}
		examples.add(this);
		return this;
	}
	
	/**
	 * Check if two example have the same prefix
	 * @param other An Example
	 * @return true if examples have the same prefix
	 */
	public boolean samePrefixe(CompressedTemporalNegativeExample other) {
		if(this.getPrefixe().size() != other.getPrefixe().size()) {
			return false;
		}
		for(float i : this.getPrefixe().keySet()) {
			if(! this.getPrefixe().get(i).equals(other.getPrefixe().get(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Merge two example
	 * @param other Example
	 * @return merged examples
	 */
	public void mergeNegative(CompressedTemporalNegativeExample other) {
		other.getNegatives().forEach((f, l) -> {
			if(this.getNegatives().containsKey(f)) {
				l.forEach(s -> {
					if(!this.getNegatives().get(f).contains(s)) {
						this.getNegatives().get(f).add(s);
					}
				});
			} else {
				this.getNegatives().put(f, l);
			}
		});
	}
	
	/**
	 * Merge two example with the same prefix
	 * @param other Examples
	 * @return merged examples
	 */
	public void mergeNegative(List<CompressedTemporalNegativeExample> examples) {
		this.hasSamePrefixe(examples).mergeNegative(this);
	}
	
	/**
	 * 
	 * @return
	 */
	/*public int scoreMax() {
		int score = 0;
		for(float f : this.negatives.keySet()) {
			score += this.negatives.get(f).size();
		}
		return score;
	}*/
}
