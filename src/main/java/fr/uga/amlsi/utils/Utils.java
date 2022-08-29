/**
 * 
 */
package fr.uga.amlsi.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxence Grand
 *
 */
public class Utils {
	/**
	 * Reverse Key Value of a Map<String String>
	 * @param param The map
	 * @return A reversed map
	 */
	public static Map<String, String> reverseParamMap(Map<String, String> param) {
		Map<String, String> newParam = new HashMap<>();
		for(Map.Entry<String, String> entry : param.entrySet()) {
			newParam.put(entry.getValue(), entry.getKey());
		}
		return newParam;
	}
	
	/**
	 * 
	 * @param l
	 * @return
	 */
	public static Float mean(List<Float> l) {
		float sum = 0f;
		int N = l.size();
		for(float f : l) {sum += f;}
		return sum/N;
	}
}
