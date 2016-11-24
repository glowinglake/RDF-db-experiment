package parser;

import javafx.util.Pair;
import parser.QueryParser.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ywang2 on 11/23/16.
 *
 * compile constraint to be ready for evaluation
 * only support conjunction.
 */
public class ConstraintCompiler {
    public enum LinkageEnum {
        Subject, Predicate, Object
    }
    public abstract static class Operand {
        boolean isLiteral;
        public abstract boolean IsLiteral();
    }
    public static class LinkageOperand extends Operand {
        int bind_id;
        public LinkageOperand(int bid) {
            isLiteral = false;
            bind_id = bid;
        }
        @Override
        public boolean IsLiteral() {return true;};
        public String toString() {
            StringBuilder sb = new StringBuilder("Bind:");
            sb.append(bind_id);
            return sb.toString();
        };
    }
    public static class LiteralOperand extends Operand {
        String literal;
        public LiteralOperand(String l) {
            isLiteral = true;
            literal = l;
        }
        @Override
        public boolean IsLiteral() {return false;};
        public String toString() {
            StringBuilder sb = new StringBuilder("Literal:");
            sb.append("\"");
            sb.append(literal);
            sb.append("\"");
            return sb.toString();
        };
    }
    public static class EdgeNode {
        int edgeNodeId;
        Operand sub;
        Operand pred;
        Operand obj;
        public EdgeNode(int eid, Operand s, Operand p, Operand o) {
            edgeNodeId = eid;
            sub = s;
            pred = p;
            obj = o;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder(" EdgeNode \n");
            sb.append("EdgeId " + edgeNodeId + "| Sub " + sub.toString()
                    + "| Pred " + pred.toString()
                    + "| Obj " + obj.toString());
            return sb.toString();
        }
    }
    // Manages a partial query, consist of one or more EdgeNode(s)
    public static class EdgeNodeTuple {
        // mapping from edge id to EdgeNodes in current tuple
        Map<Integer, EdgeNode> edgeNodeMap;
        /*
        maintain mapping between bind id and linkages in EdgeNodes
        TODO: explain Edge(a, "p1", a), Edge(a, p, b)?
        */
        Map<Integer, List<Pair<Integer, LinkageEnum>>> bind2EdgeNodeLinks;

