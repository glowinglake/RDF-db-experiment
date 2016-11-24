package parser;

import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Tuple3;
import org.codehaus.jparsec.functors.Tuple5;

import java.util.List;

/**
 * Created by ywang2 on 11/22/16.
 * Parse prolog query statements into Query object for constraint compilation.
 */
public class QueryParser {
    public static void run() {

        Terminals operators = Terminals.operators(","); // only one operator supported so far
        org.codehaus.jparsec.Parser<?> integerTokenizer = Terminals.IntegerLiteral.TOKENIZER;
        org.codehaus.jparsec.Parser<String> integerSyntacticParser = Terminals.IntegerLiteral.PARSER;
        org.codehaus.jparsec.Parser<?> ignored = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES);
        org.codehaus.jparsec.Parser<?> tokenizer = Parsers.or(operators.tokenizer(), integerTokenizer); // tokenizes the operators and integer
        org.codehaus.jparsec.Parser<List<String>> integers = integerSyntacticParser.sepBy(operators.token(","))
                .from(tokenizer, ignored.skipMany());
        System.out.print(integers.parse("1, /*this is comment*/2, 3").toString());
        //    assertEquals(Arrays.asList("1", "2", "3"), integers.parse("1, /*this is comment*/2, 3");

    }


    // begin prolog parser

    public static class Literal extends Linkage {
        String bytes;
        public Literal(String s) {
            bytes = s;
        }
        @Override
        public boolean IsLiteral() {
            return true;
        }
        public String toString() {
            return new String("Literal: " + bytes);
        }
    }
    public static class Bind extends Linkage {
        String bind_symbol;
        public Bind(String b) {
            bind_symbol = b;
        }
        @Override
        public boolean IsLiteral() {
            return false;
        }
        public String toString() {
            return new String("Bind: " + bind_symbol);
        }
    }
    public abstract static class Linkage {
        boolean isLiteral;
        public abstract boolean IsLiteral();
    }
    public  static class AllLinkage {
        Linkage sub;
        Linkage pred;
        Linkage obj;
        public AllLinkage(Linkage s, Linkage p, Linkage o) {
            sub = s;
            pred = p;
            obj = o;
        }
        public String toString() {
            return new String("AllLinks | Sub " + sub + " | pred " + pred + " | obj " + obj + "\n");
        }
    }
    public static class EdgeTerm {
        AllLinkage allLinkage;
        public EdgeTerm(AllLinkage all) {
            allLinkage = all;
        }
        public String toString() {
            return new String("EdgeTerm: " + allLinkage);
        }
    }
    // multiple edges represent a LiquidQuery
    // EdgeNode(a,"p1",b), EdgeNode(b, "p2",c), EdgeNode(c, "p3","xxx")?
    public static class LiquidQuery {
        List<EdgeTerm> edgeTerms;
        public LiquidQuery(List<EdgeTerm> terms) {
            edgeTerms = terms;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\nLiquidQuery\n");
            for (EdgeTerm e : edgeTerms) {
                sb.append(e);
            }
            sb.append("end Liquid Query\n");
            return sb.toString();
        }
    }
    private static org.codehaus.jparsec.Parser<Literal> literalParser() {
        return Parsers.tuple(Scanners.string("\""),
                Scanners.IDENTIFIER, Scanners.string("\""))
                .map(new Map<Tuple3<Void, String, Void>, Literal>() {
                    @Override
                    public Literal map(Tuple3<Void, String, Void> voidStringVoidTuple3) {
                        return new Literal(voidStringVoidTuple3.b);
                    }
                });
    }
    private static org.codehaus.jparsec.Parser<Bind> bindParser() {
        return Scanners.IDENTIFIER
                .map(new Map<String, Bind>() {
            @Override
            public Bind map(String b) {
                return new Bind(b);
            }
        });
    }
    private static org.codehaus.jparsec.Parser<Linkage> linkageParser() {
        return Parsers.or(literalParser(), bindParser());
    }
    private static org.codehaus.jparsec.Parser<Void> Delimiter() {
        return Parsers.sequence(Scanners.string(","),
                Scanners.WHITESPACES.many())
                .map(new Map<List<Void>, Void>() {
                    @Override
                    public Void map(List<Void> spaces) {
                        return null;
                    }
                });
    }
    private static org.codehaus.jparsec.Parser<AllLinkage> allLinkageParser() {
        return Parsers.tuple(linkageParser(),
                Delimiter(),
                linkageParser(),
                Delimiter(),
                linkageParser())
                .map(new Map<Tuple5<Linkage, Void, Linkage, Void, Linkage>, AllLinkage>() {
                   @Override
                    public AllLinkage map(Tuple5<Linkage, Void, Linkage, Void, Linkage> args) {
                       return new AllLinkage(args.a, args.c, args.e);
                   }
                });
    }
    private static org.codehaus.jparsec.Parser<EdgeTerm> edgeTermParser() {
        return Parsers.tuple(Scanners.string("EdgeNode("),
                allLinkageParser(),
                Scanners.string(")"))
                .map(new Map<Tuple3<Void, AllLinkage, Void>, EdgeTerm>() {
                    @Override
                    public EdgeTerm map(Tuple3<Void, AllLinkage, Void> args) {
                        return new EdgeTerm(args.b);
                    }
                });
    }

    public static org.codehaus.jparsec.Parser<LiquidQuery> queryParser() {
        return Parsers.tuple(Scanners.WHITESPACES.many(),
                edgeTermParser().sepBy1(Delimiter()),
                Scanners.WHITESPACES.many(),
                Scanners.string("?"),
                Scanners.WHITESPACES.many()).map(new Map<Tuple5<List<Void>, List<EdgeTerm>, List<Void>, Void, List<Void>>, LiquidQuery>() {
            @Override
            public LiquidQuery map(Tuple5<List<Void>, List<EdgeTerm>, List<Void>, Void, List<Void>> listListListVoidListTuple5) {
                return new LiquidQuery(listListListVoidListTuple5.b);
            }
        });
    }


    public static void test() {
        System.out.print(edgeTermParser().parse("EdgeNode(a, \"mypred\", o)").toString());
        System.out.print(queryParser().parse("EdgeNode(a,\"p1\",b), EdgeNode(b, \"p2\",c), EdgeNode(c, \"p3\",\"xxx\")? ").toString());

/*
        QueryParser<List<String>> edges = edgeParser.sepBy(operators.token(","))
                .from(tokenizer, ignored.skipMany());

*/
    }
}