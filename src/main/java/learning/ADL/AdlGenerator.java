/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exception.PlanException;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fsm.Example;
import fsm.Symbol;
import fsm.TransitionAction;
import learning.Generator;
import learning.Observation;
import learning.temporal.TemporalDomain;
import simulator.Simulator;
import simulator.temporal.TemporalOracle;

/**
 * @author Maxence Grand
 *
 */
public class AdlGenerator extends Generator{

	/**
	 * Constructs 
	 * @param sim
	 * @param r
	 */
	public AdlGenerator(Simulator sim, Random r) {
		super(sim, r);
	}
	
	/**
	 * Play action to map action/observations
	 *
	 * @param example The example
	 * @return  Observations
	 */
	public List<Observation> play(Example example) {
		super.getBlackbox().reInit();
		List<Observation> observations = new ArrayList<>();
		for(int i = 0; i<example.size(); i++) {
			TransitionAction op = (TransitionAction) example.get(i);
			if(i == 0) {
				observations.add(super.getBlackbox().getCurrentState().clone());
			}

			try {
				super.getBlackbox().apply(op.getAction());
			} catch (PlanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			observations.add(super.getBlackbox().getCurrentState().clone());
		}

		boolean first = true;
		//Add missing attribute
		for(Observation obs : observations) {
			for(Symbol pred : super.getBlackbox().getPredicates() ) {
				float prob = super.getRandom().nextFloat();
				if(prob > LEVEL && !first) {
					obs.missingPredicate(pred);
				}
			}
			first =false;
		}

		first = true;
		//Add noise
		for(Observation obs : observations) {
			for(Symbol pred : super.getBlackbox().getPredicates() ) {
				if(obs.isPresent(pred)){
					float prob = super.getRandom().nextFloat();
					if(prob < THRESH && !first) {
						obs.addNoise(pred);
					}
				}
			}
			first = false;
		}

		return observations;
	}
}
