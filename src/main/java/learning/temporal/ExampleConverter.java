/**
 * 
 */
package learning.temporal;

import fsm.Example;
import fsm.Sample;
import fsm.Symbol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simulator.temporal.TemporalOracle;

/**
 * @author Maxence Grand
 *
 */
public class ExampleConverter {
	/**
	 * All positive examples
	 */
	private static Sample positive = null;
	/**
	 * All negative examples
	 */
	private static Sample negative = null;

	/**
	 * Convert examples
	 * @param pos Positive example
	 * @param neg Negative examples
	 * @param oracle Temporal Simulator
	 */
	public static void conversion(Map<Float, Symbol> pos,
			Map<Float, List<Symbol>> neg, TemporalOracle oracle) {

		//Retrieve ordered timestamps
		List<Float> tPos = new ArrayList<>(pos.keySet());
		Collections.sort(tPos);
		List<Float> tNeg = new ArrayList<>(neg.keySet());
		Collections.sort(tNeg);
		Map<Float, List<Symbol>> endActions = new HashMap<>();

		//Iterate timestamps
		int itPos = 0;
		int itNeg = 0;
		List<Symbol> current = new ArrayList<>();
		List<List<Symbol>> negativeSample = new ArrayList<>();
		while(itPos < tPos.size() || itNeg < tNeg.size()) {
			List<Float> tEnd = new ArrayList<>(endActions.keySet());
			Collections.sort(tEnd);
			int newItPos = itPos;
			int newItNeg = itNeg;
			//Negative is min
			if( itNeg < tNeg.size()
					&& (itPos >= tPos.size() || tNeg.get(itNeg) <= tPos.get(itPos)) 
					&& (tEnd.isEmpty() ? true : tNeg.get(itNeg) < tEnd.get(0))) {
				//System.out.println(1);
				neg.get(tNeg.get(itNeg)).forEach(e -> {
					Symbol a = e.clone();
					a.setName(a.getName()+"-start");
					List<Symbol> tmp = new ArrayList<>();
					current.forEach(a2 -> {
						tmp.add(a2);
					});
					tmp.add(a);
					negativeSample.add(tmp);
				});
				newItNeg++;
				//Start positive is min
			} else if (itPos < tPos.size()
					&& (itNeg >= tNeg.size() || (tPos.get(itPos) <= tNeg.get(itNeg) ))
					&& (tEnd.isEmpty() ? true : tPos.get(itPos) < tEnd.get(0))) {
				Symbol a = pos.get(tPos.get(itPos)).clone();
				Symbol a2 = pos.get(tPos.get(itPos)).clone();
				a.setName(a.getName()+"-start");
				a2.setName(a2.getName()+"-inv");
				current.add(a);
				current.add(a2);
				newItPos++;
				//Add End action
				float endA = oracle.getDuration(pos.get(tPos.get(itPos)));
				if(!endActions.containsKey(endA)) {
					endActions.put(endA, new ArrayList<>());
				}
				endActions.get(endA).add(pos.get(tPos.get(itPos)).clone());
			} else if(!tEnd.isEmpty() &&
					(itPos >= tPos.size() || tEnd.get(0) <= tPos.get(itPos)) &&
					(itNeg >= tNeg.size() || tEnd.get(0) <= tNeg.get(itNeg))) {
				float endA = tEnd.get(0);
				endActions.get(endA).forEach(a -> {
					a.setName(a.getName()+"-end");
					current.add(a);
				});
				endActions.remove(endA);
			} else {
			}
			itPos = newItPos;
			itNeg = newItNeg;
		}
		//Add last end
		List<Float> tEnd = new ArrayList<>(endActions.keySet());
		Collections.sort(tEnd);
		tEnd.forEach(endA -> {
			endActions.get(endA).forEach(a -> {
				a.setName(a.getName()+"-end");
				current.add(a);
			});	
		});
		positive= new Sample();
		positive.addExample(new Example(current));
		negative = new Sample();
		negativeSample.forEach(ex -> {
			negative.addExample(new Example(ex));
		});
	}

	/**
	 * Getter of positive examples
	 * @return Sample
	 */
	public static Sample getPositive() {
		return ExampleConverter.positive;
	}

	/**
	 * Getter of negative examples
	 * @return Sample
	 */
	public static Sample getNegative() {
		return ExampleConverter.negative;

	}
}
