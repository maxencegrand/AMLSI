/**
 * 
 */
package learning.hierarchical;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exception.PlanException;
import fsm.DecompositionTrace;
import fsm.Example;
import fsm.Pair;
import fsm.RandomTaskTrace;
import fsm.Sample;
import fsm.Symbol;
import fsm.Trace;
import learning.Generator;
import simulator.hierarchical.HierarchicalSimulator;

/**
 * @author Maxence Grand
 *
 */
public class HierarchicalGenerator extends Generator{
	/**
	 * The oracle
	 */
	private HierarchicalSimulator blackbox;
	/**
	 * All random task traces
	 */
	private List<RandomTaskTrace> ramdomTaskTraces;
	/**
	 * All random task traces
	 */
	private List<Trace> taskSeqPos,taskSeqNeg;

	/**
	 * The constructor of the object Generator
	 *
	 * @param sim The oracle
	 * @param r The pseudo random number generator
	 */
	public HierarchicalGenerator(HierarchicalSimulator sim, Random r){
		super(sim, r);
		this.blackbox = sim;
		this.ramdomTaskTraces= new ArrayList<>();
		this.taskSeqNeg = new ArrayList<>();
		this.taskSeqPos = new ArrayList<>();
	}

	/**
	 * Interaction to generate samples
	 *
	 * @param M The size of the positive sample
	 * @param min Minimal size of positive example
	 * @param max Maximal size of positive example
	 * @return I+,I-
	 */
	public Pair<Sample, Sample> generate(int min, int max) {
		List<Trace> pos = new ArrayList<>();
		List<Trace> neg = new ArrayList<>();
		List<Symbol> ops = blackbox.getActions();
		blackbox.reInit();
		List<Symbol> seq = new ArrayList<>();
		List<Symbol> seqTask = new ArrayList<>();
		List<List<Symbol>> compressed = new ArrayList<>();
		List<Symbol> allTasks = new ArrayList<>();
		allTasks.addAll(this.blackbox.getTasks());
		List<Integer> taskIdx = new ArrayList<>();
		List<Integer> taskIdxB = new ArrayList<>();
		int n = getRandom().nextInt(max-min+1)+min;
		n = n == 0 ? 1 : n;
		for(int i=0; i<n; i++){
//			System.out.println("i="+i+" ("+n+") ");
			//Copy action list
			List<Symbol> tmp = new ArrayList<>();
			for(Symbol a : ops) {
				tmp.add(a);
			}
			for(Symbol a : allTasks) {
				tmp.add(a);
			}
			Symbol op = tmp.get(getRandom().nextInt(tmp.size()));
			List<Symbol> negative = new ArrayList<>();
			while(! blackbox.test(op) && tmp.size() > 0) {
				List<Symbol> tmp2 = new ArrayList<>();
				for(Symbol s : seq) {
					tmp2.add(s);
				}
				tmp2.add(op);
				neg.add(new Example(tmp2));
				this.taskSeqNeg.add(new Example(tmp2));
				tmp.remove(op);
				if(tmp.size() <= 0) {
					break;
				}
				op = tmp.get(getRandom().nextInt(tmp.size()));
			}
			compressed.add(negative);
			if(tmp.size() <= 0) {
				break;
			}
			if(blackbox.test(op)){
				try {
					Trace t = this.blackbox.decompose(op);
					if(t!=null) {
						blackbox.apply(op);
						taskIdxB.add(seq.size());
						seq.addAll(t.getActionSequences());
						seqTask.add(op);
						taskIdx.add(seq.size()-1);
					}
				} catch (PlanException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				break;
			}
//			System.exit(1);
		}
		pos.add(new Example(seq));
		this.ramdomTaskTraces.add(new RandomTaskTrace(
				new Example(seq), 
				new Example(seqTask),
				taskIdx,
				taskIdxB));
		this.taskSeqPos.add(new Example(seqTask));
		return new Pair<>(new Sample(pos), new Sample(neg));
	}

	/**
	 * Getter of ramdomTaskTraces
	 * @return the ramdomTaskTraces
	 */
	public List<RandomTaskTrace> getRamdomTaskTraces() {
		return ramdomTaskTraces;
	}
	
	public List<DecompositionTrace> getDecompositionTraces() {
		List<DecompositionTrace> res = new ArrayList<>();
		ramdomTaskTraces.forEach(t -> res.add(new DecompositionTrace(t)));
		return res;
	}

	public Sample posTask() {
		return new Sample(this.taskSeqPos);
	}
	
	public Sample negTask() {
		return new Sample(this.taskSeqNeg);
	}
}
