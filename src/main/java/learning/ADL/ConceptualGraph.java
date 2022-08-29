/**
 * 
 */
package learning.ADL;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fsm.Pair;
import fsm.Predicate;
import fsm.Symbol;
import fsm.TransitionAction;
import learning.Observation;
import learning.Observation.Value;

/**
 * @author Maxence Grand
 *
 */
public class ConceptualGraph {
	/**
	 * 
	 * @author Maxence Grand
	 *
	 */
	private class Var {
		/**
		 * 
		 */
		private Map<String, String> l;
		
		/**
		 * 
		 * Constructs
		 */
		public Var() {
			this.l = new LinkedHashMap<>();
		}
		
		/**
		 * 
		 * Constructs 
		 * @param l
		 */
		public Var(Map<String, String> l) {
			this();
			this.l.putAll(l);
		}
		
		/**
		 * 
		 * Constructs 
		 * @param p
		 */
		public Var (String p, String t) {
			this();
			this.l.put(p,t);
		}
		
		/**
		 * 
		 */
		public String toString() {
			String str = "";
			int i = 0;
			for(String  s : l.keySet()) {
				if(i>0) {
					str+="-";
				}
				str += s+":"+l.get(s);
				i++;
			}
			return str;
		}
		
		/**
		 * 
		 */
		public boolean equals(Object other) {
			if(other instanceof Var) {
				return ((Var)other).toString().equals(this.toString());
			}
			return false;
		}
		
		/**
		 * 
		 */
		public int hashCode() {
			return this.toString().hashCode();
		}
	}
	
	/**
	 * 
	 * @author Maxence Grand
	 *
	 */
	private class Val {
		/**
		 * 
		 */
		private Symbol pred;
		/***
		 * 
		 */
		private Observation.Value v;
		/**
		 * Constructs 
		 * @param pred
		 * @param v
		 */
		public Val(Symbol pred, Value v) {
			this.pred = pred;
			this.v = v;
		}
		
		/**
		 * 
		 */
		public String toString() {
			switch(v) {
			case FALSE:
				return "neg-"+pred.getName();
			case TRUE:
				return "pos-"+pred.getName();
			default:
				throw new IllegalArgumentException();
			}
		}
		
		/**
		 * 
		 */
		public boolean equals(Object other) {
			if(other instanceof Val) {
				boolean b = ((Val)other).toString().equals(this.toString());
				return b;
			}
			return false;
		}
		
		/**
		 * 
		 */
		public int hashCode() {
			return this.toString().hashCode();
		}
	}
	
	/**
	 * 
	 */
	public static final Symbol EQ = new Predicate("=");
	/**
	 * 
	 */
	private List<Val> proposition;
	/**
	 * 
	 */
	private Map<Symbol, List<Val>> arrow;
	/**
	 * 
	 */
	private Map<Val, List<Var>> arrow2;
	/**
	 * 
	 */
	private List<Var> var = new ArrayList<>();
	/**
	 * 
	 */
	private TransitionAction a;
	
