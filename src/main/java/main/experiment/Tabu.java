package main.experiment;

import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import learning.AutomataLearning;
import learning.DomainLearning;
import learning.LocalSearch;
import learning.Domain;
import main.Argument;
import main.Properties;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.simulator.Simulator;
import fr.uga.generator.simulator.StripsSimulator;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.utils.Pair;

/**
 * Main class for the Tabu Search
 *
 * @author Maxence Grand
 *
 */
public class Tabu {
	/**
	 * Number of positive test examples
	 */
	public static final int nbTest = 100;
	/**
	 * Size of positive test examples
	 */
	public static final int sizeTest = 100;
	/**
	 * Number of positive learning examples
	 */
	public static final int nbLearn = 30;
	/**
	 * Minimal size of positive leaning examples
	 */
	public static final int minLearn = 10;
	/**
	 * Maximal size of positive leaning examples
	 */
	public static final int maxLearn = 20;



    /**
     *
     * Run
     */
	public static void run () throws Exception{
		long[] seeds = Properties.getSeeds();

		String directory = Argument.getDirectory();
		String reference = Argument.getDomain();
		String initialState = Argument.getProblem();
		String name = Argument.getName();

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

		System.out.println("Initial state : "+initialState);
		System.out.println(reference);
		for(int seed = 0; seed < Argument.getRun() ; seed++) {
			System.out.println("\n"+Argument.getType()+" run : "+seed);
			sim = new StripsSimulator(reference, initialState);
			Random random = new Random();
			random.setSeed(seeds[seed]);
			Random random2 = new Random();
			random2.setSeed(seeds[seed]);
			Generator generator = new Generator(sim,random);

			Generator generatorTest = new Generator(sim,random);
			//Generate test set
			Pair<Sample, Sample> testSet = generatorTest.generate
					(nbTest, 1, sizeTest);


			//Generate learning set
			Pair<Sample, Sample> D = generator.generate(nbLearn,
					minLearn, maxLearn);
			Sample pos = D.getX();
			Sample neg = D.getY();
			System.out.println("I+ size : "+pos.size());
			System.out.println("I- size : "+neg.size());
			System.out.println("x+ mean size : "+pos.meanSize());
			System.out.println("x- mean size : "+neg.meanSize());
			System.out.println("E+ size : "+testSet.getX().size());
			System.out.println("E- size : "+testSet.getY().size());
			System.out.println("e+ mean size : "+testSet.getX().meanSize());
			System.out.println("e- mean size : "+testSet.getY().meanSize());

			for(float thresh : Properties.getPartial()) {
				System.out.println(
						"############################################");
				//if(thresh == 100 || thresh == 25) {
				System.out.println("### Fluent = "+(thresh)+"% ###");
				//}
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);

					System.out.println("\n*** Noise = "+(noise)+"% ***");
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

					String domainName = "";
					domainName = directory+"/domain."
								+((int)thresh)+"."+((int)noise)+"."
								+seed+".pddl";
					
					long startTime = System.currentTimeMillis();
					Domain currentPDDL = null;

					Map<Symbol, Observation> emptyPrec = new HashMap<>();
					Map<Symbol, Observation> emptyPost = new HashMap<>();
					for(Symbol act : actions) {
						emptyPrec.put(act.generalize(), new Observation());
						emptyPost.put(act.generalize(), new Observation());
					}
					currentPDDL =  new Domain(pred, actions, emptyPrec, emptyPost);

					pos = generator.map(pos);
					List<Domain> tabou = new ArrayList<>();
					LocalSearch tabu = new LocalSearch(actions, pred);
					currentPDDL = tabu.tabu(
							currentPDDL,
							pos,
							neg,
							generator.getCompressedNegativeExample(),
							initialState,
							10,
							200,
							tabou);
					//System.out.println(currentPDDL);
					BufferedWriter bw = new BufferedWriter(
							new FileWriter(domainName));
					Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
					pair = currentPDDL.decode();
					Map<Symbol, Observation> preconditions = pair.getX();
					Map<Symbol, Observation> postconditions = pair.getY();
					bw.write(learningModule.generation(
							preconditions, postconditions));
					bw.close();

					long endTime = System.currentTimeMillis();
					float time = (float)(endTime-startTime)/1000;
					System.out.println("Time : "+time);
					TestMetrics.test(reference, initialState, domainName,generatorTest,actions,testSet);
					List<List<Symbol>> examples = new ArrayList<>();
					Observation initial = ((ObservedExample) pos.getExamples().get(0)).getInitialState();
					float fscore = currentPDDL.fscore(initial, testSet.getX(), testSet.getY());
					System.out.println("FSCORE : "+fscore);
				}
			}
		}
	}
}
