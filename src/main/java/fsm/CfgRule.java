/**
 * 
 */
package fsm;

import java.util.List;
import java.util.Objects;

import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;


/**
 * @author Maxence Grand
 *
 */
public class CfgRule {
	/**
	 * 
	 */
	private Method left;
	/**
	 * 
	 */
	private List<Symbol> right;
	/**
	 * Constructs 
	 * @param left
	 * @param right
	 */
	public CfgRule(Method left, List<Symbol> right) {
		this.left = left;
		this.right = right;
	}
	/**
	 * Getter of left
	 * @return the left
	 */
	public Method getLeft() {
		return left;
	}
	/**
	 * Setter left
	 * @param left the left to set
	 */
	public void setLeft(Method left) {
		this.left = left;
	}
	/**
	 * Getter of right
	 * @return the right
	 */
	public List<Symbol> getRight() {
		return right;
	}
	/**
	 * Setter right
	 * @param right the right to set
	 */
	public void setRight(List<Symbol> right) {
		this.right = right;
	}
	
	/**
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		result = prime * result + Objects.hash(left, right);
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CfgRule))
			return false;
		CfgRule other = (CfgRule) obj;
		if(!this.getLeft().equals(other.getLeft())) {
			return false;
		}
		if(this.getRight().size() != other.getRight().size()) {
			return false;
		}
		for(int i = 0; i < this.getRight().size(); i++) {
			if(!this.getRight().get(i).equals(other.getRight().get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		return this.getLeft().getToDecompose()+" -> "+this.getRight();
	}
}
