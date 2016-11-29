package parser;

/**
 * Created by ywang2 on 11/28/16.
 */
public class LinkageConstraint extends Constraint implements Comparable<LinkageConstraint> {
    public Integer leftEdgeNodeId;
    public ConstraintCompiler.LinkageEnum leftLinkageEnum;
    public Integer rightEdgeNodeId;
    public ConstraintCompiler.LinkageEnum rightLinkageEnum;

    // TODO: might consider different costs for different eval strategy
    Long cost;

    public LinkageConstraint(Integer l, ConstraintCompiler.LinkageEnum le, Integer r, ConstraintCompiler.LinkageEnum re) throws ConstraintCompiler.ConstraintCompilationException {
        leftEdgeNodeId = l;
        leftLinkageEnum = le;
        rightEdgeNodeId = r;
        rightLinkageEnum = re;
        // current only supports EQ
        type = ConstraintType.EQ;
        if (leftEdgeNodeId == rightEdgeNodeId && leftLinkageEnum.equals(rightLinkageEnum)) {
            throw new ConstraintCompiler.ConstraintCompilationException("Constraint cannot be equal at both sides!");
        }
    }
    public boolean IsLiteralConstraint() {return false;}
    public String toString() {
        StringBuilder sb = new StringBuilder("LinkageConstraint:");
        sb.append(" LHS constraining ");
        sb.append(leftEdgeNodeId + " " + leftLinkageEnum);
        sb.append(" ,RHS constraining ");
        sb.append(rightEdgeNodeId + " " + rightLinkageEnum);
        sb.append("\n");
        return sb.toString();
    }
    // TODO: Implement cost computation.
    public int compareTo(LinkageConstraint RHS) {
        // temporary impl
        if (leftEdgeNodeId == rightEdgeNodeId) return -1;
        else return leftEdgeNodeId - rightEdgeNodeId;
    }
}
