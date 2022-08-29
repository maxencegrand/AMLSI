/**
 * 
 */
package main.experiment.hierarchical;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.generator.hierarchical.HierarchicalGenerator;
import fr.uga.generator.simulator.hierarchical.HierarchicalSimulator;
import fr.uga.generator.simulator.hierarchical.HtnSimulator;
import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.Task;
import fr.uga.generator.symbols.trace.CompressedHierarchical;
import fr.uga.generator.symbols.trace.CompressedNegativeExample;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.RandomTaskTrace;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fsm.DFA;
import learning.AutomataLearning;
import learning.Domain;
import learning.LocalSearch;
import learning.Mapping;
import learning.hierarchical.HierarchicalLocalSearch;
import learning.hierarchical.HtnDomain;
import learning.hierarchical.HtnLearner;
import main.Argument;
import main.Properties;

/**
 * @author Maxence Grand
 *
 */
public class SepLearning {
	/**
	 * The  number of positive test  examples
	 */
	public static final int nbTest = 100;
	/**
	 * The maximal size of positive test examples
	 */
	public static final int sizeTest = 50;
	/**
	 * The number of generated example at each iterations
	 */
	public static final int nbLearn = 30;
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
		String name = Argument.getName();
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
			HierarchicalGenerator generatorTest = 
					new HierarchicalGenerator(sim,randomTest);
			/*
			 * Generate test set
			 */
			Pair<Sample, Sample> testSet = generatorTest.generate(100, 1, 50);

//
			/*
			 * For logs
			 */
			System.out.println("E+ size : "+testSet.getX().size());
			System.out.println("E- size : "+testSet.getY().size());
			System.out.println("e+ mean size : "+testSet.getX().meanSize());
			System.out.println("e- mean size : "+testSet.getY().meanSize());

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
			learningModule.setTypes(sim.getHierarchy());

			String methodAutomaton = "";
			String taskAutomaton = "";
			switch(Argument.getType()){
			case RPNI:
				learner.emptyR();
			case RPNIPC:
				methodAutomaton = directory+"/method."+seed+".dot";
				taskAutomaton = directory+"/method."+seed+".dot";
				break;
			}
			long startTime=0, endTime=0;
			
			Pair<Sample, Sample> samples = 
					generator.generate(
							Argument.getT(),
							Argument.getMin(),
							Argument.getMax());
			
			Sample pos = samples.getX();
			Sample neg = samples.getY();
			Sample negNoTask = new Sample();
			neg.getExamples().forEach(ex -> {
				int idx = ex.getActionSequences().size()-1;
				Symbol s = ex.getActionSequences().get(idx);
				if(!(s instanceof Task)) {
					negNoTask.addExample(ex);
				}
			});
//			System.out.println(neg);
			for(Trace x : pos.getExamples()) {
				learner.removeRules((Example)x);
			}
			
			Sample posPrefixes = learner.decompose(pos);
			
			System.out.println("I+ size : "+pos.size());
			System.out.println("I- size : "+neg.size());
			System.out.println("I- (no task) size : "+negNoTask.size());
			System.out.println("I+ mean size : "+pos.meanSize());
			System.out.println("I- mean size : "+neg.meanSize());
			System.out.println("I- (no task) mean size : "+negNoTask.meanSize());
			
			startTime = System.currentTimeMillis();
			DFA A = learner.RPNI(posPrefixes, negNoTask);
			endTime = System.currentTimeMillis();
			
			System.out.println("Time Automaton : "+((float)(endTime-startTime)/1000));
			
			for(float thresh : Properties.getPartial()) {
				System.out.println(
						"############################################");
				System.out.println("### Fluent = "+(thresh)+"% ###");
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);

					System.out.println("\n*** Noise = "+(noise)+"% ***");

					String domainName = directory+"/domain."
							+((int)thresh)+"."+((int)noise)+"."
							+seed;
					
					
					startTime = System.currentTimeMillis();
					List<Method> methods = learningModule.methodLearning(
							pos,
							negNoTask, 
							generator.getRamdomTaskTraces(),
							A,
							Argument.getRec_type());
					
					//Create initial domain
					List<Symbol> allActions = new ArrayList<>();
					allActions.addAll(actions);
					allActions.addAll(A.getMethods());
					A.writeDotFile(methodAutomaton);
					HtnDomain domain = new HtnDomain(
							pred, allActions, 
							new HashMap<>(), 
							new HashMap<>());
					//domain.extractPreconditionsEffects(reference);
					
					//Map data
					List<RandomTaskTrace> ramdomTaskTraces = 
							learningModule.getMethodTrace(
									A, 
									generator.getRamdomTaskTraces());
					Sample posObserved = new Sample();
					for(RandomTaskTrace rt : ramdomTaskTraces) {
						ObservedExample seq = 
								(ObservedExample) generator.map(rt.getExample());
						ObservedExample task = 
								rt.getObservedTask(seq.getObservations());
						posObserved.addExample(seq);
						posObserved.addExample(task);
					}
					learningModule.setMethods(methods);
					List<Symbol> allActions2 = new ArrayList<>();
					allActions2.addAll(actions);
					allActions2.addAll(A.getMethods());
					Mapping ante = null, post=null;
					ante = Mapping.getMappingAnte(
							posObserved,
							A,
							allActions2,
							pred);
					post = Mapping.getMappingPost(
							posObserved,
							A,
							allActions2,
							pred);
					Observation initial = ((ObservedExample)posObserved.getExamples().get(0)).getInitialState();
					
