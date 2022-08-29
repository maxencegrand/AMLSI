/**
 * 
 */
package learning.hierarchical;

import java.util.Map;

import fr.uga.generator.symbols.Action;
import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Maxence Grand
 *
 */
public class Dependancy {
	/**
	 * 
	 */
	private Map<String, List<String>> dependancies;
	
	/**
	 * 
	 * Constructs
	 */
	public Dependancy() {
		this.dependancies = new HashMap<>();
	}

	/**
	 * 
	 * Constructs 
	 * @param symbols
	 */
	public Dependancy(List<Symbol> symbols) {
		this();
		symbols.forEach(s -> this.addDependancy(s.generalize()));
	}
	
	/**
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		dependancies.clear();
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public List<String> get(Object arg0) {
		return dependancies.get(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public List<String> put(String arg0, List<String> arg1) {
		return dependancies.put(arg0, arg1);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> allKey() {
		return new ArrayList<>(this.dependancies.keySet());
	}
	
	/**
	 * 
	 * @param s
	 */
	public void addDependancy(Symbol s) {
		
		if(s instanceof Method) {
			Method m = (Method)s;
			if(!this.allKey().contains(m.getToDecompose().getName())) {
				this.put(m.getToDecompose().getName(), new ArrayList<>());
			}
			for(Action a : m.getSubtasks()) {
				if(!this.get(m.getToDecompose().getName()).contains(a.getName())) {
					this.get(m.getToDecompose().getName()).add(a.getName());
				}
			}
		} else {
			this.put(s.getName(), new ArrayList<>());
		}
	}
	
	/**
	 * 
	 * @param filename
	 */
	public void writeDotFile(String filename) {
		StringBuilder str = new StringBuilder();
		str.append("digraph fsa{").append("\n")
			.append("rankdir=TB").append("\n")
			.append("graph [bgcolor=white];").append("\n");
		this.allKey().forEach(k -> str.append("\""+k+"\"").append(" "));
		str.append(";\n");
		this.allKey().forEach(k -> {
			this.get(k).forEach(v -> {
				str.append("\""+k+"\"").append("->")
				.append("\""+v+"\"").append(";").append("\n");
			});
		});
		str.append("}").append("\n");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(str.toString());
			bw.close();
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> toRefine() {
		List<String> res = new ArrayList<>();
		while(res.size() < this.allKey().size()) {
			List<String> tmp = new ArrayList<>();
			for(String s : this.allKey()) {
				if(!res.contains(s) && this.checkDependance(s, res)) {
					tmp.add(s);
				}
			}
			res.addAll(tmp);
		}
		return res;
	}
	
	/**
	 * 
	 * @param s to check
	 * @param l Already check
	 * @return true if all dependies of s are in l
	 */
	public boolean checkDependance(String s, List<String> l) {
		for(String s1 : this.get(s)) {
			if(s1.equals(s)){
				continue;
			} else if(l.contains(s1)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
}
