package parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ywang2 on 11/29/16.
 */
public class QueryParserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void TestParser1() {
        String queryString = "EvalNode(a,\"p1\",b)," +
                "EvalNode(b, \"p2\",c)," +
                "EvalNode(c, \"p3\",\"xxx\")? ";
        QueryParser.LiquidQuery liquidQuery = parser.QueryParser.queryParser().parse(queryString);
        assert liquidQuery.edgeTerms.size() == 3;

        assert !liquidQuery.edgeTerms.get(0).allLinkage.sub.IsLiteral();
        assert liquidQuery.edgeTerms.get(0).allLinkage.pred.IsLiteral();
        assert !liquidQuery.edgeTerms.get(0).allLinkage.obj.IsLiteral();

        assert !liquidQuery.edgeTerms.get(1).allLinkage.sub.IsLiteral();
        assert liquidQuery.edgeTerms.get(1).allLinkage.pred.IsLiteral();
        assert !liquidQuery.edgeTerms.get(1).allLinkage.obj.IsLiteral();

        assert !liquidQuery.edgeTerms.get(2).allLinkage.sub.IsLiteral();
        assert liquidQuery.edgeTerms.get(2).allLinkage.pred.IsLiteral();
        assert liquidQuery.edgeTerms.get(2).allLinkage.obj.IsLiteral();

        try {
            ConstraintCompiler.QuerySpec spec = ConstraintCompiler.Compile(liquidQuery);
            System.out.println(spec.toString());
            assertEquals(spec.evalNodeTuples.size(), 3);
            assertEquals(spec.literalConstraints.size(), 4);
            assertEquals(spec.linkageConstraints.size(), 2);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

}