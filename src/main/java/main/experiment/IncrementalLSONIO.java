package main.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import baseline.lsonio.LSONIO;
import learning.DomainLearning;
import learning.Domain;
import main.Argument;
import main.Properties;
import fr.uga.generator.exception.BlocException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.simulator.Simulator;
import fr.uga.generator.simulator.StripsSimulator;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
/**
 * Main class for LSONIO
 * @author Maxence Grand
 *
 */
public class IncrementalLSONIO {
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
	public static  int minLearn = 10;
	/**
	 * Maximal size of positive leaning examples
	 */
	public static  int maxLearn = 20;

	/**
	 * Test learnt domain
	 * @param learner The action model learner
	 * @param A The automaton
	 * @param reference The reference domain
	 * @param domainName The domain name
	 * @param generatorTest The generator for test sample
	 * @param actions The action set
	 * @param testSet The test sample
	 * @throws BlocException
	 */
	private static void test(
			String reference,
			String initialState,
			String domainName,
			Generator generatorTest,
			List<Symbol> actions,
			Pair<Sample, Sample> testSet) throws BlocException {
		float semantic_dist = TestMetrics.semantic_distance
				(reference, domainName, false);
		System.out.println("Syntactical distance : "+semantic_dist);
		Pair<Float, Float> errorP = TestMetrics.error_rate
				(domainName, actions, testSet.getX(), generatorTest);
		System.out.println("Error Rate Precondition : "+errorP.getX());
		System.out.println("Error Rate Postcondition : "+errorP.getY());
	}

	private static boolean containsNeg(
			Example example,
			Example counterExample) {
		for(int i = 0; i < counterExample.size() - 1; i++) {
			if(example.size() <= i || ! example.get(i).equals(counterExample.get(i))) {
				return false;
			}
		}
		return true;
	}

	private static Sample merge(Sample neg,	Sample data) {
		Map<List<Symbol>, List<Observation>> newData = new HashMap<>();
		Sample res = new Sample();
		Map<ObservedExample, Sample> toMerge = new HashMap<>();

		data.getExamples().forEach(ex -> {
			ObservedExample exObs = (ObservedExample) ex;
			List<Observation> obs = exObs.getObservations();
			//Pair<List<Symbol>, List<Observation>> p = new Pair<>(ex, obs);
			Sample tmp = new Sample();
			neg.getExamples().forEach(counterEx -> {
				if(containsNeg((Example)ex, (Example)counterEx)) {
					tmp.addExample(counterEx);
				}
			});
			tmp.getExamples().forEach(toRemove -> {
				neg.removeExample((Example)toRemove);
			});

			toMerge.put(exObs, tmp);
		});

		toMerge.forEach((d, n) -> {
			List<Symbol> newExample = new ArrayList<>();
			List<Observation> newStates = new ArrayList<>();
			newStates.add(d.getInitialState());
			for(int i = 0; i < d.size(); i++) {
				final int ii = i;
				n.getExamples().forEach(counterEx -> {
					if(counterEx.size() == ii+1) {
						newExample.add(((Example)counterEx).get(ii));
						newStates.add(d.getObservations().get(ii));
					}
				});
				newExample.add(d.getActionSequences().get(i));
				newStates.add(d.getObservations().get(i+1));
			}
			res.addExample(new ObservedExample(newExample, newStates));
		});
		return res;
	}
	
