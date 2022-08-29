/**
 * 
 */
package fsm;

import java.util.Comparator;

/**
 * @author Maxence Grand
 *
 */
public class ComparatorCFG implements Comparator<CFG>{

	/**
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public int compare(CFG c1, CFG c2) {
		int s1 = c1.score();
		int s2 = c2.score();
		if(s1 < s2) {
			return 1;
		} else if(s1 > s2) {
			return -1;
		} else {
			return 0;
		}
	}
}
