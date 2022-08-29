package main.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.exception.PlanException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.symbols.Action;
import fr.uga.generator.symbols.Predicate;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.TypeHierarchy;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fr.uga.pddl4j.parser.PDDLAction;
import fr.uga.pddl4j.parser.PDDLDomain;
import fr.uga.pddl4j.parser.PDDLExpression;
import fr.uga.pddl4j.parser.PDDLNamedTypedList;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.parser.PDDLTypedSymbol;
import learning.Measure;

/**
 * Metrics use to test a learnt domain
 * @author Maxence Grand
 *
 */
public class TestMetrics {

	/**
	 * Load a domain
	 * @param domain Domain's filename
	 * @return A Parser
	 * @throws PlanException
	 */
	public static PDDLParser load(String domain)
			throws PlanException{
		LogManager.shutdown();
		try{
			File d = new File(domain);
			PDDLParser parser = new PDDLParser();
			parser.parseDomain(d);
			return parser;
		} catch(IOException | NullPointerException e){
			throw new PlanException("");
		}
	}

	/**
	 * Compute the semantic distance
	 * @param ref The reference domain
	 * @param dom The domain to test
	 * @param negative if true, we take negative precondition into account
	 * @return the semantic distance
	 */
	public static float semantic_distance(String ref, String dom,
			boolean negative) {
		//Load domains
		try {
			PDDLParser reference = load(ref);
			Map<String, String> map = new HashMap<>();
			for(PDDLTypedSymbol t : reference.getDomain().getTypes()) {

				if(! t.getTypes().isEmpty()) {
					map.put(t.getImage(), t.getTypes().get(0).getImage());
				}
			}
			TypeHierarchy hier = new TypeHierarchy(map);
			Map<Symbol, Observation> domPrec = extract_precondition(reference);
			Map<Symbol, Observation> domEff = extract_postcondition(reference);
			PDDLParser toTest = load(dom);
			Map<Symbol, Observation> testPrec = extract_precondition(toTest);
			Map<Symbol, Observation> testEff = extract_postcondition(toTest);

			Map<Symbol, Map<String, String>> paramMap = standardize(domPrec.keySet(), testPrec.keySet());

			Map<Symbol, Observation> testPrec2 = new HashMap<>();
			for(Symbol act : testPrec.keySet()) {
				Observation obs = new Observation();
				for(Symbol pred : testPrec.get(act).getPredicatesSymbols()) {
					obs.addObservation(pred.generalize(paramMap.get(act)),
							testPrec.get(act).getValue(pred));
				}
				testPrec2.put(act.generalize(paramMap.get(act)), obs);
			}
			Map<Symbol, Observation> testEff2 = new HashMap<>();
			for(Symbol act : testEff.keySet()) {
				Observation obs = new Observation();
				for(Symbol pred : testEff.get(act).getPredicatesSymbols()) {
					obs.addObservation(pred.generalize(paramMap.get(act)),
							testEff.get(act).getValue(pred));
				}
				testEff2.put(act.generalize(paramMap.get(act)), obs);
			}
			Map<Symbol, List<Symbol>> lits = all_litterals(reference, hier);
			Map<Symbol, float[]> errors = new HashMap<>();
			for(Symbol act : domPrec.keySet()) {
				float [] tmp= new float[4];
				tmp[0] = count_errors(domPrec.get(act).getPositivePredicate(),
						testPrec2.get(act).getPositivePredicate());
				if(negative) {
					tmp[1] = count_errors(domPrec.get(act).getNegativePredicate(),
							testPrec2.get(act).getNegativePredicate());
				}else {
					tmp[1] = 0;
				}
				tmp[2] = count_errors(domEff.get(act).getPositivePredicate(),
						testEff2.get(act).getPositivePredicate());
				tmp[3] = count_errors(domEff.get(act).getNegativePredicate(),
						testEff2.get(act).getNegativePredicate());
				errors.put(act, tmp);
			}
			float dist = 0;
			int n = 0;
			int index = 0;
			for(Symbol act : domPrec.keySet()) {
				float tmp = 0;
				for(float e : errors.get(act)) {
					index = (index+1) % 4;
					tmp += (e / (float)lits.get(act).size());
				}
				tmp = (tmp / (negative ? 4 : 3));
				dist += tmp;
				n++;
			}
			return (dist/n);
		} catch (PlanException e) {
			return 1;
		}
	}

