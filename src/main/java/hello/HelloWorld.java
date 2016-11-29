package hello;

import parser.ConstraintCompiler;
import parser.ConstraintCompiler.QuerySpec;
import parser.QueryParser;
import parser.QueryParser.LiquidQuery;

public class HelloWorld {
    public static void main(String[] args) {
        Greeter greeter = new Greeter();
        System.out.println(greeter.sayHello());
        QueryParser p = new QueryParser();
        p.test();
        LiquidQuery liquidQuery = QueryParser.queryParser().parse(
                "EvalNode(a,\"p1\",b)," +
                        "EvalNode(b, \"p2\",c)," +
                        "EvalNode(c, \"p3\",\"xxx\")? ");
        try {
            QuerySpec spec = ConstraintCompiler.Compile(liquidQuery);
            System.out.println(spec.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}