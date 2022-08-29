/*
 * Copyright (c) 2020 by Damien Pellier <Damien.Pellier@imag.fr>.
 *
 * This file is part of PDDL4J library.
 *
 * PDDL4J is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * PDDL4J is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with PDDL4J.  If not, see
 * <http://www.gnu.org/licenses/>
 */

package fr.uga.amlsi.main.experiment.hierarchical;

import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.PDDLParser;
import fr.uga.pddl4j.plan.Hierarchy;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.htn.stn.AbstractSTNNode;
import fr.uga.pddl4j.planners.htn.stn.AbstractSTNPlanner;
import fr.uga.pddl4j.planners.htn.stn.tfd.TFDNode;
import fr.uga.pddl4j.problem.HTNProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.Method;
import fr.uga.amlsi.main.Argument;

import org.apache.logging.log4j.Level;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

/**
 * This class implements the code of the total ordered simple task network planner. The search method is an
 * implementation of the total order STN procedure describes in the book of Automated Planning of Ghallab and al.
 * page 239.
 *
 * @author D. Pellier
 * @version 1.0 - 15.04.2020
 * @since 4.0
 */
public final class TFDPlanner extends AbstractSTNPlanner{
	
	private int timeout;
    
	/**
	 * 
	 */
    public TFDPlanner(int timeout) {
        super(new Properties());
        this.timeout = timeout;
    }
  
    
    /**
     * Solves the planning problem and returns the first solution search found. The search method is an implementation
     * of the total order STN procedure describes in the book of Automated Planning of Ghallab and al. page 239.
     *
     * @param problem the problem to be solved.
     * @return a solution search or null if it does not exist.
     */
    public Plan search(final HTNProblem problem) {
        // Create the list of pending nodes to explore
        final PriorityQueue<TFDNode> open = new PriorityQueue<>(1000, new Comparator<TFDNode>() {
            public int compare(TFDNode n1, TFDNode n2) {
                return n1.getTasks().size() - n2.getTasks().size();
            }
        });
        // Create the root node of the search space
        final State init = new State(problem.getInitialState());
        final TFDNode root = new TFDNode(init, problem.getInitialTaskNetwork().getTasks());
        //root.getState().getNegative().set(0, problem.getFluents().size());
        //root.getState().getNegative().andNot(root.getState().getPositive());

        // Add the root node to the list of the pending nodes to explore.
        open.add(root);

        // Declare the plan used to store the result of the exploration
        Plan plan = null;

        // Get the timeout for searching
        final long start = System.currentTimeMillis();
        long elapsedTime = 0;

        boolean debug = false;

        // Start exploring the search space
        while (!open.isEmpty() && plan == null && elapsedTime < timeout) {
            // Get and remove the first node of the pending list of nodes.
            //final TFDNode currentNode = open.poll();
            final TFDNode currentNode = open.poll();

            // If the task network is empty we've got a solution
            if (currentNode.getTasks().isEmpty()) {
                if (currentNode.getState().satisfy(problem.getGoal())) {
                    return super.extractPlan(currentNode, problem);
                }  else {
                }
            } else {
                // Get and remove the fist task of the task network
                //System.out.println(currentNode);
                int task = currentNode.popTask();
                // Get the current state of the search
                final State state = currentNode.getState();
                // Get the relevant operators, i.e., action or method that are relevant for this task.
                final List<Integer> relevantOperators = problem.getTaskResolvers().get(task);
                // Case of primitive task
                if (problem.getTasks().get(task).isPrimtive()) {
                    for (Integer operator : relevantOperators) {
                        final Action action = problem.getActions().get(operator);
                        if (debug) {
                            System.out.println("\n======> Try to decompose primitive tasks "
                                + problem.toString(problem.getTasks().get(task)) + " with \n\n"
                                + problem.toString(action));
                        }
                        if (state.satisfy(action.getPrecondition())) {
                            final TFDNode childNode = new TFDNode(currentNode);
                            childNode.setParent(currentNode);
                            childNode.setOperator(operator);
                            childNode.getState().apply(action.getConditionalEffects());
                            childNode.setTask(task);
                            open.add(childNode);
                            if (debug) {
                                System.out.println("=====> Decomposition succeeded push node:");
                                System.out.println(problem.toString(childNode.getState()));
                                for (int t : childNode.getTasks()) {
                                    System.out.println(problem.toString(problem.getTasks().get(t)));
                                }
                            }
                        } else {
                            if (debug) {
                                System.out.println("=====> Decomposition failed");
                            }
                        }
                        if (debug) {
                            try {
                                System.in.read();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else { // Case of the compound task
                    for (Integer operator : relevantOperators) {
                        final Method method = problem.getMethods().get(operator);
                        if (debug) {
                            System.out.println("\n======> Try to decompose compound tasks "
                                + problem.toString(problem.getTasks().get(task)) + " with\n\n"
                                + problem.toString(method));
                        }
                        if (state.satisfy(method.getPrecondition())) {
                            final TFDNode childNode = new TFDNode(currentNode);
                            childNode.setParent(currentNode);
                            childNode.setOperator(problem.getActions().size() + operator);
                            childNode.pushAllTasks(method.getSubTasks());
                            childNode.setTask(task);
                            open.add(childNode);
                            if (debug) {
                                System.out.println("=====> Decomposition succeeded push node:");
                                System.out.println("=====>\n" + problem.toString(childNode.getState()));
                                System.out.println("=====>\n");
                                for (int t : childNode.getTasks()) {
                                    System.out.println(problem.toString(problem.getTasks().get(t)));
                                }
                            }
                        } else {
                            if (debug) {
                                System.out.println("=====> Decomposition failed");
                            }
                        }
                        if (debug) {
                            try {
                                System.in.read();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            elapsedTime = System.currentTimeMillis() - start;
        }
        return plan;
    }

    /**
     * The main method of the <code>TFDPlanner</code> example. The command line syntax is as
     * follow:
     *
     * <pre>
     * usage of TFDPlanner:
     *
     * OPTIONS   DESCRIPTIONS
     *
     * -d <i>str</i>   operator file name
     * -p <i>str</i>   fact file name
     * -t <i>num</i>   specifies the maximum CPU-time in seconds (preset: 300)
     * -h              print this message
     *
     * </pre>
     *
     * <p>
     * Commande line example:
     * <code>java -cp build/libs/pddl4j-x.x.x.jar fr.uga.pddl4j.planners.htn.stn.tfd.TFDPlanner</code><br>
     * <code>  -d src/test/resources/benchmarks/hddl/ipc2020/rover/domain.hddl</code><br>
     * <code>  -p src/test/resources/benchmarks/hddl/ipc2020/rover/pb01.hddl</code><br>
     * </p>
     *
     * @param args the arguments of the command line.
     */
    public static void run() {

    	for(int i = 1; i <= 20; i++) {
    		// Get the domain file and problem file and parse the hddl files.
    		final File domain = new File(Argument.getDomain());
            String instance = "instance-"+(i < 10 ? "0"+i : ""+i)+".hddl";
            System.out.println("Solve "+instance);
            final File problem = new File(Argument.getProblem()+"/"+instance);
            final PDDLParser parser = new PDDLParser();
            try {
                parser.parse(domain, problem);
            } catch (IOException e) {
                System.out.println("\nunexpected error when parsing the PDDL planning problem description.");
                System.out.println(Argument.getDomain());
                System.out.println(Argument.getProblem());
                e.printStackTrace();
                System.err.println("NO SOLUTION");
            	String file_plan = ("plan"+i+".txt");
                String file_decomposition = ("decomposition"+i+".txt");
                BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				}
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                continue;
            }
            final ErrorManager errorManager = parser.getErrorManager();
            // Print the syntax errors if detected
            if (!errorManager.getMessages(Message.Type.PARSER_ERROR).isEmpty()
                || !errorManager.getMessages(Message.Type.LEXICAL_ERROR).isEmpty()) {
                errorManager.printAll();
                System.err.println("NO SOLUTION");
            	String file_plan = ("plan"+i+".txt");
                String file_decomposition = ("decomposition"+i+".txt");
                BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                continue;
            }


            // Encode the problem into compact representation
            HTNProblem pb = new HTNProblem(parser.getDomain(), parser.getProblem());
            pb.instantiate();


            if (!pb.isTotallyOrederd()) {
            	System.err.println("NO SOLUTION 5");
            	String file_plan = ("plan"+i+".txt");
                String file_decomposition = ("decomposition"+i+".txt");
                BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	continue;
            }

            if (pb.isSolvable()) {
                try {
                    // Create an instance of the TFDPlanner Planner
                    final TFDPlanner planner = new TFDPlanner(30*1000);
                    final Plan plan = planner.search(pb);
                    if (plan != null) {
                        // Print plan information
//                        System.out.println(pb.toString(plan.getHierarchy()));
//                        System.out.println(pb.toString(plan));
                        String file_plan = ("plan"+i+".txt");
                        String file_decomposition = ("decomposition"+i+".txt");
                        BufferedWriter bw;
    					try {
    						bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
    	                    bw.write(pb.toString(plan));
    	                    bw.close();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    						continue;
    					}
    					try {
    						bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
    	                    bw.write(pb.toString(plan.getHierarchy()));
    	                    bw.close();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                    } else {
                    	System.err.println("NO SOLUTION 4");
                    	String file_plan = ("plan"+i+".txt");
                        String file_decomposition = ("decomposition"+i+".txt");
                        BufferedWriter bw;
    					try {
    						bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
    	                    bw.write("NO SOLUTION");
    	                    bw.close();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    						continue;
    					}
    					try {
    						bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
    	                    bw.write("NO SOLUTION");
    	                    bw.close();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                    	continue;
                    }
                } catch (OutOfMemoryError err) {
                	System.err.println("NO SOLUTION 3");
                	String file_plan = ("plan"+i+".txt");
                    String file_decomposition = ("decomposition"+i+".txt");
                    BufferedWriter bw;
					try {
						bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
	                    bw.write("NO SOLUTION");
	                    bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
					try {
						bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
	                    bw.write("NO SOLUTION");
	                    bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    continue;
                }
            } else {
            	System.err.println("NO SOLUTION 1");
            	String file_plan = ("plan"+i+".txt");
                String file_decomposition = ("decomposition"+i+".txt");
                BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_plan));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				try {
					bw = new BufferedWriter(new FileWriter(Argument.getDirectory()+"/"+file_decomposition));
                    bw.write("NO SOLUTION");
                    bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                continue;
            }
    	}
    }
}
