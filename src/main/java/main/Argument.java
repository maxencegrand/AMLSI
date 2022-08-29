package main;

/**
 * This class parse arguments
 * @author Grand Maxence
 */
public class Argument {
	/**
	 * Usage
	 */
	private static final String usage = 
			"USAGE :\n" +
					"-n, -name [string] : The planning domain's name.\n" + 
					"-t, -type [RPNI | RPNIPC] : the regular grammar induction algorithm used.\n" + 
					"-d, -directory [directory] : The directory where al logs and models are saved.\n"+
					"-o, -domain [file]: The file declaring the planning domain to encapsulate in the blackbox.\n" + 
					"-i, -initial [file] : The file declaring initial state. \n" + 
					"-r, -run [integer] : The number of runs for the experimentation. \n"+
					"-onlyTabu : AMLSI performs only the tabu search. \n"+
					"-withoutRefinement : AMLSI doesn't perform the refinement step. \n"+
					"-withoutTabu : AMLSI doesn't perform the tabu search during the refinement step. \n"+
					"-incremental : Incremental learning is performed. \n"+
					"-convergent : Convergent learning is performed. \n"+
					"-lsonio : LSO-NIO is performed. \n"+
					"-properties [file] : The file containing the different level of observability and noise. \n"+
					"-delta [integer] : The convergence criterion, only for Convergent learning. Delta must be greater than 0 and leather than 100.\n"+
					"-min [integer] : The The minimal size of examples.\n"+
					"-max [integer] : The The maximal size of examples.\n"+
					"-T [integer] : The number of iterations, only for Convergent and Incremental learning. T must be greater than 0."
					;
	
	/**
	 * Type of automaton induction
	 */
	public enum TYPE {
		/**
		 * RPNI algorithm
		 */
		RPNI,
		/**
		 * RPNI-PC algorithm
		 */
		RPNIPC
	}
	
	/**
	 * 
	 * @author Maxence Grand
	 *
	 */
	public enum STRATEGY {
		/**
		 * 
		 */
		MIN,
		/**
		 * 
		 */
		MAX
	}
	
	/**
	 * 
	 *
	 */
	public enum RECOMPOSITION_TYPE {
		/**
		 * 
		 */
		GREEDY,
		/**
		 * 
		 */
		HEURISTIC
	}
	
	/**
	 * Use planner 
	 */
	private static boolean planner=false;
	
	/**
	 * 
	 */
	private static boolean separate = false;
	
	/**
	 * is two op
	 */
	private static boolean twoOp = true;
	
	/**
	 * Max size
	 */
	private static int max = 20;
	
	/**
	 * Min size
	 */
	private static int min = 10;
	
	
	/**
	 * Getter of max
	 * @return the max
	 */
	public static int getMax() {
		return max;
	}
	/**
	 * Setter max
	 * @param max the max to set
	 */
	public static void setMax(int max) {
		Argument.max = max;
	}
	/**
	 * Getter of min
	 * @return the min
	 */
	public static int getMin() {
		return min;
	}
	/**
	 * Setter min
	 * @param min the min to set
	 */
	public static void setMin(int min) {
		Argument.min = min;
	}
	/**
	 * Getter of twoOp
	 * @return the twoOp
	 */
	public static boolean isTwoOp() {
		return twoOp;
	}
	/**
	 * Setter twoOp
	 * @param twoOp the twoOp to set
	 */
	public static void setTwoOp(boolean twoOp) {
		Argument.twoOp = twoOp;
	}
	/**
	 * Getter of planner
	 * @return the planner
	 */
	public static boolean isPlanner() {
		return planner;
	}
	/**
	 * Setter planner
	 * @param planner the planner to set
	 */
	public static void setPlanner(boolean planner) {
		Argument.planner = planner;
	}

