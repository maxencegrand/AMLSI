/**
 * 
 */
package simulator.temporal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.uga.pddl4j.parser.PDDLAction;
import fr.uga.pddl4j.parser.PDDLExpression;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.parser.PDDLProblem;
import fr.uga.pddl4j.parser.PDDLSymbol;
import fr.uga.pddl4j.parser.PDDLTypedSymbol;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.ADLProblem;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.SimpleTemporalProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.problem.operator.ConditionalEffect;
import fr.uga.pddl4j.problem.operator.Effect;
import fr.uga.pddl4j.util.BitVector;
import fsm.Pair;
import fsm.Predicate;
import fsm.Symbol;
import learning.Observation;
import learning.TypeHierarchy;
import main.Argument;
import simulator.Pddl4jUtils;

/**
 * @author Maxence Grand
 *
 */
public class TemporalBlackBox extends TemporalOracle{
	/**
	 * 
	 */
	protected float lastTimeEndEffect;

	/**
	 * The initial states
	 */
	protected Node initialState;
	/**
	 * The initial states
	 */
	protected Node currentState;
	/**
	 * The initial states
	 */
	protected float currentTime;

	/**
	 * The problem encoding
	 */
	protected ADLProblem cp;

	/**
	 * All actions
	 */
	protected List<Symbol> allActions;
	/**
	 * 
	 */
	protected Map<Symbol, TemporalAction> tableSymbolInstAction;

	/**
	 * 
	 */
	protected List<Symbol> fluents;
	/**
	 * 
	 */
	protected List<Symbol> staticPredicates;
	/**
	 * 
	 */
	protected Map<Fluent, Symbol> tableFluentSymbol;

	/**
	 * 
	 */
	protected Map<Float, TemporalAction> endEffectsToApply;
	/**
	 * 
	 */
	public TemporalAction nextAction;
	/**
	 * 
	 */
	public float nextTimestamp;
	/**
	 * 
	 */
	private TypeHierarchy hier;
	/**
	 * 
	 */
	private Map<String, String> types;
	private Map<Symbol, Integer> predicateIndex;

