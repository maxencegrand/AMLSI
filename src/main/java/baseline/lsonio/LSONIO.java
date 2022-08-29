package baseline.lsonio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import fsm.Example;
import fsm.Pair;
import fsm.Sample;
import fsm.Symbol;
import learning.Domain;
import learning.Observation;
import learning.ObservedExample;

/**
* Implements the LSO-NIO approach
* @see <a href="https://arxiv.org/abs/1210.4889"> lsonio</a>
* @author Maxence Grand
*
*/
public class LSONIO {
    /**
    * The data
    */
    private Sample data;
    /**
    * The action list
    */
    private List<Symbol> actions;
    /**
    * The proposition list
    */
    private List<Symbol> predicates;
    /**
    * The classifier vector
    */
    private Learner learner;

    /**
    *
    * Constructs
    */
    public LSONIO() {
        this.actions = new ArrayList<>();
        this.predicates = new ArrayList<>();
        this.data = new Sample();
    }

    /**
    * Constructs
    * @param data The data
    * @param actions The action list
    * @param predicates The proposition list
    */
    public LSONIO(Sample data, List<Symbol> actions, List<Symbol> predicates) {
        this();
        this.data = new Sample(data);
        for(Symbol action : actions) {
            this.actions.add(action);
        }
        for(Symbol predicate : predicates) {
            this.predicates.add(predicate);
        }
        this.predicates = predicates;
    }

    /**
    * Return the deictic vector
    * @param a An action
    * @param o An observation
    * @return Deictic vector
    */
    private List<Double> deictic(Symbol a, Observation o) {
        List<String> vector = this.vector();
        List<Double> res = new LinkedList<>();
        for(int i = 0; i< vector.size(); i++) {
            res.add(null);
        }
        Map<String, String> map = a.mapping();
        o.getPredicates().forEach((k,v) -> {
            if(a.compatible(k)) {
                int idx = 0;
                Symbol prop = k.generalize(map);

                //Case where the property is object independent
                if(prop.getListParameters().isEmpty()) {
                    idx = vector.indexOf(prop.getName());
                    switch(v) {
                        case TRUE:
                        res.set(idx,1.0);
                        break;
                        case FALSE:
                        res.set(idx,-1.0);
                        break;
                        default:
                        res.set(idx, null);
                        break;
                    }
                } else { //Case where the property is object dependent
                    String args = prop.getListParameters().get(0);
                    String tmp = prop.getName();
                    for(int i = 1; i<prop.getListParameters().size();
                    i++) {
                        String str = prop.getListParameters().get(i);
                        tmp+= (":"+str);
                    }
                    tmp+=(":"+args);
                    idx = vector.indexOf(tmp);
                    switch(v) {
                        case TRUE:
                        res.set(idx,1.0);
                        break;
                        case FALSE:
                        res.set(idx,-1.0);
                        break;
                        default:
                        res.set(idx, null);
                        break;
                    }
                }
            }
        });

        return res;
    }

