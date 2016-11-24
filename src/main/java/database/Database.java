package database;

import java.util.List;

/**
 * Created by ywang2 on 11/23/16.
 * Database is the interface that interacts with evaluator
 * and writer path.
 * Database consists of graph and index.
 * Graph is a chain of records. a record is either
 * a literal or and edge. each edge links three literals.
 * There are no anonymous nodes in the graph.
 */
public interface Database {

    /*
    All read path.
     */

    List<Edge> Lookup(Link lookup);
    Long GetLiteralID(String literal);
    Literal GetLiteral(Long id);
    Edge GetEdge(Long id);

    /*
    All write path.
     */

    boolean AddGraph(List<SymbolicEdge> symbolicEdges);

}
