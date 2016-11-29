package parser;

/**
 * Created by ywang2 on 11/28/16.
 */
public class LiteralConstraint extends Constraint {
    String literal;
    Integer evalNodeId;
    ConstraintCompiler.LinkageEnum linkageEnum;
    public boolean IsLiteralConstraint() {return true;}
    public LiteralConstraint(Integer id, ConstraintCompiler.LinkageEnum e, String literal) throws ConstraintCompiler.ConstraintCompilationException {
        evalNodeId = id;
        linkageEnum = e;
        this.literal = literal;
        // current only supports EQ
        type = ConstraintType.EQ;
        if (this.literal.isEmpty()) {
            throw new ConstraintCompiler.ConstraintCompilationException("literal constraint cannot be empty!");
        }
    }
    public String GetLiteral() {
        return literal;
    }
    public Integer GetEvalNodeId() {
        return evalNodeId;
    }
    public ConstraintCompiler.LinkageEnum GetLinkageEnum() {
        return linkageEnum;
    }
     public String toString() {
        StringBuilder sb = new StringBuilder("LiteralConstraint: ");
        sb.append(literal);
        sb.append(" constraining ");
        sb.append("Edge " + evalNodeId + " at linkageEnum " + linkageEnum);
        sb.append("\n");
        return sb.toString();
    }
}
