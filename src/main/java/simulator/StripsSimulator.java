/**
 * 
 */
package simulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import exception.PlanException;
import fr.uga.pddl4j.problem.operator.Action;
import fsm.Predicate;
import fsm.Symbol;
import learning.Observation;
import utils.BiMap;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.parser.PDDLProblem;
import fr.uga.pddl4j.parser.PDDLTypedSymbol;
import fr.uga.pddl4j.planners.ProblemFactory;
import fr.uga.pddl4j.planners.statespace.search.Node;
import fr.uga.pddl4j.problem.ADLProblem;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.State;

/**
 * @author Maxence Grand
 *
 */
public class StripsSimulator extends Simulator{
	/**
	 * Actions table
	 */
	private BiMap<fsm.Symbol, Action> tableActions;
	/**
	 * Fluents table
	 */
	private BiMap<fsm.Symbol, Fluent> tableFluents;
	/**
	 * Internal initial state
	 */
	private Node internalInit;
	/**
	 * Internal current state
	 */
	private Node internalCurrent;
	/**
	 * Internal fluents representations
	 */
	private List<Fluent> internalFluents;

	/**
	 * 
	 * Constructs
	 */
	public StripsSimulator() {
		super();
		this.tableActions = new BiMap<>();
		this.tableFluents = new BiMap<>();
		this.internalInit = new Node(new State());
		this.internalCurrent = new Node(new State());
		this.internalFluents = new LinkedList<>();
	}

