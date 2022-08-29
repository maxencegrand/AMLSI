/**
 * 
 */
package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxence Grand
 *
 */
public class BiMap <X,Y> {
	/**
	 * Left Map
	 */
	private Map<X,Y> left;
	/**
	 * Right Map
	 */
	private Map<Y,X> right;
	
	/**
	 * 
	 * Constructs
	 */
	public BiMap () {
		this.right = new HashMap<>();
		this.left = new HashMap<>();
	}
	
	/**
	 * Put in left map
	 * @param x the key
	 * @param y the value
	 */
	public void putLeft(X x, Y y) {
		this.left.put(x, y);
	}
	
	/**
	 * Put in right map
	 * @param x the key
	 * @param y the value
	 */
	public void putRight(Y y, X x) {
		this.right.put(y, x);
	}
	
	/**
	 * Put in all maps
	 * @param x A key/value
	 * @param y A key/value
	 */
	public void put(X x, Y y) {
		this.right.put(y, x);
		this.left.put(x, y);
	}
	
	/**
	 * Get from left map
	 * @param key The key
	 * @return The value
	 */
	public Y getLeft(X key) {
		return this.getLeft().get(key);
	}
	
	/**
	 * Get from left right
	 * @param key The key
	 * @return The value
	 */
	public X getRight(Y key) {
		return this.getRight().get(key);
	}
	
	/**
	 * Contains key left map
	 * @param key The key
	 * @return True if the key is contained
	 */
	public boolean containsKeyLeft(X x) {
		return this.left.containsKey(x);
	}
	
	/**
	 * Contains key right map
	 * @param key The key
	 * @return True if the key is contained
	 */
	public boolean containsKeyRight(Y y) {
		return this.right.containsKey(y);
	}

	/**
	 * Getter of left
	 * @return the left
	 */
	public Map<X, Y> getLeft() {
		return left;
	}

	/**
	 * Getter of right
	 * @return the right
	 */
	public Map<Y, X> getRight() {
		return right;
	}
	
	
}
