/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package learning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import exception.PlanException;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fsm.Sample;
import fsm.Symbol;
import fsm.Trace;
import learning.temporal.TemporalDomain;
import fsm.Example;
import fsm.Pair;
import simulator.Simulator;
import simulator.temporal.TemporalOracle;

/**
 * This class implements the generator.
 *
 * @author Maxence Grand
 * @version 1.0
 * @since   10-2019
 */
public class Generator{
	/**
	 * The level of noise to simulate noise. 0% if we doesn't want simulate noise
	 */
	public static float THRESH = (float) 0.0;
	/**
	 * The level of observable informations.
	 */
	public static float LEVEL = (float) 0.25;
	/**
	 * The pseudo random number generator
	 */
	private Random random;
	/**
	 * The oracle
	 */
	private Simulator blackbox;
	/**
	 * All compressed examples
	 */
	private List<CompressedNegativeExample> compressedNegativeExample;
	
	/**
	 * The constructor of the object Generator
	 *
	 * @param sim The oracle
	 * @param r The pseudo random number generator
	 */
	public Generator(Simulator sim, Random r){
		blackbox = sim;
		random = r;
		compressedNegativeExample=new ArrayList<>();
	}


	/**
	 * Getter of compressedNegativeExample
	 * @return the compressedNegativeExample
	 */
	public List<CompressedNegativeExample> getCompressedNegativeExample() {
		return compressedNegativeExample;
	}

	/**
	 * Map Observation with actions
	 *
	 * @param pos Positive sample
	 */
	public Sample map(Sample pos) {
		Sample res = new Sample();
		for(Trace example : pos.getExamples()) {
			List<Observation> observations = play((Example)example);
			res.addExample(new ObservedExample(example.getActionSequences(),
					observations));
		}
		return res;
	}

	public Sample map(Sample pos, TemporalOracle simT) {
		Sample res = new Sample();
		for(Trace example : pos.getExamples()) {
			List<Observation> observations = play((Example)example, simT);
			res.addExample(new ObservedExample(example.getActionSequences(),
					observations));
//			System.out.println(new ObservedExample(example.getActionSequences(),
//					observations));
		}
		return res;
	}

	/**
	 * Update the mapping
	 * @param example The new example
	 * 
	 * @return An example with observations
	 */
	public Example map(Example example) {
		List<Observation> observations = play(example);
		return new ObservedExample(example.getActionSequences(), observations);

	}
	
	/**
	 * Update the mapping
	 * @param example The new example
	 * @param simT The temporal simulator
	 * 
	 * @return An example with observations
	 */
	public Example map(Example example, TemporalOracle simT) {
		List<Observation> observations = play((Example)example, simT);
		return new ObservedExample(example.getActionSequences(),observations);
	}

	/**
	 * Interaction to generate samples
	 *
	 * @param M The size of the positive sample
	 * @param min Minimal size of positive example
	 * @param max Maximal size of positive example
	 * @return I+,I-
	 */
	public Pair<Sample, Sample> generate(int M, int min, int max) {
		List<Trace> pos = new ArrayList<>();
		List<Trace> neg = new ArrayList<>();
		for(int m=0; m<M; m++) {
			//System.out.println((m+1)+"/"+M);
			Pair<Sample, Sample> tmp = this.generate(min, max);
			pos.addAll(tmp.getX().getExamples());
			neg.addAll(tmp.getY().getExamples());
//			System.exit(1);
			
		}
		return new Pair<>(new Sample(pos), new Sample(neg));
	}
	
