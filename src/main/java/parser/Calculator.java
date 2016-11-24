package parser;

import org.codehaus.jparsec.*;
import java.util.function.BinaryOperator;
import java.util.function.*;
/**
 * Created by ywang2 on 11/21/16.
 */
public class Calculator {

    static final Parser<Double> NUMBER =
            Terminals.DecimalLiteral.PARSER.map(Double::valueOf);

    private static final Terminals OPERATORS =
            Terminals.operators("+", "-", "*", "/", "(", ")");

    static final Parser<Void> IGNORED = Parsers.or(
            Scanners.JAVA_LINE_COMMENT,
            Scanners.JAVA_BLOCK_COMMENT,
            Scanners.WHITESPACES).skipMany();

    static final Parser<?> TOKENIZER =
            Parsers.or(Terminals.DecimalLiteral.TOKENIZER, OPERATORS.tokenizer());

    static Parser<?> term(String... names) {
        return OPERATORS.token(names);
    }

    static <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }

    static Parser<Double> calculator(Parser<Double> atom) {
        Parser.Reference<Double> ref = Parser.newReference();
        Parser<Double> unit = ref.lazy().between(term("("), term(")")).or(atom);
        Parser<Double> parser = new OperatorTable<Double>()
                .infixl(op("+", (l, r) -> l + r), 10)
                .infixl(op("-", (l, r) -> l - r), 10)
                .infixl(op("/", (l, r) -> l / r), 20)
                .prefix(op("-", v -> -v), 30)
                .build(unit);
        ref.set(parser);
        return parser;
    }

    public static final Parser<Double> CALCULATOR =
            calculator(NUMBER).from(TOKENIZER, IGNORED);
}
