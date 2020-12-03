// Generated from StupsLexer.g4 by ANTLR 4.8

package lexer;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StupsLexer extends Lexer {

    public static final int
            WHITESPACE = 1, MULTILINE_COMMENT = 2, LINE_COMMENT = 3, CLASS = 4, PUBLIC = 5,
            STATIC = 6, VOID_TYPE = 7, BOOLEAN_TYPE = 8, STRING_TYPE = 9, IF = 10, ELSE = 11,
            WHILE = 12, PRINTLN = 13, ASSIGN = 14, ADD = 15, SUB = 16, MUL = 17, DIV = 18, MOD = 19,
            NOT = 20, AND = 21, OR = 22, EQUAL = 23, NOT_EQUAL = 24, LESS = 25, LESS_EQUAL = 26,
            GREATER = 27, GREATER_EQUAL = 28, L_PAREN = 29, R_PAREN = 30, L_BRACE = 31, R_BRACE = 32,
            L_BRACKET = 33, R_BRACKET = 34, SEMICOLON = 35, COMMA = 36, DOT = 37, INTEGER_LIT = 38,
            STRING_LIT = 39, BOOLEAN_LIT = 40, IDENTIFIER = 41;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2+\u0143\b\1\4\2\t" +
            "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
            "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
            "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
            "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
            "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
            ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
            "\64\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\5\6t\n\6\3\7\3\7\5\7x\n\7" +
            "\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\6\f\u0083\n\f\r\f\16\f\u0084\3" +
            "\f\3\f\3\r\3\r\3\r\3\r\7\r\u008d\n\r\f\r\16\r\u0090\13\r\3\r\3\r\3\r\3" +
            "\r\3\r\3\16\3\16\3\16\3\16\7\16\u009b\n\16\f\16\16\16\u009e\13\16\3\16" +
            "\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20" +
            "\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23" +
            "\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25" +
            "\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30" +
            "\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30" +
            "\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35" +
            "\3\36\3\36\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3%\3" +
            "%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3" +
            "/\3\60\3\60\3\61\5\61\u0122\n\61\3\61\6\61\u0125\n\61\r\61\16\61\u0126" +
            "\3\62\3\62\7\62\u012b\n\62\f\62\16\62\u012e\13\62\3\62\3\62\3\63\3\63" +
            "\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u013b\n\63\3\64\3\64\7\64\u013f" +
            "\n\64\f\64\16\64\u0142\13\64\2\2\65\3\2\5\2\7\2\t\2\13\2\r\2\17\2\21\2" +
            "\23\2\25\2\27\3\31\4\33\5\35\6\37\7!\b#\t%\n\'\13)\f+\r-\16/\17\61\20" +
            "\63\21\65\22\67\239\24;\25=\26?\27A\30C\31E\32G\33I\34K\35M\36O\37Q S" +
            "!U\"W#Y$[%]&_\'a(c)e*g+\3\2\t\3\2\63;\3\2\62;\3\2c|\3\2C\\\5\2\13\f\17" +
            "\17\"\"\3\2\2\u0081\4\2\f\f\17\17\2\u0142\2\27\3\2\2\2\2\31\3\2\2\2\2" +
            "\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2" +
            "\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2" +
            "\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2" +
            "\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2" +
            "K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3" +
            "\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2" +
            "\2\2e\3\2\2\2\2g\3\2\2\2\3i\3\2\2\2\5k\3\2\2\2\7m\3\2\2\2\to\3\2\2\2\13" +
            "s\3\2\2\2\rw\3\2\2\2\17y\3\2\2\2\21{\3\2\2\2\23}\3\2\2\2\25\177\3\2\2" +
            "\2\27\u0082\3\2\2\2\31\u0088\3\2\2\2\33\u0096\3\2\2\2\35\u00a1\3\2\2\2" +
            "\37\u00a7\3\2\2\2!\u00ae\3\2\2\2#\u00b5\3\2\2\2%\u00ba\3\2\2\2\'\u00c2" +
            "\3\2\2\2)\u00c9\3\2\2\2+\u00cc\3\2\2\2-\u00d1\3\2\2\2/\u00d7\3\2\2\2\61" +
            "\u00ea\3\2\2\2\63\u00ec\3\2\2\2\65\u00ee\3\2\2\2\67\u00f0\3\2\2\29\u00f2" +
            "\3\2\2\2;\u00f4\3\2\2\2=\u00f6\3\2\2\2?\u00f8\3\2\2\2A\u00fb\3\2\2\2C" +
            "\u00fe\3\2\2\2E\u0101\3\2\2\2G\u0104\3\2\2\2I\u0106\3\2\2\2K\u0109\3\2" +
            "\2\2M\u010b\3\2\2\2O\u010e\3\2\2\2Q\u0110\3\2\2\2S\u0112\3\2\2\2U\u0114" +
            "\3\2\2\2W\u0116\3\2\2\2Y\u0118\3\2\2\2[\u011a\3\2\2\2]\u011c\3\2\2\2_" +
            "\u011e\3\2\2\2a\u0121\3\2\2\2c\u0128\3\2\2\2e\u013a\3\2\2\2g\u013c\3\2" +
            "\2\2ij\t\2\2\2j\4\3\2\2\2kl\t\3\2\2l\6\3\2\2\2mn\t\4\2\2n\b\3\2\2\2op" +
            "\t\5\2\2p\n\3\2\2\2qt\5\7\4\2rt\5\t\5\2sq\3\2\2\2sr\3\2\2\2t\f\3\2\2\2" +
            "ux\5\13\6\2vx\5\5\3\2wu\3\2\2\2wv\3\2\2\2x\16\3\2\2\2yz\t\6\2\2z\20\3" +
            "\2\2\2{|\t\7\2\2|\22\3\2\2\2}~\n\b\2\2~\24\3\2\2\2\177\u0080\n\6\2\2\u0080" +
            "\26\3\2\2\2\u0081\u0083\5\17\b\2\u0082\u0081\3\2\2\2\u0083\u0084\3\2\2" +
            "\2\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087" +
            "\b\f\2\2\u0087\30\3\2\2\2\u0088\u0089\7\61\2\2\u0089\u008a\7,\2\2\u008a" +
            "\u008e\3\2\2\2\u008b\u008d\5\21\t\2\u008c\u008b\3\2\2\2\u008d\u0090\3" +
            "\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\3\2\2\2\u0090" +
            "\u008e\3\2\2\2\u0091\u0092\7,\2\2\u0092\u0093\7\61\2\2\u0093\u0094\3\2" +
            "\2\2\u0094\u0095\b\r\2\2\u0095\32\3\2\2\2\u0096\u0097\7\61\2\2\u0097\u0098" +
            "\7\61\2\2\u0098\u009c\3\2\2\2\u0099\u009b\5\23\n\2\u009a\u0099\3\2\2\2" +
            "\u009b\u009e\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009f" +
            "\3\2\2\2\u009e\u009c\3\2\2\2\u009f\u00a0\b\16\2\2\u00a0\34\3\2\2\2\u00a1" +
            "\u00a2\7e\2\2\u00a2\u00a3\7n\2\2\u00a3\u00a4\7c\2\2\u00a4\u00a5\7u\2\2" +
            "\u00a5\u00a6\7u\2\2\u00a6\36\3\2\2\2\u00a7\u00a8\7r\2\2\u00a8\u00a9\7" +
            "w\2\2\u00a9\u00aa\7d\2\2\u00aa\u00ab\7n\2\2\u00ab\u00ac\7k\2\2\u00ac\u00ad" +
            "\7e\2\2\u00ad \3\2\2\2\u00ae\u00af\7u\2\2\u00af\u00b0\7v\2\2\u00b0\u00b1" +
            "\7c\2\2\u00b1\u00b2\7v\2\2\u00b2\u00b3\7k\2\2\u00b3\u00b4\7e\2\2\u00b4" +
            "\"\3\2\2\2\u00b5\u00b6\7x\2\2\u00b6\u00b7\7q\2\2\u00b7\u00b8\7k\2\2\u00b8" +
            "\u00b9\7f\2\2\u00b9$\3\2\2\2\u00ba\u00bb\7d\2\2\u00bb\u00bc\7q\2\2\u00bc" +
            "\u00bd\7q\2\2\u00bd\u00be\7n\2\2\u00be\u00bf\7g\2\2\u00bf\u00c0\7c\2\2" +
            "\u00c0\u00c1\7p\2\2\u00c1&\3\2\2\2\u00c2\u00c3\7U\2\2\u00c3\u00c4\7v\2" +
            "\2\u00c4\u00c5\7t\2\2\u00c5\u00c6\7k\2\2\u00c6\u00c7\7p\2\2\u00c7\u00c8" +
            "\7i\2\2\u00c8(\3\2\2\2\u00c9\u00ca\7k\2\2\u00ca\u00cb\7h\2\2\u00cb*\3" +
            "\2\2\2\u00cc\u00cd\7g\2\2\u00cd\u00ce\7n\2\2\u00ce\u00cf\7u\2\2\u00cf" +
            "\u00d0\7g\2\2\u00d0,\3\2\2\2\u00d1\u00d2\7y\2\2\u00d2\u00d3\7j\2\2\u00d3" +
            "\u00d4\7k\2\2\u00d4\u00d5\7n\2\2\u00d5\u00d6\7g\2\2\u00d6.\3\2\2\2\u00d7" +
            "\u00d8\7U\2\2\u00d8\u00d9\7{\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7v\2\2" +
            "\u00db\u00dc\7g\2\2\u00dc\u00dd\7o\2\2\u00dd\u00de\7\60\2\2\u00de\u00df" +
            "\7q\2\2\u00df\u00e0\7w\2\2\u00e0\u00e1\7v\2\2\u00e1\u00e2\7\60\2\2\u00e2" +
            "\u00e3\7r\2\2\u00e3\u00e4\7t\2\2\u00e4\u00e5\7k\2\2\u00e5\u00e6\7p\2\2" +
            "\u00e6\u00e7\7v\2\2\u00e7\u00e8\7n\2\2\u00e8\u00e9\7p\2\2\u00e9\60\3\2" +
            "\2\2\u00ea\u00eb\7?\2\2\u00eb\62\3\2\2\2\u00ec\u00ed\7-\2\2\u00ed\64\3" +
            "\2\2\2\u00ee\u00ef\7/\2\2\u00ef\66\3\2\2\2\u00f0\u00f1\7,\2\2\u00f18\3" +
            "\2\2\2\u00f2\u00f3\7\61\2\2\u00f3:\3\2\2\2\u00f4\u00f5\7\'\2\2\u00f5<" +
            "\3\2\2\2\u00f6\u00f7\7#\2\2\u00f7>\3\2\2\2\u00f8\u00f9\7(\2\2\u00f9\u00fa" +
            "\7(\2\2\u00fa@\3\2\2\2\u00fb\u00fc\7~\2\2\u00fc\u00fd\7~\2\2\u00fdB\3" +
            "\2\2\2\u00fe\u00ff\7?\2\2\u00ff\u0100\7?\2\2\u0100D\3\2\2\2\u0101\u0102" +
            "\7#\2\2\u0102\u0103\7?\2\2\u0103F\3\2\2\2\u0104\u0105\7>\2\2\u0105H\3" +
            "\2\2\2\u0106\u0107\7>\2\2\u0107\u0108\7?\2\2\u0108J\3\2\2\2\u0109\u010a" +
            "\7@\2\2\u010aL\3\2\2\2\u010b\u010c\7@\2\2\u010c\u010d\7?\2\2\u010dN\3" +
            "\2\2\2\u010e\u010f\7*\2\2\u010fP\3\2\2\2\u0110\u0111\7+\2\2\u0111R\3\2" +
            "\2\2\u0112\u0113\7}\2\2\u0113T\3\2\2\2\u0114\u0115\7\177\2\2\u0115V\3" +
            "\2\2\2\u0116\u0117\7]\2\2\u0117X\3\2\2\2\u0118\u0119\7_\2\2\u0119Z\3\2" +
            "\2\2\u011a\u011b\7=\2\2\u011b\\\3\2\2\2\u011c\u011d\7.\2\2\u011d^\3\2" +
            "\2\2\u011e\u011f\7\60\2\2\u011f`\3\2\2\2\u0120\u0122\7/\2\2\u0121\u0120" +
            "\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0124\3\2\2\2\u0123\u0125\5\5\3\2\u0124" +
            "\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2" +
            "\2\2\u0127b\3\2\2\2\u0128\u012c\7$\2\2\u0129\u012b\5\23\n\2\u012a\u0129" +
            "\3\2\2\2\u012b\u012e\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d" +
            "\u012f\3\2\2\2\u012e\u012c\3\2\2\2\u012f\u0130\7$\2\2\u0130d\3\2\2\2\u0131" +
            "\u0132\7v\2\2\u0132\u0133\7t\2\2\u0133\u0134\7w\2\2\u0134\u013b\7g\2\2" +
            "\u0135\u0136\7h\2\2\u0136\u0137\7c\2\2\u0137\u0138\7n\2\2\u0138\u0139" +
            "\7u\2\2\u0139\u013b\7g\2\2\u013a\u0131\3\2\2\2\u013a\u0135\3\2\2\2\u013b" +
            "f\3\2\2\2\u013c\u0140\5\13\6\2\u013d\u013f\5\r\7\2\u013e\u013d\3\2\2\2" +
            "\u013f\u0142\3\2\2\2\u0140\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141h\3" +
            "\2\2\2\u0142\u0140\3\2\2\2\r\2sw\u0084\u008e\u009c\u0121\u0126\u012c\u013a" +
            "\u0140\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };
    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    static {
        RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION);
    }

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public StupsLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "FIRST_DIGIT", "DIGIT", "LOWERCASE", "UPPERCASE", "LETTER", "LETTER_DIGIT",
                "WHITE", "ANY", "ANY_NOBREAK", "ANY_NOWHITE", "WHITESPACE", "MULTILINE_COMMENT",
                "LINE_COMMENT", "CLASS", "PUBLIC", "STATIC", "VOID_TYPE", "BOOLEAN_TYPE",
                "STRING_TYPE", "IF", "ELSE", "WHILE", "PRINTLN", "ASSIGN", "ADD", "SUB",
                "MUL", "DIV", "MOD", "NOT", "AND", "OR", "EQUAL", "NOT_EQUAL", "LESS",
                "LESS_EQUAL", "GREATER", "GREATER_EQUAL", "L_PAREN", "R_PAREN", "L_BRACE",
                "R_BRACE", "L_BRACKET", "R_BRACKET", "SEMICOLON", "COMMA", "DOT", "INTEGER_LIT",
                "STRING_LIT", "BOOLEAN_LIT", "IDENTIFIER"
        };
    }

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, null, "'class'", "'public'", "'static'", "'void'",
                "'boolean'", "'String'", "'if'", "'else'", "'while'", "'System.out.println'",
                "'='", "'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'&&'", "'||'", "'=='",
                "'!='", "'<'", "'<='", "'>'", "'>='", "'('", "')'", "'{'", "'}'", "'['",
                "']'", "';'", "','", "'.'"
        };
    }

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "WHITESPACE", "MULTILINE_COMMENT", "LINE_COMMENT", "CLASS", "PUBLIC",
                "STATIC", "VOID_TYPE", "BOOLEAN_TYPE", "STRING_TYPE", "IF", "ELSE", "WHILE",
                "PRINTLN", "ASSIGN", "ADD", "SUB", "MUL", "DIV", "MOD", "NOT", "AND",
                "OR", "EQUAL", "NOT_EQUAL", "LESS", "LESS_EQUAL", "GREATER", "GREATER_EQUAL",
                "L_PAREN", "R_PAREN", "L_BRACE", "R_BRACE", "L_BRACKET", "R_BRACKET",
                "SEMICOLON", "COMMA", "DOT", "INTEGER_LIT", "STRING_LIT", "BOOLEAN_LIT",
                "IDENTIFIER"
        };
    }

    @Override
    public String[] getRuleNames() { return ruleNames; }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getSerializedATN() { return _serializedATN; }

    @Override
    public String getGrammarFileName() { return "StupsLexer.g4"; }

    @Override
    public ATN getATN() { return _ATN; }

    @Override
    public String[] getChannelNames() { return channelNames; }

    @Override
    public String[] getModeNames() { return modeNames; }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }
}