	/**
	 * Compute all parameters' types for a list of symbol
	 * @param p A list of symbol
	 * @return A map between parameters and their types
	 */
	private static Map<String, String> params(List<PDDLTypedSymbol> p) {
		Map<String, String> res = new LinkedHashMap<>();
		for(PDDLTypedSymbol t : p) {
			res.put(t.getImage(), t.getTypes().get(0).getImage());
		}
		return res;
	}

	/**
	 * Extract action's preconditions
	 * @param parser A parser
	 * @return Action's precondition
	 */
	private static Map<Symbol, Observation> extract_precondition
	(PDDLParser parser) {
		PDDLDomain dom = parser.getDomain();
		Map<Symbol, Observation> res = new HashMap<>();
		List<PDDLAction> ops = dom.getActions();
		for(PDDLAction op : ops) {
			Map<String, String> params = params(op.getParameters());
			Observation obs = new Observation();
			Symbol sym = new Action(op.getName().getImage(),params);
			switch(op.getPreconditions().getConnective()) {
			case AND://Multiple precondition
				if (!op.getPreconditions().getChildren().isEmpty()) {
					for (int i = 0; i < op.getPreconditions().getChildren().size(); i++) {
						switch(op.getPreconditions().getChildren().get(i).getConnective()) {
						case NOT:
							PDDLExpression exp1 = op.getPreconditions().getChildren().get(i).getChildren().get(0);
							String litName1 = exp1.getAtom().get(0).getImage();
							Map<String, String> litParam1 = new LinkedHashMap<>();
							for(int j = 1; j< exp1.getAtom().size(); j++) {
								String arg = exp1.getAtom().get(j).getImage();
								litParam1.put(arg, params.get(arg));
							}
							Symbol s1 = new Predicate(litName1, litParam1);
							obs.addFalseObservation(s1);
							break;
						default:
							PDDLExpression exp = op.getPreconditions().getChildren().get(i);
							String litName = exp.getAtom().get(0).getImage();
							Map<String, String> litParam = new LinkedHashMap<>();
							for(int j = 1; j< exp.getAtom().size(); j++) {
								String arg = exp.getAtom().get(j).getImage();
								litParam.put(arg, params.get(arg));
							}
							Symbol s = new Predicate(litName, litParam);
							obs.addTrueObservation(s);
							break;
						}
					}
				}
				res.put(sym, obs);
				break;
			case NOT://Negative precondition
				if (!op.getPreconditions().getChildren().isEmpty()) {
					for (int i = 0; i < op.getPreconditions().getChildren().size(); i++) {
						switch(op.getPreconditions().getChildren().get(i).getConnective()) {
						case NOT:
							PDDLExpression exp1 = op.getPreconditions().getChildren().get(i).getChildren().get(0);
							String litName1 = exp1.getAtom().get(0).getImage();
							Map<String, String> litParam1 = new LinkedHashMap<>();
							for(int j = 1; j< exp1.getAtom().size(); j++) {
								String arg = exp1.getAtom().get(j).getImage();
								litParam1.put(arg, params.get(arg));
							}
							Symbol s1 = new Predicate(litName1, litParam1);
							obs.addFalseObservation(s1);
							break;
						default:
							PDDLExpression exp = op.getPreconditions().getChildren().get(i);
							String litName = exp.getAtom().get(0).getImage();
							Map<String, String> litParam = new LinkedHashMap<>();
							for(int j = 1; j< exp.getAtom().size(); j++) {
								String arg = exp.getAtom().get(j).getImage();
								litParam.put(arg, params.get(arg));
							}
							Symbol s = new Predicate(litName, litParam);
							obs.addTrueObservation(s);
							break;
						}
					}
				}
				res.put(sym, obs);
				break;
			default://Only one positive precondition
				PDDLExpression exp = op.getPreconditions();
				String litName = exp.getAtom().get(0).getImage();
				Map<String, String> litParam = new LinkedHashMap<>();
				for(int j = 1; j< exp.getAtom().size(); j++) {
					String arg = exp.getAtom().get(j).getImage();
					litParam.put(arg, params.get(arg));
				}
				Symbol s = new Predicate(litName, litParam);
				obs.addTrueObservation(s);
				res.put(sym, obs);
			}
		}
		return res;
	}

