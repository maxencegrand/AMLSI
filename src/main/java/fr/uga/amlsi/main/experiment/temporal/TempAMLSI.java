package fr.uga.amlsi.main.experiment.temporal;

import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import fr.uga.amlsi.fsm.DFA;
import fr.uga.amlsi.fsm.Partition;
import java.util.Random;
import java.util.Scanner;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.generator.temporal.TemporalGenerator;
import fr.uga.generator.simulator.Simulator;
import fr.uga.generator.simulator.StripsSimulator;
import fr.uga.generator.simulator.temporal.TemporalBlackBox;
import fr.uga.generator.simulator.temporal.TemporalExample;
import fr.uga.generator.simulator.temporal.TemporalOracle;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fr.uga.amlsi.learning.AutomataLearning;
import fr.uga.amlsi.learning.LocalSearch;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.Mapping;
import fr.uga.amlsi.main.Argument;
import fr.uga.amlsi.main.Properties;
import fr.uga.amlsi.main.experiment.TestMetrics;
import fr.uga.amlsi.learning.temporal.TemporalDomainLearning;
import fr.uga.amlsi.learning.temporal.TemporalLocalSearch;
import fr.uga.amlsi.learning.temporal.TemporalDomain;
import fr.uga.amlsi.learning.temporal.translator.Translator;

/**
 * Main class for Temporal AMLSI
 *
 * @author Maxence Grand
 *
 */
public class TempAMLSI {
	/**
	 * Number of positive test examples
	 */
	public static final int nbTest = 100;
	/**
	 * Size of positive test examples
	 */
	public static final int sizeTest = 30;
	/**
	 * Number of positive learning examples
	 */
	public static final int nbLearn = 30;
	

