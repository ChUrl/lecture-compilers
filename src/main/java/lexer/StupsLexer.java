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

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    public static final int
            WHITESPACE = 1, MULTILINE_COMMENT = 2, LINE_COMMENT = 3, CLASS = 4, PUBLIC = 5,
            STATIC = 6, VOID_TYPE = 7, BOOLEAN_TYPE = 8, STRING_TYPE = 9, IF = 10, ELSE = 11,
            WHILE = 12, PRINTLN = 13, ADD = 14, SUB = 15, MUL = 16, DIV = 17, MOD = 18, NOT = 19,
            AND = 20, OR = 21, EQUAL = 22, NOT_EQUAL = 23, LESS = 24, LESS_EQUAL = 25, GREATER = 26,
            GREATER_EQUAL = 27, ASSIGN = 28, L_PAREN = 29, R_PAREN = 30, L_BRACE = 31, R_BRACE = 32,
            L_BRACKET = 33, R_BRACKET = 34, SEMICOLON = 35, COMMA = 36, DOT = 37, INTEGER_LIT = 38,
            STRING_LIT = 39, BOOLEAN_LIT = 40, IDENTIFIER_MAIN = 41, IDENTIFIER = 42;
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static final String[] ruleNames = makeRuleNames();
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

    public StupsLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "FIRST_DIGIT", "DIGIT", "LOWERCASE", "UPPERCASE", "LETTER", "LETTER_DIGIT",
                "WHITE", "ANY", "ANY_NOBREAK", "ANY_NOWHITE", "WHITESPACE", "MULTILINE_COMMENT",
                "LINE_COMMENT", "CLASS", "PUBLIC", "STATIC", "VOID_TYPE", "BOOLEAN_TYPE",
                "STRING_TYPE", "IF", "ELSE", "WHILE", "PRINTLN", "ADD", "SUB", "MUL",
                "DIV", "MOD", "NOT", "AND", "OR", "EQUAL", "NOT_EQUAL", "LESS", "LESS_EQUAL",
                "GREATER", "GREATER_EQUAL", "ASSIGN", "L_PAREN", "R_PAREN", "L_BRACE",
                "R_BRACE", "L_BRACKET", "R_BRACKET", "SEMICOLON", "COMMA", "DOT", "INTEGER_LIT",
                "STRING_LIT", "BOOLEAN_LIT", "IDENTIFIER_MAIN", "IDENTIFIER"
        };
    }

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, null, "'class'", "'public'", "'static'", "'void'",
                "'boolean'", "'String'", "'if'", "'else'", "'while'", "'System.out.println'",
                "'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'&&'", "'||'", "'=='", "'!='",
                "'<'", "'<='", "'>'", "'>='", "'='", "'('", "')'", "'{'", "'}'", "'['",
                "']'", "';'", "','", "'.'", null, null, null, "'main'"
        };
    }

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "WHITESPACE", "MULTILINE_COMMENT", "LINE_COMMENT", "CLASS", "PUBLIC",
                "STATIC", "VOID_TYPE", "BOOLEAN_TYPE", "STRING_TYPE", "IF", "ELSE", "WHILE",
                "PRINTLN", "ADD", "SUB", "MUL", "DIV", "MOD", "NOT", "AND", "OR", "EQUAL",
                "NOT_EQUAL", "LESS", "LESS_EQUAL", "GREATER", "GREATER_EQUAL", "ASSIGN",
                "L_PAREN", "R_PAREN", "L_BRACE", "R_BRACE", "L_BRACKET", "R_BRACKET",
                "SEMICOLON", "COMMA", "DOT", "INTEGER_LIT", "STRING_LIT", "BOOLEAN_LIT",
                "IDENTIFIER_MAIN", "IDENTIFIER"
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

    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2,\u014a\b\1\4\2\t" +
            "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
            "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
            "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
            "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
            "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
            ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
            "\64\4\65\t\65\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\5\6v\n\6\3\7\3\7" +
            "\5\7z\n\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\6\f\u0085\n\f\r\f\16\f" +
            "\u0086\3\f\3\f\3\r\3\r\3\r\3\r\7\r\u008f\n\r\f\r\16\r\u0092\13\r\3\r\3" +
            "\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\7\16\u009d\n\16\f\16\16\16\u00a0\13" +
            "\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3" +
            "\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3" +
            "\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3" +
            "\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3" +
            "\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3" +
            "\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3" +
            "\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\3#" +
            "\3$\3$\3$\3%\3%\3&\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3" +
            ".\3.\3/\3/\3\60\3\60\3\61\5\61\u0124\n\61\3\61\6\61\u0127\n\61\r\61\16" +
            "\61\u0128\3\62\3\62\7\62\u012d\n\62\f\62\16\62\u0130\13\62\3\62\3\62\3" +
            "\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u013d\n\63\3\64\3\64" +
            "\3\64\3\64\3\64\3\65\3\65\7\65\u0146\n\65\f\65\16\65\u0149\13\65\2\2\66" +
            "\3\2\5\2\7\2\t\2\13\2\r\2\17\2\21\2\23\2\25\2\27\3\31\4\33\5\35\6\37\7" +
            "!\b#\t%\n\'\13)\f+\r-\16/\17\61\20\63\21\65\22\67\239\24;\25=\26?\27A" +
            "\30C\31E\32G\33I\34K\35M\36O\37Q S!U\"W#Y$[%]&_\'a(c)e*g+i,\3\2\t\3\2" +
            "\63;\3\2\62;\3\2c|\3\2C\\\5\2\13\f\17\17\"\"\3\2\2\u0081\4\2\f\f\17\17" +
            "\2\u0149\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2" +
            "\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2" +
            "\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3" +
            "\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2" +
            "\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2" +
            "Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3" +
            "\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2" +
            "\2\3k\3\2\2\2\5m\3\2\2\2\7o\3\2\2\2\tq\3\2\2\2\13u\3\2\2\2\ry\3\2\2\2" +
            "\17{\3\2\2\2\21}\3\2\2\2\23\177\3\2\2\2\25\u0081\3\2\2\2\27\u0084\3\2" +
            "\2\2\31\u008a\3\2\2\2\33\u0098\3\2\2\2\35\u00a3\3\2\2\2\37\u00a9\3\2\2" +
            "\2!\u00b0\3\2\2\2#\u00b7\3\2\2\2%\u00bc\3\2\2\2\'\u00c4\3\2\2\2)\u00cb" +
            "\3\2\2\2+\u00ce\3\2\2\2-\u00d3\3\2\2\2/\u00d9\3\2\2\2\61\u00ec\3\2\2\2" +
            "\63\u00ee\3\2\2\2\65\u00f0\3\2\2\2\67\u00f2\3\2\2\29\u00f4\3\2\2\2;\u00f6" +
            "\3\2\2\2=\u00f8\3\2\2\2?\u00fb\3\2\2\2A\u00fe\3\2\2\2C\u0101\3\2\2\2E" +
            "\u0104\3\2\2\2G\u0106\3\2\2\2I\u0109\3\2\2\2K\u010b\3\2\2\2M\u010e\3\2" +
            "\2\2O\u0110\3\2\2\2Q\u0112\3\2\2\2S\u0114\3\2\2\2U\u0116\3\2\2\2W\u0118" +
            "\3\2\2\2Y\u011a\3\2\2\2[\u011c\3\2\2\2]\u011e\3\2\2\2_\u0120\3\2\2\2a" +
            "\u0123\3\2\2\2c\u012a\3\2\2\2e\u013c\3\2\2\2g\u013e\3\2\2\2i\u0143\3\2" +
            "\2\2kl\t\2\2\2l\4\3\2\2\2mn\t\3\2\2n\6\3\2\2\2op\t\4\2\2p\b\3\2\2\2qr" +
            "\t\5\2\2r\n\3\2\2\2sv\5\7\4\2tv\5\t\5\2us\3\2\2\2ut\3\2\2\2v\f\3\2\2\2" +
            "wz\5\13\6\2xz\5\5\3\2yw\3\2\2\2yx\3\2\2\2z\16\3\2\2\2{|\t\6\2\2|\20\3" +
            "\2\2\2}~\t\7\2\2~\22\3\2\2\2\177\u0080\n\b\2\2\u0080\24\3\2\2\2\u0081" +
            "\u0082\n\6\2\2\u0082\26\3\2\2\2\u0083\u0085\5\17\b\2\u0084\u0083\3\2\2" +
            "\2\u0085\u0086\3\2\2\2\u0086\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088" +
            "\3\2\2\2\u0088\u0089\b\f\2\2\u0089\30\3\2\2\2\u008a\u008b\7\61\2\2\u008b" +
            "\u008c\7,\2\2\u008c\u0090\3\2\2\2\u008d\u008f\5\21\t\2\u008e\u008d\3\2" +
            "\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091" +
            "\u0093\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0094\7,\2\2\u0094\u0095\7\61" +
            "\2\2\u0095\u0096\3\2\2\2\u0096\u0097\b\r\2\2\u0097\32\3\2\2\2\u0098\u0099" +
            "\7\61\2\2\u0099\u009a\7\61\2\2\u009a\u009e\3\2\2\2\u009b\u009d\5\23\n" +
            "\2\u009c\u009b\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f" +
            "\3\2\2\2\u009f\u00a1\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a2\b\16\2\2" +
            "\u00a2\34\3\2\2\2\u00a3\u00a4\7e\2\2\u00a4\u00a5\7n\2\2\u00a5\u00a6\7" +
            "c\2\2\u00a6\u00a7\7u\2\2\u00a7\u00a8\7u\2\2\u00a8\36\3\2\2\2\u00a9\u00aa" +
            "\7r\2\2\u00aa\u00ab\7w\2\2\u00ab\u00ac\7d\2\2\u00ac\u00ad\7n\2\2\u00ad" +
            "\u00ae\7k\2\2\u00ae\u00af\7e\2\2\u00af \3\2\2\2\u00b0\u00b1\7u\2\2\u00b1" +
            "\u00b2\7v\2\2\u00b2\u00b3\7c\2\2\u00b3\u00b4\7v\2\2\u00b4\u00b5\7k\2\2" +
            "\u00b5\u00b6\7e\2\2\u00b6\"\3\2\2\2\u00b7\u00b8\7x\2\2\u00b8\u00b9\7q" +
            "\2\2\u00b9\u00ba\7k\2\2\u00ba\u00bb\7f\2\2\u00bb$\3\2\2\2\u00bc\u00bd" +
            "\7d\2\2\u00bd\u00be\7q\2\2\u00be\u00bf\7q\2\2\u00bf\u00c0\7n\2\2\u00c0" +
            "\u00c1\7g\2\2\u00c1\u00c2\7c\2\2\u00c2\u00c3\7p\2\2\u00c3&\3\2\2\2\u00c4" +
            "\u00c5\7U\2\2\u00c5\u00c6\7v\2\2\u00c6\u00c7\7t\2\2\u00c7\u00c8\7k\2\2" +
            "\u00c8\u00c9\7p\2\2\u00c9\u00ca\7i\2\2\u00ca(\3\2\2\2\u00cb\u00cc\7k\2" +
            "\2\u00cc\u00cd\7h\2\2\u00cd*\3\2\2\2\u00ce\u00cf\7g\2\2\u00cf\u00d0\7" +
            "n\2\2\u00d0\u00d1\7u\2\2\u00d1\u00d2\7g\2\2\u00d2,\3\2\2\2\u00d3\u00d4" +
            "\7y\2\2\u00d4\u00d5\7j\2\2\u00d5\u00d6\7k\2\2\u00d6\u00d7\7n\2\2\u00d7" +
            "\u00d8\7g\2\2\u00d8.\3\2\2\2\u00d9\u00da\7U\2\2\u00da\u00db\7{\2\2\u00db" +
            "\u00dc\7u\2\2\u00dc\u00dd\7v\2\2\u00dd\u00de\7g\2\2\u00de\u00df\7o\2\2" +
            "\u00df\u00e0\7\60\2\2\u00e0\u00e1\7q\2\2\u00e1\u00e2\7w\2\2\u00e2\u00e3" +
            "\7v\2\2\u00e3\u00e4\7\60\2\2\u00e4\u00e5\7r\2\2\u00e5\u00e6\7t\2\2\u00e6" +
            "\u00e7\7k\2\2\u00e7\u00e8\7p\2\2\u00e8\u00e9\7v\2\2\u00e9\u00ea\7n\2\2" +
            "\u00ea\u00eb\7p\2\2\u00eb\60\3\2\2\2\u00ec\u00ed\7-\2\2\u00ed\62\3\2\2" +
            "\2\u00ee\u00ef\7/\2\2\u00ef\64\3\2\2\2\u00f0\u00f1\7,\2\2\u00f1\66\3\2" +
            "\2\2\u00f2\u00f3\7\61\2\2\u00f38\3\2\2\2\u00f4\u00f5\7\'\2\2\u00f5:\3" +
            "\2\2\2\u00f6\u00f7\7#\2\2\u00f7<\3\2\2\2\u00f8\u00f9\7(\2\2\u00f9\u00fa" +
            "\7(\2\2\u00fa>\3\2\2\2\u00fb\u00fc\7~\2\2\u00fc\u00fd\7~\2\2\u00fd@\3" +
            "\2\2\2\u00fe\u00ff\7?\2\2\u00ff\u0100\7?\2\2\u0100B\3\2\2\2\u0101\u0102" +
            "\7#\2\2\u0102\u0103\7?\2\2\u0103D\3\2\2\2\u0104\u0105\7>\2\2\u0105F\3" +
            "\2\2\2\u0106\u0107\7>\2\2\u0107\u0108\7?\2\2\u0108H\3\2\2\2\u0109\u010a" +
            "\7@\2\2\u010aJ\3\2\2\2\u010b\u010c\7@\2\2\u010c\u010d\7?\2\2\u010dL\3" +
            "\2\2\2\u010e\u010f\7?\2\2\u010fN\3\2\2\2\u0110\u0111\7*\2\2\u0111P\3\2" +
            "\2\2\u0112\u0113\7+\2\2\u0113R\3\2\2\2\u0114\u0115\7}\2\2\u0115T\3\2\2" +
            "\2\u0116\u0117\7\177\2\2\u0117V\3\2\2\2\u0118\u0119\7]\2\2\u0119X\3\2" +
            "\2\2\u011a\u011b\7_\2\2\u011bZ\3\2\2\2\u011c\u011d\7=\2\2\u011d\\\3\2" +
            "\2\2\u011e\u011f\7.\2\2\u011f^\3\2\2\2\u0120\u0121\7\60\2\2\u0121`\3\2" +
            "\2\2\u0122\u0124\7/\2\2\u0123\u0122\3\2\2\2\u0123\u0124\3\2\2\2\u0124" +
            "\u0126\3\2\2\2\u0125\u0127\5\5\3\2\u0126\u0125\3\2\2\2\u0127\u0128\3\2" +
            "\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129b\3\2\2\2\u012a\u012e" +
            "\7$\2\2\u012b\u012d\5\23\n\2\u012c\u012b\3\2\2\2\u012d\u0130\3\2\2\2\u012e" +
            "\u012c\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u0131\3\2\2\2\u0130\u012e\3\2" +
            "\2\2\u0131\u0132\7$\2\2\u0132d\3\2\2\2\u0133\u0134\7v\2\2\u0134\u0135" +
            "\7t\2\2\u0135\u0136\7w\2\2\u0136\u013d\7g\2\2\u0137\u0138\7h\2\2\u0138" +
            "\u0139\7c\2\2\u0139\u013a\7n\2\2\u013a\u013b\7u\2\2\u013b\u013d\7g\2\2" +
            "\u013c\u0133\3\2\2\2\u013c\u0137\3\2\2\2\u013df\3\2\2\2\u013e\u013f\7" +
            "o\2\2\u013f\u0140\7c\2\2\u0140\u0141\7k\2\2\u0141\u0142\7p\2\2\u0142h" +
            "\3\2\2\2\u0143\u0147\5\13\6\2\u0144\u0146\5\r\7\2\u0145\u0144\3\2\2\2" +
            "\u0146\u0149\3\2\2\2\u0147\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148j\3" +
            "\2\2\2\u0149\u0147\3\2\2\2\r\2uy\u0086\u0090\u009e\u0123\u0128\u012e\u013c" +
            "\u0147\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