        public EdgeNodeTuple() {
            edgeNodeMap = new HashMap<>();
            bind2EdgeNodeLinks = new HashMap<>();
        }
        public void AddEdgeNode(EdgeNode edgeNode) {
            edgeNodeMap.put(edgeNode.edgeNodeId, edgeNode);
            BuildBind2EdgeNodeLinksMap();
        }
        public String toString() {
            StringBuilder sb = new StringBuilder("EdgeNodeTuple\n");
            for (Map.Entry<Integer, EdgeNode> e : edgeNodeMap.entrySet()) {
                sb.append(e.getValue());
            }
            sb.append("\nEnd of EdgeNodeTuple\n");
            return sb.toString();
        }
        public void BuildBind2EdgeNodeLinksMap() {
            bind2EdgeNodeLinks.clear();
            for (Map.Entry<Integer, EdgeNode> entry : edgeNodeMap.entrySet()) {
                Integer edgeNodeId = entry.getKey();
                if (!entry.getValue().sub.IsLiteral()) {
                    LinkageOperand sub = (LinkageOperand)entry.getValue().sub;
                    Pair<Integer, LinkageEnum> pair = new Pair<>(edgeNodeId, LinkageEnum.Subject);
                    List<Pair<Integer, LinkageEnum>> list =
                            bind2EdgeNodeLinks.getOrDefault(sub.bind_id, new ArrayList<Pair<Integer, LinkageEnum>>());
                    list.add(pair);
                    bind2EdgeNodeLinks.put(sub.bind_id, list);
                }
                if (!entry.getValue().pred.IsLiteral()) {
                    LinkageOperand pred = (LinkageOperand)entry.getValue().pred;
                    Pair<Integer, LinkageEnum> pair = new Pair<>(edgeNodeId, LinkageEnum.Predicate);
                    List<Pair<Integer, LinkageEnum>> list =
                            bind2EdgeNodeLinks.getOrDefault(pred.bind_id, new ArrayList<Pair<Integer, LinkageEnum>>());
                    list.add(pair);
                    bind2EdgeNodeLinks.put(pred.bind_id, list);
                }
                if (!entry.getValue().obj.IsLiteral()) {
                    LinkageOperand obj = (LinkageOperand)entry.getValue().obj;
                    Pair<Integer, LinkageEnum> pair = new Pair<>(edgeNodeId, LinkageEnum.Object);
                    List<Pair<Integer, LinkageEnum>> list =
                            bind2EdgeNodeLinks.getOrDefault(obj.bind_id, new ArrayList<Pair<Integer, LinkageEnum>>());
                    list.add(pair);
                    bind2EdgeNodeLinks.put(obj.bind_id, list);
                }
            }
        }
    }
    public static class ConstraintCompilationException extends Exception {
        public ConstraintCompilationException(String msg) {super(msg);};
    }
    // Represents a constraint in the query. A constraint forces
    // two linkage fields in one or more EdgeNodes to be equal.
    // Only equal constraint is being considered.
    public static class Constraint {
        Integer leftEdgeNodeId;
        LinkageEnum leftLinkageEnum;
        Integer rightEdgeNodeId;
        LinkageEnum rightLinkageEnum;
        public Constraint(Integer l, LinkageEnum le, Integer r, LinkageEnum re) throws ConstraintCompilationException {
            leftEdgeNodeId = l;
            leftLinkageEnum = le;
            rightEdgeNodeId = r;
            rightLinkageEnum = re;
            if (leftEdgeNodeId == rightEdgeNodeId && leftLinkageEnum.equals(rightLinkageEnum)) {
                throw new ConstraintCompilationException("Constraint cannot be equal at both sides!");
            }
        }
    }
    // A QuerySpec represents the full specification of a query.
    // in the beginning each EdgeNodeTuple contains one EdgeNode
    // the evaluation process resolves one constraint in each round,
    // and can merges two EdgeNodeTuples into one as a result.
    // In the end all constraints are resolved,
    // there is a single tuple remaining, and evaluation is complete.
    public static class QuerySpec {
        List<EdgeNodeTuple> edgeNodeTuples;
        public QuerySpec() {
            edgeNodeTuples = new ArrayList<>();
        }
        public void AddTuple(EdgeNodeTuple t) {
            edgeNodeTuples.add(t);
        }
        public void CompileConstraints() {
            // build a list of constraints based on binds
            // A constraint is either EQ or NEQ between two linkages
            

        }
        public String toString() {
            StringBuilder sb = new StringBuilder(" QuerySpec \n");
            for (EdgeNodeTuple t : edgeNodeTuples) {
                sb.append(t.toString());
            }
            sb.append(" End of QuerySpec\n");
            return sb.toString();
        }
    }
    public static class BindInfo {
        Integer EdgeNodeId;
        Integer BindId;
        LinkageEnum linkageEnum;
    }
    public static Integer GetBindId(String bind_symbol,
                                    Map<String, Integer> bind_to_id) {
        if (bind_to_id.containsKey(bind_symbol)) {
            return bind_to_id.get(bind_symbol);
        } else {
            Integer new_id = bind_to_id.size();
            bind_to_id.put(bind_symbol, new_id);
            return new_id;
        }
    }
    public static Operand GetOperand(Linkage linkage,
                                     Map<String, Integer> bind_to_id) {
        Operand op = null;
        if (linkage.IsLiteral()) {
            Literal literal = (Literal)linkage;
            op = new LiteralOperand(literal.bytes);
        } else {
            Bind bind = (Bind)linkage;
            op = new LinkageOperand(GetBindId(bind.bind_symbol, bind_to_id));
        }
        return op;
    }

    public static QuerySpec Compile(LiquidQuery query) {
        Map<String, Integer> bind_to_id = new HashMap<>();
        Map<String, BindInfo> bindInfoMap = new HashMap<>();
        QuerySpec spec = new QuerySpec();
        int edgeId = 0;
        for (EdgeTerm edgeTerm : query.edgeTerms) {
            Operand sub = GetOperand(edgeTerm.allLinkage.sub, bind_to_id);
            Operand pred = GetOperand(edgeTerm.allLinkage.pred, bind_to_id);
            Operand obj = GetOperand(edgeTerm.allLinkage.obj, bind_to_id);
            EdgeNode edgeNode = new EdgeNode(edgeId, sub, pred, obj);
            EdgeNodeTuple edgeNodeTuple = new EdgeNodeTuple();
            edgeNodeTuple.AddEdgeNode(edgeNode);
            spec.AddTuple(edgeNodeTuple);
            edgeId++;
        }
        spec.CompileConstraints();
        return spec;
    }

}
