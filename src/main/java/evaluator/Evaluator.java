package evaluator;

import database.Database;
import database.SymbolicEdge;
import parser.ConstraintCompiler;

import java.util.List;

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
        return null;
    }
}
