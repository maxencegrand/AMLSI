package main.experiment.adl;

import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import fsm.Symbol;
import fsm.Trace;
import fsm.Sample;
import fsm.Example;
import fsm.DFA;
import fsm.Pair;
import java.util.Random;
import learning.AutomataLearning;
import learning.DomainLearning;
import learning.Generator;
import learning.LocalSearch;
import learning.Domain;
import learning.Mapping;
import learning.Observation;
import learning.ObservedExample;
import learning.ADL.AdlDomain;
import learning.ADL.AdlDomainLearner;
import learning.ADL.AdlGenerator;
import main.Argument;
import main.Properties;
import main.experiment.TestMetrics;
import simulator.Simulator;
import simulator.StripsSimulator;

/**
 * Main class for AMLSI
 *
 * @author Maxence Grand
 *
 */
public class AAMLSI {
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
	public static final int nbLearn = 5;
	

    /**
     *
     * Run
     */
	public static void run () throws Exception{
		int minLearn = Argument.getMin();
		int maxLearn = Argument.getMax();
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
			Generator generator = new AdlGenerator(sim,random);

			Generator generatorTest = new Generator(sim,random);
			Pair<Sample, Sample> testSet = generatorTest.
					generate (nbTest, 1, sizeTest);


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
				System.out.println("### Fluent = "+(thresh)+"% ###");
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);

					System.out.println("\n*** Noise = "+(noise)+"% ***");
					
					AdlDomainLearner learningModule = new AdlDomainLearner(
							pred,
							actions,
							directory,
							name,
							reference,
							initialState,
							generator);
					LocalSearch tabu = new LocalSearch(actions, pred);
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
					switch(Argument.getType()){
					case RPNI:
						learner.emptyR();
					case RPNIPC:
						domainName = directory+"/domain."
								+((int)thresh)+"."+((int)noise)+"."
								+seed+".pddl";
						break;
					}
					long startTime = System.currentTimeMillis();
					Domain currentPDDL = null;

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
					A.writeDotFile("adl.dot");
					DFA split = learningModule.convert(A, actions, pred);
					split.writeDotFile("strips.dot");
					Sample newPos = learningModule.convertPostive(pos, A, split);
					//Step 2: PDDL Generation

					//Step 2: PDDL Generation
					System.out.println("STRIPS Generation");
					//Step 2.1: Mapping
					newPos = generator.map(newPos);
//					newPos.getExamples().forEach(e -> System.out.println(e));
					Mapping mapAnte = null, mapPost=null;
					mapAnte = Mapping.getMappingAnte(
							newPos,
							split,
							learningModule.getNewActions(),
							pred);
					mapPost = Mapping.getMappingPost(
							newPos,
							split,
							learningModule.getNewActions(),
							pred);
					Observation initial = ((ObservedExample) newPos.getExamples().get(0)).getInitialState();
					
//					//Step 2.2: Operator Generation
					currentPDDL = learningModule.generatePDDLOperator(
							split, mapAnte, mapPost);
					
					
					if(Argument.isRefinement()) {
						System.out.println("STRIPS Refinment");
						//Step 3: Refinement
						//Step 3.1: First prec/post refinement
						currentPDDL = learningModule.refineOperator(
								currentPDDL.clone(), split, mapAnte, mapPost);
//						if(! Argument.isWithoutTabu()) {
//							//Step 3.2: Tabu refinement
//							currentPDDL =  currentPDDL.clone();
//							float previous = tabu.fitness(
//									currentPDDL, pos, neg, 
//									generator.getCompressedNegativeExample(),initialState);
//							float current = previous;
//							boolean b = false;
//							List<Domain> tabou = new ArrayList<>();
//							do {
//								if(b) {
//									currentPDDL = learningModule.refineOperator(
//											currentPDDL, A, mapAnte, mapPost);
//								}
//								int sizeT = 100;
//								if(currentPDDL.acceptAll(initial, pos) &&
//										currentPDDL.rejectAll(initial, neg)) {
//									sizeT=3;
//								} else {
//									float p = currentPDDL.rateOfAccepted(initial, pos);
//									p *= (1-currentPDDL.rateOfAccepted(initial, neg));
//									sizeT *= (1-p);
//								}
//								currentPDDL = tabu.tabu(
//										currentPDDL,
//										pos,
//										neg,
//										generator.getCompressedNegativeExample(),
//										initialState,
//										sizeT,
//										50,
//										tabou);
//								b = true;
//								previous = current;
//								current = tabu.fitness(
//										currentPDDL, pos, neg, 
//										generator.getCompressedNegativeExample(),initialState);
//							}while(current > previous);
//						}
					}
//					AdlDomain adl = new AdlDomain(currentPDDL);
//					System.out.println("//////////////// Initialization ///////////////");
//					System.out.println(adl);
//					adl.step1();
//					System.out.println("/////////////////// Step1 //////////////////");
//					System.out.println(adl);
//					System.out.println("/////////////////// Step2 //////////////////");
//					adl.step2();
//					adl.lift();
//					System.out.println(adl);
//					System.out.println("/////////////////// Step3 //////////////////");
//					adl.step3(currentPDDL);
//					System.out.println(adl);
//					System.out.println("/////////////////// Merge //////////////////");
//					adl = adl.merge();
//					System.out.println(adl);
//					System.out.println("/////////////////// Simplify precondition //////////////////");
//					adl.merge();
//					System.out.println(adl);
//					System.out.println("/////////////////// Simplify effect //////////////////");
//					adl.merge();
//					System.out.println(adl);
//					//Step 4: Write PDDL domain and Dot automaton
					//Step 4.1: Write PDDL domain
//					BufferedWriter bw = new BufferedWriter(
//							new FileWriter(domainName));
//					bw.write(currentPDDL.generatePDDL(Argument.getName(), sim.getHierarchy()));
//					bw.close();
//
//					long endTime = System.currentTimeMillis();
//					float time = (float)(endTime-startTime)/1000;
//					System.out.println("Time : "+time);
					System.out.println("Fscore automaton "+learner.test(A));
//					TestMetrics.test(reference, initialState, domainName,generatorTest,actions,testSet);
//					float fscore = currentPDDL.fscore(initial, pos, neg);
//					System.out.println("FSCORE : "+fscore);
					
					System.exit(1);
					
				}
			}
		}
	}
}
