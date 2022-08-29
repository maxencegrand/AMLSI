package fr.uga.amlsi.main.experiment;

import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import fr.uga.amlsi.fsm.DFA;
import java.util.Random;

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
import fr.uga.amlsi.learning.AutomataLearning;
import fr.uga.amlsi.learning.DomainLearning;
import fr.uga.amlsi.learning.LocalSearch;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.Mapping;
import fr.uga.amlsi.main.Argument;
import fr.uga.amlsi.main.Properties;

/**
 * Main class for AMLSI
 *
 * @author Maxence Grand
 *
 */
public class AMLSI {
	/**
	 * Number of positive test examples
	 */
	public static final int nbTest = 100;
	/**
	 * Size of positive test examples
	 */
	public static final int sizeTest = 100;

    /**
     *
     * Run
     */
	public static void run () throws Exception{
		int minLearn = Argument.getMin();
		int maxLearn = Argument.getMax();
		int nbLearn = Argument.getT();
		long[] seeds = Properties.getSeeds();

		String directory = Argument.getDirectory();
		String reference = Argument.getDomain();
		String initialState = Argument.getProblem();
		String name = Argument.getName();

		Simulator sim = new StripsSimulator(reference, initialState);
		List<Symbol> actions = sim.getActions();
		List<Symbol> pred = new ArrayList<>();
		pred.addAll(sim.getPredicates());
		pred.addAll(sim.getPositiveStaticPredicates());
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
			Pair<Sample, Sample> testSet = generatorTest.generate (nbTest, 1, sizeTest);


			//Generate learning set
			Pair<Sample, Sample> D = generator.generate(nbLearn,minLearn, maxLearn);
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

			DomainLearning learningModule = new DomainLearning(
					pred,
					actions,
					directory,
					name,
					reference,
					initialState,
					generator);

			AutomataLearning learner;
			learner = new AutomataLearning(
					pred,
					actions,
					generator,
					learningModule);

			if (Argument.getType() == Argument.TYPE.RPNI) {
				learner.emptyR();
			}

			learner.setSamples(testSet.getX(), testSet.getY());
			learningModule.setSamples(testSet.getX(), testSet.getY());
			learningModule.setTypes(sim.getHierarchy());

			long startTime = System.currentTimeMillis();

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
			learner.writeDataCompression(A, pos);

			long endTime = System.currentTimeMillis();
			float time = (float)(endTime-startTime)/1000;
			System.out.println("Time Automaton : "+time);
			System.out.println("Fscore automaton "+learner.test(A));

			for(float thresh : Properties.getPartial()) {
				System.out.println("############################################");
				System.out.println("### Fluent = "+(thresh)+"% ###");
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);

					System.out.println("\n*** Noise = "+(noise)+"% ***");
					
					String domainName = "";
					domainName = directory+"/domain."+((int)thresh)+"."+((int)noise)+"."+seed+".pddl";

					startTime = System.currentTimeMillis();
					Domain currentPDDL = null;



					//Step 2: PDDL Generation
					//Step 2: PDDL Generation
					System.out.println("STRIPS Generation");
					//Step 2.1: Mapping
					pos = generator.map(D.getX().clone());
					Mapping mapAnte = null, mapPost=null;
					mapAnte = Mapping.getMappingAnte(
							pos,
							A,
							actions,
							pred);
					mapPost = Mapping.getMappingPost(
							pos,
							A,
							actions,
							pred);
					Observation initial = ((ObservedExample) pos.getExamples().get(0)).getInitialState();
					
					//Step 2.2: Operator Generation
					currentPDDL = learningModule.generatePDDLOperator(A, mapAnte, mapPost);
					
					if(Argument.isRefinement()) {
						System.out.println("STRIPS Refinment");
						//Step 3: Refinement
						//Step 3.1: First prec/post refinement
						currentPDDL = learningModule.refineOperator(
								currentPDDL.clone(), A, mapAnte, mapPost);
						if(! Argument.isWithoutTabu()) {
							LocalSearch tabu = new LocalSearch(actions, pred);


							//Step 3.2: Tabu refinement
							currentPDDL =  currentPDDL.clone();
							float previous = tabu.fitness(
									currentPDDL, pos, neg, 
									generator.getCompressedNegativeExample(),initialState);
							float current = previous;
							boolean b = false;
							List<Domain> tabou = new ArrayList<>();
							do {
								if(b) {
									currentPDDL = learningModule.refineOperator(
											currentPDDL, A, mapAnte, mapPost);
								}
								int sizeT = 100;
								if(currentPDDL.acceptAll(initial, pos) &&
										currentPDDL.rejectAll(initial, neg)) {
									sizeT=3;
								} else {
									float p = currentPDDL.rateOfAccepted(initial, pos);
									p *= (1-currentPDDL.rateOfAccepted(initial, neg));
									sizeT *= (1-p);
								}
								sizeT = sizeT==0 ? 3 : sizeT;
								currentPDDL = tabu.tabu(
										currentPDDL,
										pos,
										neg,
										generator.getCompressedNegativeExample(),
										initialState,
										sizeT,
										50,
										tabou);
								b = true;
								previous = current;
								current = tabu.fitness(
										currentPDDL, pos, neg, 
										generator.getCompressedNegativeExample(),initialState);
							}while(current > previous);
						}
					}
					
					//Step 4: Write PDDL domain and Dot automaton
					//Step 4.1: Write PDDL domain
					BufferedWriter bw = new BufferedWriter(
							new FileWriter(domainName));
					bw.write(currentPDDL.generatePDDL(Argument.getName(), sim.getHierarchy()));
					bw.close();

					endTime = System.currentTimeMillis();
					time = (float)(endTime-startTime)/1000;
					System.out.println("Time Domain : "+time);
					TestMetrics.test(reference, initialState, domainName,generatorTest,actions,testSet);
					float fscore = currentPDDL.fscore(initial, pos, neg);
					System.out.println("FSCORE : "+fscore);
				}
			}
		}
	}
}
