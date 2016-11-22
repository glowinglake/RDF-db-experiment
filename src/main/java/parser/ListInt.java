package parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;

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
}