	/**
	 * Run
	 */
	public static void run () throws Exception{
		minLearn = Argument.getMin();
		maxLearn = Argument.getMax();
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
			System.out.println("\nRPNI-R run : "+seed);
			sim = new StripsSimulator(reference, initialState);
			Random random = new Random();
			random.setSeed(seeds[seed]);
			Random random2 = new Random();
			random2.setSeed(seeds[seed]);
			Generator generator = new Generator(sim,random);

			Generator generatorTest = new Generator(sim,random);
			Pair<Sample, Sample> testSet = generatorTest.generate
					(nbTest, 1, sizeTest);


			System.out.println("E+ size : "+testSet.getX().size());
			System.out.println("E- size : "+testSet.getY().size());
			System.out.println("e+ mean size : "+testSet.getX().meanSize());
			System.out.println("e- mean size : "+testSet.getY().meanSize());

			for(float thresh : Properties.getPartial()) {
				System.out.println(
						"############################################");
				System.out.println("### Fluent = "+(thresh)+"% ###");
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);
					System.out.println("\n*** Noise = "+(noise)+"% ***");
					
					//Generate learning set
					Pair<Sample, Sample> D = generator.generate(1, minLearn, maxLearn);
					System.out.println("Iteration 1");
					Sample pos = D.getX();
					Sample neg = D.getY();
					System.out.println("I+ size : "+pos.size());
					System.out.println("I- size : "+neg.size());
					pos = generator.map(pos);
					
					Sample dataMerge = merge(neg.clone(), pos.clone());
					LSONIO lsonio = new LSONIO(dataMerge, actions, pred);
					long startTime = System.currentTimeMillis();
					Domain i = lsonio.learn();
					long endTime = System.currentTimeMillis();
					float time = (float)(endTime-startTime)/1000;
					System.out.println("Time : "+time);
					Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> p =
							i.decode();
					DomainLearning learningModule = new DomainLearning(
							pred,
							actions,
							directory,
							name,
							reference,
							initialState,
							generator);
					learningModule.setTypes(sim.getHierarchy());
					String file = directory+"/domain."
							+((int)thresh)+"."+((int)noise)+"."
							+seed+"."+1+".pddl";
					BufferedWriter bw =new BufferedWriter(new FileWriter(file));
					bw.write(learningModule.generation(
							p.getX(), p.getY()));
					bw.close();
					IncrementalLSONIO.test(
							reference, initialState, file,
							generatorTest, actions, testSet);
					List<Trace> examples = new ArrayList<>();
					examples.addAll(pos.getExamples());
					Observation initial = ((ObservedExample)pos.getExamples()
							.get(0)).getInitialState();
					float fscore = i.fscore(initial, testSet.getX(), testSet.getY());
					System.out.println("FSCORE : "+fscore);
					for(int t = 2; t<= Argument.getT(); t++) {
						System.out.println("Iteration "+t);
						Pair<Sample, Sample> Dnew = generator.generate(1, minLearn, maxLearn);
						Sample posNew = Dnew.getX();
						Sample negNew = Dnew.getY();
						posNew = generator.map(posNew);
						pos.add(posNew);
						neg.add(negNew);
						System.out.println("I+ size : "+pos.size());
						System.out.println("I- size : "+neg.size());
						dataMerge = merge(neg.clone(), pos.clone());
						lsonio = new LSONIO(dataMerge, actions, pred);
						startTime = System.currentTimeMillis();
						i = lsonio.learn();
						endTime = System.currentTimeMillis();
						time = (float)(endTime-startTime)/1000;
						System.out.println("Time : "+time);
						p = i.decode();
						learningModule = new DomainLearning(
								pred,
								actions,
								directory,
								name,
								reference,
								initialState,
								generator);
						learningModule.setTypes(sim.getHierarchy());
						file = directory+"/domain."
								+((int)thresh)+"."+((int)noise)+"."
								+seed+"."+t+".pddl";
						bw =new BufferedWriter(new FileWriter(file));
						bw.write(learningModule.generation(
								p.getX(), p.getY()));
						bw.close();
						IncrementalLSONIO.test(
								reference, initialState, file,
								generatorTest, actions, testSet);
						examples = new ArrayList<>();
						examples.addAll(pos.getExamples());
						initial = ((ObservedExample)pos.getExamples()
								.get(0)).getInitialState();
						fscore = i.fscore(initial, testSet.getX(), testSet.getY());
						System.out.println("FSCORE : "+fscore);
					}
				}
			}
		}
	}
}