	/**
	 * Extract action's effects
	 * @param parser A parser
	 * @return Action's effects
	 */
	private static Map<Symbol, Observation> extract_postcondition
	(PDDLParser parser) {
		PDDLDomain dom = parser.getDomain();
		Map<Symbol, Observation> res = new HashMap<>();
		List<PDDLAction> ops = dom.getActions();
		for(PDDLAction op : ops) {
			Map<String, String> params = params(op.getParameters());
			Observation obs = new Observation();
			Symbol sym = new Action(op.getName().getImage(),params);
			switch(op.getEffects().getConnective()) {
			case AND://Multiple precondition
				if (!op.getEffects().getChildren().isEmpty()) {
					for (int i = 0; i < op.getEffects().getChildren().size(); i++) {
						switch(op.getEffects().getChildren().get(i).getConnective()) {
						case NOT:
							PDDLExpression exp1 = op.getEffects().getChildren().get(i).getChildren().get(0);
							String litName1 = exp1.getAtom().get(0).getImage();
							Map<String, String> litParam1 = new LinkedHashMap<>();
							for(int j = 1; j< exp1.getAtom().size(); j++) {
								String arg = exp1.getAtom().get(j).getImage();
								litParam1.put(arg, params.get(arg));
							}
							Symbol s1 = new Predicate(litName1, litParam1);
							obs.addFalseObservation(s1);
							break;
						default:
							PDDLExpression exp = op.getEffects().getChildren().get(i);
							String litName = exp.getAtom().get(0).getImage();
							Map<String, String> litParam = new LinkedHashMap<>();
							for(int j = 1; j< exp.getAtom().size(); j++) {
								String arg = exp.getAtom().get(j).getImage();
								litParam.put(arg, params.get(arg));
							}
							Symbol s = new Predicate(litName, litParam);
							obs.addTrueObservation(s);
							break;
						}
					}
				}
				res.put(sym, obs);
				break;
			case NOT://Negative precondition
				if (!op.getEffects().getChildren().isEmpty()) {
					for (int i = 0; i < op.getEffects().getChildren().size(); i++) {
						switch(op.getEffects().getChildren().get(i).getConnective()) {
						case NOT:
							PDDLExpression exp1 = op.getEffects().getChildren().get(i).getChildren().get(0);
							String litName1 = exp1.getAtom().get(0).getImage();
							Map<String, String> litParam1 = new LinkedHashMap<>();
							for(int j = 1; j< exp1.getAtom().size(); j++) {
								String arg = exp1.getAtom().get(j).getImage();
								litParam1.put(arg, params.get(arg));
							}
							Symbol s1 = new Predicate(litName1, litParam1);
							obs.addFalseObservation(s1);
							break;
						default:
							PDDLExpression exp = op.getEffects().getChildren().get(i);
							String litName = exp.getAtom().get(0).getImage();
							Map<String, String> litParam = new LinkedHashMap<>();
							for(int j = 1; j< exp.getAtom().size(); j++) {
								String arg = exp.getAtom().get(j).getImage();
								litParam.put(arg, params.get(arg));
							}
							Symbol s = new Predicate(litName, litParam);
							obs.addTrueObservation(s);
							break;
						}
					}
				}
				res.put(sym, obs);
				break;
			default://Only one positive precondition
				PDDLExpression exp = op.getEffects();
				String litName = exp.getAtom().get(0).getImage();
				Map<String, String> litParam = new LinkedHashMap<>();
				for(int j = 1; j< exp.getAtom().size(); j++) {
					String arg = exp.getAtom().get(j).getImage();
					litParam.put(arg, params.get(arg));
				}
				Symbol s = new Predicate(litName, litParam);
				obs.addTrueObservation(s);
				res.put(sym, obs);
			}
		}
		return res;
	}
	/**
	 * Compute all possible litterals for each actions
	 * @param parser A parser
	 * @return A map between actions and possible litterals
	 */
	private static Map<Symbol, List<Symbol>> all_litterals
	(PDDLParser parser, TypeHierarchy h) {
		PDDLDomain dom = parser.getDomain();

		Map<Symbol, List<Symbol>> res = new HashMap<>();
		List<PDDLAction> ops = dom.getActions();
		for(PDDLAction op : ops) {
			Map<String, String> params = params(op.getParameters());
			Symbol sym = new Action(op.getName().getImage(),params);
			res.put(sym, new ArrayList<>());
			for(PDDLNamedTypedList lit : dom.getPredicates()) {
				Symbol pred = new Predicate(lit.getName().getImage(),
						params(lit.getArguments()));
				res.get(sym).addAll(all_permutations(
						(Action)sym,(Predicate)pred, h));
			}
		}
		return res;
	}