	/**
	 * Constructs the blackbox
	 * @param d the domain
	 * @param p the initial state
	 */
	public TemporalBlackBox(String d, String p){
		try{

			File domain = new File(d);
			File problem = new File(p);
			//ProblemFactory factory = ProblemFactory.getInstance();
			//factory.parse(domain, problem).printAll();
			PDDLParser parser = new PDDLParser();
			parser.parse(d, p);
			cp = new SimpleTemporalProblem(parser.getDomain(), parser.getProblem());
			cp.instantiate();
			//cp = factory.encode();

			if(cp == null){
				System.out.println("cp null "+d+" "+p);
				System.exit(1);
			}
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}

		State init = new State(cp.getInitialState());                                                                                                                        
		initialState = new Node(init);
		currentState = new Node(this.getInit());

		/*
		 * Retrieve all actions
		 */
		allActions = new ArrayList<>();
		tableSymbolInstAction = new HashMap<>();
		Map<String, Map<String, TemporalAction>> tmpTemporalAct = new HashMap<>();
		//		for (fr.uga.pddl4j.problem.operator.Action  op : cp.getActions()) {
		//			System.out.println(cp.toString(op));
		//		}
		for (fr.uga.pddl4j.problem.operator.Action  op : cp.getActions()) {
			String params= "";
			String[] tmp = op.getName().split("_");
			String name = op.getName().substring(0, op.getName().length()-tmp[tmp.length-1].length()-1);
			for (int i = 0; i < op.arity(); i++) {
				int index = op.getValueOfParameter(i);
				String param = cp.getConstantSymbols().get(index);
				params+= params.equals("") ? param : " "+param;
			}
			if(! tmpTemporalAct.containsKey(name)) {
				tmpTemporalAct.put(name, new HashMap<>());
			}
			if(! tmpTemporalAct.get(name).containsKey(params)) {
				tmpTemporalAct.get(name).put(params, new TemporalAction());
			}
			switch(tmp[tmp.length-1]) {
			case "start":
				tmpTemporalAct.get(name).get(params).setStart(op);
				break;
			case "end":
				tmpTemporalAct.get(name).get(params).setEnd(op);
				break;
			case "inv":
				tmpTemporalAct.get(name).get(params).setInv(op);
				break;
			}
			//tmpTemporalAct.get(name).get(params).setDuration(durations.get(name));
		}
		List<TemporalAction> temporalActions = new ArrayList<>();

		tmpTemporalAct.forEach((k,v) -> {
			v.forEach((kk,vv) -> {
				if(vv.isComplete()) {
					//System.out.println(vv.getInv().getDuration());
					vv.setDuration((float)vv.getStart().getDurationConstraints()
							.get(0).getRightExpression().getValue());
					temporalActions.add(vv);
					//					System.out.println("*************************************");
					//					System.out.println(vv.getName());
					//					System.out.println(cp.toString(vv.getStart()));
					//					System.out.println(cp.toString(vv.getInv()));
					//					System.out.println(cp.toString(vv.getEnd()));
				}
			});
		});
		//System.out.println(temporalActions.size());
		for (TemporalAction  op : temporalActions) {
			Map<String, String> paramMap = new LinkedHashMap<>();
			boolean b = true;
			for (int i = 0; i < op.getStart().arity(); i++) {
				int index = op.getStart().getValueOfParameter(i);
				int index2 = op.getStart().getTypeOfParameters(i);
				String param = cp.getConstantSymbols().get(index);
				//System.out.println(p+" "+);
				b &= (!paramMap.keySet().contains(param));
				paramMap.put(param, cp.getTypes().get(index2));
			}
			if(b) {
				fsm.Action act = new fsm.Action(op.getName(),paramMap);
				allActions.add(act);
				//System.out.println(act+" "+op.getDuration());
				tableSymbolInstAction.put(act, op);
			}
		}

		/*
		 * Retrieve all propositions and types
		 */
		staticPredicates = new ArrayList<>();
		List<Symbol> allSym = new ArrayList<>();
		List<Symbol> allFluents = new ArrayList<>();
		tableFluentSymbol=new HashMap<>();
		this.predicateIndex = new HashMap<>();
		types = new LinkedHashMap<>();
		try{
			PDDLParser parser = new PDDLParser();
			parser.parse(d,p);
			PDDLProblem pb = parser.getProblem();

			//Types
			for(PDDLTypedSymbol tt : pb.getObjects()) {
				types.put(tt.getImage(), tt.getTypes().get(0).getImage());
			}

			//Propositions fluents
			for(Fluent fluent : cp.getFluents()) {
				String name = Pddl4jUtils.getPredicateName(cp.toString(fluent));
				Map<String, String> paramMap = new LinkedHashMap<>();
				boolean b = true;
				//System.out.println(cp.getConstantSymbols()+" "+pb.getObjects());
				for(int idx : fluent.getArguments()) {
					String param = cp.getConstantSymbols().get(idx);
					String paramType = this.getParamTypes().get(param);
					b &= (!paramMap.keySet().contains(param));
					paramMap.put(param, paramType);
				}
				if(b) {
					Symbol pred = new Predicate(name, paramMap);
//					System.out.println(pred);
					this.predicateIndex.put(pred, cp.getFluents().indexOf(fluent));
					allSym.add(pred);
					switch(cp.getInertia().get(cp.getPredicateSymbols().indexOf(name))) {
					case INERTIA:
						staticPredicates.add(pred);
						this.tableFluentSymbol.put(fluent, pred);
						break;
					default:
						allFluents.add(pred);
						this.tableFluentSymbol.put(fluent, pred);
						break;
					}
				}		
			}

			//			//Propositions fluents
			//			for(Fluent fluent : cp.getFluents()) {
			//				String name = BlackBox.getPredicateName(cp.toString(fluent));
			//				Map<String, String> paramMap = new LinkedHashMap<>();
			//				boolean b = true;
			//				//System.out.println(cp.getConstantSymbols()+" "+pb.getObjects());
			//				for(int idx : fluent.getArguments()) {
			//					String param = cp.getConstantSymbols().get(idx);
			//					String paramType = this.types.get(param);
			//					b &= (!paramMap.keySet().contains(param));
			//					paramMap.put(param, paramType);
			//				}
			//				if(b) {
			//					Symbol pred = new Predicate(name, paramMap);
			//					allSym.add(pred);
			//					allFluents.add(pred);
			//					tableFluentSymbol.put(fluent, pred);
			//				}		
			//			}
			//			//Proposition static
			//			for(PDDLExpression exp : pb.getInit()) {
			//				Map<String, String> paramMap = new LinkedHashMap<>();
			//				String name = exp.getAtom().get(0).toString();
			//				for(int i = 1; i < exp.getAtom().size(); i++) {AMLSI
			//					String param = exp.getAtom().get(i).toString();
			//					String paramType = this.types.get(param);
			//					paramMap.put(param, paramType);
			//				}
			//				Symbol pred = new Predicate(name, paramMap);
			//				if(! allSym.contains(pred)) {
			//					staticPredicates.add(pred);
			//					allSym.add(pred);
			//				};
			//			}

		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}

		/**
		 * Compute type hierarchy
		 */
		hier = new TypeHierarchy();
		//allSym.forEach(pr -> System.out.println(pr.toStringType()));
		hier.infere(allSym, this.getParamTypes());
		allActions = hier.compute_litterals_operators
				(allActions, this.getParamTypes());
		staticPredicates = hier.compute_litterals_operators
				(staticPredicates, this.getParamTypes());
		fluents = hier.compute_litterals_operators
				(allFluents, this.getParamTypes());
		this.endEffectsToApply = new HashMap<>();
//		System.out.println(predicateIndex.keySet());
//		System.out.println(staticPredicates);
//		System.out.println(fluents);
		this.correction(d);
	}

