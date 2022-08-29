/**
 * 
 */
package simulator.temporal;

import java.util.List;

import fsm.Action;
import fsm.Pair;
import fsm.Symbol;
import junit.framework.TestCase;
import test.fsm.ActionTest;

/**
 * @author Maxence Grand
 *
 */
public class TemporalBlackBoxTest extends TestCase{
	private String pathData = "/home/maxence/Documents/amlsi/data_test/";
	private String pathDomain = "/home/maxence/Documents/amlsi/pddl/temporal/";
	
	public void testPegSolitaire() {
		String domainName = "peg";
		String data = pathData+domainName+"/";
		String domain = pathDomain+domainName+"/domain.pddl";
		System.out.println("Domain Peg Solitaire");
		for(int i = 1; i <= 10; i++) {
			System.out.println("  #instance "+i);
			this.testPlan(domainName, domain, data, i);
		}
	}
	
	public void testMatch() {
		String domainName = "match";
		String data = pathData+domainName+"/";
		String domain = pathDomain+domainName+"/domain.pddl";
		System.out.println("Domain Match");
		for(int i = 1; i <= 10; i++) {
			System.out.println("  #instance "+i);
			this.testPlan(domainName, domain, data, i);
		}
	}

	public void testDepot() {
		String domainName = "depot";
		String data = pathData+domainName+"/";
		String domain = pathDomain+domainName+"/domain.pddl";
		System.out.println("Domain Depot");
		for(int i = 1; i <= 4; i++) {
			System.out.println("  #instance "+i);
			this.testPlan(domainName, domain, data, i);
		}
	}
	
	public void testDriverlog() {
		String domainName = "driverlog";
		String data = pathData+domainName+"/";
		String domain = pathDomain+domainName+"/domain.pddl";
		System.out.println("Domain Driverlog");
		for(int i = 1; i <= 1; i++) {
			System.out.println("  #instance "+i);
			this.testPlan(domainName, domain, data, i);
		}
	}
	
	private void testPlan(String domainName, String domain, String data, int pb) {
		String instance = pathDomain+domainName+"/instances/instance-"+pb+".pddl";
		TemporalBlackBox blackbox = new TemporalBlackBox(domain, instance);
		Pair<List<Float>, List<Symbol>> ex = Tools.PlanConverter.convertTfdPlan(blackbox.getAllActions(), data+"plan_"+pb);
		blackbox.reInit();
		for(int i = 0; i< ex.getX().size(); i++) {
			System.out.println("    "+ex.getY().get(i)+" "+ex.getX().get(i));
			assertTrue(blackbox.isApplicable(ex.getY().get(i), ex.getX().get(i)));
			blackbox.apply();
		}
		blackbox.endSimulation();
	}
}
