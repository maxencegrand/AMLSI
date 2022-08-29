/**
 * 
 */
package simulator;

/**
 * @author Maxence Grand
 *
 */
public class Pddl4jUtils {

	/**
	 * Get the name of an read predicate
	 * @param predicate a predicate
	 * @return predicate's name
	 */
	public static String getPredicateName(String predicate){
		String tmp = predicate.substring(1);
		String name = tmp.substring(0,tmp.length()-1).split(" ")[0];
		return name;
	}
}