	private void correction(String d) {
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(d);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Get all operators
		List<PDDLAction> operators = parser.getDomain().getActions();
		Map<Symbol, Observation> invPrec = new HashMap<>();
		Map<Symbol, Observation> atStartPrec = new HashMap<>();
		Map<Symbol, Observation> atStartEff = new HashMap<>();
		Map<Symbol, Observation> atEndPrec = new HashMap<>();
		Map<Symbol, Observation> atEndEff = new HashMap<>();
		for(PDDLAction operator : operators) {
			Map<String, String> p = new LinkedHashMap<>();
			operator.getParameters().forEach(pa -> {
				p.put(pa.getImage(), pa.getTypes().get(0).getImage());
			});
			Symbol op = new fsm.Action(operator.getName().getImage(), p);
			atStartPrec.put(op, new Observation());
			atEndPrec.put(op, new Observation());
			invPrec.put(op, new Observation());
			atStartEff.put(op, new Observation());
			atEndEff.put(op, new Observation());
			//			System.out.println(operator);
			for(PDDLExpression exp : 
				operator.getPreconditions().getChildren()) {
				Symbol pred = new fsm.Predicate();
				switch(exp.getConnective().getImage()) {
				case "at start":
					switch(exp.getChildren().get(0).getConnective()) {
					case ATOM:
						pred = this.getPredicate(
								exp.getChildren().get(0).getAtom(), 
								operator);
						atStartPrec.get(op).addTrueObservation(pred);
						break;
					case NOT:
						pred = this.getPredicate(
								exp.getChildren().get(0).getChildren().
								get(0).getAtom(), 
								operator);
						atStartPrec.get(op).addFalseObservation(pred);
						break;
					default:
						break;
					}
					break;
				case "at end":
					switch(exp.getChildren().get(0).getConnective()) {
					case ATOM:
						pred = this.getPredicate(
								exp.getChildren().get(0).getAtom(), 
								operator);
						atEndPrec.get(op).addTrueObservation(pred);
						break;
					case NOT:
						pred = this.getPredicate(
								exp.getChildren().get(0).getChildren().
								get(0).getAtom(), 
								operator);
						atEndPrec.get(op).addFalseObservation(pred);
						break;
					default:
						break;
					}
					break;
				case "over all":
					switch(exp.getChildren().get(0).getConnective()) {
					case ATOM:
						pred = this.getPredicate(
								exp.getChildren().get(0).getAtom(), 
								operator);
						invPrec.get(op).addTrueObservation(pred);
						break;
					case NOT:
						pred = this.getPredicate(
								exp.getChildren().get(0).getChildren().get(0).getAtom(), 
								operator);
						invPrec.get(op).addTrueObservation(pred);
						break;
					default:
						break;
					}
					break;
				}
			}
			for(PDDLExpression exp : 
				operator.getEffects().getChildren()) {
				Symbol pred = new fsm.Predicate();
				switch(exp.getConnective().getImage()) {
				case "at start":
					switch(exp.getChildren().get(0).getConnective()) {
					case ATOM:
						pred = this.getPredicate(
								exp.getChildren().get(0).getAtom(), 
								operator);
						atStartEff.get(op).addTrueObservation(pred);
						break;
					case NOT:
						pred = this.getPredicate(
								exp.getChildren().get(0).getChildren().
								get(0).getAtom(), 
								operator);
						atStartEff.get(op).addFalseObservation(pred);
						break;
					default:
						break;
					}
					break;
				case "at end":
					switch(exp.getChildren().get(0).getConnective()) {
					case ATOM:
						pred = this.getPredicate(
								exp.getChildren().get(0).getAtom(), 
								operator);
						atEndEff.get(op).addTrueObservation(pred);
						break;
					case NOT:
						pred = this.getPredicate(
								exp.getChildren().get(0).getChildren().
								get(0).getAtom(), 
								operator);
						atEndEff.get(op).addFalseObservation(pred);
						break;
					default:
						break;
					}
					break;
				default:
					//					
					break;
				}
			}
		}
		//		System.out.println(invPrec);
		//		System.out.println(atStartPrec);
		//		System.out.println(atEndPrec);
		//		System.out.println(atStartEff);
		//		System.out.println(atEndEff);
		for(PDDLAction operator : operators) {
			Map<String, String> p = new LinkedHashMap<>();
			operator.getParameters().forEach(pa -> {
				p.put(pa.getImage(), pa.getTypes().get(0).getImage());
			});
			Symbol op = new fsm.Action(operator.getName().getImage(), p);
			for(Symbol act : allActions) {
				if(act.getName().equals(op.getName())) {
					Map<String, String> param = new LinkedHashMap<>();
					for(int i = 0; i < operator.getParameters().size(); i++) {
						param.put(
								op.getListParameters().get(i), 
								act.getListParameters().get(i));
					}
//					System.out.println(param);
					//					this.tableSymbolInstAction.get(act).setDuration((float) ((double)operator.getDuration().getValue()));
					this.correction(
							atStartPrec.get(op).instanciate(param),
							atEndPrec.get(op).instanciate(param),
							invPrec.get(op).instanciate(param),
							atStartEff.get(op).instanciate(param),
							atEndEff.get(op).instanciate(param),
							this.tableSymbolInstAction.get(act));
				}
			}
		}
		//		System.out.println(this.allActions);
	}