	/**
	 * Type of automaton induction
	 */
	private static TYPE type = TYPE.RPNIPC;
	/**
	 * 
	 */
	private static RECOMPOSITION_TYPE rec_type = RECOMPOSITION_TYPE.HEURISTIC;
	/**
	 * 
	 */
	private static STRATEGY strategy = STRATEGY.MIN;
	/**
	 * If true only tabu search is done
	 */
	private static boolean onlyTabu = false;
	/**
	 * If true only tabu search is done
	 */
	private static boolean temporal = false;
	/**
	 * If true hierarchical learning is done
	 */
	private static boolean hierarchical = false;
	/**
	 * If true active convergent learning is done
	 */
	private static boolean active = false;
	/**
	 * If true, AMLSI doesn't use Tabu Refinement
	 */
	private static boolean withoutTabu = false;
	/**
	 * If true, perform LSONIO
	 */
	private static boolean lsonio = false;
	/**
	 * If true, perform a preprocessig step during the mapping
	 */
	private static boolean preprocessing = false;
	/**
	 * If true, perform a preprocessig step during the mapping
	 */
	private static boolean grammar = false;
	/**
	 * If true, perform refinement
	 */
	private static boolean refinement = true;
	/**
	 * File containing Observabilty and noise levels
	 */
	private static String propertiesFile = "config.properties";
	/**
	 * Reference domain
	 */
	private static String domain = "";//
	/**
	 * Log directory
	 */
	private static String directory = ".";//
	/**
	 * Action model's name
	 */
	private static String name = "";//
	/**
	 * Initial state
	 */
	private static String problem = "";//
	/**
	 * Number of runs
	 */
	private static int run = 1;
	/**
	 * If true, perform Incremental AMLSI
	 */
	private static boolean incremental=false;
	/**
	 * If true, perform Convergent AMLSI
	 */
	private static boolean convergent=false;
	/**
	 * (Max) Number of iterations for (Convergent) Increment AMLSI
	 */
	private static int T=10;
	/**
	 * Convergence criterion
	 */
	private static int Delta=2;
	/**
	 * if true, test learnt domain from a specific initial state
	 */
	private static boolean hasSpecificTestInit = false;
	/**
	 * A a specific initial state
	 */
	private static String specificTestInit = "";
	/**
	 * 
	 */
	private static String taskState = "";
	/**
	 * 
	 */
	private static boolean onlyMethod = false;
	/**
	 * 
	 */
	private static boolean onlyActionModel = false;
	
	
	
	/**
	 * Getter of onlyActionModel
	 * @return the onlyActionModel
	 */
	public static boolean isOnlyActionModel() {
		return onlyActionModel;
	}
	/**
	 * Setter onlyActionModel
	 * @param onlyActionModel the onlyActionModel to set
	 */
	public static void setOnlyActionModel(boolean onlyActionModel) {
		Argument.onlyActionModel = onlyActionModel;
	}
	/**
	 * Getter of taskState
	 * @return the taskState
	 */
	public static String getTaskState() {
		return taskState;
	}
	/**
	 * Setter taskState
	 * @param taskState the taskState to set
	 */
	public static void setTaskState(String taskState) {
		Argument.taskState = taskState;
	}
	/**
	 * Getter of hierarchical
	 * @return the hierarchical
	 */
	public static boolean isHierarchical() {
		return hierarchical;
	}
	/**
	 * Setter hierarchical
	 * @param hierarchical the hierarchical to set
	 */
	public static void setHierarchical(boolean hierarchical) {
		Argument.hierarchical = hierarchical;
	}
	/**
	 * Getter of preprocessing
	 * @return the preprocessing
	 */
	public static boolean isPreprocessing() {
		return preprocessing;
	}
	/**
	 * Setter preprocessing
	 * @param preprocessing the preprocessing to set
	 */
	public static void setPreprocessing(boolean preprocessing) {
		Argument.preprocessing = preprocessing;
	}
	
	
	/**
	 * Getter of grammar
	 * @return the grammar
	 */
	public static boolean isGrammar() {
		return grammar;
	}
	/**
	 * Setter grammar
	 * @param grammar the grammar to set
	 */
	public static void setGrammar(boolean grammar) {
		Argument.grammar = grammar;
	}
	/**
	 * Getter of run
	 * @return the number of run
	 */
	public static int getRun() {
		return run;
	}
	/**
	 * Set the number of run
	 * @param aRun the number of run to set
	 */
	public static void setRun(int aRun) {
		run = aRun;
	}

	/**
	 * Get the reference domain
	 * @return the reference domain
	 */
	public static String getDomain() {
		return domain;
	}
	/**
	 * Set the reference domain
	 * @param aDomain the domain to set
	 */
	public static void setDomain(String aDomain) {
		domain = aDomain;
	}

	/**
	 * Get the type
	 * @return the automaton induction type
	 */
	public static TYPE getType() {
		return type;
	}

