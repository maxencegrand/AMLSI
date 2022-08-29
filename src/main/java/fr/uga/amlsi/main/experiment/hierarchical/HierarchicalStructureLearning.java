/**
 * 
 */
package fr.uga.amlsi.main.experiment.hierarchical;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.generator.hierarchical.HierarchicalGenerator;
import fr.uga.generator.simulator.hierarchical.HierarchicalSimulator;
import fr.uga.generator.simulator.hierarchical.HtnSimulator;
import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.Task;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fr.uga.amlsi.fsm.DFA;
import fr.uga.amlsi.fsm.Partition;
import fr.uga.amlsi.learning.AutomataLearning;
import fr.uga.amlsi.learning.hierarchical.HtnLearner;
import fr.uga.amlsi.main.Argument;
import fr.uga.amlsi.main.Properties;

/**
 * @author Maxence Grand
 *
 */
public class HierarchicalStructureLearning {
	/**
	 * The  number of positive test  examples
	 */
	public static final int nbTest = 100;
	/**
	 * The maximal size of positive test examples
	 */
	public static final int sizeTest = 100;
	/**
	 * The number of generated example at each iterations
	 */
	public static final int nbLearn = 1;
	/**
	 * The number of epochs for each tabu search
	 */
	public static final int epochs = 200;
	/*
	 * The number of generation
	 */

	public static final int Delta = 5;
	/**
	 * The minimal size of generated positive example
	 */
	public static final int minLearn = 10;
	/**
	 * The maximal size of generated positive example
	 */
	public static final int maxLearn = 20;
	/**
	 * The pseudo random number generator used by our example generator
	 */
	private static Random random ;

    /**
     *
     * Run
     */
	public static void run() throws Exception{
		long[] seeds = Properties.getSeeds();

		String directory = Argument.getDirectory();
		String reference = Argument.getDomain();
		String initialState = Argument.getProblem();
		String taskState = Argument.getProblem();
		String name = Argument.getName();
 
		int T = Argument.getT();
		/*
		 * The blackbox
		 */
		HierarchicalSimulator sim = new HtnSimulator(reference, initialState);
		List<Symbol> actions = sim.getActions();
		List<Symbol> pred = new ArrayList<>();
		for(Symbol a : sim.getPredicates()){
			pred.add(a);
		}
		for(Symbol a : sim.getPositiveStaticPredicates()){
			pred.add(a);
		}
		System.out.println("# actions "+sim.getActions().size());
		System.out.println("# predicate "+pred.size());

		System.out.println("Initial state : "+initialState);
		System.out.println(reference);

		for(int seed = 0; seed < Argument.getRun() ; seed++) {
			/*
			 * The blackbox
			 */
			sim = new HtnSimulator(reference, initialState);
			/*
			 * Pseudo random number generator for test set generation
			 */
			Random randomTest = new Random();
			randomTest.setSeed(seeds[seed]);

			/*
			 * Example generator used for the test set
			 */
			Generator generatorTest = 
					new HierarchicalGenerator(sim,randomTest);
			/*
			 * Generate test set
			 */
			Pair<Sample, Sample> testSet = generatorTest.generate(100, 0, 100);

//
			/*
			 * For logs
			 */
			System.out.println("E+ size : "+testSet.getX().size());
			System.out.println("E- size : "+testSet.getY().size());
			System.out.println("e+ mean size : "+testSet.getX().meanSize());
			System.out.println("e- mean size : "+testSet.getY().meanSize());

			int[] sizes = {10,30,50};
			int[] numbers = {5,10,20,30,50};
			for(int nb : numbers) {
				System.out.println();
				System.out.println("////////////// Number of random walks : "+nb);
				for(int size : sizes) {
					System.out.println();
					System.out.println("************* Max size : "+size);
					/*
					 * Pseudo random number generator for test set generation
					 */
					random = new Random(seeds[seed]);
					/*
					 * Example generator for learning samples
					 */
					HierarchicalGenerator generator = 
							new HierarchicalGenerator(sim,random);
					
					AutomataLearning learner;
					learner = new AutomataLearning(
							pred,
							actions,
							null,
							null);
					learner.setSamples(testSet.getX(), testSet.getY());
					HtnLearner learningModule = new HtnLearner(
							pred,
							actions,
							directory,
							name,
							reference,
							initialState,
							generator);

					long startTime=0, endTime=0;
					double time=0;
					
					startTime = System.currentTimeMillis();

					Sample pos = new Sample();
					Sample neg = new Sample();
					Sample negNoTask = new Sample();
					DFA A = null;
					Pair<DFA, Partition> p = null;
					for(int i = 0; i < nb; i++) {
						Pair<Sample, Sample> samples = generator.generate(1,10,size);
						Sample newPos = samples.getX();
						Sample newNeg = samples.getY();
						Sample newNegNoTask = new Sample();
						newNeg.getExamples().forEach(ex -> {
							neg.addExample(ex);
							int idx = ex.getActionSequences().size()-1;
							Symbol s = ex.getActionSequences().get(idx);
							if(!(s instanceof Task)) {
								negNoTask.addExample(ex);
								newNegNoTask.addExample(ex);
							}
						});
						
						for(Trace x : newPos.getExamples()) {
							learner.removeRules((Example)x);
						}

						Sample posPrefixes = new Sample();
						for(Trace x : newPos.getExamples()) {
							pos.addExample(x);
							posPrefixes.addExample(x);
						}
						posPrefixes = learner.decompose(posPrefixes);
						if(i == 0) {
							A = learner.RPNI(
									posPrefixes, 
									negNoTask);
						} else {
							A = learner.RPNI2(
									pos, 
									negNoTask, 
									posPrefixes, 
									newNegNoTask, 
									A, 
									A.getPartition());
						}
						endTime = System.currentTimeMillis();
					}
					System.out.println("I+ size : "+pos.size());
					System.out.println("I- size : "+neg.size());
					System.out.println("I- (no task) size : "+negNoTask.size());
					System.out.println("Time Aotomaton : "+((float)(endTime-startTime)/1000));
					
					startTime = System.currentTimeMillis();
					List<Method> methods = learningModule.methodLearning(
							pos,
							negNoTask, 
							generator.getRamdomTaskTraces(),
							A,
							Argument.getRec_type());
					System.out.println("# Methods : "+methods.size());
					methods.forEach(m -> System.out.println(m));
					endTime = System.currentTimeMillis();
					System.out.println("Time Method Generation : "+((float)(endTime-startTime)/1000));
				}
			}
		}
	}
}