	private void correction(
			Observation atStartPrec,
			Observation atEndPrec,
			Observation invPrec,
			Observation atStartEff,
			Observation atEndEff,
			TemporalAction temp
			) {
		//		System.out.println(temp.getName());
		
		Pair<BitVector, BitVector> newVec = null;
		Condition startPrec = temp.getAtStartPrecondition();
//				System.out.println("At Start Prec");
		newVec = this.getVector(
				atStartPrec, 
				startPrec.getPositiveFluents().size());
		startPrec = new Condition(newVec.getX(), newVec.getY());
		Condition endPrec = temp.getAtEndPrecondition();
//				System.out.println("At End Prec");
		newVec = this.getVector(
				atEndPrec, 
				endPrec.getPositiveFluents().size());
		endPrec = new Condition(newVec.getX(), newVec.getY());
		Condition inv = temp.getOverAllPrecondition();
//				System.out.println("Inv Prec");
		newVec = this.getVector(
				invPrec, 
				inv.getPositiveFluents().size());
		inv = new Condition(newVec.getX(), newVec.getY());
		Effect startEff = temp.getAtStartEffect();
//				System.out.println("At Start Eff");
		newVec = this.getVector(
				atStartEff, 
				startEff.getPositiveFluents().size());
		startEff = new Effect(newVec.getX(), newVec.getY());
		Effect endEff = temp.getAtStartEffect();
//				System.out.println("At End Eff");
		newVec = this.getVector(
				atEndEff, 
				endEff.getPositiveFluents().size());
		endEff = new Effect(newVec.getX(), newVec.getY());
		//		System.out.println(cp.toString(temp.getStart()));
		//		temp.setStart(new Action(
		//				temp.getStart().getName(), 
		//				temp.getStart().arity(),
		//				startPrec, 
		//				startEff));
		temp.getStart().setPrecondition(startPrec);
		temp.getStart().getConditionalEffects().clear();
		temp.getStart().addConditionalEffect(new ConditionalEffect(startEff));
		temp.getEnd().setPrecondition(endPrec);
		temp.getEnd().getConditionalEffects().clear();
		temp.getEnd().addConditionalEffect(new ConditionalEffect(endEff));
		//		System.err.println(cp.toString(temp.getInv()));
		temp.getInv().setPrecondition(inv);
		//		temp.getStart().
		//		temp.setEnd(new Action(
		//				temp.getEnd().getName(), 
		//				temp.getEnd().arity(), 
		//				endPrec, 
		//				endEff));
		//		temp.setInv(new Action(
		//				temp.getInv().getName(), 
		//				temp.getInv().arity(), 
		//				inv, 
		//				new Effect()));
//		System.out.println(cp.toString(temp.getStart()));
//		System.out.println(cp.toString(temp.getEnd()));
//		System.out.println(cp.toString(temp.getInv()));
//		System.out.println(temp.getDuration());
	}

	/**
	 * 
	 * @param o
	 * @param size
	 * @return
	 */
	private Pair<BitVector, BitVector> getVector(Observation o, int size) {
		final BitVector positive = new BitVector(size);
		final BitVector negative = new BitVector(size);
		o.getPredicates().forEach((k,v) -> {
			Integer idxFluent = this.predicateIndex.get(k);
			if(idxFluent != null) {
				switch(v) {
				case FALSE:
					negative.set(idxFluent, true);
					break;
				case TRUE:
					positive.set(idxFluent, true);
					break;
				case ANY:
				case MISSED:
				default:
					break;

				}
			}
		});
//				System.out.println(o);
//				System.out.println(this.predicateIndex);
//				System.out.println(positive);
//				System.out.println(negative);
		return new Pair<>(positive, negative);
	}