	/**
	 * 
	 * Constructs
	 */
	public ConceptualGraph() {
		this.arrow = new HashMap<>();
		this.arrow2 = new HashMap<>();
		this.proposition = new ArrayList<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param o
	 * @param a
	 */
	public ConceptualGraph(Observation o, TransitionAction a) {
		this();
		this.a = a;
		this.a.free().forEach(p -> this.var.add(new Var(p, a.getParameters().get(p))));
		this.a.linked().forEach(p -> this.var.add(new Var(p, a.getParameters().get(p))));
		Map<String, String> inst = new HashMap<>();
		this.a.free().forEach(e -> {
			inst.put(e, this.a.getParameters().get(e));
		});
		this.a.linked().forEach(e -> {
			inst.put(e, this.a.getParameters().get(e));
		});
		o.getPredicates().forEach((k,v) -> {
			if(!this.arrow.containsKey(k.generalize())) {
				Val pos = new Val(k.generalize(), Observation.Value.TRUE), 
						neg = new Val(k.generalize(), Observation.Value.FALSE);
				this.proposition.add(neg);
				this.proposition.add(pos);
				this.arrow.put(k.generalize(), new ArrayList<>());
				this.arrow2.put(pos, new ArrayList<>());
				this.arrow2.put(neg, new ArrayList<>());
				this.arrow.get(k.generalize()).add(pos);
				this.arrow.get(k.generalize()).add(neg);
			}
			switch(v) {
			case TRUE:
			case FALSE:
				if(k.getListParameters().size() >= 1) {
					Var var = new Var(k.getParameters());
					this.arrow2.get(new Val(k.generalize(), v)).add(var);
					if(!this.var.contains(var)) {
						this.var.add(var);
					}
				}
				break;
			default:
				break;
			
			}
		});
		Val pos = new Val(EQ, Observation.Value.TRUE), 
				neg = new Val(EQ, Observation.Value.FALSE);
		this.proposition.add(neg);
		this.proposition.add(pos);
		this.arrow.put(EQ, new ArrayList<>());
		this.arrow2.put(pos, new ArrayList<>());
		this.arrow2.put(neg, new ArrayList<>());
		this.arrow.get(EQ).add(pos);
		this.arrow.get(EQ).add(neg);
		List<String> tabu = new ArrayList<>();
		this.a.getParameters().forEach((k,v) -> {
			tabu.add(k);
			this.a.getParameters().forEach((k1,v1) -> {
				if(!k1.equals(k) && !tabu.contains(k1)) {
					if(v1.equals(v)) {
						Map<String, String> tmp = new LinkedHashMap<>();
						tmp.put(k,v);
						tmp.put(k1, v1);
						Var var = new Var(tmp);
						this.arrow2.get(new Val(EQ, Observation.Value.FALSE)).add(var);
						if(!this.var.contains(var)) {
							this.var.add(var);
						}
					}
				}
			});
		});
//		this.proposition.add(NEQ);
//		this.arrow.put(NEQ, new ArrayList<>());
//		List<String> tabu = new ArrayList<>();
//		this.a.getParameters().forEach((k,v) -> {
//			tabu.add(k);
//			this.a.getParameters().forEach((k1,v1) -> {
//				if(!k1.equals(k) && !tabu.contains(k1)) {
//					if(v1.equals(v)) {
//						List<String> tmp = new ArrayList<>(2);
//						tmp.add(k);
//						tmp.add(k1);
//						this.arrow.get(NEQ).add(new Pair<>(tmp, true));
//					}
//				}
//			});
//		});
	}
	
	/**
	 * 
	 * @param rep
	 */
	public void writeDotFile(String rep) {
		String filename = rep+"/"+a.getName()+".dot";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(this.getDotFile());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private String getDotFile() {
		StringBuilder builder = new StringBuilder();
		builder.append("graph conceptual{").append("\n");
		builder.append("node [shape = ellipse];");
		proposition.forEach(p -> {
			builder.append(" \"").append(p).append("\"");
		});
		builder.append("node [shape = diamond];");
		this.arrow.keySet().forEach(p -> {
			builder.append(" \"").append(p.getName()).append("\"");
		});
		builder.append(";").append("\n");
		if(!this.var.isEmpty()) {
			builder.append("node [shape = rectangle];");
			this.var.forEach(p -> builder.append(" \"").append(p).append("\""));
			builder.append(";").append("\n");
		}
//		if(!this.a.linked().isEmpty()) {
//			builder.append("node [shape = rectangle, fillcolor = grey, style=filled];");
//			a.linked().forEach(p -> builder.append(" \"").append(p).append(":")
//					.append(a.getParameters().get(p)).append("\""));
//			builder.append(";").append("\n");
//		}
		this.arrow.forEach((k,v) -> {
			v.forEach(vv -> {
				builder.append("\"").append(k.getName()).append("\"")
				.append(" -- \"").append(vv).append("\"").append("\n");
			});
		});
		this.arrow2.forEach((k,v) -> {
			v.forEach(vv -> {
				builder.append("\"").append(k).append("\"")
				.append(" -- \"").append(vv).append("\"").append("\n");
			});
		});
//		this.arrow.forEach((k,v) -> {
//			v.forEach(a -> {
//				String color = a.getY() ? "green" : "red";
//				builder.append("\"").append(k).append("\"")
//				.append(" -- \"")
//				.append(a.getX()).append(":")
//				.append(this.a.getParameters().get(a.getX())).append("\"")
//				.append("[color=").append(color).append(", ").append("];\n");
//				
//			});
//		});
		builder.append("}").append("\n");
		return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Formula> getUniversalSimple() {
		List<Formula> res = new ArrayList<>();
//		this.proposition.forEach(p -> {
//			if(this.allSame(p)) {
//				boolean b = this.arrow.get(p).get(0).getY();
//				List<String> param = this.arrow.get(p).get(0).getX();
//				List<Integer> size = new ArrayList<>();
//				List<String> free = new ArrayList<>();
//				param.forEach(pa -> {
//					if(this.a.free().contains(pa) ) {
//						free.add(pa);
//					}
//				});
//				if(!free.isEmpty()) {
//					System.out.println("forall "+free+" "+p+" is "+b);
//				}
//			}
//		});
		return res;
	}
	
//	private boolean allSame(String p) {
//		List<Pair<List<String>, Boolean>> l = this.arrow.get(p);
//		boolean ref = l.get(0).getY();
//		for(int i = 0; i < l.size(); i++) {
//			if(ref != l.get(i).getY()) {
//				return false;
//			}
//		}
//		return !(l.isEmpty());
//	}
//	
//	private int getNumberofType(String t) {
//		int res = 0;
//		
//		return res;
//	}
}