	/**
	 * Set the automaton induction type
	 * @param type the induction type
	 */
	public static void setType(TYPE type) {
		Argument.type = type;
	}

	/**
	 * Get the log directory
	 * @return the log directory
	 */
	public static String getDirectory() {
		return directory;
	}

	/**
	 * Set the log directory
	 * @param aDirectory the directory to set
	 */
	public static void setDirectory(String aDirectory) {
		directory = aDirectory;
	}
	
	
	/**
	 * Getter of hasSpecificTestInit
	 * @return the hasSpecificTestInit
	 */
	public static boolean isHasSpecificTestInit() {
		return hasSpecificTestInit;
	}
	/**
	 * Setter hasSpecificTestInit
	 * @param hasSpecificTestInit the hasSpecificTestInit to set
	 */
	public static void setHasSpecificTestInit(boolean hasSpecificTestInit) {
		Argument.hasSpecificTestInit = hasSpecificTestInit;
	}
	/**
	 * Getter of specificTestInit
	 * @return the specificTestInit
	 */
	public static String getSpecificTestInit() {
		return specificTestInit;
	}
	/**
	 * Setter specificTestInit
	 * @param specificTestInit the specificTestInit to set
	 */
	public static void setSpecificTestInit(String specificTestInit) {
		Argument.specificTestInit = specificTestInit;
	}
	/**
	 * Get the action model's name
	 * @return the name
	 */
	public static String getName() {
		return name;
	}

	/**
	 * Set the action model's name
	 * @param aName the name to set
	 */
	public static void setName(String aName) {
		name = aName;
	}

	/**
	 * Get the initial sate
	 * @return the initial state
	 */
	public static String getProblem() {
		return problem;
	}

	/**
	 * Set the initial state
	 * @param aProblem the problem to set
	 */
	public static void setProblem(String aProblem) {
		problem = aProblem;
	}
	
	/**
	 * Getter of onlyTabu
	 * @return the onlyTabu
	 */
	public static boolean isOnlyTabu() {
		return onlyTabu;
	}
	/**
	 * Setter onlyTabu
	 * @param onlyTabu the onlyTabu to set
	 */
	public static void setOnlyTabu(boolean onlyTabu) {
		Argument.onlyTabu = onlyTabu;
	}
	/**
	 * Getter of withoutTabu
	 * @return the withoutTabu
	 */
	public static boolean isWithoutTabu() {
		return withoutTabu;
	}
	/**
	 * Setter withoutTabu
	 * @param withoutTabu the withoutTabu to set
	 */
	public static void setWithoutTabu(boolean withoutTabu) {
		Argument.withoutTabu = withoutTabu;
	}
	/**
	 * Getter of lsonio
	 * @return the lsonio
	 */
	public static boolean isLsonio() {
		return lsonio;
	}
	/**
	 * Setter lsonio
	 * @param lsonio the lsonio to set
	 */
	public static void setLsonio(boolean lsonio) {
		Argument.lsonio = lsonio;
	}
	/**
	 * Getter of propertiesFile
	 * @return the propertiesFile
	 */
	public static String getPropertiesFile() {
		return propertiesFile;
	}
	/**
	 * Setter propertiesFile
	 * @param propertiesFile the propertiesFile to set
	 */
	public static void setPropertiesFile(String propertiesFile) {
		Argument.propertiesFile = propertiesFile;
	}
	
	
	/**
	 * Getter of refinement
	 * @return the refinement
	 */
	public static boolean isRefinement() {
		return refinement;
	}
	/**
	 * Setter refinement
	 * @param refinement the refinement to set
	 */
	public static void setRefinement(boolean refinement) {
		Argument.refinement = refinement;
	}
	/**
	 * Getter of usage
	 * @return the usage
	 */
	public static String getUsage() {
		return usage;
	}

	/**
	 * Print usage
	 */
	public static void printUsage() {
		System.err.println(usage);
		System.exit(1);
	}
	
