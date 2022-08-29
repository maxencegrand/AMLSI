/**
 * 
 */
package learning.temporal.translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.TypeHierarchy;
import fr.uga.generator.utils.Pair;
import fr.uga.pddl4j.parser.PDDLAction;
import fr.uga.pddl4j.parser.PDDLExpression;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.parser.PDDLTypedSymbol;

/**
 * @author Maxence Grand
 *
 */
public class Translator {
	/**
	 * 
	 * @param temporal
	 * @param filename
	 * @return
	 */
	public static File translate(File temporal, String filename) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(temporal);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();

		//Map durative operators' name with classical operators' name
		Map<String, List<String>> opNames = new HashMap<>();
		for(PDDLAction op : operators) {
			String str = op.getName().toString();
			List<String> newOp = new ArrayList<>();
			newOp.add(str+"-start");
			newOp.add(str+"-inv");
			newOp.add(str+"-end");
			opNames.put(str, newOp);
		}

		//Get preconditions
		Map<String, List<String>> startPreconditions = new HashMap<>();
		Map<String, List<String>> endPreconditions = new HashMap<>();
		Map<String, List<String>> overAllPreconditions = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();

			startPreconditions.put(name, new LinkedList<>());
			endPreconditions.put(name, new LinkedList<>());
			overAllPreconditions.put(name, new LinkedList<>());