	/**
	 * 
	 * @param atom
	 * @param operator
	 * @return
	 */
	private Symbol getPredicate(List<PDDLSymbol> atom, PDDLAction operator) {
		String name = atom.get(0).getImage();
		Map<String, String> param = new LinkedHashMap<>();
		for(int i = 1; i < atom.size(); i++) {
			param.put(
					atom.get(i).getImage(), 
					operator.getParameter(atom.get(i)).getTypes().get(0).getImage());
		}
		return new fsm.Predicate(name, param);
	}
	/**
	 * Apply the selected action (start effects)
	 * @return the resulting state
	 */
	@Override
	public Node apply() {
		if(this.nextAction == null) {
			return null;
		}
		Node current = new Node(this.currentState);
		Effect eff  = nextAction.getAtStartEffect();
		//		System.out.println("---------------------------"+this.nextAction.getName()+"_START-"+currentTime+"------------------------------");
		//		System.out.println(this.currentState);
		//		System.out.println("\t"+nextAction.getAtStartPrecondition().getPositiveFluents());
		//		System.out.println("\t"+nextAction.getAtStartPrecondition().getNegativeFluents());
		//		System.out.println("\t"+nextAction.getOverAllPrecondition().getPositiveFluents());
		//		System.out.println("\t"+nextAction.getOverAllPrecondition().getNegativeFluents());
		//		System.out.println("\t"+eff.getPositiveFluents());
		//		System.out.println("\t"+eff.getNegativeFluents());
		current.or(eff.getPositiveFluents());
		current.andNot(eff.getNegativeFluents());
		//System.out.println(current);
		this.currentState = new Node(current);
		//		System.out.println(this.currentState);
		float t = this.currentTime + this.nextAction.getDuration();
		while(this.endEffectsToApply.containsKey(t)) {
			t += TemporalOracle.TOLERANCE_VALUE;
		}
		this.endEffectsToApply.put(t, new TemporalAction(this.nextAction));
		this.nextAction = null;
		return new Node(current);
	}

	/**
	 * Reinitialize current state
	 */
	@Override
	public void reInit() {
		// TODO Auto-generated method stub
		//		this.getAllActions().forEach(a -> {
		//			System.out.println(a);
		//			System.out.println(this.tableSymbolInstAction.get(a).getAtStartPrecondition());
		//			System.out.println(this.tableSymbolInstAction.get(a).getAtEndPrecondition());
		//			System.out.println(this.tableSymbolInstAction.get(a).getAtStartEffect());
		//			System.out.println(this.tableSymbolInstAction.get(a).getAtEndEffect());
		//		});
		this.currentState = new Node(this.initialState);
		this.currentTime = 0f;

	}

	@Override
	public Node getInitialState() {
		// TODO Auto-generated method stub
		return this.initialState;
	}

	/**
	 * Get the initial state
	 * @return the initial state
	 */
	@Override
	public Node getCurrentState() {
		// TODO Auto-generated method stub
		return this.currentState;
	}

