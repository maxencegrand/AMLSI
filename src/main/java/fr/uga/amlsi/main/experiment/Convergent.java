/**
 *
 */
package fr.uga.amlsi.main.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import fr.uga.amlsi.fsm.DFA;
import fr.uga.amlsi.learning.AutomataLearning;
import fr.uga.amlsi.learning.DomainLearning;
import fr.uga.amlsi.learning.LocalSearch;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.Mapping;
import fr.uga.amlsi.main.Argument;
import fr.uga.amlsi.main.Properties;

/**
 * Main class for convergent AMLSI
 * @author Maxence Grand
 *
 */
public class Convergent{
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
	/**
	 * The number of generation
	 */
	public static final int T = 100;
	/**
	 * The pseudo random number generator used by our example generator
	 */
	private static Random random ;

    /**
     *
     * Run
     */
	public static void run() throws BlocException, CloneNotSupportedException, IOException {
		int minLearn = Argument.getMin();
		int maxLearn = Argument.getMax();
		long[] seeds = Properties.getSeeds();

		String directory = Argument.getDirectory();
		String reference = Argument.getDomain();
		String initialState = Argument.getProblem();
		String name = Argument.getName();

		int T = Argument.getT();
		int Criterion = Argument.getCriterion();

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
		System.out.println("criterion="+Argument.getCriterion());
		System.out.println(reference);

		for(int seed = 0; seed < Argument.getRun() ; seed++) {
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

			for(float thresh : Properties.getPartial()) {
				System.out.println("############################################"
						+ "\n### Fluent = "+(thresh)+"% ###");
				/*
				 * Set the level of observable fluents
				 */
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);
					System.out.println("\n*** Noise = "+(noise)+"% ***");

					String domainName = 
							directory+"/domain."+((int)thresh)+"."+((int)noise)+"."+seed;

					/*
					 * Pseudo random number generator for test set generation
					 */
					random = new Random(seeds[seed]);
					/*
					 * Example generator for learning samples
					 */
					Generator generator = new Generator(sim,random);
					LocalSearch tabu = new LocalSearch(actions, pred);
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
					long startTime = System.currentTimeMillis(), endTime=0;
					//First iteration
					//System.out.println("Iteration 1");
					Pair<Sample, Sample> samples = generator.generate
							(1, minLearn, maxLearn);
					Sample pos = samples.getX();
					Sample neg = samples.getY();
					//System.out.println("Iteration 1");


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
					pos = generator.map(pos);
					//Step 2: PDDL Generation
					//Step 2.1: Mapping
					Mapping mapAnte = Mapping.getMappingAnte(
							pos,
							A,
							actions,
							pred);
					Mapping mapPost = Mapping.getMappingPost(
							pos,
							A,
							actions,
							pred);
					//Step 2.2: Operator Generation
					Domain initialPDDL = learningModule.generatePDDLOperator(
							A, mapAnte, mapPost);
					//System.out.println("Fscore automaton "+learner.test(A));

					//Step 3: Refinement
					//Step 3.1: First prec/post refinement
					Domain refinedPDDL = learningModule.refineOperator(
							initialPDDL.clone(), A, mapAnte, mapPost);
					//Step 3.2: Tabu refinement
					Domain currentPDDL =  refinedPDDL.clone();
					float previous = tabu.fitness(
							currentPDDL,  pos, neg, 
							generator.getCompressedNegativeExample(),initialState);
					float current = previous;
					boolean b = false;
					List<Domain> tabou = new ArrayList<>();
					do {
						if(b) {
							previous=current;
							currentPDDL = learningModule.refineOperator(
									currentPDDL, A, mapAnte, mapPost);
						}
						int sizeT = 100;
						Observation initial = ((ObservedExample) pos.getExamples().get(0)).getInitialState();
						if(currentPDDL.acceptAll(initial, pos) &&
								currentPDDL.rejectAll(initial, neg)) {
							sizeT=3;
						} else {
							float p = currentPDDL.rateOfAccepted(initial, pos);
							p *= (1-currentPDDL.rateOfAccepted(initial, neg));
							sizeT *= (1-p);
						}
						sizeT = sizeT<=3 ? 3 : sizeT;
						currentPDDL = tabu.tabu(
								currentPDDL,
								pos,
								neg,
								generator.getCompressedNegativeExample(),
								initialState,
								sizeT,
								200,
								tabou);
						b = true;
						current = tabu.fitness(
								currentPDDL,  pos, neg, 
								generator.getCompressedNegativeExample(),initialState);
					}while(current > previous);
					//Iteration 2:T
					int criterion = 0;
					for(int t = 2; t <= T; t++ ) {
						
						//Generate new data
						//Update I+ nand I-
						Sample previousPos = pos.clone();
						Sample previousNeg = neg.clone();
						samples = generator.generate(
								1,minLearn,maxLearn);
						//Update R
						//Delete obsolete rules
						for(Trace x : samples.getX().getExamples()) {
							learner.removeRules((Example)x);
						}

						//Update A
						//Decompose new positive examples
						Sample tmp = new Sample();
						for(Trace xPos : samples.getX().getExamples()) {
							xPos = generator.map((Example)xPos);
							previousPos.addExample((Example)xPos);
							tmp.addExample((Example)xPos);
						}
						tmp = learner.decompose(tmp);
						A = learner.RPNI2(previousPos, previousNeg, tmp, samples.getY(), A, A.getPartition());

						pos = previousPos;
						neg = previousNeg;

						//Update mapping
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

						//Backtrack
						Domain previousPDDL = currentPDDL.clone();

						Domain tmpPDDL = learningModule.generatePDDLOperator(A, mapAnte, mapPost);
						tmpPDDL = learningModule.refineOperator(tmpPDDL, A, mapAnte, mapPost);

						currentPDDL.backtrackUncompatibleEffects(
								A, mapAnte, mapPost);
						currentPDDL.backtrackUncompatiblePreconditions2(
								A, mapAnte, mapPost);
						currentPDDL.backtrackNegativeEffects();
						Observation initial = ((ObservedExample) pos.getExamples().get(0)).getInitialState();;//generator.getInitialState().clone();
						do {
							while(! currentPDDL.acceptAll(initial, pos)) {
								currentPDDL.backtrackPrecondition(initial, pos);
								currentPDDL.backtrackNegativeEffects();
							};
							currentPDDL.backtrackEffects(initial, pos, neg);
						}while(! currentPDDL.acceptAll(initial, pos));
						currentPDDL = currentPDDL.merge(tmpPDDL);

						//Update D
						previous = tabu.fitness(
								currentPDDL, pos, neg,
								generator.getCompressedNegativeExample(), initialState);
						current = previous;
						tabou = new ArrayList<>();
						do {
							previous=current;
							currentPDDL = learningModule.refineOperator(
									currentPDDL, A, mapAnte, mapPost);
							int sizeT = 100;
							if(currentPDDL.acceptAll(initial, pos) &&
									currentPDDL.rejectAll(initial, neg)) {
								sizeT=3;
							} else {
								float p = currentPDDL.rateOfAccepted(initial, pos);
								p *= (1-currentPDDL.rateOfAccepted(initial, neg));
								sizeT *= (1-p);
							}
							sizeT = sizeT<=3 ? 3 : sizeT;
							currentPDDL = tabu.tabu(
									currentPDDL,
									pos,
									neg,
									generator.getCompressedNegativeExample(),
									initialState,
									sizeT,
									50,
									tabou);
							current = tabu.fitness(
									currentPDDL, pos, neg, 
									generator.getCompressedNegativeExample(),initialState);
						}while(current > previous);
						if(currentPDDL.acceptAll(initial, pos) &&
								currentPDDL.rejectAll(initial, neg)) {
							/*System.out.println(previousPDDL);
							System.out.println(currentPDDL);
							System.out.println(previousPDDL.equals(currentPDDL));*/
							if(previousPDDL.equals(currentPDDL)) {
								criterion++;
							}else {
								criterion=1;
							}
						} else {
							criterion=0;
						}
						if(t == T || criterion >= Criterion) {
							System.out.println("Iteration "+t);
							System.out.println("Size I+ "+pos.size());
							System.out.println("Size I- "+neg.size());
							endTime= System.currentTimeMillis();
							float time = (float)(endTime-startTime)/1000;
							System.out.println("Time : "+time);
							Convergent.test(
									currentPDDL,testSet.getX(), testSet.getY(),
									reference,
									Argument.isHasSpecificTestInit() ?
											Argument.getSpecificTestInit() :
												initialState, domainName+".pddl",
									learningModule, actions, generatorTest);
							float fscore = currentPDDL.fscore(initial, testSet.getX(), testSet.getY());
							System.out.println("FSCORE : "+fscore);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Test the learnt domain
	 * @param i The action model
	 * @param posTest The positive test sample
	 * @param negTest The negative test sample
	 * @param ref The reference domain
	 * @param s0 The initial state
	 * @param domainName The domain name
	 * @param learningModule The action model learner
	 * @param actions The action set
	 * @param generatorTest The generator for test samples
	 * @throws IOException
	 */
	private static void test (
			Domain i, Sample posTest, Sample negTest, String ref,
			String s0, String domainName, DomainLearning learningModule,
			List<Symbol> actions, Generator generatorTest)
					throws IOException {
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(domainName));
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = i.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		bw.write(learningModule.generation(
				preconditions, postconditions));
		bw.close();
		try {
			TestMetrics.test(ref, s0, domainName,generatorTest,actions,new Pair<>(posTest, negTest));
		} catch (BlocException e) {
			// TODO Auto-generated catch block
			System.exit(1);
			e.printStackTrace();
		}
	}
}
