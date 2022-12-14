/**
 * 
 */
package fr.uga.amlsi.main.experiment.hierarchical;

import java.util.List;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.exception.PlanException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.simulator.Simulator;
import fr.uga.generator.simulator.StripsSimulator;
import fr.uga.generator.simulator.hierarchical.HtnSimulator;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.DecompositionTrace;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;

/**
 * @author Maxence Grand
 *
 */
public class Measure {
	
	/**
	 * 
	 * @param pos
	 * @param learnt
	 * @param initial
	 * @return
	 */
	public static float succesfull(Sample pos, String learnt, String initial) {
		try {
			HtnSimulator test = new HtnSimulator(learnt, initial);
			float res = 0f;
			for(Trace t : pos.getExamples()) {
				test.reInit();
				float tmp =0f;
				for(Symbol op : t.getActionSequences()) {
					if(test.test(op)) {
						try {
							test.apply(op);
							tmp++;
						} catch (PlanException e) {
							// TODO Auto-generated catch block
							return 0;
						}
					} else {
						break;
					}
				}
				res += (tmp/t.size());
			}
			return res / pos.size();
		} catch(Exception e) {
			return 0;
		}
	}
	
	/**
	 * 
	 * @param pos
	 * @param learnt
	 * @param initial
	 * @return
	 */
	public static float correction(
			List<DecompositionTrace> decompositions,
			String learnt, 
			String initial) {
		try {
			HtnSimulator test = new HtnSimulator(learnt, initial);
			float res = 0f;
			for(DecompositionTrace t : decompositions) {
				test.reInit();
				float tmp =0f;
				for(int i = 0; i < t.size(); i++) {
					Symbol op = t.getActionSequences().get(i);
					if(test.test(op)) {
						try {
							Trace t1 = test.decompose(op);
							Trace t2 = t.getDecomposition(i, op);
							if(t1.equals(t2)) {
								tmp++;
							} else {
							}
							test.apply(op);
						} catch (PlanException e) {
							// TODO Auto-generated catch block
							return 0;
						}
					} else {
						break;
					}
				}
				res += (tmp/t.size());
			}
			return res / decompositions.size();
		} catch(Exception e) {
			return 0;
		}
	}
	
	/**
	 * 
	 * @param pos
	 * @param learnt
	 * @param initial
	 * @return
	 */
	public static float precision(Sample neg, String learnt, String initial) {
		try {
			HtnSimulator test = new HtnSimulator(learnt, initial);
			float res = 0f;
			for(Trace t : neg.getExamples()) {
				test.reInit();
				int tmp =0;
				boolean b = false;
				for(Symbol op : t.getActionSequences()) {
					if(test.test(op)) {
						try {
							test.apply(op);
							tmp++;
						} catch (PlanException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						b = true;
						break;
					}
				}
				if(b) {
					res++;
				}
			}
			return res / neg.size();
		} catch(Exception e) {
			return 0;
		}
	}
}