	/**
	 * Compute all parameters permutation for a predicate compatible with a
	 * given action
	 *
	 * @param act An action
	 * @param pred An predicate
	 * @return A set of preicate
	 */
	private static List<Symbol> all_permutations(Action act, Predicate pred, TypeHierarchy h){
		List<Symbol> tmp = new ArrayList<>();
		tmp.add(pred);
		return act.getAllPredicates(tmp, h);
	}

	/**
	 * Count the number of extra and missing symbols in l1 wrt l2
	 * @param l1 A list of symbols
	 * @param l2 A list of symbols
	 * @return The number of errors
	 */
	private static int count_errors(List<Symbol> l1,List<Symbol> l2) {
		int err = 0;
		//Missing lit
		for(Symbol s : l1) {
			err += l2.contains(s) ? 0 : 1;
		}

		//Added lit
		for(Symbol s : l2) {
			err += l1.contains(s) ? 0 : 1;
		}
		//System.out.println(err);
		return err;
	}

	/**
	 * Standardize arguments name between two set of actions and predicates
	 * @param s1 A first set of actions and predicates
	 * @param s2 A second set of actions and predicates
	 * @return Mapping between argument of s1 and s2
	 */
	private static Map<Symbol, Map<String, String>> standardize
	(Collection<Symbol>s1, Collection<Symbol> s2){
		Map<Symbol, Map<String, String> >res = new HashMap<>();
		for(Symbol s : s1) {
			for(Symbol ss : s2) {
				if(s.getName().equals(ss.getName())) {
					res.put(ss, ss.getParametersMapping(s));
				}
			}
		}
		return res;
	}


	/**
	 * Compute the both precondition and effects error rates
	 * @param dom the domain
	 * @param A the list of actions
	 * @param examples all example
	 * @param generator the generator
	 * @return the error rates
	 */
	public static Pair<Float,Float> error_rate(
			String dom, List<Symbol> A, Sample examples,Generator generator) {
		Pair<Float, Float> res = new Pair<>((float)1,(float)1);
		try {
			PDDLParser parser = load(dom);
			Map<Symbol, Observation> preconditions =
					extract_precondition(parser);
			Map<Symbol, Observation> postconditions =
					extract_postcondition(parser);
			res.setX(error_rate_precondition(examples, generator,A,
					preconditions));
			res.setY(error_rate_postcondition(examples, generator,A,
					postconditions));
			return res;
		} catch (PlanException e) {
			// TODO Auto-generated catch block
			return res;
		}
	}

	/**
	 * Compute precondition error rate
	 * @param examples the examples
	 * @param generator the generator
	 * @param A the set of actions
	 * @param preconditions action's preconditions
	 * @return the error rate
	 */
	public static float error_rate_precondition(
			Sample examples, Generator generator, List<Symbol> A,
			Map<Symbol, Observation> preconditions ) {

		//Get parameters map for generalization
		Map<Symbol, Map<String, String>> paramMap =
				standardize(preconditions.keySet(),A);

		float e = 0;
		List<Integer> err = new ArrayList<>();
		List<Integer> prec = new ArrayList<>();
		for(Trace t : examples.getExamples()) {
			Example example = (Example)t;
			List<Observation> observations = generator.playWithoutNoise(example);
			for(int i = 0; i<example.size(); i++) {
				Observation precond = preconditions.get(example.get(i).
						generalize(paramMap.get(example.get(i))));
				prec.add(precond.size());
				int error = 0;
				for(Symbol pred : precond.getPredicatesSymbols()){
					switch(precond.getValue(pred)){
					case TRUE:
						switch(observations.get(i).getValue
								(pred.generalize(utils.Utils.reverseParamMap
										(paramMap.get(example.get(i)))))){
										case FALSE:
											error ++;
										default:
											break;
						}
						break;
					case FALSE:
						switch(observations.get(i).getValue
								(pred.generalize(utils.Utils.reverseParamMap
										(paramMap.get(example.get(i)))))){
										case TRUE:
											error ++;
										default:
											break;
						}
						break;
					default:
						break;
					}
				}
				err.add(error);
			}
		}
		e = Measure.erroRate(err, prec);
		return e;
	}

