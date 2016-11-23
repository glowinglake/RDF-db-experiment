package parser;

import com.sun.javafx.geom.Edge;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Tuple3;
import org.codehaus.jparsec.functors.Tuple5;

import java.util.List;

/**
 * Created by ywang2 on 11/22/16.
 */
public class ListInt {
    public static void run() {

        Terminals operators = Terminals.operators(","); // only one operator supported so far
        Parser<?> integerTokenizer = Terminals.IntegerLiteral.TOKENIZER;
        Parser<String> integerSyntacticParser = Terminals.IntegerLiteral.PARSER;
        Parser<?> ignored = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES);
        Parser<?> tokenizer = Parsers.or(operators.tokenizer(), integerTokenizer); // tokenizes the operators and integer
        Parser<List<String>> integers = integerSyntacticParser.sepBy(operators.token(","))
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
        public String toString() {
            return new String("Literal: " + bytes);
        }
    }
    public static class Bind extends Linkage {
        String bind_symbol;
        public Bind(String b) {
            bind_symbol = b;
        }
        public String toString() {
            return new String("Bind: " + bind_symbol);
        }
    }
    public static class Linkage {
        boolean isLiteral;
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
            return new String("AllLinks | Sub " + sub + " | pred " + pred + " | obj " + obj);
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
    private static Parser<Literal> literalParser() {
        return Parsers.tuple(Scanners.string("\""),
                Scanners.IDENTIFIER, Scanners.string("\""))
                .map(new Map<Tuple3<Void, String, Void>, Literal>() {
                    @Override
                    public Literal map(Tuple3<Void, String, Void> voidStringVoidTuple3) {
                        return new Literal(voidStringVoidTuple3.b);
                    }
                });
    }
    private static Parser<Bind> bindParser() {
        return Scanners.IDENTIFIER
                .map(new Map<String, Bind>() {
            @Override
            public Bind map(String b) {
                return new Bind(b);
            }
        });
    }
    private static Parser<Linkage> linkageParser() {
        return Parsers.or(literalParser(), bindParser());
    }
    private static Parser<Void> LinkageDelimiter() {
        return Parsers.sequence(Scanners.string(","),
                Scanners.WHITESPACES.many())
                .map(new Map<List<Void>, Void>() {
                    @Override
                    public Void map(List<Void> spaces) {
                        return null;
                    }
                });
    }
    private static Parser<AllLinkage> allLinkageParser() {
        return Parsers.tuple(linkageParser(),
                LinkageDelimiter(),
                linkageParser(),
                LinkageDelimiter(),
                linkageParser())
                .map(new Map<Tuple5<Linkage, Void, Linkage, Void, Linkage>, AllLinkage>() {
                   @Override
                    public AllLinkage map(Tuple5<Linkage, Void, Linkage, Void, Linkage> args) {
                       return new AllLinkage(args.a, args.c, args.e);
                   }
                });
    }
    private static Parser<EdgeTerm> edgeTermParser() {
        return Parsers.tuple(Scanners.string("Edge("),
                allLinkageParser(),
                Scanners.string(")"))
                .map(new Map<Tuple3<Void, AllLinkage, Void>, EdgeTerm>() {
                    @Override
                    public EdgeTerm map(Tuple3<Void, AllLinkage, Void> args) {
                        return new EdgeTerm(args.b);
                    }
                });
    }

    public static void test() {


        System.out.print(edgeTermParser().parse("Edge(a, \"mypred\", o)").toString());

/*
        Parser<List<String>> edges = edgeParser.sepBy(operators.token(","))
                .from(tokenizer, ignored.skipMany());

*/
    }
}