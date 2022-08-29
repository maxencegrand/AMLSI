/**
 * 
 */
package simulator.hierarchical.translator;

import java.io.File;
import java.io.FileNotFoundException;

import fr.uga.pddl4j.parser.PDDLDomain;
import fr.uga.pddl4j.parser.PDDLParser;

/**
 * @author Maxence Grand
 *
 */
public class NavratTranslator {

	/**
	 * 
	 * @param STRIPS_domain
	 * @param HTN_domain
	 * @return
	 */
	public static File translateHTN2STRIPS(String HTN_domain, String STRIPS_domain) {
		//Parse temporal domain
		PDDLParser parser = new PDDLParser();
		try {
			parser.parseDomain(HTN_domain);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		PDDLDomain htn = parser.getDomain();
		
		htn.getTasks().forEach(t -> {
			System.out.println(t+" "+t.getTypes());
		});
		
		htn.getMethods().forEach(m -> {
			System.out.println(m.getName());
			m.getSubTasks().getChildren().forEach(t -> {
				System.out.println(t);
			});
		});
		return null;
		//throw new IllegalArgumentException();
	}
}