	/**
	 * Compute effects error rate
	 * @param examples the examples
	 * @param generator the generator
	 * @param A the set of actions
	 * @param postconditions action's effects
	 * @return the error rate
	 */
	public static float error_rate_postcondition(
			Sample examples, Generator generator, List<Symbol> A,
			Map<Symbol, Observation> postconditions) {

		//Get parameters map for generalization
		Map<Symbol, Map<String, String>> paramMap =
				standardize(postconditions.keySet(),A);

		float e = 0;
		List<Integer> err = new ArrayList<>();
		List<Integer> prec = new ArrayList<>();
		for(Trace t : examples.getExamples()) {
			Example example = (Example)t;
			List<Observation> observations = generator.playWithoutNoise(example);
			for(int i = 0; i<example.size(); i++) {
				Observation precond = postconditions.get(example.get(i).
						generalize(paramMap.get(example.get(i))));
				prec.add(precond.size());
				int error = 0;
				for(Symbol pred : precond.getPredicatesSymbols()){
					switch(precond.getValue(pred)){
					case TRUE:
						switch(observations.get(i+1).getValue
								(pred.generalize(utils.Utils.reverseParamMap
										(paramMap.get(example.get(i)))))){
										case FALSE:
											error ++;
										default:
											break;
						}
						break;
					case FALSE:
						switch(observations.get(i+1).getValue
								(pred.generalize(utils.Utils.reverseParamMap
										(paramMap.get(example.get(i)))))){
										case TRUE:
											error ++;
										default:
											break;
						}
						break;
					default:
						break;
					}
				}

				err.add(error);
			}
		}
		e = Measure.erroRate(err, prec);
		return e;
	}