	/**
	 * Getter of incremental
	 * @return the incremental
	 */
	public static boolean isIncremental() {
		return incremental;
	}
	/**
	 * Setter incremental
	 * @param incremental the incremental to set
	 */
	public static void setIncremental(boolean incremental) {
		Argument.incremental = incremental;
	}
	/**
	 * Getter of convergent
	 * @return the convergent
	 */
	public static boolean isConvergent() {
		return convergent;
	}
	/**
	 * Setter convergent
	 * @param convergent the convergent to set
	 */
	public static void setConvergent(boolean convergent) {
		Argument.convergent = convergent;
	}
	/**
	 * Getter of t
	 * @return the t
	 */
	public static int getT() {
		return T;
	}
	/**
	 * Setter t
	 * @param t the t to set
	 */
	public static void setT(int t) {
		T = t;
	}
	/**
	 * Getter of delta
	 * @return the delta
	 */
	public static int getCriterion() {
		return Delta;
	}
	/**
	 * Setter delta
	 * @param delta the delta to set
	 */
	public static void setDelta(int delta) {
		Delta = delta;
	}
	
	
	
	/**
	 * Getter of temporal
	 * @return the temporal
	 */
	public static boolean isTemporal() {
		return temporal;
	}
	/**
	 * Setter temporal
	 * @param temporal the temporal to set
	 */
	public static void setTemporal(boolean temporal) {
		Argument.temporal = temporal;
	}
	
	
	/**
	 * Getter of rec_type
	 * @return the rec_type
	 */
	public static RECOMPOSITION_TYPE getRec_type() {
		return rec_type;
	}
	/**
	 * Setter rec_type
	 * @param rec_type the rec_type to set
	 */
	public static void setRec_type(RECOMPOSITION_TYPE rec_type) {
		Argument.rec_type = rec_type;
	}
	
	
	
	/**
	 * Getter of strategy
	 * @return the strategy
	 */
	public static STRATEGY getStrategy() {
		return strategy;
	}
	/**
	 * Setter strategy
	 * @param strategy the strategy to set
	 */
	public static void setStrategy(STRATEGY strategy) {
		Argument.strategy = strategy;
	}
	/**
	 * Getter of active
	 * @return the active
	 */
	public static boolean isActive() {
		return active;
	}
	/**
	 * Setter active
	 * @param active the active to set
	 */
	public static void setActive(boolean active) {
		Argument.active = active;
	}
	
	
	/**
	 * Getter of onlyMethod
	 * @return the onlyMethod
	 */
	public static boolean isOnlyMethod() {
		return onlyMethod;
	}
	
	/**
	 * Setter onlyMethod
	 * @param onlyMethod the onlyMethod to set
	 */
	public static void setOnlyMethod(boolean onlyMethod) {
		Argument.onlyMethod = onlyMethod;
	}
	
	
	/**
	 * Getter of separate
	 * @return the separate
	 */
	public static boolean isSeparate() {
		return separate;
	}
	
	/**
	 * Setter separate
	 * @param separate the separate to set
	 */
	public static void setSeparate(boolean separate) {
		Argument.separate = separate;
	}
	