    /**
    * Learn action model
    * @return Action model learnt
    */
    public Domain learn() {
        Map<Symbol, List<Pair<List<Double>,List<Double>>>> training
        = new HashMap<>();

        //Train perceptron
        data.getExamples().forEach((k)->{
        	List<Observation> v = ((ObservedExample)k).getObservations();
            for(int i = 0; i<k.size(); i++) {
                List<Double> prior = this.deictic(((Example)k).get(i), v.get(i));
                List<Double> succ = this.deictic(((Example)k).get(i), v.get(i+1));
                if(! training.containsKey(((Example)k).get(i).generalize())) {
                    training.put(((Example)k).get(i).generalize(), new ArrayList<>());
                }
                training.get(((Example)k).get(i).generalize()).add(new Pair<>(prior, succ));
            }
        });
        int nbActions = 0;
        for(Symbol a : training.keySet()) {
            nbActions+=training.get(a).size();
        }
        System.out.println(nbActions+" actions in training set");
        learner = new Learner(training, this.vector().size());
        System.out.println("Train Perceptron");
        learner.train();

        //Extract SV+/SV-
        Map<Pair<Symbol, Integer>, List<List<Double>>> SVp = new HashMap<>();
        Map<Pair<Symbol, Integer>, List<List<Double>>> SVm = new HashMap<>();
        training.forEach((k,v)->{
            List<String> vec = this.vector();
            for(int i = 0; i < vec.size(); i++) {
                List<List<Double>> setPrec = new ArrayList<>();
                List<List<Double>> setNeg = new ArrayList<>();
                final int idx = i;
                v.forEach(p -> {
                    List<Double> prior = p.getX();
                    List<Double> succ = p.getY();
                    List<Double> diff = Classifier.diff(prior, succ);
                    if(diff.get(idx) != null && diff.get(idx) == 1) {
                        if(! LSONIO.containsVector(setPrec, prior)) {
                            setPrec.add(prior);
                        }
                    } else if(diff.get(idx) != null && diff.get(idx) == 0) {
                        if(! LSONIO.containsVector(setNeg, prior)) {
                            setNeg.add(prior);
                        }
                    }
                });
                SVp.put(new Pair<>(k,i), setPrec);
                SVm.put(new Pair<>(k,i), setNeg);
            }
        });

        //Extract rules
        System.out.println("Extract rules");
        Map<Symbol, PriorityQueue<Rule>> rules = new HashMap<>();
        SVp.forEach((p,v) -> {
            if(!rules.containsKey(p.getX())) {
                PriorityQueue<Rule> prio = new PriorityQueue<>();
                rules.put(p.getX(), prio);
            }
            List<Rule> r = new ArrayList<>();
            v.forEach(parent -> {
                List<List<Double>> childs = new ArrayList<>();
                LSONIO.getChilds(parent).forEach(c -> {
                    if(LSONIO.notNeg(SVm.get(p), c)) {
                        childs.add(c);
                    }
                });
                while(! childs.isEmpty()) {
                    double wP = learner.weight(p.getX(), parent, p.getY());
                    double tmp = wP - learner.weight
                                            (p.getX(), childs.get(0), p.getY());
                    int idx = 0;
                    for(int i = 1; i < childs.size(); i++) {
                        double tmp2 = wP - learner.weight
                                            (p.getX(), childs.get(i), p.getY());
                        if(tmp2 < tmp) {
                            idx = i;
                        }
                    }
                    parent = childs.get(idx);
                    childs.clear();
                    LSONIO.getChilds(parent).forEach(c -> {
                        if(LSONIO.notNeg(SVm.get(p), c)) {
                            childs.add(c);
                        }
                    });
                }
                Rule rule = new Rule(parent, p.getY(),
                learner.weight(p.getX(), parent, p.getY()));
                if(!r.contains(rule)) {
                    r.add(rule);
                }

            });
            for(Rule rule : r) {
                rules.get(p.getX()).offer(rule);
            }
        });

        //Combine rules
        RulesCombiner comb = new RulesCombiner(rules, training, learner);
        Map<Symbol, List<String>>
        pre = new HashMap<>(),
        neg = new HashMap<>(),
        addList = new HashMap<>(),
        delList = new HashMap<>();
        System.out.println("Combine rules");
        training.keySet().forEach(action -> {
            Pair<List<Double>, List<Integer>> p = comb.combine(action);
            List<String> prePos = new ArrayList<>(), preNeg = new ArrayList<>();
            List<String> add = new ArrayList<>(), del = new ArrayList<>();

            if(p!=null) {
                for(int i = 0; i < p.getX().size(); i++) {
                    if(p.getX().get(i) == null) {
                        continue;
                    } else if (p.getX().get(i) == 1.0) {
                        prePos.add(this.vector().get(i));
                    } else {
                        preNeg.add(this.vector().get(i));
                    }
                }

                for(int e : p.getY()) {
                    if(p.getX().get(e) == null) {
                        continue;
                    } else if (p.getX().get(e) == -1.0) {
                        add.add(this.vector().get(e));
                    } else {
                        del.add(this.vector().get(e));
                    }
                }
            }
            pre.put(action, prePos);
            neg.put(action, preNeg);
            addList.put(action, add);
            delList.put(action, del);
        });

        Domain Domain = new Domain(
        this.predicates,
        this.actions,
        pre,
        neg,
        addList,
        delList);
        return Domain;
    }

    /**
    * Return all childs of a vector x in the lattice of preconditions
    * @param x A vector
    * @return A set of vector
    */
    private static List<List<Double>> getChilds(List<Double> x) {
        List<List<Double>> childs = new ArrayList<>();
        for(int i = 0; i<x.size(); i++) {
            if(x.get(i) != null) {
                List<Double> c = new ArrayList<>();
                c.addAll(x);
                c.set(i, null);
                childs.add(c);
            }
        }
        return childs;
    }