	/**
	 * Extract action's preconditions
	 * @param parser A parser
	 * @return Action's precondition
	 */
	private static List<Map<Symbol, Observation>> extract_precondition_temporal(PDDLParser parser) {
		PDDLDomain dom = parser.getDomain();
		List<Map<Symbol, Observation>> res = new ArrayList<>();
		Map<Symbol, Observation> startPreconditions = new HashMap<>();
		Map<Symbol, Observation> endPreconditions = new HashMap<>();
		Map<Symbol, Observation> overAllPreconditions = new HashMap<>();
		for(PDDLAction op : dom.getActions()) {
			Map<String, String> params = params(op.getParameters());
			Symbol sym = new Action(op.getName().getImage(),params);

			startPreconditions.put(sym, new Observation());
			endPreconditions.put(sym, new Observation());
			overAllPreconditions.put(sym, new Observation());

			for(PDDLExpression exp : op.getPreconditions().getChildren()) {
				switch(exp.getConnective().getImage()) {
				case "at start":
					switch(exp.getChildren().get(0).getConnective()) {
					case NOT:
						PDDLExpression expStartNot = exp.getChildren().get(0).getChildren().get(0);
						String litNameStartNot = expStartNot.getAtom().get(0).getImage();
						Map<String, String> litParamStartNot = new LinkedHashMap<>();
						for(int j = 1; j< expStartNot.getAtom().size(); j++) {
							String arg = expStartNot.getAtom().get(j).getImage();
							litParamStartNot.put(arg, params.get(arg));
						}
						Symbol sStartNot = new Predicate(litNameStartNot, litParamStartNot);
						startPreconditions.get(sym).addFalseObservation(sStartNot);;
						break;
					default:
						PDDLExpression expStart = exp.getChildren().get(0);
						String litNameStart = expStart.getAtom().get(0).getImage();
						Map<String, String> litParamStart = new LinkedHashMap<>();
						for(int j = 1; j< expStart.getAtom().size(); j++) {
							String arg = expStart.getAtom().get(j).getImage();
							litParamStart.put(arg, params.get(arg));
						}
						Symbol sStart = new Predicate(litNameStart, litParamStart);
						startPreconditions.get(sym).addTrueObservation(sStart);;
						break;
					}
					break;
				case "at end":
					switch(exp.getChildren().get(0).getConnective()) {
					case NOT:
						PDDLExpression expEndNot = exp.getChildren().get(0).getChildren().get(0);
						String litNameEndNot = expEndNot.getAtom().get(0).getImage();
						Map<String, String> litParamEndNot = new LinkedHashMap<>();
						for(int j = 1; j< expEndNot.getAtom().size(); j++) {
							String arg = expEndNot.getAtom().get(j).getImage();
							litParamEndNot.put(arg, params.get(arg));
						}
						Symbol sEndNot = new Predicate(litNameEndNot, litParamEndNot);
						endPreconditions.get(sym).addFalseObservation(sEndNot);;
						break;
					default:
						PDDLExpression expEnd = exp.getChildren().get(0);
						String litNameEnd = expEnd.getAtom().get(0).getImage();
						Map<String, String> litParamEnd = new LinkedHashMap<>();
						for(int j = 1; j< expEnd.getAtom().size(); j++) {
							String arg = expEnd.getAtom().get(j).getImage();
							litParamEnd.put(arg, params.get(arg));
						}
						Symbol sEnd = new Predicate(litNameEnd, litParamEnd);
						endPreconditions.get(sym).addTrueObservation(sEnd);
						break;
					}
					break;
				case "over all":
					switch(exp.getChildren().get(0).getConnective()) {
					case NOT:
						PDDLExpression expEndNot = exp.getChildren().get(0).getChildren().get(0);
						String litNameEndNot = expEndNot.getAtom().get(0).getImage();
						Map<String, String> litParamEndNot = new LinkedHashMap<>();
						for(int j = 1; j< expEndNot.getAtom().size(); j++) {
							String arg = expEndNot.getAtom().get(j).getImage();
							litParamEndNot.put(arg, params.get(arg));
						}
						Symbol sEndNot = new Predicate(litNameEndNot, litParamEndNot);
						overAllPreconditions.get(sym).addFalseObservation(sEndNot);;
						break;
					default:
						PDDLExpression expEnd = exp.getChildren().get(0);
						String litNameEnd = expEnd.getAtom().get(0).getImage();
						Map<String, String> litParamEnd = new LinkedHashMap<>();
						for(int j = 1; j< expEnd.getAtom().size(); j++) {
							String arg = expEnd.getAtom().get(j).getImage();
							litParamEnd.put(arg, params.get(arg));
						}
						Symbol sEnd = new Predicate(litNameEnd, litParamEnd);
						overAllPreconditions.get(sym).addTrueObservation(sEnd);
						break;
					}
					break;
				}
			}
		}
		res.add(startPreconditions);
		res.add(overAllPreconditions);
		res.add(endPreconditions);
		return res;
	}
	