	/**
	 * Interaction to generate samples
	 *
	 * @param min Minimal size of positive example
	 * @param max Maximal size of positive example
	 * @return I+,I-
	 */
	public Pair<Sample, Sample> generate(int min, int max) {
		List<Trace> pos = new ArrayList<>();
		List<Trace> neg = new ArrayList<>();
		List<Symbol> ops = blackbox.getActions();

		blackbox.reInit();
		int n = random.nextInt(max-min+1)+min;
		List<Symbol> seq = new ArrayList<>();
		List<Trace> compressed = new ArrayList<>();
		for(int i=0; i<n; i++){
			//Copy action list
			List<Symbol> tmp = new ArrayList<>();
			for(Symbol a : ops) {
				tmp.add(a);
			}
			Symbol op = tmp.get(random.nextInt(tmp.size()));
			List<Symbol> negative = new ArrayList<>();
			while(! blackbox.testAction(op) && tmp.size() > 0) {
				negative.add(op);
				List<Symbol> tmp2 = new ArrayList<>();
				for(Symbol s : seq) {
					tmp2.add(s);
				}
				tmp2.add(op);
				neg.add(new Example(tmp2));
				tmp.remove(op);
				if(tmp.size() <= 0) {
					break;
				}
				op = tmp.get(random.nextInt(tmp.size()));
				compressed.add(new Example(tmp2));
			}
			if(tmp.size() <= 0) {
				break;
			}
			if(blackbox.testAction(op)){
				try {
					blackbox.apply(op);
				} catch (PlanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				seq.add(op);
			} else {
				break;
			}

		}
		this.compressedNegativeExample.add(new CompressedNegativeExample(new Example(seq), compressed));

		pos.add(new Example(seq));
		return new Pair<>(new Sample(pos), new Sample(neg));
	}
	
	/**
	 * Interaction to generate samples
	 *
	 * @param min Minimal size of positive example
	 * @param max Maximal size of positive example
	 * @return I+,I-
	 */
	public Pair<Sample, Sample> generate(
			int min, 
			int max, 
			CompressedNegativeExample comp) {
		
		List<Trace> pos = new ArrayList<>();
		List<Trace> neg = new ArrayList<>();
		List<Symbol> ops = blackbox.getActions();

		blackbox.reInit();
		int n = random.nextInt(max-min+1)+min;
		List<Symbol> seq = new ArrayList<>();
		List<Trace> compressed = new ArrayList<>();
		boolean active = true;
		if(comp.getPrefix().size() > n) {
			n = comp.getPrefix().size();
		}
		for(int i=0; i<n; i++){
			//Copy action list
			List<Symbol> tmp = new ArrayList<>();
			for(Symbol a : ops) {
				tmp.add(a);
			}
			Symbol op = null;
			List<Symbol> negative = new ArrayList<>();
			boolean activeNeg = true;
			if(active && i < comp.getPrefix().size()) {
				//Test Negative
				for(Example e : comp.getNegativeIndex(i)) {
					op = e.getActionSequences().get(e.size()-1);
					if(!blackbox.testAction(op)) {
						negative.add(op);
						List<Symbol> tmp2 = new ArrayList<>();
						for(Symbol s : seq) {
							tmp2.add(s);
						}
						tmp2.add(op);
						tmp.remove(op);
						neg.add(new Example(tmp2));
						compressed.add(new Example(tmp2));
					} else {
						activeNeg=false;
						break;
					}
				}
				if(activeNeg) {
					op =comp.getPrefix().get(i);
					while(!blackbox.testAction(op)) {
						negative.add(op);
						List<Symbol> tmp2 = new ArrayList<>();
						for(Symbol s : seq) {
							tmp2.add(s);
						}
						tmp2.add(op);
						neg.add(new Example(tmp2));
						tmp.remove(op);
						if(tmp.size() <= 0) {
							break;
						}
						op = tmp.get(random.nextInt(tmp.size()));
						compressed.add(new Example(tmp2));
						active=false;
					}
				}
			} else {
				op = tmp.get(random.nextInt(tmp.size()));
				while(! blackbox.testAction(op) && tmp.size() > 0) {
					negative.add(op);
					List<Symbol> tmp2 = new ArrayList<>();
					for(Symbol s : seq) {
						tmp2.add(s);
					}
					tmp2.add(op);
					neg.add(new Example(tmp2));
					tmp.remove(op);
					if(tmp.size() <= 0) {
						break;
					}
					op = tmp.get(random.nextInt(tmp.size()));
					compressed.add(new Example(tmp2));
				}
			}
			if(tmp.size() <= 0) {
				break;
			}
			if(blackbox.testAction(op)){
				try {
					blackbox.apply(op);
				} catch (PlanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				seq.add(op);
			} else {
				break;
			}

		}
		this.compressedNegativeExample.add(new CompressedNegativeExample(new Example(seq), compressed));

		pos.add(new Example(seq));
		return new Pair<>(new Sample(pos), new Sample(neg));
	}

	/**
	 * Play action to map action/observations
	 *
	 * @param example The example
	 * @return  Observations
	 */
	public List<Observation> play(Example example) {
		blackbox.reInit();
		List<Observation> observations = new ArrayList<>();
		if(example.getActionSequences().isEmpty()) {
			observations.add(blackbox.getCurrentState().clone());
		}
		for(int i = 0; i<example.size(); i++) {
			Symbol op = example.get(i);
			if(i == 0) {
				observations.add(blackbox.getCurrentState().clone());
			}

			try {
				blackbox.apply(op);
			} catch (PlanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			observations.add(blackbox.getCurrentState().clone());
		}

		boolean first = true;
		//Add missing attribute
		for(Observation obs : observations) {
			for(Symbol pred : this.blackbox.getPredicates() ) {
				float prob = this.random.nextFloat();
				if(prob > LEVEL && !first) {
					obs.missingPredicate(pred);
				}
			}
			first =false;
		}

		first = true;
		//Add noise
		for(Observation obs : observations) {
			for(Symbol pred : this.blackbox.getPredicates() ) {
				if(obs.isPresent(pred)){
					float prob = this.random.nextFloat();
					if(prob < THRESH && !first) {
						obs.addNoise(pred);
					}
				}
			}
			first = false;
		}

		return observations;
	}

	/**
	 * Play action to map action/observations
	 *
	 * @param example The example
	 * @param simT the temporal simulator
	 * @return  Observations
	 */
	public List<Observation> play(Example example, TemporalOracle simT) {
		simT.reInit();
		List<Observation> observations = new ArrayList<>();
		for(int i = 0; i<example.size(); i++) {
			Symbol op = example.get(i);
			if(i == 0) {
				Node currentState = new Node(simT.getCurrentState());
				Observation obs = new Observation(simT.getAllPredicates());
				for(Symbol s : simT.getSymbolsState(currentState)){
					obs.addTrueObservation(s);
				}
				for(Symbol s : simT.getPositiveStaticPredicate()){
					obs.addTrueObservation(s);
				}
				observations.add(obs);
			}

			simT.apply_sequential(op);
			if(!TemporalDomain.isInvariantOp(op)) {
				Node currentState = new Node(simT.getCurrentState());
				Observation obs = new Observation(simT.getAllPredicates());
				for(Symbol s : simT.getSymbolsState(currentState)){
					obs.addTrueObservation(s);
				}
				for(Symbol s : simT.getPositiveStaticPredicate()){
					obs.addTrueObservation(s);
				}
				observations.add(obs);
			} else {
				observations.add(observations.get(i).clone());
			}
		}

		boolean first = true;
		//Add missing attribute
		for(int i = 1; i < observations.size(); i++) {
			Observation obs = observations.get(i);
			if(!TemporalDomain.isInvariantOp(example.get(i-1))) {
				for(Symbol pred : simT.getAllPredicates() ) {
					float prob = this.random.nextFloat();
					if(prob > LEVEL) {
						obs.missingPredicate(pred);
					}
				}
			} else {
				observations.set(i, observations.get(i-1));
			}
			first =false;
		}

		first = true;
		//Add noise
		for(int i = 1; i < observations.size(); i++) {
			Observation obs = observations.get(i);
			if(!TemporalDomain.isInvariantOp(example.get(i-1))) {
				for(Symbol pred : simT.getAllPredicates() ) {
					if(obs.isPresent(pred)){
						float prob = this.random.nextFloat();
						if(prob < THRESH) {
							obs.addNoise(pred);
						}
					}
				}
			} else {
				//obs = observations2.get(i-1).clone();
				observations.set(i, observations.get(i-1).clone());
			}
			//System.out.println(example.get(i-1));
			//System.out.println(observations.get(i));
			//observations2.add(obs);
			first = false;
		}

		return observations;
	}

	/**
	 * Return the initial state
	 * @return An observation
	 */
	public Observation getInitialState() {
		return blackbox.getInitialState();
	}
	/**
	 * Play action to map action/observations without simulate noise
	 *
	 * @param example The example
	 * @return  Observations
	 */
	public List<Observation> playWithoutNoise(Example example) {
		blackbox.reInit();
		List<Observation> observations = new ArrayList<>();
		for(int i = 0; i<example.size(); i++) {
			Symbol op = example.get(i);
			if(i == 0) {
				observations.add(blackbox.getCurrentState().clone());
			}

			try {
				blackbox.apply(op);
			} catch (PlanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
			observations.add(blackbox.getCurrentState().clone());
		}
		return observations;
	}


	/**
	 * Getter of random
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}


	/**
	 * Setter random
	 * @param random the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}


	/**
	 * Setter compressedNegativeExample
	 * @param compressedNegativeExample the compressedNegativeExample to set
	 */
	public void setCompressedNegativeExample(List<CompressedNegativeExample> compressedNegativeExample) {
		this.compressedNegativeExample = compressedNegativeExample;
	}


	/**
	 * Getter of tHRESH
	 * @return the tHRESH
	 */
	public static float getTHRESH() {
		return THRESH;
	}


	/**
	 * Setter tHRESH
	 * @param tHRESH the tHRESH to set
	 */
	public static void setTHRESH(float tHRESH) {
		THRESH = tHRESH;
	}


	/**
	 * Getter of lEVEL
	 * @return the lEVEL
	 */
	public static float getLEVEL() {
		return LEVEL;
	}


	/**
	 * Setter lEVEL
	 * @param lEVEL the lEVEL to set
	 */
	public static void setLEVEL(float lEVEL) {
		LEVEL = lEVEL;
	}


	/**
	 * Getter of blackbox
	 * @return the blackbox
	 */
	public Simulator getBlackbox() {
		return blackbox;
	}


	/**
	 * Setter blackbox
	 * @param blackbox the blackbox to set
	 */
	public void setBlackbox(Simulator blackbox) {
		this.blackbox = blackbox;
	}
	
	
	
}
