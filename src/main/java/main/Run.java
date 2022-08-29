/**
 *
 */
package main;

import main.experiment.AMLSI;
import main.experiment.Incremental;
import main.experiment.IncrementalLSONIO;
import main.experiment.Convergent;
import main.experiment.Grammar;
import main.experiment.Tabu;
import main.experiment.TestLSONIO;
import main.experiment.hierarchical.HierAMLSI;
import main.experiment.hierarchical.HierarchicalStructureLearning;
import main.experiment.hierarchical.OnlyActionModel;
import main.experiment.hierarchical.OnlyMethod;
import main.experiment.hierarchical.SepLearning;
import main.experiment.hierarchical.TFDPlanner;
import main.experiment.temporal.TempAMLSI;

/**
 * The runnable class
 * @author Maxence Grand
 *
 */
public class Run {

	/**
	 * Main class
	 * @param args
	 */
	public static void main(String[] args) {
		Argument.read(args);
		
		if(Argument.isLsonio()) {
			//Run LSO-NIO
			if(Argument.isIncremental()) {
				try {
					Properties.read(Argument.getPropertiesFile());
					IncrementalLSONIO.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				try {
					Properties.read(Argument.getPropertiesFile());
					TestLSONIO.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			}
		} else  if (Argument.isTemporal()) {
			try {
				Properties.read(Argument.getPropertiesFile());
				TempAMLSI.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		else if(Argument.isIncremental()) {
			//Run Incremental AMLSI
			try {
				Properties.read(Argument.getPropertiesFile());
				Incremental.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		} else if(Argument.isGrammar()) {
			//Run Incremental AMLSI
			try {
				Properties.read(Argument.getPropertiesFile());
				Grammar.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}else if(Argument.isConvergent()) {
			//Run Convergent AMLSI
			try {
				Properties.read(Argument.getPropertiesFile());
				Convergent.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		} else  if (Argument.isOnlyTabu()) {
			//Run Tabu search alone
			try {
				Properties.read(Argument.getPropertiesFile());
				Tabu.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		} else  if (Argument.isHierarchical()) {
			//Run Tabu search alone
			if(Argument.isGrammar()) {
				try {
					Properties.read(Argument.getPropertiesFile());
					HierarchicalStructureLearning.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			} else if(Argument.isOnlyMethod()) {
				try {
					Properties.read(Argument.getPropertiesFile());
					OnlyMethod.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			} else if(Argument.isSeparate()) {
				try {
					Properties.read(Argument.getPropertiesFile());
					SepLearning.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			} else if(Argument.isOnlyActionModel()) {
				try {
					Properties.read(Argument.getPropertiesFile());
					OnlyActionModel.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			} else if(Argument.isPlanner()) {
				try {
					TFDPlanner.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				try {
					Properties.read(Argument.getPropertiesFile());
					HierAMLSI.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			}
		} else {
			//Run vanilla AMLSI
			try {
				Properties.read(Argument.getPropertiesFile());
				AMLSI.run();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
