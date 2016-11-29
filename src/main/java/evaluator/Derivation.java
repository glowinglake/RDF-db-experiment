package evaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tuple that forms a derivation.
 * the LazyEdgeSet in derivation is sorted by EvalNodeId, ascending.
 * Created by ywang2 on 11/29/16.
 */
public class Derivation {
    List<LazyEdgeSet> lazyEdgeSets;
    public Derivation() {lazyEdgeSets = new ArrayList<>();}
    public void AddLazyEdgeSet(LazyEdgeSet lazyEdgeSet) {
        lazyEdgeSets.add(lazyEdgeSet);
    }
    Integer Length() {return lazyEdgeSets.size();}
}