			for(PDDLExpression exp : op.getPreconditions().getChildren()) {
				switch(exp.getConnective().getImage()) {
				case "at start":
					startPreconditions.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				case "at end":
					endPreconditions.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				case "over all":
					overAllPreconditions.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				}
			}
		}

		//Get effects
		Map<String, List<String>> startEffects = new HashMap<>();
		Map<String, List<String>> endEffects = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();

			startEffects.put(name, new LinkedList<>());
			endEffects.put(name, new LinkedList<>());

			for(PDDLExpression exp : op.getEffects().getChildren()) {
				switch(exp.getConnective().getImage()) {
				case "at start":
					startEffects.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				case "at end":
					endEffects.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				}
			}
		}

		//Get preconditions and effects of classical domain
		Map<String, Pair<List<String>, List<String>>> classical = 
				new HashMap<>();
		for(String str : opNames.keySet()) {
			//str-start action
			//str-start precondition
			List<String> l1 = new LinkedList<>();
			l1.addAll(startPreconditions.get(str));
			//str-start effect
			List<String> l2 = new LinkedList<>();
			l2.addAll(startEffects.get(str));
			classical.put(opNames.get(str).get(0), new Pair<>(l1,l2));

			//str-end action
			//str-end precondition
			List<String> l3 = new LinkedList<>();
			l3.addAll(endPreconditions.get(str));
			//str-start effect
			List<String> l4 = new LinkedList<>();
			l4.addAll(endEffects.get(str));
			classical.put(opNames.get(str).get(2), new Pair<>(l3,l4));

			//str-in action
			//str-inv precondition
			List<String> l5 = new LinkedList<>();
			l5.addAll(overAllPreconditions.get(str));
			classical.put(opNames.get(str).get(1), new Pair<>(l5,new ArrayList<>()));
		}

		//Get Type Hierarchy
		Map<String, String> hier = new HashMap<>();
		for(PDDLTypedSymbol t : parser.getDomain().getTypes()) {
			if(!t.getTypes().isEmpty()) {
				hier.put(t.getImage().toLowerCase(),
						t.getTypes().get(0).getImage().toLowerCase());
			}
		}
		TypeHierarchy types = new TypeHierarchy(hier);

		//Write Classical domain
		StringBuilder str = new StringBuilder();
		str.append("(define (domain "+parser.getDomain().getName().getImage()+")\n");
		str.append("(:requirements :strips :typing :negative-preconditions)\n");
		str.append("(:types \n"+types+")");
		str.append("(:predicates\n");
		parser.getDomain().getPredicates().forEach(p -> {
			str.append("\t"+p.toString()+"\n");
		});
		/*parser.getDomain().getActions().forEach(op -> {
			String inv = "("+op.getName().toString()+"-inv";
			String iinv = "(i"+op.getName().toString()+"-inv";
			for(PDDLTypedSymbol p : op.getParameters()) {
				inv += (" "+p.toString());
				iinv += (" "+p.toString());
			}
			inv+=")";
			iinv+=")";
			str.append("\t"+inv+"\n\t"+iinv+"\n");
		});*/
		str.append(")\n");
		parser.getDomain().getActions().forEach(op -> {
			List<String> actions = opNames.get(op.getName().toString());
			//Start action
			str.append("(:action "+actions.get(0)+"\n");
			str.append("\t:parameters (");
			StringBuilder paramInv = new StringBuilder();
			paramInv.append(op.getName().toString());
			paramInv.append("-inv");
			op.getParameters().forEach(p -> {
				str.append(" "+op.getParameter(p));
				paramInv.append(" "+p.getImage());
			});
			paramInv.append(")");
			//System.out.println(paramInv);
			str.append(" )\n");
			str.append("\t:precondition (and\n");
			classical.get(actions.get(0)).getX().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			str.append("\t)\n");
			str.append("\t:effect (and\n");
			classical.get(actions.get(0)).getY().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			//str.append("\t\t("+paramInv+""+"\n");
			str.append("\t)\n)\n");
			//End action
			str.append("(:action "+actions.get(2)+"\n");
			str.append("\t:parameters (");
			op.getParameters().forEach(p -> {
				str.append(" "+op.getParameter(p));
			});
			str.append(" )\n");
			str.append("\t:precondition (and\n");
			classical.get(actions.get(2)).getX().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			//str.append("\t\t("+paramInv+""+"\n");
			//str.append("\t\t("+"i"+paramInv+""+"\n");
			str.append("\t)\n");
			str.append("\t:effect (and\n");
			classical.get(actions.get(2)).getY().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			//str.append("\t\t(not ("+paramInv+")"+"\n");
			//str.append("\t\t(not ("+"i"+paramInv+")"+"\n");
			str.append("\t)\n)\n");
			//Invariant action
			str.append("(:action "+actions.get(1)+"\n");
			str.append("\t:parameters (");
			op.getParameters().forEach(p -> {
				str.append(" "+op.getParameter(p));
			});
			str.append(" )\n");
			str.append("\t:precondition (and\n");
			classical.get(actions.get(1)).getX().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			//str.append("\t\t("+paramInv+""+"\n");
			str.append("\t)\n");
			str.append("\t:effect (and\n");
			classical.get(actions.get(1)).getY().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			//str.append("\t\t("+paramInv+""+"\n");
			//str.append("\t\t("+"i"+paramInv+""+"\n");
			str.append("\t)\n)\n");
		});
		str.append(")");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

			bw.write(str.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		File file = new File(filename);
		return file;
	}

	/**
	 * 
	 * @param temporal
	 * @return
	 */
	public static Map<String, Float> getDurations(File temporal) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(temporal);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();

		//Map durative operators' name with classical operators' name
		Map<String, Float> opD = new HashMap<>();
		for(PDDLAction op : operators) {
			String str = op.getName().toString();
			//System.out.println(str+" "+op.getDuration().getChildren().get(1).getValue().floatValue());
			opD.put(str, op.getDuration().getChildren().get(1).getValue().floatValue());
		}
		return opD;
	}

	/**
	 * 
	 * @param temporal
	 * @return
	 */
	public static List<String> getExtraDumpy(File temporal) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(temporal);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();

		List<String> extraDumpyPropositions = new ArrayList<>();
		for(PDDLAction op : operators) {
			String str = new String(op.getName().toString());
			String extra1 = str+"-inv";
			String extra2 = "i"+str+"-inv";
			if(!extraDumpyPropositions.contains(extra1)) {
				extraDumpyPropositions.add(extra1);
				extraDumpyPropositions.add(extra2);
			}
		}
		return extraDumpyPropositions;
	}

	/**
	 * 
	 * @param classical
	 * @param durations
	 * @param filename
	 * @return
	 */
	public static File translate(File classical, Map<String, Float> durations,
			String filename) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(classical);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();

		//Get durative operators
		List<String> extraDumpyPropositions = new ArrayList<>();
		Map<String, String> opNames = new HashMap<>();
		Map<String, PDDLAction> mapDurativeClassical = new HashMap<>();
		for(PDDLAction op : operators) {
			String str = new String(op.getName().toString());
			String[] tmp = str.split("-");
			int s = tmp[tmp.length-1].length();
			str = str.substring(0, str.length()-s-1);
			opNames.put(op.getName().toString(), str);
			String extra1 = str+"-inv";
			String extra2 = "i"+str+"-inv";
			//System.out.println(extra1+" "+extra2);
			if(!extraDumpyPropositions.contains(extra1)) {
				extraDumpyPropositions.add(extra1);
				extraDumpyPropositions.add(extra2);
			}
			mapDurativeClassical.put(str, op);
		}
		//System.out.println(extraDumpyPropositions);
		//Get preconditions
		Map<String, List<String>> startPreconditions = new HashMap<>();
		Map<String, List<String>> endPreconditions = new HashMap<>();
		Map<String, List<String>> overAllPreconditions = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();
			String[] tmp = name.split("-");
			switch(tmp[tmp.length-1]) {
			case "start":
				startPreconditions.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getPreconditions().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							startPreconditions.get(opNames.get(name)).add(exp.toString());
						}
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							startPreconditions.get(opNames.get(name)).add(exp.toString());
						}
						break;
					default:
						break;
					}
				}
				break;
			case "inv":
				overAllPreconditions.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getPreconditions().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							overAllPreconditions.get(opNames.get(name)).add(exp.toString());
						}
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							overAllPreconditions.get(opNames.get(name)).add(exp.toString());
						}
						break;
					default:
						break;
					}
				}
				break;
			case "end":
				endPreconditions.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getPreconditions().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							endPreconditions.get(opNames.get(name)).add(exp.toString());
						}
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							endPreconditions.get(opNames.get(name)).add(exp.toString());
						}
						break;
					default:
						break;
					}
				}
				break;
			}
		}

		//Get effects
		Map<String, List<String>> startEffects = new HashMap<>();
		Map<String, List<String>> endEffects = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();
			String[] tmp = name.split("-");
			switch(tmp[tmp.length-1]) {
			case "start":
				startEffects.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getEffects().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							startEffects.get(opNames.get(name)).add(exp.toString());
						}
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							startEffects.get(opNames.get(name)).add(exp.toString());
						}
						break;
					default:
						break;
					}
				}
				break;
			case "end":
				endEffects.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getEffects().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							endEffects.get(opNames.get(name)).add(exp.toString());
						}
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						//System.out.println(prec+" "+str+" "+extraDumpyPropositions.contains(str));
						if(!extraDumpyPropositions.contains(str)) {
							endEffects.get(opNames.get(name)).add(exp.toString());
						}
						break;
					default:
						break;
					}
				}
				break;
			}
		}
		System.out.println(endEffects);
		//Get Type Hierarchy
		Map<String, String> hier = new HashMap<>();
		for(PDDLTypedSymbol t : parser.getDomain().getTypes()) {
			if(!t.getTypes().isEmpty()) {
				hier.put(t.getImage().toLowerCase(),
						t.getTypes().get(0).getImage().toLowerCase());
			}
		}
		TypeHierarchy types = new TypeHierarchy(hier);

		//Write Temporal domain
		StringBuilder str = new StringBuilder();
		str.append("(define (domain "+parser.getDomain().getName().getImage()+")\n");
		str.append("(:requirements :strips :typing :negative-preconditions :durative-actions)\n");
		str.append("(:types \n"+types+")");
		str.append("(:predicates\n");
		parser.getDomain().getPredicates().forEach(p -> {
			if(!extraDumpyPropositions.contains(p.getName().toString())) {
				str.append("\t"+p.toString()+"\n");
			}
		});
		str.append(")\n");
		startPreconditions.keySet().forEach(op -> {
			//Begin operator declaration
			str.append("(:durative-action "+op+"\n");
			//PArameter
			str.append("\t:parameters (");
			StringBuilder paramInv = new StringBuilder();
			mapDurativeClassical.get(op).getParameters().forEach(p -> {
				str.append(" "+mapDurativeClassical.get(op).getParameter(p));
				paramInv.append(" "+p.getImage());
			});
			str.append(" )\n");
			//Duration
			str.append("\t:duration (= ?duration "+durations.get(op)+")\n");
			//Conditions
			str.append("\t:condition (and\n");
			startPreconditions.get(op).forEach(prec -> {
				if(! overAllPreconditions.get(op).contains(prec) &&
						! endPreconditions.get(op).contains(prec)) {
					str.append("\t\t(at start "+prec+" )\n");
				}
			});
			overAllPreconditions.get(op).forEach(prec -> {
				if(! startEffects.get(op).contains(prec)) {
					str.append("\t\t(over all "+prec+" )\n");
				}
			});
			endPreconditions.get(op).forEach(prec -> {
				if(! overAllPreconditions.get(op).contains(prec) &&
						! startPreconditions.get(op).contains(prec)) {
					str.append("\t\t(at end "+prec+" )\n");
				}
			});
			str.append("\t)\n");
			//Effects
			str.append("\t:effect (and\n");
			startEffects.get(op).forEach(eff -> {
				str.append("\t\t(at start "+eff+" )\n");
			});
			endEffects.get(op).forEach(eff -> {
				str.append("\t\t(at end "+eff+" )\n");
			});
			str.append("\t)\n)\n");
		});
		str.append(")");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

			bw.write(str.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		File file = new File(filename);
		return file;
	}

	/**
	 * 
	 * @param temporal
	 * @param filename
	 * @return
	 */
	public static File translate2Op(File temporal, String filename) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(temporal);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();

		//Map durative operators' name with classical operators' name
		Map<String, List<String>> opNames = new HashMap<>();
		
		for(PDDLAction op : operators) {
			String str = op.getName().toString();
			List<String> newOp = new ArrayList<>();
			newOp.add(str+"-start");
			newOp.add(str+"-end");
			opNames.put(str, newOp);
		}

		//Get preconditions
		Map<String, List<String>> startPreconditions = new HashMap<>();
		Map<String, List<String>> endPreconditions = new HashMap<>();
		Map<String, List<String>> overAllPreconditions = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();

			startPreconditions.put(name, new LinkedList<>());
			endPreconditions.put(name, new LinkedList<>());
			overAllPreconditions.put(name, new LinkedList<>());

			for(PDDLExpression exp : op.getPreconditions().getChildren()) {
				switch(exp.getConnective().getImage()) {
				case "at start":
					startPreconditions.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				case "at end":
					endPreconditions.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				case "over all":
					overAllPreconditions.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				}
			}
		}

		//Get effects
		Map<String, List<String>> startEffects = new HashMap<>();
		Map<String, List<String>> endEffects = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();

			startEffects.put(name, new LinkedList<>());
			endEffects.put(name, new LinkedList<>());

			for(PDDLExpression exp : op.getEffects().getChildren()) {
				switch(exp.getConnective().getImage()) {
				case "at start":
					startEffects.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				case "at end":
					endEffects.get(name).add(
							exp.getChildren().get(0).toString());
					break;
				}
			}
		}

		//Get preconditions and effects of classical domain
		Map<String, Pair<List<String>, List<String>>> classical = 
				new HashMap<>();
		for(String str : opNames.keySet()) {
			//System.out.println(str);
			//str-start action
			//str-start precondition
			List<String> l1 = new LinkedList<>();
			l1.addAll(startPreconditions.get(str));
			l1.addAll(overAllPreconditions.get(str));
			//str-start effect
			List<String> l2 = new LinkedList<>();
			l2.addAll(startEffects.get(str));
			classical.put(opNames.get(str).get(0), new Pair<>(l1,l2));

			//str-end action
			//str-end precondition
			List<String> l3 = new LinkedList<>();
			l3.addAll(endPreconditions.get(str));
			l3.addAll(overAllPreconditions.get(str));
			//str-start effect
			List<String> l4 = new LinkedList<>();
			l4.addAll(endEffects.get(str));
			classical.put(opNames.get(str).get(1), new Pair<>(l3,l4));
		}
		System.err.println(classical);
		//Get Type Hierarchy
		Map<String, String> hier = new HashMap<>();
		for(PDDLTypedSymbol t : parser.getDomain().getTypes()) {
			if(!t.getTypes().isEmpty()) {
				hier.put(t.getImage().toLowerCase(),
						t.getTypes().get(0).getImage().toLowerCase());
			}
		}
		TypeHierarchy types = new TypeHierarchy(hier);

		//Write Classical domain
		StringBuilder str = new StringBuilder();
		str.append("(define (domain "+parser.getDomain().getName().getImage()+")\n");
		str.append("(:requirements :strips :typing :negative-preconditions)\n");
		str.append("(:types \n"+types+")");
		str.append("(:predicates\n");
		parser.getDomain().getPredicates().forEach(p -> {
			str.append("\t"+p.toString()+"\n");
		});
		
		str.append(")\n");
		parser.getDomain().getActions().forEach(op -> {
			List<String> actions = opNames.get(op.getName().toString());
			//Start action
			str.append("(:action "+actions.get(0)+"\n");
			str.append("\t:parameters (");
			op.getParameters().forEach(p -> {
				str.append(" "+op.getParameter(p));
			});
			//System.out.println(paramInv);
			str.append(" )\n");
			str.append("\t:precondition (and\n");
			classical.get(actions.get(0)).getX().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			str.append("\t)\n");
			str.append("\t:effect (and\n");
			classical.get(actions.get(0)).getY().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			str.append("\t)\n)\n");
			//End action
			str.append("(:action "+actions.get(1)+"\n");
			str.append("\t:parameters (");
			op.getParameters().forEach(p -> {
				str.append(" "+op.getParameter(p));
			});
			str.append(" )\n");
			str.append("\t:precondition (and\n");
			classical.get(actions.get(1)).getX().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			str.append("\t)\n");
			str.append("\t:effect (and\n");
			classical.get(actions.get(1)).getY().forEach(p -> {
				str.append("\t\t"+p+"\n");
			});
			str.append("\t)\n)\n");
			
		});
		str.append(")");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

			bw.write(str.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		File file = new File(filename);
		return file;
	}
	
	/**
	 * 
	 * @param classical
	 * @param durations
	 * @param filename
	 * @return
	 */
	public static File translate2Op(File classical, Map<String, Float> durations,
			String filename) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(classical);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();
		Map<String, PDDLAction> mapDurativeClassical = new HashMap<>();
		
		//Get durative operators
		Map<String, String> opNames = new HashMap<>();
		for(PDDLAction op : operators) {
			String str = new String(op.getName().toString());
			String[] tmp = str.split("-");
			int s = tmp[tmp.length-1].length();
			str = str.substring(0, str.length()-s-1);
			opNames.put(op.getName().toString(), str);
			mapDurativeClassical.put(str, op);
		}
		//System.out.println(extraDumpyPropositions);
		//Get preconditions
		Map<String, List<String>> startPreconditions = new HashMap<>();
		Map<String, List<String>> endPreconditions = new HashMap<>();
		Map<String, List<String>> overAllPreconditions = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();
			String[] tmp = name.split("-");
			switch(tmp[tmp.length-1]) {
			case "start":
				startPreconditions.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getPreconditions().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						startPreconditions.get(opNames.get(name)).add(exp.toString());
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						startPreconditions.get(opNames.get(name)).add(exp.toString());
						break;
					default:
						break;
					}
				}
				break;
			case "end":
				endPreconditions.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getPreconditions().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						endPreconditions.get(opNames.get(name)).add(exp.toString());
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						endPreconditions.get(opNames.get(name)).add(exp.toString());
						break;
					default:
						break;
					}
				}
				break;
			}
		}

		//System.out.println(endPreconditions);
		//Get effects
		Map<String, List<String>> startEffects = new HashMap<>();
		Map<String, List<String>> startNegEffects = new HashMap<>();
		Map<String, List<String>> endEffects = new HashMap<>();
		for(PDDLAction op : operators) {
			String name = op.getName().toString();
			String[] tmp = name.split("-");
			switch(tmp[tmp.length-1]) {
			case "start":
				startEffects.put(opNames.get(name), new LinkedList<>());
				startNegEffects.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getEffects().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						startEffects.get(opNames.get(name)).add(exp.toString());
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						startEffects.get(opNames.get(name)).add(exp.toString());
						startNegEffects.get(opNames.get(name)).add(exp.toString());
						break;
					default:
						break;
					}
				}
				break;
			case "end":
				endEffects.put(opNames.get(name), new LinkedList<>());
				for(PDDLExpression exp : op.getEffects().getChildren()) {
					switch(exp.getConnective()) {
					case ATOM:
						String prec = exp.toString();
						String str = exp.getAtom().get(0).toString();
						endEffects.get(opNames.get(name)).add(exp.toString());
						break;
					case NOT:
						prec = exp.getChildren().get(0).toString();
						str =  exp.getChildren().get(0).getAtom().get(0).toString();
						endEffects.get(opNames.get(name)).add(exp.toString());
						break;
					default:
						break;
					}
				}
				break;
			}
		}
		//System.out.println(endPreconditions);
		for(String name : startPreconditions.keySet()) {
			overAllPreconditions.put(name, new ArrayList<>());
			for(String prec : startPreconditions.get(name)) {
				if(endPreconditions.get(name).contains(prec)) {
					overAllPreconditions.get(name).add(prec);
				}
			}
			for(String prec : overAllPreconditions.get(name)) {
				endPreconditions.get(name).remove(prec);
				startPreconditions.get(name).remove(prec);
			}
		}
		for(String name : startPreconditions.keySet()) {
			for(String eff : startNegEffects.get(name)) {
				endPreconditions.get(name).remove(eff);
			}
			
		}
		//System.out.println(endPreconditions);
		//Get Type Hierarchy
		Map<String, String> hier = new HashMap<>();
		for(PDDLTypedSymbol t : parser.getDomain().getTypes()) {
			if(!t.getTypes().isEmpty()) {
				hier.put(t.getImage().toLowerCase(),
						t.getTypes().get(0).getImage().toLowerCase());
			}
		}
		TypeHierarchy types = new TypeHierarchy(hier);

		//Write Temporal domain
		StringBuilder str = new StringBuilder();
		str.append("(define (domain "+parser.getDomain().getName().getImage()+")\n");
		str.append("(:requirements :strips :typing :negative-preconditions :durative-actions)\n");
		str.append("(:types \n"+types+")");
		str.append("(:predicates\n");
		parser.getDomain().getPredicates().forEach(p -> {
			str.append("\t"+p.toString()+"\n");
		});
		str.append(")\n");
		startPreconditions.keySet().forEach(op -> {
			//Begin operator declaration
			str.append("(:durative-action "+op+"\n");
			//PArameter
			str.append("\t:parameters (");
			StringBuilder paramInv = new StringBuilder();
			mapDurativeClassical.get(op).getParameters().forEach(p -> {
				str.append(" "+mapDurativeClassical.get(op).getParameter(p));
				paramInv.append(" "+p.getImage());
			});
			str.append(" )\n");
			//Duration
			str.append("\t:duration (= ?duration "+durations.get(op)+")\n");
			//Conditions
			str.append("\t:condition (and\n");
			startPreconditions.get(op).forEach(prec -> {
				if(! overAllPreconditions.get(op).contains(prec) &&
						! endPreconditions.get(op).contains(prec)) {
					str.append("\t\t(at start "+prec+" )\n");
				}
			});
			overAllPreconditions.get(op).forEach(prec -> {
				if(! startEffects.get(op).contains(prec)) {
					str.append("\t\t(over all "+prec+" )\n");
				}
			});
			endPreconditions.get(op).forEach(prec -> {
				if(! overAllPreconditions.get(op).contains(prec) &&
						! startPreconditions.get(op).contains(prec)) {
					str.append("\t\t(at end "+prec+" )\n");
				}
			});
			str.append("\t)\n");
			//Effects
			str.append("\t:effect (and\n");
			startEffects.get(op).forEach(eff -> {
				str.append("\t\t(at start "+eff+" )\n");
			});
			endEffects.get(op).forEach(eff -> {
				str.append("\t\t(at end "+eff+" )\n");
			});
			str.append("\t)\n)\n");
		});
		str.append(")");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

			bw.write(str.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		File file = new File(filename);
		return file;
	}
}
