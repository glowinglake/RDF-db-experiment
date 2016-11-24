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
                "EdgeNode(a,\"p1\",b)," +
                        "EdgeNode(b, \"p2\",c)," +
                        "EdgeNode(c, \"p3\",\"xxx\")? ");
        QuerySpec spec = ConstraintCompiler.Compile(liquidQuery);
        System.out.println(spec.toString());
    }
}