    /**
     *
     * Run
     */
	public static void run () throws Exception{
		int minLearn = Argument.getMin();
		int maxLearn = Argument.getMax();
		int T = Argument.getT();
		long[] seeds = Properties.getSeeds();

		String directory = Argument.getDirectory();
		String reference = Argument.getDomain();
		String initialState = Argument.getProblem();
		String name = Argument.getName();

		TemporalOracle tempSim = new TemporalBlackBox(reference, initialState);
		
		/*
		 * All actions
		 */
		List<Symbol> tempActions = tempSim.getAllActions();
		
		/*
		 * All predicates
		 */
		List<Symbol> tempPred = new ArrayList<>();
		for(Symbol a : tempSim.getAllPredicates()){
			tempPred.add(a);
		}
		for(Symbol a : tempSim.getPositiveStaticPredicate()){
			tempPred.add(a);
		}
		
		File file_temporal = new File(reference);
		if(Argument.isTwoOp()) {
			Translator.translate2Op(file_temporal, 
					"classical_"+Argument.getName()+".pddl");
		} else {
			Translator.translate(file_temporal, 
					"classical_"+Argument.getName()+".pddl");
		}
		
		System.out.println("# actions "+tempActions.size());
		System.out.println("# predicate "+tempPred.size());

		System.out.println("Initial state : "+initialState);
		System.out.println(reference);
		for(int seed = 0; seed < Argument.getRun() ; seed++) {
			System.out.println("\n"+"Run : "+seed);
			tempSim = new TemporalBlackBox(reference, initialState);
			Random random = new Random();
			random.setSeed(seeds[seed]);
			Random random2 = new Random();
			random2.setSeed(seeds[seed]);
			TemporalGenerator generator = new TemporalGenerator(tempSim,random);

			TemporalGenerator generatorTest = new TemporalGenerator
					(tempSim,random);
			Pair<Sample, Sample> testSet = generatorTest.generate(
					minLearn, sizeTest,	100, nbTest);
			//Pair<Sample, Sample> testSet = new Pair<>(new Sample(), new Sample());
			Simulator sim = new StripsSimulator("classical_"+Argument.getName()+".pddl",
					initialState);
			
			/*
			 * All actions
			 */
			List<Symbol> actions = tempSim.getSeqActions();
			/*
			 * All predicates
			 */
			List<Symbol> pred = new ArrayList<>();
			for(Symbol a : tempSim.getAllPredicates()){
				pred.add(a);
			}
			for(Symbol a : tempSim.getPositiveStaticPredicate()){
				pred.add(a);
			}
			
			Map<String, Float> durationString = 
					Translator.getDurations(file_temporal);
			Map<Symbol, Float> duration = new HashMap<>();
			for(Symbol action : tempSim.getAllActions()) {
				if(!duration.containsKey(action.generalize())) {
					duration.put(action.generalize(), durationString.get(
							action.getName()));
				}
			}
			
			Generator generatorSeq = new Generator(sim,random);
			
			//Generate learning set
			Pair<Sample, Sample> D = generator.generate(minLearn, maxLearn,	100, T);
			Sample pos = D.getX();
			Sample neg = D.getY();
			
			
			System.out.println("I+ size : "+pos.size());
			System.out.println("I- size : "+neg.size());
			int x_=0;
			for(TemporalExample ex : generator.getPositive()) {
				x_ += ex.size();
			};
			float meanPos = (float)x_/generator.getPositive().size();
			x_=0;
			for(TemporalExample ex : generator.getNegative()) {
				x_ += ex.size();
			};
			float meanNeg = (float)x_/generator.getNegative().size();
			System.out.println("x+ mean size : "+meanPos);
			System.out.println("x- mean size : "+meanNeg);
			System.out.println("E+ size : "+testSet.getX().size());
			System.out.println("E- size : "+testSet.getY().size());
			x_=0;
			for(TemporalExample ex : generatorTest.getPositive()) {
				x_ += ex.size();
			};
			meanPos = (float)x_/generatorTest.getPositive().size();
			x_=0;
			for(TemporalExample ex : generatorTest.getNegative()) {
				x_ += ex.size();
			};
			meanNeg = (float)x_/generatorTest.getNegative().size();
			System.out.println("e+ mean size : "+meanPos);
			System.out.println("e- mean size : "+meanNeg);

			for(Symbol a : TemporalDomainLearning.getAllActions(pos)) {
				if(!actions.contains(a)) {
					actions.add(a);
				}
			}
			for(Symbol a : TemporalDomainLearning.getAllActions(neg)) {
				if(!actions.contains(a)) {
					actions.add(a);
				}
			}
			TemporalDomainLearning learningModule = new TemporalDomainLearning(
					pred,
					actions,
					directory,
					name,
					reference,
					initialState,
					generatorSeq);
			LocalSearch tabu = new TemporalLocalSearch(actions, pred, duration, tempActions, Argument.isTwoOp());
			learningModule.setSamples(testSet.getX(), testSet.getY());
			learningModule.setTypes(sim.getHierarchy());

			AutomataLearning learner;
			learner = new AutomataLearning(
					pred,
					actions,
					generatorSeq,
					learningModule);
		
			learner.setSamples(testSet.getX(), testSet.getY());
			long startTimeAut = System.currentTimeMillis();
			Sample posPrefixes = pos.clone();
			posPrefixes = learner.decompose(posPrefixes);
			for(Trace x : pos.getExamples()) {
				learner.removeRules((Example)x);
			}
					for(Symbol a : tempActions) {
				if(Argument.isTwoOp()) {
					Symbol aStart = a.clone();
					aStart.setName(aStart.getName()+"-start");
					Symbol aEnd = a.clone();
					aEnd.setName(aEnd.getName()+"-end");
					learner.removeRules(aStart, aEnd);
				} else {
					Symbol aStart = a.clone();
					aStart.setName(aStart.getName()+"-start");
					Symbol aInv = a.clone();
					aInv.setName(aInv.getName()+"-inv");
					Symbol aEnd = a.clone();
					aEnd.setName(aEnd.getName()+"-end");
					learner.removeRules(aStart, aInv);
					learner.removeRules(aInv, aEnd);
				}
			}
			//Step 1.3: Automaton learning
			DFA A = learner.RPNI(posPrefixes, neg);
			A.writeDotFile(directory+"/automaton."+seed+".dot");
			long endTimeAut = System.currentTimeMillis();
			float timeAut = (float)(endTimeAut-startTimeAut)/1000;
			System.out.println("Automaton Time : "+timeAut);
			System.out.println("Automaton Fscore : "+learner.test(A));
			learner.writeDataCompression(A, pos);
			
			for(float thresh : Properties.getPartial()) {
				System.out.println(
						"############################################");
				System.out.println("### Fluent = "+(thresh)+"% ###");
				Generator.LEVEL = ( (float) thresh / 100);
				for(float noise : Properties.getNoise()){
					Generator.THRESH = ((float)noise / 100);

					System.out.println("\n*** Noise = "+(noise)+"% ***");
					String domainName = "";
					domainName = directory+"/domain."
							+((int)thresh)+"."+((int)noise)+"."
							+seed;
					long startTime = System.currentTimeMillis();
					Domain currentPDDL = null;
					//Step 2: PDDL Generation
					//Step 2.1: Mapping
					pos = generatorSeq.map(pos, tempSim);
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
					//Step 2.2: Operator Generation
					currentPDDL = learningModule.generatePDDLOperator(
							A, mapAnte, mapPost);
					
					if(Argument.isRefinement()) {
						//Step 3: Refinement
						//Step 3.1: First prec/post refinement
						currentPDDL = learningModule.refineOperator(
								currentPDDL, A, mapAnte, mapPost, pos);
						
						if(! Argument.isWithoutTabu()) {
							//Step 3.2: Tabu refinement
							currentPDDL =  currentPDDL.clone();
							float previous = tabu.fitness(
									currentPDDL,
									pos,
									neg, 
									generator.getCompressedSequential(),
									initialState);								
							float current = previous;
							boolean b = false;
							List<Domain> tabou = new ArrayList<>();
							do {
								if(b) {
									currentPDDL = learningModule.refineOperator(
											currentPDDL, A, mapAnte, mapPost, pos);
								}
								currentPDDL = TemporalDomain.convert2(
										currentPDDL.clone(), duration, Argument.isTwoOp(), 
										pred, actions, tempActions);
								currentPDDL = tabu.tabu(
										currentPDDL,
										pos,
										neg,
										generator.getCompressedSequential(),
										initialState,
										50,
										200,
										tabou);
//								currentPDDL = tabu.tabu(
//										currentPDDL,
//										pos,
//										neg,
//										generator.getCompressedSequential(),
//										initialState,
//										50);
								b = true;
								previous = current;					
								current = tabu.fitness(
										currentPDDL,
										pos, 
										neg, 
										generator.getCompressedSequential(),
										initialState);
								//System.out.println("previous = "+previous+" current = "+current);
							}while(current > previous);
						}
						
					}

//					System.out.println(currentPDDL);
					
					//Step 4: Write PDDL domain and Dot automaton
					//Step 4.1: Write PDDL domain
					Observation initial2 = ((ObservedExample) 
							pos.getExamples().get(0)).getInitialState();
					//System.out.println(currentPDDL);
					currentPDDL = TemporalDomain.convert2(
							currentPDDL.clone(), duration, Argument.isTwoOp(), 
							pred, actions, tempActions);
					BufferedWriter bw = new BufferedWriter(
							new FileWriter(domainName+"_intermediate.pddl"));
					
					TemporalDomain tempCurrent = 
							new TemporalDomain(currentPDDL, duration,
									Argument.isTwoOp(), pred, tempActions);
					
					Pair<Map<Symbol, Observation>,Map<Symbol, Observation>> pair
														= currentPDDL.decode();
					Map<Symbol, Observation> preconditions = pair.getX();
					Map<Symbol, Observation> postconditions = pair.getY();
					bw.write(learningModule.generation(
							preconditions, postconditions));
					bw.close();
					File f = new File(domainName+"_intermediate.pddl");
					if(Argument.isTwoOp()) {
						Translator.translate2Op(f, 
								Translator.getDurations(file_temporal),
								domainName+".pddl");
					} else {

						Translator.translate(f, 
								Translator.getDurations(file_temporal), 
								domainName+".pddl");
					}
					long endTime = System.currentTimeMillis();
					float time = (float)(endTime-startTime)/1000;
					System.out.println("Time : "+time);
					f = new File(domainName+".pddl");
					Translator.translate(f, Argument.getDirectory()+"/tmp.pddl");
					Translator.translate(file_temporal, Argument.getDirectory()+"/tmp_ref.pddl");
					TempAMLSI.test(
							Argument.getDirectory()+"/tmp_ref.pddl",
							Argument.getDirectory()+"/tmp.pddl");
					float fscore = 
							tempCurrent.fscore(
									generatorTest.getPositive(), 
									generatorTest.getNegative(), 
									initial2);
//					System.out.println("Intermediate FSCORE : "+
//									currentPDDL.fscore(initial2, testSet.getX(), 
//											testSet.getY()));
					System.out.println("Temoral FSCORE : "+fscore);
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
	private static void test (String ref, String domainName)
					throws IOException {
		try {
			TestMetrics.test(ref,domainName);
		} catch (BlocException e) {
			// TODO Auto-generated catch block
			System.exit(1);
			e.printStackTrace();
		}
	}
}
