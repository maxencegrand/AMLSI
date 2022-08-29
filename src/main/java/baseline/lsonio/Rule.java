package baseline.lsonio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class implements a rule. A rule, is a weighted effect and a precondition.
 * @author Maxence Grand
 *
 */
public class Rule implements Comparable<Rule>{
	/**
	 * The vector representing the preconodition
	 */
	private List<Double> pre;
	/**
	 * The bit of the effect
	 */
	private int e;
	/**
	 * The weight of the rule
	 */
	private double w;

    /**
     * Constructs
     */
	public Rule() {
		this.pre = new ArrayList<>();
		this.e = 0;
		this.w = 0.0;
	}

	/**
	 * Constructs
	 * @param pre The precondition
	 * @param e The effect
	 * @param w The weight
	 */
	public Rule(List<Double> pre, int e, double w) {
		this.pre = pre;
		this.e = e;
		this.w = w;
	}
	/**
	 * Getter of pre
	 * @return the pre
	 */
	public List<Double> getPre() {
		return pre;
	}
	/**
	 * Setter pre
	 * @param pre the pre to set
	 */
	public void setPre(List<Double> pre) {
		this.pre = pre;
	}
	/**
	 * Getter of e
	 * @return the e
	 */
	public int getE() {
		return e;
	}
	/**
	 * Setter e
	 * @param e the e to set
	 */
	public void setE(int e) {
		this.e = e;
	}
	/**
	 * Getter of w
	 * @return the w
	 */
	public double getW() {
		return w;
	}
	/**
	 * Setter w
	 * @param w the w to set
	 */
	public void setW(double w) {
		this.w = w;
	}

	/**
	 * The hashcode
     * @return The hashcode
	 */
	@Override
	public int hashCode() {
		return Objects.hash(e, pre, w);
	}

	/**
	 * Equality test
     * @param obj Object to test
     * @return True if this and obj are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Rule))
			return false;
		Rule other = (Rule) obj;
		return e == other.e && Rule.equalVectors(pre, other.pre)
				&& Double.doubleToLongBits(w) == Double.doubleToLongBits(other.w);
	}

	/**
    * Add a precondition
	 * @param e The new preconsition
	 * @return True if the precondition vector has been modified
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Double e) {
		return pre.add(e);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return pre.contains(o);
	}

	/**
	 * Test equelity between two vectors
	 * @param x A vector
	 * @param y A vector
	 * @return True if x == y
	 */
	private static boolean equalVectors(List<Double> x, List<Double> y) {
		for(int i = 0; i < x.size(); i++) {
			if(x.get(i) == null && y.get(i) != null) {
				return false;
			} else if(x.get(i) != null && y.get(i) == null) {
				return false;
			} else if(x.get(i) == null && y.get(i) == null) {
				continue;
			} else if((double)x.get(i) != (double)y.get(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The String representation of a rule
     * @return A string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rule [pre=");
		builder.append(pre);
		builder.append(", e=");
		builder.append(e);
		builder.append(", w=");
		builder.append(w);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * COmpare two rules
	 * @param arg0 A rule
	 * @return 1 if this.weight is inferior to arg0.weight
	 */
	@Override
	public int compareTo(Rule arg0) {
		return this.getW() < arg0.getW() ? 1 :
			this.getW() > arg0.getW() ? -1 : 0;
	}

	/**
	 * Remove a precondition
	 * @param idx Precondition index
	 */
	public void remove(int idx) {
		this.pre.set(idx, null);
	}
}
