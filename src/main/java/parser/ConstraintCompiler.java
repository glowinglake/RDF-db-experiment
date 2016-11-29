package parser;

import evaluator.EvalNode;
import evaluator.EvalNodeTuple;
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
        public int bind_id;
        public LinkageOperand(int bid) {
            isLiteral = false;
            bind_id = bid;
        }
        @Override
        public boolean IsLiteral() {return false;};
        public String toString() {
            StringBuilder sb = new StringBuilder("Bind:");
            sb.append(bind_id);
            return sb.toString();
        };
    }
    public static class LiteralOperand extends Operand {
        String literal;
        String GetLiteral() {return literal;}
        public LiteralOperand(String l) {
            isLiteral = true;
            literal = l;
        }
        @Override
        public boolean IsLiteral() {return true;};
        public String toString() {
            StringBuilder sb = new StringBuilder("literal:");
            sb.append("\"");
            sb.append(literal);
            sb.append("\"");
            return sb.toString();
        };
    }

    public static class ConstraintCompilationException extends Exception {
        public ConstraintCompilationException(String msg) {super(msg);};
    }
    // A QuerySpec represents the full specification of a query.
    // in the beginning each EvalNodeTuple contains one EvalNode
    // the evaluation process resolves one constraint in each round,
    // and can merges two EvalNodeTuples into one as a result.
    // In the end all constraints are resolved,
    // there is a single tuple remaining, and evaluation is complete.
    public static class QuerySpec {
        public List<EvalNodeTuple> evalNodeTuples;
        public List<LiteralConstraint> literalConstraints;
        public List<LinkageConstraint> linkageConstraints;
        public Map<Integer, EvalNodeTuple> evalNodeId2EvalNodeTuple;

        public QuerySpec() {
            evalNodeTuples = new ArrayList<>();
            evalNodeId2EvalNodeTuple = new HashMap<>();
        }

        public void AddTuple(EvalNodeTuple t) {
            evalNodeTuples.add(t);
            for (Map.Entry<Integer, EvalNode> kv : t.evalNodeMap.entrySet()) {
                evalNodeId2EvalNodeTuple.put(kv.getKey(), t);
            }
        }
        public void CompileConstraints() throws ConstraintCompilationException {
            // build a list of constraints based on binds
            // A constraint is either EQ or NEQ between two linkages
            linkageConstraints = BuildLinkageConstraints();
            literalConstraints = BuildLiteralConstraints();

        }
        private List<LinkageConstraint> BuildLinkageConstraints() throws ConstraintCompilationException {
            List<LinkageConstraint> res = new ArrayList<>();
            Map<Integer, Pair<Integer, LinkageEnum>> bind2FirstAppearance
                    = new HashMap<>();
            for (EvalNodeTuple evalNodeTuple : evalNodeTuples) {
                for (Map.Entry<Integer, List<Pair<Integer, LinkageEnum>>> allPairs :
                        evalNodeTuple.bind2EvalNodeLinks.entrySet()) {
                    for (Pair<Integer, LinkageEnum> pair : allPairs.getValue()) {
                        Integer bind_id = allPairs.getKey();
                        if (!bind2FirstAppearance.containsKey(bind_id)) {
                            bind2FirstAppearance.put(bind_id, pair);
                        } else {
                            // already have LHS, create a constraint to connect LHS to RHS
                            Pair<Integer, LinkageEnum> LHS = bind2FirstAppearance.get(bind_id);
                            LinkageConstraint linkageConstraint
                                    = new LinkageConstraint(LHS.getKey(), LHS.getValue(),
                                    pair.getKey(), pair.getValue());
                            res.add(linkageConstraint);
                        }
                    }
                }
            }
            return res;
        }
        private List<LiteralConstraint> BuildLiteralConstraints() throws ConstraintCompilationException {
            List<LiteralConstraint> res = new ArrayList<>();
            for (EvalNodeTuple evalNodeTuple : evalNodeTuples) {
                for (Map.Entry<Integer, EvalNode> evalNodeKV : evalNodeTuple.evalNodeMap.entrySet()) {
                    EvalNode evalNode = evalNodeKV.getValue();
                    if (evalNode.sub.IsLiteral()) {
                        res.add(new LiteralConstraint(
                                evalNodeKV.getKey(), LinkageEnum.Subject,
                                ((LiteralOperand) evalNode.sub).GetLiteral()));
                    }
                    if (evalNode.pred.IsLiteral()) {
                        res.add(new LiteralConstraint(
                                evalNodeKV.getKey(), LinkageEnum.Predicate,
                                ((LiteralOperand) evalNode.pred).GetLiteral()));
                    }
                    if (evalNode.obj.IsLiteral()) {
                        res.add(new LiteralConstraint(
                                evalNodeKV.getKey(), LinkageEnum.Object,
                                ((LiteralOperand) evalNode.obj).GetLiteral()));
                    }
                }
            }
            return res;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder(" QuerySpec \n");
            for (EvalNodeTuple t : evalNodeTuples) {
                sb.append(t.toString());
            }
            for (LiteralConstraint c : literalConstraints) {
                sb.append(c.toString());
            }
            for (LinkageConstraint c : linkageConstraints) {
                sb.append(c.toString());
            }
            sb.append(" End of QuerySpec\n");
            return sb.toString();
        }
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

    public static QuerySpec Compile(LiquidQuery query) throws ConstraintCompilationException{
        Map<String, Integer> bind_to_id = new HashMap<>();
        QuerySpec spec = new QuerySpec();
        // create all EvalNodeTuples, each containing one EvalNode initially.
        int edgeId = 0;
        for (EdgeTerm edgeTerm : query.edgeTerms) {
            Operand sub = GetOperand(edgeTerm.allLinkage.sub, bind_to_id);
            Operand pred = GetOperand(edgeTerm.allLinkage.pred, bind_to_id);
            Operand obj = GetOperand(edgeTerm.allLinkage.obj, bind_to_id);
            EvalNode evalNode = new EvalNode(edgeId, sub, pred, obj);
            EvalNodeTuple evalNodeTuple = new EvalNodeTuple();
            evalNodeTuple.AddEvalNode(evalNode);
            spec.AddTuple(evalNodeTuple);
            edgeId++;
        }
        // create all constraints based on linkage/literal bindings
        spec.CompileConstraints();
        return spec;
    }

}