	/**
	 * Extract action's preconditions
	 * @param parser A parser
	 * @return Action's precondition
	 */
	private static List<Map<Symbol, Observation>> extract_postcondition_temporal(PDDLParser parser) {
		PDDLDomain dom = parser.getDomain();
		List<Map<Symbol, Observation>> res = new ArrayList<>();
		Map<Symbol, Observation> startPreconditions = new HashMap<>();
		Map<Symbol, Observation> endPreconditions = new HashMap<>();
		Map<Symbol, Observation> overAllPreconditions = new HashMap<>();
		for(PDDLAction op : dom.getActions()) {
			Map<String, String> params = params(op.getParameters());
			Symbol sym = new Action(op.getName().getImage(),params);

			startPreconditions.put(sym, new Observation());
			endPreconditions.put(sym, new Observation());
			overAllPreconditions.put(sym, new Observation());

			for(PDDLExpression exp : op.getEffects().getChildren()) {
				switch(exp.getConnective().getImage()) {
				case "at start":
					switch(exp.getChildren().get(0).getConnective()) {
					case NOT:
						PDDLExpression expStartNot = exp.getChildren().get(0).getChildren().get(0);
						String litNameStartNot = expStartNot.getAtom().get(0).getImage();
						Map<String, String> litParamStartNot = new LinkedHashMap<>();
						for(int j = 1; j< expStartNot.getAtom().size(); j++) {
							String arg = expStartNot.getAtom().get(j).getImage();
							litParamStartNot.put(arg, params.get(arg));
						}
						Symbol sStartNot = new Predicate(litNameStartNot, litParamStartNot);
						startPreconditions.get(sym).addFalseObservation(sStartNot);;
						break;
					default:
						PDDLExpression expStart = exp.getChildren().get(0);
						String litNameStart = expStart.getAtom().get(0).getImage();
						Map<String, String> litParamStart = new LinkedHashMap<>();
						for(int j = 1; j< expStart.getAtom().size(); j++) {
							String arg = expStart.getAtom().get(j).getImage();
							litParamStart.put(arg, params.get(arg));
						}
						Symbol sStart = new Predicate(litNameStart, litParamStart);
						startPreconditions.get(sym).addTrueObservation(sStart);;
						break;
					}
					break;
				case "at end":
					switch(exp.getChildren().get(0).getConnective()) {
					case NOT:
						PDDLExpression expEndNot = exp.getChildren().get(0).getChildren().get(0);
						String litNameEndNot = expEndNot.getAtom().get(0).getImage();
						Map<String, String> litParamEndNot = new LinkedHashMap<>();
						for(int j = 1; j< expEndNot.getAtom().size(); j++) {
							String arg = expEndNot.getAtom().get(j).getImage();
							litParamEndNot.put(arg, params.get(arg));
						}
						Symbol sEndNot = new Predicate(litNameEndNot, litParamEndNot);
						endPreconditions.get(sym).addFalseObservation(sEndNot);;
						break;
					default:
						PDDLExpression expEnd = exp.getChildren().get(0);
						String litNameEnd = expEnd.getAtom().get(0).getImage();
						Map<String, String> litParamEnd = new LinkedHashMap<>();
						for(int j = 1; j< expEnd.getAtom().size(); j++) {
							String arg = expEnd.getAtom().get(j).getImage();
							litParamEnd.put(arg, params.get(arg));
						}
						Symbol sEnd = new Predicate(litNameEnd, litParamEnd);
						endPreconditions.get(sym).addTrueObservation(sEnd);
						break;
					}
					break;
				case "over all":
					switch(exp.getChildren().get(0).getConnective()) {
					case NOT:
						PDDLExpression expEndNot = exp.getChildren().get(0).getChildren().get(0);
						String litNameEndNot = expEndNot.getAtom().get(0).getImage();
						Map<String, String> litParamEndNot = new LinkedHashMap<>();
						for(int j = 1; j< expEndNot.getAtom().size(); j++) {
							String arg = expEndNot.getAtom().get(j).getImage();
							litParamEndNot.put(arg, params.get(arg));
						}
						Symbol sEndNot = new Predicate(litNameEndNot, litParamEndNot);
						overAllPreconditions.get(sym).addFalseObservation(sEndNot);;
						break;
					default:
						PDDLExpression expEnd = exp.getChildren().get(0);
						String litNameEnd = expEnd.getAtom().get(0).getImage();
						Map<String, String> litParamEnd = new LinkedHashMap<>();
						for(int j = 1; j< expEnd.getAtom().size(); j++) {
							String arg = expEnd.getAtom().get(j).getImage();
							litParamEnd.put(arg, params.get(arg));
						}
						Symbol sEnd = new Predicate(litNameEnd, litParamEnd);
						overAllPreconditions.get(sym).addTrueObservation(sEnd);
						break;
					}
					break;
				}
			}
		}
		res.add(startPreconditions);
		res.add(overAllPreconditions);
		res.add(endPreconditions);
		return res;
	}