    /**
    * Return the vector representation of the action model to learn
    * @return A vector
    */
    private List<String> vector() {
        //Compute operator
        List<Symbol> tmp = new ArrayList<>();
        int maxArity= 0;
        for(Symbol a : actions) {
            if(! tmp.contains(a.generalize())) {
                tmp.add(a.generalize());
            }
            if(a.getListParameters().size() > maxArity) {
                maxArity = a.getListParameters().size();
            }
        }
        //sort operator
        List<String> sortedOperator = new LinkedList<>();
        for(int i = 0; i <= maxArity; i++) {
            for(Symbol o : tmp) {
                if(o.getListParameters().size() == i) {
                    sortedOperator.add(o.toString());
                }
            }
        }
        //compute generalized predicates
        tmp.clear();
        int maxArityPred = 0;
        List<String> sortedProperties = new LinkedList<>();
        for(Symbol p : predicates) {
            if(! tmp.contains(p.generalize())) {
                tmp.add(p.generalize());
            }
            if(p.getListParameters().size() > maxArityPred) {
                maxArityPred = p.getListParameters().size();
            }
        }

        //Add object independent properties
        for(Symbol p : predicates) {
            if(p.getListParameters().size() == 0) {
                sortedProperties.add(p.getName());
            }
        }

        //Add properties for each object
        for(int obj = 1; obj <= maxArity; obj++) {
            for(int propArity = 1; propArity <= maxArityPred; propArity++ ) {
                for(Symbol p : tmp) {
                    if(p.getListParameters().size() == propArity) {
                        sortedProperties.addAll(getPermut
                                    (p.getName(),maxArity, obj, propArity-1));
                    }
                }
            }
        }
        return sortedProperties;
    }

    /**
    * Compute all parameters' permutations
    * @param name The predicate's name
    * @param arity The predicate's arity
    * @param index The index of arg
    * @param size The total number of parameters
    * @return All parameters' permutations
    */
    private List<String> getPermut(String name, int arity, int index, int size) {

        //Compute arg
        String currentArg = "?x"+index;
        if(size == 0) {
            String tmp = name+":"+currentArg;
            List<String> res = new ArrayList<>();
            res.add(tmp);
            //System.out.println(res);
            return res;
        }
        List<String> args = new LinkedList<>();
        for(int i = 1; i<=arity; i++) {
            args.add("?x"+i);
        }

        //Compute all permutation
        List<Integer> idxs = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            idxs.add(0);
        }

        List<String> allPermut = new LinkedList<>();
        int idx = size-1;
        while(idxs.get(0) < (arity)) {
            String tmp = name+":";
            for(int i = 0; i < size; i++) {
                tmp += args.get(idxs.get(i));
                tmp+=":";
            }
            tmp+=currentArg;
            allPermut.add(tmp);
            boolean b = true;
            while(b) {
                idxs.set(idx, idxs.get(idx)+1);
                if(idxs.get(idx) >= arity && idx > 0) {
                    idxs.set(idx, 0);
                    idx--;
                } else {
                    b = false;
                    idx=size-1;
                }
            }
        }
        return allPermut;
    }

    /**
    * Check if two vector are equal
    * @param x A vector
    * @param y A vector
    * @return True if x = y
    */
    private static boolean equalVectors(List<Double> x, List<Double> y) {
        for(int i = 0; i < x.size(); i++) {
            if(x.get(i) == null && y.get(i) != null) {
                return false;
            } else if(x.get(i) != null && y.get(i) == null) {
                return false;
            } else if(x.get(i) == null && y.get(i) == null) {
                continue;
            } else if((double)x.get(i) != (double)y.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
    * Check if a vector list contains a given victor
    * @param v A vector list
    * @param y A vector
    * @return true if y in v
    */
    private static boolean containsVector(List<List<Double>> v, List<Double> y){
        for(List<Double> x : v) {
            if(LSONIO.equalVectors(x, y) ) {
                return true;
            }
        }
        return false;
    }

    /**
    * Check if x doesn't cover negative examples
    * @param svm SV-
    * @param x A prior vector
    * @return true if x doesn't cover negative examples
    */
    private static boolean notNeg(List<List<Double>> svm, List<Double> x) {
        for(List<Double> y : svm) {
            if(LSONIO.contains(y, x)) {
                return false;
            }
        };
        return true;
    }

    /**
    * Check if all values different of null of y are in x
    * @param x A vector
    * @param y A vector
    * @return true if all values different of null of y are in x
    */
    protected static boolean contains(List<Double> x, List<Double> y) {
        for(int i = 0; i < y.size(); i++) {
            if(y.get(i) != null) {
                if(x.get(i) == null || x.get(i)!=y.get(i)) {
                    return false;
                }
            }
        }
        return true;
    }
}
