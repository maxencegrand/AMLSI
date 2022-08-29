/**
 * 
 */
package main.experiment;

import fr.uga.generator.generator.Generator;
import fr.uga.generator.simulator.Simulator;
import fr.uga.generator.simulator.StripsSimulator;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import fsm.DFA;
import learning.AutomataLearning;
import learning.DomainLearning;
import main.Argument;
import main.Properties;

/**
 * @author Maxence Grand
 *
 */
public class Grammar {
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
	 * The pseudo random number generator used by our example generator
	 */
	private static Random random ;

	public static void run() throws Exception{
		int minLearn = Argument.getMin();
		int maxLearn = Argument.getMax();
		long[] seeds = Properties.getSeeds();

		String directory = Argument.getDirectory();
		String reference = Argument.getDomain();
		String initialState = Argument.getProblem();
		String name = Argument.getName();

		int T = Argument.getT();
		/*
		 * The blackbox
		 */
		Simulator sim = new StripsSimulator(reference, initialState);
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

		/**
		 * For logs
		 */
		System.out.println("# actions "+sim.getActions().size());
		System.out.println("# predicate "+pred.size());

		System.out.println("Initial state : "+initialState);
		System.out.println(reference);

		for(int seed = 0; seed < Argument.getRun() ; seed++) {
			System.out.println("\n"+"Run "+seed);
			/*
			 * The blackbox
			 */
			sim = new StripsSimulator(reference, initialState);
			/*
			 * Pseudo random number generator for test set generation
			 */
			Random randomTest = new Random();
			randomTest.setSeed(seeds[seed]);

			/*
			 * Example generator used for the test set
			 */
			Generator generatorTest = new Generator(sim,randomTest);
			/*
			 * Generate test set
			 */
			Pair<Sample, Sample> testSet = generatorTest.generate(nbTest, 1,sizeTest);

			/*
			 * For logs
			 */
			System.out.println("E+ size : "+testSet.getX().size());
			System.out.println("E- size : "+testSet.getY().size());
			System.out.println("e+ mean size : "+testSet.getX().meanSize());
			System.out.println("e- mean size : "+testSet.getY().meanSize());
			int [] maxs = {10,20,30,50,100};
			for(int max : maxs) {
				System.out.println("\n"+"Max "+max);
				for(int size = 10; size <= T; size +=10) {
					/*
					 * Pseudo random number generator for test set generation
					 */
					random = new Random(seeds[seed]);
					/*
					 * Example generator for learning samples
					 */
					Generator generator = new Generator(sim,random);
					DomainLearning learningModule = new DomainLearning(
							pred,
							actions,
							directory,
							name,
							reference,
							initialState,
							generator);

					learningModule.setSamples(testSet.getX(), testSet.getY());
					learningModule.setTypes(sim.getHierarchy());

					AutomataLearning learner;
					learner = new AutomataLearning(
							pred,
							actions,
							generator,
							learningModule);
					learner.setSamples(testSet.getX(), testSet.getY());
					
					if(Argument.getType() == Argument.TYPE.RPNI) {
						learner.emptyR();
					}
					
					Pair<Sample, Sample> samples = generator.generate
							(size, minLearn, max);
					Sample pos = samples.getX();
					pos = generator.map(pos);
					Sample neg = samples.getY();
					System.out.println("Size "+size);


					//Step 1: grammar induction
					//Step 1.1: Adding prefixes
					Sample posPrefixes = pos.clone();
					posPrefixes = learner.decompose(posPrefixes);
					//Step 1.2: pairewise constraints
					for(Trace x : pos.getExamples()) {
						learner.removeRules((Example)x);
					}
					//Step 1.3: Automaton learning
					DFA A = learner.RPNI(posPrefixes, neg);
					System.out.println(learner.test(A));
				}
			}
		}
	}
}
