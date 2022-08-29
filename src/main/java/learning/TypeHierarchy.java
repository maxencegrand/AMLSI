package learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import exception.TypeException;
import fsm.Pair;
import fsm.Symbol;

/**
 * A class representing the hierarchy of types
 * @author Grand Maxence
 * @since March 2020
 * @version 1.0.0
 */
public class TypeHierarchy {
	/**
	 * The root
	 */
	public static final String ROOT = "object";

	/**
	 * A class representing a link in the hierarchy of types
	 * @author Grand Maxence
	 * @since March 2020
	 * @version 1.0.0
	 */
	public class Link {
		/**
		 * Link's name
		 */
		private String name;
		/**
		 * name's getteur
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * All childs
		 */
		private List<Link> childs;

		/**
		 * Constructs a link
		 */
		public Link() {
			this.name = "";
			childs = new ArrayList<>();
		}

		/**
		 * Construct a links
		 * @param name The link's name
		 */
		public Link(String name) {
			this();
			this.name = name;
		}

		/**
		 * Construct a links
		 * @param name The link's name
		 * @param childs The link's child
		 */
		public Link(String name, List<Link> childs) {
			this(name);
			for(Link child : childs) {
				this.childs.add(child);
			}
		}

		/**
		 * Child's getteur
		 * @return The childs
		 */
		public List<Link> getChilds(){
			return this.childs;
		}

		/**
		 * Check if a type is a child
		 * @param type A type
		 * @return true if type is a child
		 */
		private boolean isPresent(String type) {
			boolean b = false;
			for(Link child : this.childs) {
				b |= child.getName().equals(type);
			}
			return b;
		}

		/**
		 * Find all ancestors of a type
		 * @param type A type
		 * @return All ancestors
		 */
		public List<String> ancestors(String type){
			List<String> res = null;
			if(this.name.equals(type)) {
				return new ArrayList<>();
			}else if(childs.isEmpty()) {
				return null;
			}else {
				for(Link child : childs) {
					res = child.ancestors(type);
					if(res != null) {
						res.add(this.name);
						return res;
					}
				}
			}
			return null;
		}

		/**
		 * Find all descendants
		 * @return All descendants
		 */
		public List<String> descendants(){
			List<String> res = new ArrayList<>();
			res.add(name);
			for(Link child : childs) {
				res.addAll(child.descendants());
			}
			return res;
		}

		/**
		 * Find all descendants of a type t
		 * @param t A type
		 * @return All descendants
		 */
		public List<String> descendants(String t){
			if(t.equals(name)) {
				List<String> res = new ArrayList<>();
				res.add(t);
				for(Link child : childs) {
					res.addAll(child.descendants());
				}
				return res;
			} else {
				List<String> res = new ArrayList<>();
				for(Link child : childs) {
					res.addAll(child.descendants(t));
				}
				return res;
			}
		}

		/**
		 * String representation
		 * @return a String
		 */
		public String toString() {
			if(childs.isEmpty()) {
				return "";
			}else {
				String res = "";
				String next = "";
				for(Link child : childs) {
					res += child.getName()+" ";
					next += child.toString();
				}
				res += "- "+this.name+"\n"+next;
				return res;
			}
		}
	};

	/**
	 * The first link
	 */
	private Link first;


	/**
	 *
	 * Constructs a type hierarchy
	 * @param childs all child
	 */
	public TypeHierarchy(List<Link> childs) {
		this.first = new Link(ROOT, childs);
	}

	/**
	 *
	 * Constructs a type hierarchy
	 */
	public TypeHierarchy() {
		this(new ArrayList<Link> ());
	}

	/**
	 *
	 * Constructs a type hierarchy
	 * @param map A map
	 */
	public TypeHierarchy(Map<String, String> map) {
		this(new ArrayList<Link> ());

		//Construct map type -> list< subtype >
		Map<String, List<String>> h = new HashMap<>();
		for(String subtype : map.keySet()) {
			String type = map.get(subtype);
			if(! h.containsKey(type)) {
				h.put(type, new ArrayList<>());
			}
			h.get(type).add(subtype);
		}
		Queue<String> types = new LinkedList<>();
		types.add(TypeHierarchy.ROOT);
		while(!types.isEmpty()) {
			String type = types.poll();
			for(String str : h.get(type)) {
				if(h.containsKey(str)) {
					types.add(str);
				}
				if(type.equals(ROOT)) {
					this.addType(str);
				} else {
					this.addType(type, str);
				}
			}
		}
	}

