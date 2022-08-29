/**
 * 
 */
package fsm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxence Grand
 *
 */
public class Example extends Trace{
	
	/**
	 * 
	 */
	protected List<Symbol> actionSequences;

	/**
	 * 
	 * Constructs
	 */
	public Example() {
		this.actionSequences = new ArrayList<>();
	}
	
	/**
	 * Constructs 
	 * @param actionSequences
	 */
	public Example(List<Symbol> actionSequences) {
		this();
		this.actionSequences = actionSequences;
	}

	/**
	 * Getter of actionSequences
	 * @return the actionSequences
	 */
	public List<Symbol> getActionSequences() {
		return actionSequences;
	}

	/**
	 * Setter actionSequences
	 * @param actionSequences the actionSequences to set
	 */
	public void setActionSequences(List<Symbol> actionSequences) {
		this.actionSequences = actionSequences;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#get(int)
	 */
	public Symbol get(int arg0) {
		return actionSequences.get(arg0);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return actionSequences.size();
	}
	
	/**
     * Check if two examples are equals
     * @param other the example to compare
     * @return True of the two examples are equals
     */
	public boolean equals(Object other) {
		if(other instanceof Example) {
			Example o = (Example) other;
			if(this.size() == o.size()) {
				for(int i = 0; i<this.size(); i++) {
					if(! this.get(i).equals(o.get(i))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.actionSequences.hashCode();
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Symbol e) {
		return actionSequences.add(e);
	}
	
	/**
	 * 
	 * @param pref
	 * @return
	 */
	public Example getPrefix(int pref) {
		List<Symbol> sym = new ArrayList<>();
		for(int i = 0; i < pref && i < this.size(); i++) {
			sym.add(this.get(i));
		}
		return new Example(sym);
	}
	
	/**
	 * 
	 * @param suff
	 * @return
	 */
	public Example getSuffix(int suf) {
		List<Symbol> sym = new ArrayList<>();
		for(int i = suf; i < this.size(); i++) {
			sym.add(this.get(i));
		}
		return new Example(sym);
	}

}