	/**
	 * Get the current time
	 * @return the current time
	 */
	@Override
	public float getCurrentTime() {
		// TODO Auto-generated method stub
		return this.currentTime;
	}
	/**
	 * Test if an action is feasible in the current state
	 * @param op an action
	 * @param t the timestamp
	 * @return True the action is feasible
	 */
	@Override
	public boolean isApplicable(Symbol op, float t) {
		TemporalAction a = this.tableSymbolInstAction.get(op);
		while(this.endEffectsToApply.containsKey(t)) {
			t += TemporalOracle.TOLERANCE_VALUE;
		}
		this.currentTime = t;
		this.applyAllEffectsBeforeDeadline(this.currentTime);
		//Check that current node satisfy at start preconditions
		boolean b = true;

		//		System.out.println("---------------------------"+a.getName()+"_START-"+t+"------------------------------");
		//		System.out.println(this.currentState);
		//		System.out.println("\t"+a.getAtStartPrecondition().getPositiveFluents());//+" "+this.currentState.include(a.getAtStartPrecondition().getPositiveFluents()));
		//		System.out.println("\t"+a.getAtStartPrecondition().getNegativeFluents());//+" "+this.currentState.exclude(a.getAtStartPrecondition().getNegativeFluents()));
		//		System.out.println("\t"+a.getOverAllPrecondition().getPositiveFluents());//+" "+this.currentState.include(a.getOverAllPrecondition().getPositiveFluents()));
		//		System.out.println("\t"+a.getOverAllPrecondition().getNegativeFluents());//+" "+this.currentState.exclude(a.getOverAllPrecondition().getNegativeFluents()));

		b &= this.currentState.include(a.getAtStartPrecondition().getPositiveFluents());
		b &= this.currentState.exclude(a.getAtStartPrecondition().getNegativeFluents());
		b &= this.currentState.include(a.getOverAllPrecondition().getPositiveFluents());
		b &= this.currentState.exclude(a.getOverAllPrecondition().getNegativeFluents());
		//System.err.println(b);
		if(b) {
			if(this.consistantWithCurrentActions(a, t)) {
				this.nextAction = a;
				//this.currentTime = t;
				//System.out.println(this.tableSymbolInstAction);
				//				System.out.println("\t"+a.getAtStartEffect().getPositiveFluents());
				//				System.out.println("\t"+a.getAtStartEffect().getNegativeFluents());
				//				System.out.println("\t"+a.getAtEndEffect().getPositiveFluents());
				//				System.out.println("\t"+a.getAtEndEffect().getNegativeFluents());
				return true;
			}

		}
		this.nextAction = null;
		return false;
	}

	/**
	 * Get all predicates present in the state
	 * @param state the state
	 * @return a set of predicate
	 */
	@Override
	public List<Symbol> getSymbolsState(Node state) {
		List<Symbol> res = new ArrayList<>();
		for (int i = state.nextSetBit(0); i >= 0; i = state.nextSetBit(i + 1)) {
			Symbol p = tableFluentSymbol.get(cp.getFluents().get(i));
			res.add(p);
		}
		return res;
	}

	/**
	 * Get all predicates
	 * @return all predicates
	 */
	@Override
	public List<Symbol> getAllPredicates() {
		// TODO Auto-generated method stub
		return fluents;
	}

	/**
	 * Return all actions
	 * @return all actions
	 */
	@Override
	public List<Symbol> getAllActions() {
		// TODO Auto-generated method stub
		return allActions;
	}

	/**
	 * Get all types
	 * @return all types
	 */
	@Override
	public List<String> getTypes() {
		// TODO Auto-generated method stub
		List<String> res = new ArrayList<>();
		types.forEach((s1, s2) ->{
			if(! res.contains(s2)) {
				res.add(s2);
			}
		});
		return res;
	}

	/**
	 * Get positive static predicates
	 * @return positive static predicates
	 */
	@Override
	public List<Symbol> getPositiveStaticPredicate() {
		// TODO Auto-generated method stub
		return this.staticPredicates;
	}

	/**
	 * Check if an example is feasible starting by the initial state
	 * @param example to test
	 * @param All timestamps
	 * @return True if the example is feasible
	 */
	@Override
	public boolean accept(List<Symbol> example, List<Float> timestamps) {
		boolean b = true;
		if(example.size() != timestamps.size()) {
			return false;
		}
		for(int i =0; i < example.size(); i++) {
			b &= this.accept(example.get(i), timestamps.get(i));
		}
		return b;
	}

	/**
	 * Check if an action is feasible in the current state and apply the action
	 * @param example action to test
	 * @param t the timestamp
	 * @return True if the action is feasible
	 */
	@Override
	public boolean accept(Symbol example, float t) {
		if(this.isApplicable(example, t)) {
			this.apply();
			return true;
		}
		return false;
	}

	/**
	 * Get all parameters with types
	 * @return All typed parameters
	 */
	@Override
	public Map<String, String> getParamTypes() {
		// TODO Auto-generated method stub
		return this.types;
	}

	/**
	 * Get the type hierarchy
	 * @return The type Hierarchy
	 */
	@Override
	public TypeHierarchy typeHierarchy() {
		// TODO Auto-generated method stub
		return this.hier;
	}

	/**
	 * 
	 * @param deadline
	 */
	private void applyAllEffectsBeforeDeadline(float deadline) {
		List<Float> t = new ArrayList<>(this.endEffectsToApply.keySet());
		Collections.sort(t);
		for(int i = 0; i<t.size(); i++) {
			//System.out.println("deadline = "+deadline+" next end "+t.get(i));
			if(t.get(i) > deadline) {
				break;
			}
			TemporalAction a = this.endEffectsToApply.get(t.get(i));
			Node current = new Node(this.currentState);
			Effect eff = a.getAtEndEffect();
			//System.out.println("---------------------------"+a.getName()+"_END-"+t.get(i)+"-----------------------------");
			//System.out.println(this.currentState);
			/*System.out.println("\t"+eff.getPositiveFluents());
			System.out.println("\t"+eff.getNegativeFluents());*/
			current.or(eff.getPositiveFluents());
			current.andNot(eff.getNegativeFluents());
			this.currentState = new Node(current);
			//System.out.println(this.currentState);
			this.currentTime = t.get(i)+TemporalOracle.TOLERANCE_VALUE;
			deadline = deadline < this.currentTime ? this.currentTime : deadline;
			this.endEffectsToApply.remove(t.get(i));
		}
	}