	/**
	 * Add a type tPrime as a child of t
	 * @param t A type
	 * @param tPrime A type
	 */
	public void addType(String t, String tPrime) {
		Queue<Link> fifo = new LinkedList<>();
		for(Link child : this.first.getChilds()) {
			fifo.add(child);
		}
		while(! fifo.isEmpty()) {
			Link current = fifo.poll();
			if(current.getName().equals(t)) {
				if(! current.isPresent(tPrime)) {
					current.getChilds().add(new Link(tPrime));
				}
			}else {
				for(Link child : current.getChilds()) {
					fifo.add(child);
				}
			}
		}
	}

	/**
	 * Check if a type is present in the hierarchy
	 * @param type A type
	 * @return True if type is present
	 */
	private boolean isPresent(String type) {
		boolean b = false;
		for(Link child : this.first.childs) {
			b |= child.getName().equals(type);
		}
		return b;
	}

	/**
	 * Add a type type as a child of the first link
	 * @param type A type
	 */
	public void addType(String type) {
		if(!isPresent(type)) {
			this.first.getChilds().add(new Link(type));
		}
	}



	/**
	 * Infere the hierarchy from all actions/propositions
	 * @param sym All actions/preconditions
	 * @param constants constants' types
	 */
	public void infere(List<Symbol> sym, Map<String, String> constants) {
		List<String> trivials = new ArrayList<>();
		List<List<String>> formulas = new ArrayList<>();
		Map<String,List<Symbol>> names = new HashMap<>();
		for (Symbol s : sym) {
			if(! names.containsKey(s.getName())) {
				names.put(s.getName(), new ArrayList<>());
			}
			names.get(s.getName()).add(s);
		}

		//Construct formulas
		for(String n : names.keySet()) {
//			System.out.println(n+" "+names.get(n));
			for(int i = 0; i < names.get(n).get(0).getListParameters().size();
					i++) {
				List<String> tmp = new ArrayList<>();
				//System.out.println(names);
				//System.out.println(constants);
				for(Symbol s : names.get(n)) {
					String ss = constants.get(s.getListParameters().get(i));
					if(! tmp.contains(ss)) {
						tmp.add(ss);
					}
				}
				if(isTrivial(tmp)) {
					if(!trivials.contains(tmp.get(0))) {
						trivials.add(tmp.get(0));
					}
				}else {
					if(! redundant(formulas, tmp)) {
						formulas.add(tmp);
					}
				}
			}
		}

		//Add hierarchy
		int i = 1;
		Stack<Pair<String, String>> rules = new Stack<>();
		while(! formulas.isEmpty()) {
			List<String> f = formulas.get(smallest(formulas));
			formulas.remove(smallest(formulas));
			String newType = "T"+i;
			i++;
			for(String type : f) {
				rules.add(new Pair<>(newType, type));
				trivials.remove(type);
				if(!trivials.contains(newType)) {
					trivials.add(newType);
				}
			}
			formulas = replace(formulas, rules);
		}

		//Add trivials rules
		for(String triv : trivials) {
			this.addType(triv);
		}
		//Add non-trivial rules
		while(!rules.isEmpty()) {
			Pair<String, String> p = rules.pop();
			this.addType(p.getX(), p.getY());
		}
	}

	/**
	 * Check if a formula is trivial
	 * @param formula A formula
	 * @return True if the formula is trivial
	 */
	private static boolean isTrivial(List<String> formula) {
		boolean b = true;
		for(String t : formula) {
			for(String tt : formula) {
				b &= t.equals(tt);
			}
		}
		return b;
	}

	/**
	 * Check if a formula is redundant w.r.t. to a set of formulas
	 * @param formulas A set of formulas
	 * @param formula A formula
	 * @return True if the formula is redundant
	 */
	private static boolean redundant(List<List<String>> formulas, List<String> formula) {
		boolean b = false;
		for(List<String> f : formulas) {
			if(f.size() == formula.size()) {
				boolean bb = true;
				for(String s : f) {
					bb &= formula.contains(s);
				}
				b |= bb;
			}
		}
		return b;
	}