	/**
	 * Read arguments
	 * @param args The arguments
	 */
	public static void read(String[] args) {

		if(args.length == 0){
			printUsage();
		}

		for(int i=0; i<args.length; i++){
			switch (args[i].charAt(0)) {
			case '-':
				if (args[i].length() < 2){
					System.err.println("Bad argument");
					printUsage();
				}
				else {
					if(args[i].substring(1,args[i].length()).equals("h")) {
						printUsage();
					}
					if (args.length-1 == i){
						System.err.println("Expected parameters after argument"
								+" : "+args[i]);
						printUsage();
					}
					String arg = args[i].substring(1,args[i].length());
					switch (arg) {
					case "onlyTabu":
						Argument.setOnlyTabu(true);
						i--;
						break;
					case "onlyMethod":
						Argument.setOnlyMethod(true);
						i--;
						break;
					case "onlyActionModel":
						Argument.setOnlyActionModel(true);
						i--;
						break;
					case "separate":
						Argument.setSeparate(true);
						i--;
						break;
					case "preprocessing":
						Argument.setPreprocessing(true);
						i--;
						break;
					case "withoutRefinement":
						Argument.setRefinement(false);
						i--;
						break;
					case "withoutTabu":
						Argument.setWithoutTabu(true);
						i--;
						break;
					case "convergent":
						Argument.setConvergent(true);
						i--;
						break;
					case "temporal":
						Argument.setTemporal(true);
						i--;
						break;
					case "hierarchical":
					case "htn":
						Argument.setHierarchical(true);
						i--;
						break;
					case "incremental":
						Argument.setIncremental(true);
						i--;
						break;
					case "grammar":
						Argument.setGrammar(true);
						i--;
						break;
					case "lgp":
						Argument.setTwoOp(false);
						i--;
						break;
					case "lsonio":
						Argument.setLsonio(true);
						i--;
						break;
					case "active":
						Argument.setActive(true);
						i--;
						break;
					case "planner":
						Argument.setPlanner(true);
						i--;
						break;
					case "properties":
						Argument.setPropertiesFile(args[i+1]);
						break;
					case "domain":
					case "o":
						setDomain(args[i+1]);
						break;
					case "name":
					case "n":
						setName(args[i+1]);
						break;
					case "directory":
					case "d":
						setDirectory(args[i+1]);
						break;
					case "type":
					case "t":
						switch(args[i+1]) {
						case "RPNI":
							Argument.setType(TYPE.RPNI);
							break;
						case "RPNIPC":
							Argument.setType(TYPE.RPNIPC);
							break;
						default:
							System.err.println("Type "+args[i+1]+" unknown");
							printUsage();
							System.exit(1);
						}
						break;
					case "strategy":
						switch(args[i+1]) {
						case "min":
							Argument.setStrategy(STRATEGY.MIN);
							break;
						case "max":
							Argument.setStrategy(STRATEGY.MAX);
							break;
						default:
							System.err.println("Strategy "+args[i+1]+" unknown");
							printUsage();
							System.exit(1);
						}
						break;
					case "recomposition":
						switch(args[i+1]) {
						case "greedy":
							Argument.setRec_type(RECOMPOSITION_TYPE.GREEDY);
							break;
						case "heuristic":
							Argument.setRec_type(RECOMPOSITION_TYPE.HEURISTIC);
							break;
						default:
							System.err.println("Recomposition method "+args[i+1]+" unknown");
							printUsage();
							System.exit(1);
						}
						break;
					case "task":
						Argument.setTaskState(args[i+1]);
						break;
					case "inital":
					case "i":
						setProblem(args[i+1]);
						break;
					case "specific":
						System.out.println("Yo ");
						setSpecificTestInit(args[i+1]);
						setHasSpecificTestInit(true);
						break;
					case "run":
					case "r":
						try{
							int r = Integer.parseInt(args[i+1]);
							if(r > 0 && r <= 10) {
								setRun(r);
							} else {
								System.err.println("run value must be"
										+" between 1 and 10");
								printUsage();
							}
						} catch(NumberFormatException e) {
							System.err.println(args[i+1]+
									"can't be parsed to int");
							printUsage();
						}
						break;
					case "min":
						try{
							int mi = Integer.parseInt(args[i+1]);
							if(mi > 0) {
								setMin(mi);
							} else {
								System.err.println("min value must be"
										+" greater than 0");
								printUsage();
							}
						} catch(NumberFormatException e) {
							System.err.println(args[i+1]+
									"can't be parsed to int");
							printUsage();
						}
						break;
					case "max":
						try{
							int ma = Integer.parseInt(args[i+1]);
							if(ma > 0) {
								setMax(ma);
							} else {
								System.err.println("max value must be"
										+" greater than 0");
								printUsage();
							}
						} catch(NumberFormatException e) {
							System.err.println(args[i+1]+
									"can't be parsed to int");
							printUsage();
						}
						break;
					case "criterion":
						try{
							int r = Integer.parseInt(args[i+1]);
							if(r > 0 && r <= 100) {
								setDelta(r);
							} else {
								System.err.println("Criterion value must be between 1 and 100");
								printUsage();
							}
						} catch(NumberFormatException e) {
							System.err.println(args[i+1]+" can't be parsed to int");
							printUsage();
						}
						break;
					case "T":
						try{
							int t = Integer.parseInt(args[i+1]);
							if(t > 0) {
								Argument.setT(t);
							} else {
								System.err.println("T value must be greater than 0");
								printUsage();
							}
						} catch(NumberFormatException e) {
							System.err.println(args[i+1]+" can't be parsed to int");
							printUsage();
						}
						break;
					default:
						System.err.println("Unknown argument : "+args[i]);
						printUsage();
						break;
					}
					i++;

				}
				break;
			default:
				System.err.println("-args expected "+args[i]);
				printUsage();
			}
		}
	}
}
