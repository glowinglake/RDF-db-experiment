package evaluator;

import javafx.util.Pair;
import parser.ConstraintCompiler;

import java.util.*;

/**
 * Created by ywang2 on 11/29/16.
 * Manages the state of a partial query,
 * which consists of one or more EvalNode(s).
 */
public class EvalNodeTuple implements Cloneable {
    // mapping from edge id to EvalNodes in current tuple.
    // entries sorted by EvalNodeId
    public TreeMap<Integer, EvalNode> evalNodeMap;
    /*
    maintain mapping between bind id and linkages in EvalNodes
    TODO: explain Edge(a, "p1", a), Edge(a, p, b)?
    */
    public Map<Integer, List<Pair<Integer, ConstraintCompiler.LinkageEnum>>> bind2EvalNodeLinks;

    public TreeMap<Integer, Integer> evalNodeId2DerivationPos;

    public List<Derivation> derivations;

    public EvalNodeTuple() {
        evalNodeMap = new TreeMap<>();
        bind2EvalNodeLinks = new HashMap<>();
        derivations = new ArrayList<>();
        evalNodeId2DerivationPos = new TreeMap<>();
    }

    // Construct the initial single derivation
    // for future evaluation. All LazyEdgeSet in the
    // derivation will be fully-unconstrained.
    public void InitDerivations() {
        Derivation d = new Derivation();
        for (Map.Entry<Integer, EvalNode> entry : evalNodeMap.entrySet()) {
            evalNodeId2DerivationPos.put(entry.getValue().evalNodeId, d.Length());
            d.AddLazyEdgeSet(new LazyEdgeSet());
        }
        derivations.add(d);

    }

    public void AddEvalNode(EvalNode evalNode) {
        evalNodeMap.put(evalNode.evalNodeId, evalNode);
        BuildBind2EvalNodeLinksMap();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("EvalNodeTuple\n");
        for (Map.Entry<Integer, EvalNode> e : evalNodeMap.entrySet()) {
            sb.append(e.getValue());
        }
        sb.append("\nEnd of EvalNodeTuple\n");
        return sb.toString();
    }

    public void BuildBind2EvalNodeLinksMap() {
        bind2EvalNodeLinks.clear();
        for (Map.Entry<Integer, EvalNode> entry : evalNodeMap.entrySet()) {
            Integer evalNodeId = entry.getKey();
            if (!entry.getValue().sub.IsLiteral()) {
                ConstraintCompiler.LinkageOperand sub = (ConstraintCompiler.LinkageOperand) entry.getValue().sub;
                Pair<Integer, ConstraintCompiler.LinkageEnum> pair = new Pair<>(evalNodeId, ConstraintCompiler.LinkageEnum.Subject);
                List<Pair<Integer, ConstraintCompiler.LinkageEnum>> list =
                        bind2EvalNodeLinks.getOrDefault(sub.bind_id, new ArrayList<Pair<Integer, ConstraintCompiler.LinkageEnum>>());
                list.add(pair);
                bind2EvalNodeLinks.put(sub.bind_id, list);
            }
            if (!entry.getValue().pred.IsLiteral()) {
                ConstraintCompiler.LinkageOperand pred = (ConstraintCompiler.LinkageOperand) entry.getValue().pred;
                Pair<Integer, ConstraintCompiler.LinkageEnum> pair = new Pair<>(evalNodeId, ConstraintCompiler.LinkageEnum.Predicate);
                List<Pair<Integer, ConstraintCompiler.LinkageEnum>> list =
                        bind2EvalNodeLinks.getOrDefault(pred.bind_id, new ArrayList<Pair<Integer, ConstraintCompiler.LinkageEnum>>());
                list.add(pair);
                bind2EvalNodeLinks.put(pred.bind_id, list);
            }
            if (!entry.getValue().obj.IsLiteral()) {
                ConstraintCompiler.LinkageOperand obj = (ConstraintCompiler.LinkageOperand) entry.getValue().obj;
                Pair<Integer, ConstraintCompiler.LinkageEnum> pair = new Pair<>(evalNodeId, ConstraintCompiler.LinkageEnum.Object);
                List<Pair<Integer, ConstraintCompiler.LinkageEnum>> list =
                        bind2EvalNodeLinks.getOrDefault(obj.bind_id, new ArrayList<Pair<Integer, ConstraintCompiler.LinkageEnum>>());
                list.add(pair);
                bind2EvalNodeLinks.put(obj.bind_id, list);
            }
        }
    }
}