	/**
	 * Find the index of the smallest formula
	 * @param formulas A set of formula
	 * @return the index of the smallest formula
	 */
	private int smallest(List<List<String>> formulas) {
		int iMin = 0;
		for(int i = 1; i<formulas.size(); i++) {
			if(formulas.get(i).size() < formulas.get(iMin).size()) {
				iMin = i;
			}
		}
		return iMin;
	}

	/**
	 * Re-write all formulas w.r.t. to a set of rules
	 * @param formulas A set of formulas
	 * @param rules A set of rules
	 * @return Corrected set of formula
	 */
	private List<List<String>> replace(List<List<String>> formulas,
			List<Pair<String, String>> rules) {
		List<List<String>> res = new ArrayList<>();
		for(List<String> f : formulas) {
			List<String> newF = new ArrayList<>();
			for(String t : f) {
				boolean b = true;
				for(Pair<String, String> r : rules) {
					if(r.getY().equals(t) && ! newF.contains(t)) {
						b = false;
						newF.add(r.getX());
					}
				}
				if(b) {
					newF.add(t);
				}
			}
			res.add(newF);
		}
		return res;
	}

	/**
	 * Compute predicates/operators w.r.t to the hierarchy
	 * @param sym All actions/propositions
	 * @param constants All constants
	 * @return predicates/operators
	 */
	public List<Symbol>  compute_litterals_operators(
			List<Symbol> sym, Map<String, String> constants) {
		Map<String,List<Symbol>> names = new HashMap<>();
		Map<String, Map<String, String>> params = new HashMap<>();
		for (Symbol s : sym) {
			if(! names.containsKey(s.getName())) {
				names.put(s.getName(), new ArrayList<>());
			}
			names.get(s.getName()).add(s);
		}
		for(String n : names.keySet()) {
			int j = 1;
			params.put(n,new HashMap<>());
			for(int i = 0; i < names.get(n).get(0).getListParameters().size();
					i++) {
				List<String> tmp = new ArrayList<>();
				for(Symbol s : names.get(n)) {
					if(!tmp.contains(constants.get
							(s.getListParameters().get(i)))) {

						tmp.add(constants.get(s.getListParameters().get(i)));
					}
				}
				String type = "";
				if(tmp.size() == 1) {
					type = tmp.get(0);
				}else {
					List<List<String>> ancestors = new ArrayList<>();
					for(String t : tmp) {
						try {
							ancestors.add(this.ancestors(t));
						} catch (TypeException e) {
							e.printStackTrace();
						}
					}
					int [] idxs = new int[ancestors.size()];
					List<String> types = new ArrayList<>();
					for(int k = 0; k < idxs.length; k++ ) {
						idxs[k] = 0;
						types.add(ancestors.get(k).get(0));
					}
					while(! isTrivial(types)){
						//Update indexes
						for(int k = 0; k < idxs.length; k++ ) {
							idxs[k]++;
							if(idxs[k] >= ancestors.get(k).size()) {
								idxs[k]=0;
							}else {
								break;
							}
						}

						//Update types
						types.clear();
						for(int k = 0; k < idxs.length; k++ ) {
							types.add(ancestors.get(k).get(idxs[k]));
						}
					}
					type = types.get(0);
				}
				params.get(n).put("?x"+j, type);
				j++;
			}
		}
		List<Symbol> res = new ArrayList<>();
		for(Symbol s : sym) {
			Map<String, String> absParam = new HashMap<>();
			int k =1;
			for(String p : s.getParameters().keySet()) {
				absParam.put(p, params.get(s.getName()).get("?x"+k));
				k++;
			}
			s.setAbstractParameters(absParam);
			res.add(s);
		}
		return res;
	}

	/**
	 * Find all descendants of t
	 * @param type A type
	 * @return all descendants
	 */
	public List<String> ancestors(String type) throws TypeException{
		List<String> res = this.first.ancestors(type);
		if(res == null) {
			throw new exception.TypeException("Type "+type+" doesn't exist");
		}
		return res;
	}

	/**
	 * String representation of the type hierarchy
	 *
	 * @return A string
	 */
	@Override
	public String toString() {
		return this.first.toString();
	}

	/**
	 * Find all descendants of t
	 * @param t A type
	 * @return all descendants
	 */
	public List<String> descendants(String t) {
		return this.first.descendants(t);
	}
}