	/**
	 * 
	 * @param a
	 * @param t
	 * @return
	 */
	private boolean consistantWithCurrentActions(TemporalAction a, float t) {
		float endNewAction = t + a.getDuration();
		//Ordered timestamps
		List<Float> timestamps = new ArrayList<>(this.endEffectsToApply.keySet());
		Collections.sort(timestamps);

		//Check overall current actions
		BitVector b1 = a.getAtStartEffect().getPositiveFluents();
		BitVector b2 = a.getAtStartEffect().getNegativeFluents();
		Node tmp = new Node(currentState);
		tmp.or(b1);
		tmp.andNot(b2);
		for(int i = 0; i<timestamps.size(); i++) {
			float t1 = timestamps.get(i);
			//if(t1 > endNewAction) {
			BitVector b3 = this.endEffectsToApply.get(t1).getOverAllPrecondition().getPositiveFluents();
			BitVector b4 = this.endEffectsToApply.get(t1).getOverAllPrecondition().getNegativeFluents();

			if(!(tmp.include(b3) && tmp.exclude(b4))) {
				return false;
			}
			//}
		}
		b1 = a.getAtEndEffect().getPositiveFluents();
		b2 = a.getAtEndEffect().getNegativeFluents();
		tmp = new Node(currentState);
		tmp.or(b1);
		tmp.andNot(b2);
		for(int i = 0; i<timestamps.size(); i++) {
			float t1 = timestamps.get(i);
			if(t1 > endNewAction) {
				BitVector b3 = this.endEffectsToApply.get(t1).getOverAllPrecondition().getPositiveFluents();
				BitVector b4 = this.endEffectsToApply.get(t1).getOverAllPrecondition().getNegativeFluents();

				if(!(tmp.include(b3) && tmp.exclude(b4))) {
					return false;
				}
			}
		}

		//Check overall new action
		b1 = a.getAtStartEffect().getPositiveFluents();
		b2 = a.getAtStartEffect().getNegativeFluents();
		BitVector b3 = a.getOverAllPrecondition().getPositiveFluents();
		BitVector b4 = a.getOverAllPrecondition().getNegativeFluents();
		tmp = new Node(currentState);
		tmp.or(b1);
		tmp.andNot(b2);
		if(!(tmp.include(b3) && tmp.exclude(b4))) {
			return false;
		}
		for(int i = 0; i<timestamps.size(); i++) {
			float t1 = timestamps.get(i);
			//Check at end effects if the new action ends before
			if(t1 <= endNewAction) {
				b1 = this.endEffectsToApply.get(t1).getAtEndEffect().getPositiveFluents();
				b2 = this.endEffectsToApply.get(t1).getAtEndEffect().getNegativeFluents();
				tmp = new Node(currentState);
				tmp.or(b1);
				tmp.andNot(b2);
				if(!(tmp.include(b3) && tmp.exclude(b4))) {
					return false;
				}
			}
		}

		//Check at end preconditions for all actions
		tmp = new Node(currentState);
		b1 = a.getAtStartEffect().getPositiveFluents();
		b2 = a.getAtStartEffect().getNegativeFluents();
		tmp.or(b1);
		tmp.andNot(b2);
		for(int i = 0; i<timestamps.size(); i++) {
			float t1 = timestamps.get(i);

			b3 = this.endEffectsToApply.get(t1).getAtEndPrecondition().getPositiveFluents();
			b4 = this.endEffectsToApply.get(t1).getAtEndPrecondition().getNegativeFluents();
			if(!(tmp.include(b3) && tmp.exclude(b4))) {
				return false;
			}
			b1 = this.endEffectsToApply.get(t1).getAtEndEffect().getPositiveFluents();
			b2 = this.endEffectsToApply.get(t1).getAtEndEffect().getNegativeFluents();
			tmp.or(b1);
			tmp.andNot(b2);
		}

		return true;
	}

	/**
	 * Finish the simulation
	 */
	public void endSimulation() {
		while(this.executeNextEnd());
	}

	/**
	 * 
	 * @return
	 */
	private Node getInit() {
		return this.initialState;
	}

