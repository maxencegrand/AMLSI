/**
 * 
 */
package Tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsm.Pair;
import fsm.Symbol;
import simulator.temporal.TemporalExample;

/**
 * @author Maxence Grand
 *
 */
public class PlanConverter {

	public static Pair<List<Float>, List<Symbol>> convertTfdPlan(List<Symbol> actions, String plan) {
		//System.out.println(actions);
		try {
			List<Float> timestamps = new ArrayList<>();
			List<Symbol> actionsD = new ArrayList<>();
			BufferedReader br = new BufferedReader(new FileReader(plan));
			String line = br.readLine();
			Map<Float, Symbol> seq = new HashMap<>();
			while(line != null) {
				if(line.contentEquals("")) {
					line = br.readLine();
					continue;
				} else if(line.substring(0, 4).equals(";;;;")) {
					line = br.readLine();
					continue;
				}
				float time = Float.parseFloat(line.split(": \\(")[0]);
				String act = line.split(": \\(")[1].split("\\) \\[")[0];
				String[] actTab = act.split(" "); 
				String name = actTab[0].toLowerCase();
				List<String> args = new ArrayList<>();
				for(int i =1; i < actTab.length; i++) {
					args.add(actTab[i].toLowerCase());
				}
				for(Symbol a: actions) {
					if(a.getName().equals(name)) {
						if(a.getListParameters().size() == args.size()) {
							boolean b = true;
							for(int i = 0; i < args.size(); i++) {
								b &= args.get(i).equals(a.getListParameters().get(i));
							}
							if(b) {
								timestamps.add(time);
								actionsD.add(a);
								break;
							}
						}
					}
				}
				line=br.readLine();
			}
			return new Pair<List<Float>, List<Symbol>>(timestamps, actionsD);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
