package evaluator;

import parser.ConstraintCompiler;

/**
 * Created by ywang2 on 11/29/16.
 */
public class EvalNode {
    int evalNodeId;
    public ConstraintCompiler.Operand sub;
    public ConstraintCompiler.Operand pred;
    public ConstraintCompiler.Operand obj;

    public EvalNode(int eid, ConstraintCompiler.Operand s, ConstraintCompiler.Operand p, ConstraintCompiler.Operand o) {
        evalNodeId = eid;
        sub = s;
        pred = p;
        obj = o;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(" EvalNode \n");
        sb.append("EdgeId " + evalNodeId + "| Sub " + sub.toString()
                + "| Pred " + pred.toString()
                + "| Obj " + obj.toString());
        return sb.toString();
    }
}
