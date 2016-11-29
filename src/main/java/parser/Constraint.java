package parser;

/**
 * Created by ywang2 on 11/28/16.
 */
// Represents a constraint in the query. A constraint forces
// two linkage fields in one or more EdgeNodes to be EQ / NEQ.
// or a literal to be EQ / NEQ to some byte string.
public abstract class Constraint {
    public enum ConstraintType {
        EQ, NEQ
    }
    abstract boolean IsLiteralConstraint();
    ConstraintType GetConstraintType() {return type;}
    ConstraintType type;
}