					//Precondition and effect learn
					System.out.println("Learn primitive Tasks");
					Domain primitive = learningModule.generatePDDLOperator(A, ante, post);
					
					
					//Refinement Primitive task
					System.out.println(posObserved.size());
					Sample posOnlyPrim = new Sample();
					posObserved.getExamples().forEach(t -> {
						if(isOnlyPrimitive(t)) {
							posOnlyPrim.addExample(t);
						}
					});
					LocalSearch tabu = new LocalSearch(actions, pred);
					primitive = learningModule.refineOperator(
							primitive.clone(), A, ante, post);
					if(! Argument.isWithoutTabu()) {
						//Step 3.2: Tabu refinement
						primitive =  primitive.clone();
						float previous = tabu.fitness(
								primitive, posOnlyPrim, negNoTask, 
								generator.getCompressedNegativeExample(),initialState);
						float current = previous;
						boolean b = false;
						List<Domain> tabou = new ArrayList<>();
						do {
							if(b) {
								primitive = learningModule.refineOperator(
										primitive, A, ante, post);
							}
							int sizeT = 100;
							if(primitive.acceptAll(initial, posOnlyPrim) &&
									primitive.rejectAll(initial, negNoTask)) {
								sizeT=3;
							} else {
								float p = primitive.rateOfAccepted(initial, posOnlyPrim);
								p *= (1-primitive.rateOfAccepted(initial, negNoTask));
								sizeT *= (1-p);
							}
							sizeT = sizeT==0 ? 3 : sizeT;
							primitive = tabu.tabu(
									primitive,
									posOnlyPrim,
									negNoTask,
									generator.getCompressedNegativeExample(),
									initialState,
									sizeT,
									200,
									tabou);
							b = true;
							previous = current;
							current = tabu.fitness(
									primitive, posOnlyPrim, negNoTask, 
									generator.getCompressedNegativeExample(),initialState);
						}while(current > previous);
					}
					
					//Methods Learning
					System.out.println("Learn Methods");
					Domain methods_ = learningModule.generateHDDLOperator(A, ante, post);
					domain.mergeMethods(methods_);
					domain.mergeActions(primitive);
					//Refinement Methods
					tabu = new HierarchicalLocalSearch(allActions, pred);
					Sample negMethod = new Sample();
					for(Trace t : neg.getExamples()) {
						Symbol op = t.getActionSequences().get(t.size()-1);
						//System.out.println(t+" "+op+" "+(op instanceof Task));
						if(op instanceof Task) {
							for(Symbol m : A.getMethods()) {
								if(!((Method)m).getToDecompose().equals(op)) {
									//System.out.println(m);
									continue;
								}
								List<Symbol> l = new ArrayList<>();
								for(int i = 0; i < t.size()-1; i++) {
									l.add(t.getActionSequences().get(i));
								}
								l.add(m);
								negMethod.addExample(new Example(l));
							}
						} else {
							negMethod.addExample(t);
						}
					}
					
					List<CompressedNegativeExample> compressed = new ArrayList<>();
					for(RandomTaskTrace rt : ramdomTaskTraces) {
						compressed.add(new CompressedHierarchical(
								rt, 
								A.getMethods(),
								negMethod));
					}
					
//					System.out.println(posObserved);
					float previous = tabu.fitness(
							domain,
							posObserved, 
							negMethod, 
							compressed,
							initialState);
					float current = previous;
					boolean b = false;
					List<Domain> tabou = new ArrayList<>();
					if(!Argument.isWithoutTabu()) {
						do {
							domain = (HtnDomain) tabu.tabu(
									domain,
									posObserved,
									negMethod,
									compressed,
									initialState,
									10,
									2000,
									tabou);
							b = true;
							previous = current;
							current = tabu.fitness(
									domain,
									posObserved,
									negMethod, 
									compressed,
									initialState);
						}while(current > previous);
					}
					endTime = System.currentTimeMillis();
					System.out.println("Time Domain Learning : "+((float)(endTime-startTime)/1000));
					
					BufferedWriter bw = new BufferedWriter(
							new FileWriter(domainName+".hddl"));
					bw.write(domain.generateHDDL(
							Argument.getName(), 
							sim.getHierarchy(), 
							methods,
							sim.getTasks()));
					bw.close();
					bw = new BufferedWriter(
							new FileWriter(domainName+".pddl"));
					bw.write(domain.generatePDDL(
							Argument.getName(), 
							sim.getHierarchy()));
					bw.close();
					float recall = Measure.correction(
							generatorTest.getDecompositionTraces(),
							domainName+".hddl", 
							initialState);
					float precision = Measure.precision(
							generatorTest.negTask(), 
							domainName+".hddl", 
							initialState);
					float fscore = learning.Measure.FScore(recall, precision);
					System.out.println("FScore : "+fscore);

				}
			}
		}
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	private static boolean isOnlyPrimitive(Trace t) {
		for(Symbol s : t.getActionSequences()) {
			if(s instanceof Task) {
				return false;
			}
			if(s instanceof Method) {
				return false;
			}
		}
		return true;
	}
}
