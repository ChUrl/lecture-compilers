// Generated from StupsLexer.g4 by ANTLR 4.8

package lexer;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StupsLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WHITESPACE=1, MULTILINE_COMMENT=2, LINE_COMMENT=3, CLASS=4, PUBLIC=5, 
		STATIC=6, VOID_TYPE=7, BOOLEAN_TYPE=8, INTEGER_TYPE=9, STRING_TYPE=10, 
		IF=11, ELSE=12, WHILE=13, PRINTLN=14, ADD=15, SUB=16, MUL=17, DIV=18, 
		MOD=19, NOT=20, AND=21, OR=22, EQUAL=23, NOT_EQUAL=24, LESS=25, LESS_EQUAL=26, 
		GREATER=27, GREATER_EQUAL=28, ASSIGN=29, L_PAREN=30, R_PAREN=31, L_BRACE=32, 
		R_BRACE=33, L_BRACKET=34, R_BRACKET=35, SEMICOLON=36, COMMA=37, DOT=38, 
		INTEGER_LIT=39, STRING_LIT=40, BOOLEAN_LIT=41, IDENTIFIER_MAIN=42, IDENTIFIER=43;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"FIRST_DIGIT", "DIGIT", "LOWERCASE", "UPPERCASE", "LETTER", "LETTER_DIGIT", 
			"WHITE", "ANY", "ANY_NOBREAK", "ANY_NOWHITE", "WHITESPACE", "MULTILINE_COMMENT", 
			"LINE_COMMENT", "CLASS", "PUBLIC", "STATIC", "VOID_TYPE", "BOOLEAN_TYPE", 
			"INTEGER_TYPE", "STRING_TYPE", "IF", "ELSE", "WHILE", "PRINTLN", "ADD", 
			"SUB", "MUL", "DIV", "MOD", "NOT", "AND", "OR", "EQUAL", "NOT_EQUAL", 
			"LESS", "LESS_EQUAL", "GREATER", "GREATER_EQUAL", "ASSIGN", "L_PAREN", 
			"R_PAREN", "L_BRACE", "R_BRACE", "L_BRACKET", "R_BRACKET", "SEMICOLON", 
			"COMMA", "DOT", "INTEGER_LIT", "STRING_LIT", "BOOLEAN_LIT", "IDENTIFIER_MAIN", 
			"IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'class'", "'public'", "'static'", "'void'", 
			"'boolean'", "'int'", "'String'", "'if'", "'else'", "'while'", "'System.out.println'", 
			"'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'&&'", "'||'", "'=='", "'!='", 
			"'<'", "'<='", "'>'", "'>='", "'='", "'('", "')'", "'{'", "'}'", "'['", 
			"']'", "';'", "','", "'.'", null, null, null, "'main'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WHITESPACE", "MULTILINE_COMMENT", "LINE_COMMENT", "CLASS", "PUBLIC", 
			"STATIC", "VOID_TYPE", "BOOLEAN_TYPE", "INTEGER_TYPE", "STRING_TYPE", 
			"IF", "ELSE", "WHILE", "PRINTLN", "ADD", "SUB", "MUL", "DIV", "MOD", 
			"NOT", "AND", "OR", "EQUAL", "NOT_EQUAL", "LESS", "LESS_EQUAL", "GREATER", 
			"GREATER_EQUAL", "ASSIGN", "L_PAREN", "R_PAREN", "L_BRACE", "R_BRACE", 
			"L_BRACKET", "R_BRACKET", "SEMICOLON", "COMMA", "DOT", "INTEGER_LIT", 
			"STRING_LIT", "BOOLEAN_LIT", "IDENTIFIER_MAIN", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
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

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public StupsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "StupsLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2-\u014d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\5\6x\n"+
		"\6\3\7\3\7\5\7|\n\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\6\f\u0087\n"+
		"\f\r\f\16\f\u0088\3\f\3\f\3\r\3\r\3\r\3\r\7\r\u0091\n\r\f\r\16\r\u0094"+
		"\13\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\7\16\u009f\n\16\f\16\16"+
		"\16\u00a2\13\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27"+
		"\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32"+
		"\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3 \3!\3!\3!\3"+
		"\"\3\"\3\"\3#\3#\3#\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3*\3"+
		"*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\6\62\u012a\n"+
		"\62\r\62\16\62\u012b\3\63\3\63\7\63\u0130\n\63\f\63\16\63\u0133\13\63"+
		"\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64\5\64\u0140\n\64"+
		"\3\65\3\65\3\65\3\65\3\65\3\66\3\66\7\66\u0149\n\66\f\66\16\66\u014c\13"+
		"\66\2\2\67\3\2\5\2\7\2\t\2\13\2\r\2\17\2\21\2\23\2\25\2\27\3\31\4\33\5"+
		"\35\6\37\7!\b#\t%\n\'\13)\f+\r-\16/\17\61\20\63\21\65\22\67\239\24;\25"+
		"=\26?\27A\30C\31E\32G\33I\34K\35M\36O\37Q S!U\"W#Y$[%]&_\'a(c)e*g+i,k"+
		"-\3\2\t\3\2\63;\3\2\62;\3\2c|\3\2C\\\5\2\13\f\17\17\"\"\3\2\2\u0081\4"+
		"\2\f\f\17\17\2\u014b\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2"+
		"\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2"+
		"\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2"+
		"O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3"+
		"\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2"+
		"\2\2i\3\2\2\2\2k\3\2\2\2\3m\3\2\2\2\5o\3\2\2\2\7q\3\2\2\2\ts\3\2\2\2\13"+
		"w\3\2\2\2\r{\3\2\2\2\17}\3\2\2\2\21\177\3\2\2\2\23\u0081\3\2\2\2\25\u0083"+
		"\3\2\2\2\27\u0086\3\2\2\2\31\u008c\3\2\2\2\33\u009a\3\2\2\2\35\u00a5\3"+
		"\2\2\2\37\u00ab\3\2\2\2!\u00b2\3\2\2\2#\u00b9\3\2\2\2%\u00be\3\2\2\2\'"+
		"\u00c6\3\2\2\2)\u00ca\3\2\2\2+\u00d1\3\2\2\2-\u00d4\3\2\2\2/\u00d9\3\2"+
		"\2\2\61\u00df\3\2\2\2\63\u00f2\3\2\2\2\65\u00f4\3\2\2\2\67\u00f6\3\2\2"+
		"\29\u00f8\3\2\2\2;\u00fa\3\2\2\2=\u00fc\3\2\2\2?\u00fe\3\2\2\2A\u0101"+
		"\3\2\2\2C\u0104\3\2\2\2E\u0107\3\2\2\2G\u010a\3\2\2\2I\u010c\3\2\2\2K"+
		"\u010f\3\2\2\2M\u0111\3\2\2\2O\u0114\3\2\2\2Q\u0116\3\2\2\2S\u0118\3\2"+
		"\2\2U\u011a\3\2\2\2W\u011c\3\2\2\2Y\u011e\3\2\2\2[\u0120\3\2\2\2]\u0122"+
		"\3\2\2\2_\u0124\3\2\2\2a\u0126\3\2\2\2c\u0129\3\2\2\2e\u012d\3\2\2\2g"+
		"\u013f\3\2\2\2i\u0141\3\2\2\2k\u0146\3\2\2\2mn\t\2\2\2n\4\3\2\2\2op\t"+
		"\3\2\2p\6\3\2\2\2qr\t\4\2\2r\b\3\2\2\2st\t\5\2\2t\n\3\2\2\2ux\5\7\4\2"+
		"vx\5\t\5\2wu\3\2\2\2wv\3\2\2\2x\f\3\2\2\2y|\5\13\6\2z|\5\5\3\2{y\3\2\2"+
		"\2{z\3\2\2\2|\16\3\2\2\2}~\t\6\2\2~\20\3\2\2\2\177\u0080\t\7\2\2\u0080"+
		"\22\3\2\2\2\u0081\u0082\n\b\2\2\u0082\24\3\2\2\2\u0083\u0084\n\6\2\2\u0084"+
		"\26\3\2\2\2\u0085\u0087\5\17\b\2\u0086\u0085\3\2\2\2\u0087\u0088\3\2\2"+
		"\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b"+
		"\b\f\2\2\u008b\30\3\2\2\2\u008c\u008d\7\61\2\2\u008d\u008e\7,\2\2\u008e"+
		"\u0092\3\2\2\2\u008f\u0091\5\21\t\2\u0090\u008f\3\2\2\2\u0091\u0094\3"+
		"\2\2\2\u0092\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0095\3\2\2\2\u0094"+
		"\u0092\3\2\2\2\u0095\u0096\7,\2\2\u0096\u0097\7\61\2\2\u0097\u0098\3\2"+
		"\2\2\u0098\u0099\b\r\2\2\u0099\32\3\2\2\2\u009a\u009b\7\61\2\2\u009b\u009c"+
		"\7\61\2\2\u009c\u00a0\3\2\2\2\u009d\u009f\5\23\n\2\u009e\u009d\3\2\2\2"+
		"\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a3"+
		"\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a3\u00a4\b\16\2\2\u00a4\34\3\2\2\2\u00a5"+
		"\u00a6\7e\2\2\u00a6\u00a7\7n\2\2\u00a7\u00a8\7c\2\2\u00a8\u00a9\7u\2\2"+
		"\u00a9\u00aa\7u\2\2\u00aa\36\3\2\2\2\u00ab\u00ac\7r\2\2\u00ac\u00ad\7"+
		"w\2\2\u00ad\u00ae\7d\2\2\u00ae\u00af\7n\2\2\u00af\u00b0\7k\2\2\u00b0\u00b1"+
		"\7e\2\2\u00b1 \3\2\2\2\u00b2\u00b3\7u\2\2\u00b3\u00b4\7v\2\2\u00b4\u00b5"+
		"\7c\2\2\u00b5\u00b6\7v\2\2\u00b6\u00b7\7k\2\2\u00b7\u00b8\7e\2\2\u00b8"+
		"\"\3\2\2\2\u00b9\u00ba\7x\2\2\u00ba\u00bb\7q\2\2\u00bb\u00bc\7k\2\2\u00bc"+
		"\u00bd\7f\2\2\u00bd$\3\2\2\2\u00be\u00bf\7d\2\2\u00bf\u00c0\7q\2\2\u00c0"+
		"\u00c1\7q\2\2\u00c1\u00c2\7n\2\2\u00c2\u00c3\7g\2\2\u00c3\u00c4\7c\2\2"+
		"\u00c4\u00c5\7p\2\2\u00c5&\3\2\2\2\u00c6\u00c7\7k\2\2\u00c7\u00c8\7p\2"+
		"\2\u00c8\u00c9\7v\2\2\u00c9(\3\2\2\2\u00ca\u00cb\7U\2\2\u00cb\u00cc\7"+
		"v\2\2\u00cc\u00cd\7t\2\2\u00cd\u00ce\7k\2\2\u00ce\u00cf\7p\2\2\u00cf\u00d0"+
		"\7i\2\2\u00d0*\3\2\2\2\u00d1\u00d2\7k\2\2\u00d2\u00d3\7h\2\2\u00d3,\3"+
		"\2\2\2\u00d4\u00d5\7g\2\2\u00d5\u00d6\7n\2\2\u00d6\u00d7\7u\2\2\u00d7"+
		"\u00d8\7g\2\2\u00d8.\3\2\2\2\u00d9\u00da\7y\2\2\u00da\u00db\7j\2\2\u00db"+
		"\u00dc\7k\2\2\u00dc\u00dd\7n\2\2\u00dd\u00de\7g\2\2\u00de\60\3\2\2\2\u00df"+
		"\u00e0\7U\2\2\u00e0\u00e1\7{\2\2\u00e1\u00e2\7u\2\2\u00e2\u00e3\7v\2\2"+
		"\u00e3\u00e4\7g\2\2\u00e4\u00e5\7o\2\2\u00e5\u00e6\7\60\2\2\u00e6\u00e7"+
		"\7q\2\2\u00e7\u00e8\7w\2\2\u00e8\u00e9\7v\2\2\u00e9\u00ea\7\60\2\2\u00ea"+
		"\u00eb\7r\2\2\u00eb\u00ec\7t\2\2\u00ec\u00ed\7k\2\2\u00ed\u00ee\7p\2\2"+
		"\u00ee\u00ef\7v\2\2\u00ef\u00f0\7n\2\2\u00f0\u00f1\7p\2\2\u00f1\62\3\2"+
		"\2\2\u00f2\u00f3\7-\2\2\u00f3\64\3\2\2\2\u00f4\u00f5\7/\2\2\u00f5\66\3"+
		"\2\2\2\u00f6\u00f7\7,\2\2\u00f78\3\2\2\2\u00f8\u00f9\7\61\2\2\u00f9:\3"+
		"\2\2\2\u00fa\u00fb\7\'\2\2\u00fb<\3\2\2\2\u00fc\u00fd\7#\2\2\u00fd>\3"+
		"\2\2\2\u00fe\u00ff\7(\2\2\u00ff\u0100\7(\2\2\u0100@\3\2\2\2\u0101\u0102"+
		"\7~\2\2\u0102\u0103\7~\2\2\u0103B\3\2\2\2\u0104\u0105\7?\2\2\u0105\u0106"+
		"\7?\2\2\u0106D\3\2\2\2\u0107\u0108\7#\2\2\u0108\u0109\7?\2\2\u0109F\3"+
		"\2\2\2\u010a\u010b\7>\2\2\u010bH\3\2\2\2\u010c\u010d\7>\2\2\u010d\u010e"+
		"\7?\2\2\u010eJ\3\2\2\2\u010f\u0110\7@\2\2\u0110L\3\2\2\2\u0111\u0112\7"+
		"@\2\2\u0112\u0113\7?\2\2\u0113N\3\2\2\2\u0114\u0115\7?\2\2\u0115P\3\2"+
		"\2\2\u0116\u0117\7*\2\2\u0117R\3\2\2\2\u0118\u0119\7+\2\2\u0119T\3\2\2"+
		"\2\u011a\u011b\7}\2\2\u011bV\3\2\2\2\u011c\u011d\7\177\2\2\u011dX\3\2"+
		"\2\2\u011e\u011f\7]\2\2\u011fZ\3\2\2\2\u0120\u0121\7_\2\2\u0121\\\3\2"+
		"\2\2\u0122\u0123\7=\2\2\u0123^\3\2\2\2\u0124\u0125\7.\2\2\u0125`\3\2\2"+
		"\2\u0126\u0127\7\60\2\2\u0127b\3\2\2\2\u0128\u012a\5\5\3\2\u0129\u0128"+
		"\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c"+
		"d\3\2\2\2\u012d\u0131\7$\2\2\u012e\u0130\5\23\n\2\u012f\u012e\3\2\2\2"+
		"\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0134"+
		"\3\2\2\2\u0133\u0131\3\2\2\2\u0134\u0135\7$\2\2\u0135f\3\2\2\2\u0136\u0137"+
		"\7v\2\2\u0137\u0138\7t\2\2\u0138\u0139\7w\2\2\u0139\u0140\7g\2\2\u013a"+
		"\u013b\7h\2\2\u013b\u013c\7c\2\2\u013c\u013d\7n\2\2\u013d\u013e\7u\2\2"+
		"\u013e\u0140\7g\2\2\u013f\u0136\3\2\2\2\u013f\u013a\3\2\2\2\u0140h\3\2"+
		"\2\2\u0141\u0142\7o\2\2\u0142\u0143\7c\2\2\u0143\u0144\7k\2\2\u0144\u0145"+
		"\7p\2\2\u0145j\3\2\2\2\u0146\u014a\5\13\6\2\u0147\u0149\5\r\7\2\u0148"+
		"\u0147\3\2\2\2\u0149\u014c\3\2\2\2\u014a\u0148\3\2\2\2\u014a\u014b\3\2"+
		"\2\2\u014bl\3\2\2\2\u014c\u014a\3\2\2\2\f\2w{\u0088\u0092\u00a0\u012b"+
		"\u0131\u013f\u014a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}