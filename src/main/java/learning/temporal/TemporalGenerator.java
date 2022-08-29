/**
 * 
 */
package learning.temporal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fsm.Example;
import fsm.Pair;
import fsm.Sample;
import fsm.Symbol;
import fsm.Trace;
import learning.CompressedNegativeExample;
import main.Argument;
import simulator.temporal.TemporalExample;
import simulator.temporal.TemporalOracle;

/**
 * @author Maxence Grand
 *
 */
public class TemporalGenerator {
	/**
	 * 
	 */
	private float WAIT=0.5f;
	/**
	 * The pseudo random number generator
	 */
	private Random random;
	/**
	 * The oracle
	 */
	public TemporalOracle TemporalBlackbox;
	/**
	 * 
	 */
	private List<TemporalExample> positive;
	/**
	 * 
	 */
	private List<TemporalExample> negative;
	/**
	 * 
	 */
	List<CompressedTemporalNegativeExample> compressed;
	/**
	 * 
	 */
	List<CompressedNegativeExample> compressedSequential;
	
	/**
	 * The constructor of the object Generator
	 *
	 * @param sim The oracle
	 * @param r The pseudo random number generator
	 */
	public TemporalGenerator(TemporalOracle TemporalBlackbox,
			Random r){
		this.random = r;
		this.TemporalBlackbox = TemporalBlackbox;
		this.positive = new ArrayList<>();
		this.negative = new ArrayList<>();
		this.compressed = new ArrayList<>();
		this.compressedSequential = new ArrayList<>();
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public Pair<Sample, Sample> generate(int min, int max,float timeMax) {
		/*this.WAIT = this.random.nextFloat();
		if(this.WAIT <= 0.2f) {
			this.WAIT = 0.2f;
		} else if(this.WAIT <= 0.4f) {
			this.WAIT = 0.4f;
		} else if(this.WAIT <= 0.6f) {
			this.WAIT = 0.6f;
		} else if(this.WAIT <= 0.8f) {
			this.WAIT = 0.8f;
		} else if(this.WAIT <= 1f) {
			this.WAIT = 0.99f;
		}*/
		//System.out.println(this.WAIT);
		int n = this.random.nextInt(max-min+1)+min;
		//System.out.println(" "+n);
		float timestamp = 0f;
		Pair<Sample, Sample> res = new Pair<>(new Sample(), new Sample());
		Map<Float, Symbol> pos = new HashMap<>();
		this.TemporalBlackbox.reInit();
		Map<Float, List<Symbol>> neg = new HashMap<>();
//		System.out.print(n);
		for(int i=0; i<n; i++) {
			//System.out.println(i);
			boolean b =true;
			while(b && this.random.nextFloat() > WAIT) {
				if(this.TemporalBlackbox.executeNextEnd()) {
					timestamp = this.TemporalBlackbox.getCurrentTime() +
							TemporalOracle.TOLERANCE_VALUE;
				} else {
					b=false;
				}
			}
			List<Symbol> durativeActions = new ArrayList<>();
			this.TemporalBlackbox.getAllActions().forEach(a -> durativeActions.add(a));
			b = true;
			boolean fail=true;
			do {
				Symbol a = durativeActions.get(this.random.nextInt(durativeActions.size()));
				if(this.TemporalBlackbox.isApplicable(a, timestamp)) {
					b = false;
					fail=false;
					this.TemporalBlackbox.apply();
					pos.put(TemporalBlackbox.getCurrentTime(), a);
				} else {
					durativeActions.remove(a);
					b = !durativeActions.isEmpty();
					if(! neg.containsKey(timestamp)) {
						neg.put(timestamp, new ArrayList<>());
					}
					neg.get(timestamp).add(a);
				}
			}while(b);
			if(fail) {
				//Blakcbox execute next end
				if(this.TemporalBlackbox.executeNextEnd()) {
					i--;
					timestamp = this.TemporalBlackbox.getCurrentTime() +
							TemporalOracle.TOLERANCE_VALUE;
				} else {
					//End Generation
					break;
				}
				
			}
			timestamp = this.TemporalBlackbox.getCurrentTime() +
					TemporalOracle.TOLERANCE_VALUE;
		}
		this.TemporalBlackbox.endSimulation();
		
		List<Float> stp = new ArrayList<>(pos.keySet());
		Collections.sort(stp);
		stp = new ArrayList<>(neg.keySet());
		Collections.sort(stp);
		TemporalExample example = new TemporalExample(pos);
		this.positive.add(example);
		this.negative.addAll(example.generateAllNegativeExamples(neg));
		if(Argument.isTwoOp()) {
			List<Trace> negativeClassical = new ArrayList<>();
			Trace positiveClassical = 
					example.convertIntoSequential2Op(TemporalBlackbox);
			example.generateAllNegativeExamples(neg).forEach(exNeg -> {
				negativeClassical.add(
						exNeg.convertIntoSequential2Op(TemporalBlackbox));
			});
			this.compressedSequential.add(new CompressedNegativeExample(
					positiveClassical, negativeClassical));
			res.getX().addExample(positiveClassical);
			res.getY().add(new Sample(negativeClassical));
		} else {
			List<Trace> negativeClassical = new ArrayList<>();
			Trace positiveClassical = 
					example.convertIntoSequential3Op(TemporalBlackbox);
			Trace positiveClassical2OP = 
					example.convertIntoSequential2Op(TemporalBlackbox);
			example.generateAllNegativeExamples(neg).forEach(exNeg -> {
				negativeClassical.add(
						exNeg.convertIntoSequential3Op(TemporalBlackbox));
			});
			this.compressedSequential.add(new CompressedNegativeExample(
					positiveClassical, negativeClassical));
			res.getX().addExample(positiveClassical);
//			res.getX().addExample(positiveClassical2OP);
			res.getY().add(new Sample(negativeClassical));
		}
		
		return res;
	}

	/**
	 * 
	 * @param min
	 * @param max
	 * @param timeMax
	 * @param n
	 * @return
	 */
	public Pair<Sample, Sample> generate(int min, int max,float timeMax, int n) {
		Sample pos=new Sample(), neg=new Sample();
		//System.err.println(n);
		for(int i =0; i<n; i++) {
			//System.out.print(i);
			Pair<Sample, Sample> p = this.generate(min, max, timeMax);
			pos.add(p.getX());
			neg.add(p.getY());
		}
		return new Pair<>(pos, neg);
	}
	
	/**
	 * Getter of positive
	 * @return the positive
	 */
	public List<TemporalExample> getPositive() {
		return positive;
	}

	/**
	 * Getter of negative
	 * @return the negative
	 */
	public List<TemporalExample> getNegative() {
		return negative;
	}

	/**
	 * Getter of compressed
	 * @return the compressed
	 */
	public List<CompressedTemporalNegativeExample> getCompressed() {
		return compressed;
	}

	/**
	 * Getter of compressedSequential
	 * @return the compressedSequential
	 */
	public List<CompressedNegativeExample> getCompressedSequential() {
		return compressedSequential;
	}
	
	
	
}