	/**
	 * Execute the next end action
	 * 
	 * @return True if there exist at least one current action
	 */
	@Override
	public boolean executeNextEnd() {
		List<Float> t = new ArrayList<>(this.endEffectsToApply.keySet());
		Collections.sort(t);
		if(t.isEmpty()) {
			return false;
		}
		//System.err.println(t);
		this.applyAllEffectsBeforeDeadline(t.get(0));
		return true;
	}

	/**
	 * GTet the duration of an action op
	 * @param op The action
	 * @return The duration
	 */
	@Override
	public float getDuration(Symbol op) {
		//System.out.println(op+" "+tableSymbolInstAction);
		return this.tableSymbolInstAction.get(op).getDuration();
	}

	@Override
	public boolean acceptAll(List<TemporalExample> S) {
		for(TemporalExample s : S) {
			this.reInit();
			List<Float> t = new ArrayList<>(s.keySet());
			Collections.sort(t);
			for(float timestamp: t) {
				//System.out.print(timestamp+" "+s.get(timestamp)+" ");
				if(this.isApplicable(s.get(timestamp),	timestamp)) {
					this.apply();
					//System.out.println();
				} else {
					//System.out.println("Fail !!!");
					this.endSimulation();
					return false;
				}
			}
			this.endSimulation();
		}
		return true;
	}

	@Override
	public boolean rejectAll(List<TemporalExample> S) {
		for(TemporalExample s : S) {
			this.reInit();
			List<Float> t = new ArrayList<>(s.keySet());
			Collections.sort(t);
			boolean b = false;
			int i = 0;
			for(float timestamp: t) {
				//System.out.println(timestamp+" "+s.get(timestamp));
				if(this.isApplicable(s.get(timestamp),	timestamp)) {
					this.apply();
					b=true;
					i++;
				} else {
					b=false;
					break;
				}
			}
			this.endSimulation();
			if(b) {
				return false;
			}
			this.endSimulation();
		}
		return true;
	}

	@Override
	public boolean rejectLast(TemporalExample s) {
		this.reInit();
		List<Float> t = new ArrayList<>(s.keySet());
		Collections.sort(t);
		boolean b = false;
		int i = 0;
		for(float timestamp: t) {
			//System.out.println(timestamp+" "+s.get(timestamp));
			if(this.isApplicable(s.get(timestamp),	timestamp)) {
				this.apply();
				b=true;
				i++;
			} else if (i == t.size()-1){
				b=false;
				break;
			} else {
				break;
			}
		}
		this.endSimulation();
		if(b) {
			return false;
		}
		return true;
	}

	@Override
	public boolean accept(TemporalExample s) {
		this.reInit();
		List<Float> t = new ArrayList<>(s.keySet());
		Collections.sort(t);
		boolean b = false;
		int i = 0;
		for(float timestamp: t) {
			System.out.println(timestamp+" "+s.get(timestamp));
			if(this.isApplicable(s.get(timestamp),	timestamp)) {
				this.apply();
				b=true;
				i++;
			} else {
				this.endSimulation();
				return false;
			}
		}
		this.endSimulation();

		return true;
	}

	public Node apply_sequential(Symbol op) {
		Node current = new Node(this.currentState);
		/*System.out.println(op);
		System.out.println(this.getSplitTable());
		System.out.println(this.getSplitTable().get(op));*/
		Effect eff  = this.getSplitTable().get(op).getUnconditionalEffect();

		current.or(eff.getPositiveFluents());
		current.andNot(eff.getNegativeFluents());
		//System.out.println(current);
		this.currentState = new Node(current);

		return new Node(current);
	}

	/**
	 * 
	 * @return
	 */
	public Map<Symbol, Action> getSplitTable() {
		Map<Symbol, Action> table = new HashMap<>();
		this.tableSymbolInstAction.forEach((s,ta) -> {
			Symbol start = s.clone();
			start.setName(start.getName()+"-start");
			Symbol end = s.clone();
			end.setName(end.getName()+"-end");
			table.put(start, ta.getStart());
			table.put(end, ta.getEnd());
			if(!Argument.isTwoOp()) {
				Symbol inv = s.clone();
				inv.setName(inv.getName()+"-inv");
				table.put(inv, ta.getInv());
			}
		});
		return table;
	}

	public List<Symbol> getSeqActions() {
		List<Symbol> list= new ArrayList<>();
		this.tableSymbolInstAction.forEach((s,ta) -> {
			Symbol start = s.clone();
			start.setName(start.getName()+"-start");
			Symbol end = s.clone();
			end.setName(end.getName()+"-end");
			list.add(start);
			list.add(end);
		});
		return list;
	}
}