	/**
	 * 
	 * Constructs 
	 * @param d The PDDL domain
	 * @param p The PDDL problem
	 */
	public StripsSimulator(String d, String p){
		this();
		ADLProblem instance;
		try{

			File domain = new File(d);
			File problem = new File(p);
			ProblemFactory factory = ProblemFactory.getInstance();
			factory.parse(domain, problem);
			instance = new ADLProblem(factory.getParser().getDomain(), factory.getParser().getProblem());
			instance.instantiate();

			State init = new State(instance.getInitialState());                                                                                                                        
			Node root = new Node(init);
			this.setInternalInit(new Node(root));
			this.setInternalInit(new Node(root));


			/*
			 * Retrieve all actions
			 */
			List<Symbol> allActions = new ArrayList<>();
			for (Action  op : instance.getActions()) {
				Map<String, String> paramMap = new LinkedHashMap<>();
				boolean b = true;
				for (int i = 0; i < op.arity(); i++) {
					int index = op.getValueOfParameter(i);
					int index2 = op.getTypeOfParameters(i);
					String param = instance.getConstantSymbols().get(index);
					b &= (!paramMap.keySet().contains(param));
					paramMap.put(param, instance.getTypes().get(index2));
				}
				if(b) {
					fsm.Action act = new fsm.Action(op.getName(),paramMap);
					allActions.add(act);
					this.getTableActions().put(act, op);
				}
			}

			/*
			 * Retrieve all propositions and types
			 */
			List<Symbol> allSym = new ArrayList<>();
			List<Symbol> allFluents = new ArrayList<>();
			List<Symbol> allStatics = new ArrayList<>();
			Map<String, String> constants = new HashMap<>(); 
			try{
				PDDLParser parser = new PDDLParser();
				parser.parse(d,p);
				PDDLProblem pb = parser.getProblem();
				
				
				//Types
				for(PDDLTypedSymbol tt : pb.getObjects()) {
					this.getParamTypes().put(tt.getImage(), tt.getTypes().get(0).getImage());
				}

				parser.getDomain().getConstants().forEach(cst -> {
					String strName= cst.getImage();
					String t = cst.getTypes().get(0).getImage().toLowerCase();
					this.getParamTypes().put(strName, t);
				});
				
				//Propositions fluents
				for(Fluent fluent : instance.getFluents()) {
					String name = Pddl4jUtils.getPredicateName(instance.toString(fluent));
					Map<String, String> paramMap = new LinkedHashMap<>();
					boolean b = true;
					for(int idx : fluent.getArguments()) {
						String param = instance.getConstantSymbols().get(idx);
						String paramType = this.getParamTypes().get(param);
						b &= (!paramMap.keySet().contains(param));
						paramMap.put(param, paramType);
					}
					if(b) {
						Symbol pred = new Predicate(name, paramMap);
						allSym.add(pred);
						switch(instance.getInertia().get(instance.getPredicateSymbols().indexOf(name))) {
						case INERTIA:
							allStatics.add(pred);
							break;
						default:
							allFluents.add(pred);
							break;
						}
						this.getTableFluents().put(pred, fluent);
					}		
				}

			} catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}

			/**
			 * Compute type hierarchy
			 */
			this.getHierarchy().infere(allSym, this.getParamTypes());
			this.setActions(this.getHierarchy().compute_litterals_operators
					(allActions, this.getParamTypes()));
			this.setPositiveStaticPredicates(
					this.getHierarchy().compute_litterals_operators
					(allStatics, this.getParamTypes()));
			this.setPredicates(this.getHierarchy().compute_litterals_operators
					(allFluents, this.getParamTypes()));
			
			for(Fluent f : instance.getFluents()) {
				this.internalFluents.add(f);
			}
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Test action's feasability
	 * @param op an action
	 * @throws PlanException
	 */
	@Override
	public boolean testAction(fsm.Symbol action) {
		if(this.tableActions.getLeft(action) == null) {
			return false;
		}
		return this.tableActions.getLeft(action).isApplicable(internalCurrent);
	}

	/**
	 * 
	 */
	@Override
	public void apply(fsm.Symbol op) throws PlanException {
		if(this.testAction(op)) {
			Action ops = this.tableActions.getLeft(op);
			this.internalCurrent.apply(ops.getUnconditionalEffect());
			this.internalCurrent.apply(ops.getConditionalEffects());
//			List<ConditionalEffect> toApply = new ArrayList<>();
//			ops.getConditionalEffects().forEach(ce -> {
//				if(this.internalCurrent.satisfy(ce.getCondition())) {
//					toApply.add(ce);
//				}
//			});
//			toApply.forEach(ce -> this.internalCurrent.apply(ce.getEffect()));
		} else {
			throw new PlanException();
		}

	}

	/**
	 * 
	 */
	@Override
	public void reInit() {
		this.setInternalCurrent(new Node(this.internalInit));	
	}

	/**
	 * Getter of tableActions
	 * @return the tableActions
	 */
	public BiMap<fsm.Symbol, Action> getTableActions() {
		return tableActions;
	}

	/**
	 * Setter tableActions
	 * @param tableActions the tableActions to set
	 */
	public void setTableActions(BiMap<fsm.Symbol, Action> tableActions) {
		this.tableActions = tableActions;
	}

	/**
	 * Getter of tableFluents
	 * @return the tableFluents
	 */
	public BiMap<fsm.Symbol, Fluent> getTableFluents() {
		return tableFluents;
	}

	/**
	 * Setter tableFluents
	 * @param tableFluents the tableFluents to set
	 */
	public void setTableFluents(BiMap<fsm.Symbol, Fluent> tableFluents) {
		this.tableFluents = tableFluents;
	}

	/**
	 * Getter of internalInit
	 * @return the internalInit
	 */
	public Node getInternalInit() {
		return internalInit;
	}

	/**
	 * Setter internalInit
	 * @param internalInit the internalInit to set
	 */
	public void setInternalInit(Node internalInit) {
		this.internalInit = internalInit;
	}

	/**
	 * Getter of internalCurrent
	 * @return the internalCurrent
	 */
	public Node getInternalCurrent() {
		return internalCurrent;
	}

	/**
	 * Setter internalCurrent
	 * @param internalCurrent the internalCurrent to set
	 */
	public void setInternalCurrent(Node internalCurrent) {
		this.internalCurrent = internalCurrent;
	}

	/**
	 * Get observed state
	 * @param n The Internal state
	 * @return The observed state
	 */
	private Observation getObservedState(State n) {
		Observation res = new Observation(this.getPredicates());
		for(fsm.Symbol p : this.getPositiveStaticPredicates()) {
			res.addTrueObservation(p);
		}
		for (int i = n.nextSetBit(0); i >= 0; i = n.nextSetBit(i + 1)) {
			Symbol p = this.tableFluents.getRight(this.internalFluents.get(i));
			if(p != null) {
				res.addTrueObservation(p);
			}
		}
		return res;
	}

	/**
	 * Get the current state
	 * @return current state
	 */
	public Observation getCurrentState() {
		return this.getObservedState(new State(this.getInternalCurrent()));
	}

	/**
	 * Get the initial state
	 * @return initial state
	 */
	public Observation getInitialState() {
		return this.getObservedState(new State(this.getInternalInit()));
	}

	/**
	 * Getter of internal Fluents
	 * @return the internal Fluents
	 */
	public List<Fluent> getInternalFluents() {
		return internalFluents;
	}

	/**
	 * Setter internalFluents
	 * @param internalFluents the internalFluents to set
	 */
	public void setInternalFluents(List<Fluent> internalFluents) {
		this.internalFluents = internalFluents;
	}

}
