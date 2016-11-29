package evaluator;

import database.Database;
import database.SymbolicEdge;
import parser.ConstraintCompiler;
import parser.LinkageConstraint;
import parser.LiteralConstraint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ywang2 on 11/23/16.
 */
public class Evaluator {
    ConstraintCompiler.QuerySpec querySpec;
    Database database;
    public Evaluator(Database db, ConstraintCompiler.QuerySpec spec) {
        database  = db;
        querySpec = spec;
    }
    /*
    Evaluate the query as specified in querySpec and return
    results as a set of symbolic edge tuples.
     */
    public List<List<SymbolicEdge>> Evaluate() {
        // Initialize data structure
        for (EvalNodeTuple tuple : querySpec.evalNodeTuples) {
            tuple.InitDerivations();
        }
        // Evaluate all the literal constraints.
        if (EvaluateLiterals()) {
            return null;
        }
        // Evaluate all linkage constraints based on costs.
        EvaluateLinkages();
        return null;
    }

    public boolean EvaluateLiterals() {
        for (LiteralConstraint literalConstraint:
             querySpec.literalConstraints) {
            Long id = database.GetLiteralID(literalConstraint.GetLiteral());
            if (id < 0) {
                return false;
            }
            Integer evalNodeId = literalConstraint.GetEvalNodeId();
            EvalNodeTuple tuple = querySpec.evalNodeId2EvalNodeTuple.get(evalNodeId);
            ConstraintCompiler.LinkageEnum e = literalConstraint.GetLinkageEnum();
            // Set the LazyEdgeSet in initial derivation
            assert(tuple.derivations.size() == 1);
            Integer derivationPos = tuple.evalNodeId2DerivationPos.get(evalNodeId);
            tuple.derivations.get(0).lazyEdgeSets.get(derivationPos).SetLinkage(e, id);
        }
        return true;
    }

    public void EvaluateLinkages() {
        while (!querySpec.linkageConstraints.isEmpty()) {
            Collections.sort(querySpec.linkageConstraints);
            LinkageConstraint front = querySpec.linkageConstraints.remove(0);
            if (IsInnerConstraint(front)) {
                // create a new EvalNodeTuple to replace old one
                EvalNodeTuple newNodeTuple
                        = querySpec.evalNodeId2EvalNodeTuple.get(front.leftEdgeNodeId);
            } else {
                // merge two EvalNodeTuple into a new one
            }

        }
        // Materialize all derivations in final EvalNodeTuple
    }

    boolean IsInnerConstraint(LinkageConstraint c) {
        return querySpec.evalNodeId2EvalNodeTuple.get(c.leftEdgeNodeId) ==
                querySpec.evalNodeId2EvalNodeTuple.get(c.rightEdgeNodeId);
    }
}