	/**
	 * Compute the semantic distance
	 * @param ref The reference domain
	 * @param dom The domain to test
	 * @param negative if true, we take negative precondition into account
	 * @return the semantic distance
	 */
	public static float semantic_distance_temporal(String ref, String dom,
			boolean negative) {
		//Load domains
		try {
			PDDLParser reference = load(ref);
			Map<String, String> map = new HashMap<>();
			for(PDDLTypedSymbol t : reference.getDomain().getTypes()) {

				if(! t.getTypes().isEmpty()) {
					map.put(t.getImage(), t.getTypes().get(0).getImage());
				}
			}
			TypeHierarchy hier = new TypeHierarchy(map);
			List<Map<Symbol, Observation>> domPrec = extract_precondition_temporal(reference);
			List<Map<Symbol, Observation>> domEff = extract_postcondition_temporal(reference);
			PDDLParser toTest = load(dom);
			List<Map<Symbol, Observation>> testPrec = extract_precondition_temporal(toTest);
			List<Map<Symbol, Observation>> testEff = extract_postcondition_temporal(toTest);

			Map<Symbol, Map<String, String>> paramMap = standardize(domPrec.get(0).keySet(), testPrec.get(0).keySet());

			List<Map<Symbol, Observation>> testPrec2 = new ArrayList<>();
			for(Map<Symbol, Observation> tmp : testPrec) {
				Map<Symbol, Observation> tmp2 = new HashMap<>();
				for(Symbol act : tmp.keySet()) {
					Observation obs = new Observation();
					for(Symbol pred : tmp.get(act).getPredicatesSymbols()) {
						obs.addObservation(pred.generalize(paramMap.get(act)),
								tmp.get(act).getValue(pred));
					} 
					tmp2.put(act.generalize(paramMap.get(act)), obs);
				}
				testPrec2.add(tmp2);
			}
			List<Map<Symbol, Observation>> testEff2 = new ArrayList<>();
			for(Map<Symbol, Observation> tmp : testEff) {
				Map<Symbol, Observation> tmp2 = new HashMap<>();
				for(Symbol act : tmp.keySet()) {
					Observation obs = new Observation();
					for(Symbol pred : tmp.get(act).getPredicatesSymbols()) {
						obs.addObservation(pred.generalize(paramMap.get(act)),
								tmp.get(act).getValue(pred));
					} 
					tmp2.put(act.generalize(paramMap.get(act)), obs);
				}
				testEff2.add(tmp2);
			}
			Map<Symbol, List<Symbol>> lits = all_litterals(reference, hier);
			Map<Symbol, float[]> errors = new HashMap<>();
			for(Symbol act : domPrec.get(0).keySet()) {
				float [] tmp= new float[4];
				tmp[0] = 0;
				for(int i = 0; i < domPrec.size(); i++) {
					tmp[0]+=count_errors(domPrec.get(i).get(act).getPositivePredicate(),
							testPrec2.get(i).get(act).getPositivePredicate());
				}
				if(negative) {
					tmp[1] = 0;
					for(int i = 0; i < domPrec.size(); i++) {
						tmp[1]+=count_errors(domPrec.get(i).get(act).getNegativePredicate(),
								testPrec2.get(i).get(act).getNegativePredicate());
					}
				}else {
					tmp[1] = 0;
				}
				tmp[2] = 0;
				for(int i = 0; i < domPrec.size(); i++) {
					tmp[2]+=count_errors(domEff.get(i).get(act).getPositivePredicate(),
							testEff2.get(i).get(act).getPositivePredicate());
				}
				tmp[3] = 0;
				for(int i = 0; i < domPrec.size(); i++) {
					tmp[3]+=count_errors(domEff.get(i).get(act).getNegativePredicate(),
							testEff2.get(i).get(act).getNegativePredicate());
				}
				errors.put(act, tmp);
			}
			float dist = 0;
			int n = 0;
			int index = 0;
			for(Symbol act : domPrec.get(0).keySet()) {
				float tmp = 0;
				for(float e : errors.get(act)) {
					index = (index+1) % 4;
					tmp += (e / (float)(lits.get(act).size()*3));
				}
				tmp = (tmp / (negative ? 4 : 3));
				dist += tmp;
				n++;
			}
			return (dist/n);
		} catch (PlanException e) {
			return 1;
		}
	}
	
    /**
	 * Test learnt domain
	 * @param reference The reference domain
     * @param initialState The intial state
	 * @param domainName The domain name
	 * @param generatorTest The generator for test sample
	 * @param actions The action set
	 * @param testSet The test sample
	 * @throws BlocException
	 */
	public static void test(
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
	
	public static void test(
			String reference,
			String domainName) throws BlocException {
		float semantic_dist = TestMetrics.semantic_distance
				(reference, domainName, false);
		System.out.println("Syntactical distance : "+semantic_dist);
	}
	
	public static void testTemporal(
			String reference,
			String domainName) throws BlocException {
		float semantic_dist = TestMetrics.semantic_distance_temporal
				(reference, domainName, false);
		System.out.println("Syntactical distance : "+semantic_dist);
	}
}
