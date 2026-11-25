package stanhebben.zenscript;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import stanhebben.zenscript.parser.CompiledDFA;
import stanhebben.zenscript.parser.NFA;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.TokenStream;

public class ZenTokener extends TokenStream {
   public static final int T_ID = 1;
   public static final int T_INTVALUE = 2;
   public static final int T_FLOATVALUE = 3;
   public static final int T_STRINGVALUE = 4;
   public static final int T_AOPEN = 5;
   public static final int T_ACLOSE = 6;
   public static final int T_SQBROPEN = 7;
   public static final int T_SQBRCLOSE = 8;
   public static final int T_DOT2 = 9;
   public static final int T_DOT = 10;
   public static final int T_COMMA = 11;
   public static final int T_PLUSASSIGN = 12;
   public static final int T_PLUS = 13;
   public static final int T_MINUSASSIGN = 14;
   public static final int T_MINUS = 15;
   public static final int T_MULASSIGN = 16;
   public static final int T_MUL = 17;
   public static final int T_DIVASSIGN = 18;
   public static final int T_DIV = 19;
   public static final int T_MODASSIGN = 20;
   public static final int T_MOD = 21;
   public static final int T_ORASSIGN = 22;
   public static final int T_OR = 23;
   public static final int T_OR2 = 40;
   public static final int T_ANDASSIGN = 24;
   public static final int T_AND2 = 41;
   public static final int T_AND = 25;
   public static final int T_XORASSIGN = 26;
   public static final int T_XOR = 27;
   public static final int T_QUEST = 28;
   public static final int T_COLON = 29;
   public static final int T_BROPEN = 30;
   public static final int T_BRCLOSE = 31;
   public static final int T_TILDEASSIGN = 45;
   public static final int T_TILDE = 32;
   public static final int T_SEMICOLON = 33;
   public static final int T_LTEQ = 34;
   public static final int T_LT = 35;
   public static final int T_GTEQ = 36;
   public static final int T_GT = 37;
   public static final int T_EQ = 38;
   public static final int T_ASSIGN = 39;
   public static final int T_NOTEQ = 42;
   public static final int T_NOT = 43;
   public static final int T_DOLLAR = 44;
   public static final int T_ANY = 99;
   public static final int T_BOOL = 100;
   public static final int T_BYTE = 101;
   public static final int T_SHORT = 102;
   public static final int T_INT = 103;
   public static final int T_LONG = 104;
   public static final int T_FLOAT = 105;
   public static final int T_DOUBLE = 106;
   public static final int T_STRING = 107;
   public static final int T_FUNCTION = 108;
   public static final int T_IN = 109;
   public static final int T_VOID = 110;
   public static final int T_AS = 120;
   public static final int T_VERSION = 121;
   public static final int T_IF = 122;
   public static final int T_ELSE = 123;
   public static final int T_FOR = 124;
   public static final int T_RETURN = 125;
   public static final int T_VAR = 126;
   public static final int T_VAL = 127;
   public static final int T_WHILE = 128;
   public static final int T_BREAK = 129;
   public static final int T_CONTINUE = 130;
   public static final int T_NULL = 140;
   public static final int T_TRUE = 141;
   public static final int T_FALSE = 142;
   public static final int T_IMPORT = 160;
   public static final int T_GLOBAL = 666;
   public static final int T_STATIC = 667;
   public static final int T_INSTANCEOF = 668;
   public static final int T_ZEN_CLASS = 123456789;
   public static final int T_ZEN_CONSTRUCTOR = 987654321;
   private static final HashMap<String, Integer> KEYWORDS = new HashMap();
   private static final String[] REGEXPS = new String[]{"#[^\n]*[\n\\e]", "//[^\n]*[\n\\e]", "/\\*[^\\*]*(\\*|\\*[^/\\*][^\\*]*)*\\*/", "[ \t\r\n]*", "[a-zA-Z_][a-zA-Z_0-9]*", "\\-?(0|[1-9][0-9]*)\\.[0-9]+([eE][\\+\\-]?[0-9]+)?[fFdD]?", "\\-?(0|[1-9][0-9]*)", "0x[A-Fa-f0-9]*", "\"([^\"\\\\]|\\\\(['\"\\\\/bfnrt]|u[0-9a-fA-F]{4}))*\"", "'([^'\\\\]|\\\\(['\"\\\\/bfnrt]|u[0-9a-fA-F]{4}))*'", "\\{", "\\}", "\\[", "\\]", "\\.\\.", "\\.", ",", "\\+=", "\\+", "\\-=", "\\-", "\\*=", "\\*", "/=", "/", "%=", "%", "\\|=", "\\|\\|", "\\|", "&=", "&&", "&", "\\^=", "\\^", "\\?", ":", "\\(", "\\)", "~=", "~", ";", "<=", "<", ">=", ">", "==", "=", "!=", "!", "$"};
   private static final int[] FINALS = new int[]{-1, -1, -1, -1, 1, 3, 2, 2, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 40, 23, 24, 41, 25, 26, 27, 28, 29, 30, 31, 45, 32, 33, 34, 35, 36, 37, 38, 39, 42, 43, 44};
   private static final CompiledDFA DFA = (new NFA(REGEXPS, FINALS)).toDFA().optimize().compile();
   public final boolean ignoreBracketErrors;
   private final IZenCompileEnvironment environment;

   public ZenTokener(Reader contents, IZenCompileEnvironment environment, String fileNameFallback, boolean ignoreBracketErrors) throws IOException {
      super(contents, DFA, fileNameFallback);
      this.ignoreBracketErrors = ignoreBracketErrors;
      this.environment = environment;
   }

   public ZenTokener(String contents, IZenCompileEnvironment environment, String fileNameFallback, boolean ignoreBracketErrors) throws IOException {
      super(new StringReader(contents), DFA, "");
      this.ignoreBracketErrors = ignoreBracketErrors;
      this.environment = environment;
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.environment;
   }

   public Token process(Token token) {
      return token.getType() == 1 && KEYWORDS.containsKey(token.getValue()) ? new Token(token.getValue(), ((Integer)KEYWORDS.get(token.getValue())).intValue(), token.getPosition()) : token;
   }

   static {
      KEYWORDS.put("any", Integer.valueOf(99));
      KEYWORDS.put("bool", Integer.valueOf(100));
      KEYWORDS.put("byte", Integer.valueOf(101));
      KEYWORDS.put("short", Integer.valueOf(102));
      KEYWORDS.put("int", Integer.valueOf(103));
      KEYWORDS.put("long", Integer.valueOf(104));
      KEYWORDS.put("float", Integer.valueOf(105));
      KEYWORDS.put("double", Integer.valueOf(106));
      KEYWORDS.put("string", Integer.valueOf(107));
      KEYWORDS.put("function", Integer.valueOf(108));
      KEYWORDS.put("in", Integer.valueOf(109));
      KEYWORDS.put("has", Integer.valueOf(109));
      KEYWORDS.put("void", Integer.valueOf(110));
      KEYWORDS.put("as", Integer.valueOf(120));
      KEYWORDS.put("version", Integer.valueOf(121));
      KEYWORDS.put("if", Integer.valueOf(122));
      KEYWORDS.put("else", Integer.valueOf(123));
      KEYWORDS.put("for", Integer.valueOf(124));
      KEYWORDS.put("return", Integer.valueOf(125));
      KEYWORDS.put("var", Integer.valueOf(126));
      KEYWORDS.put("val", Integer.valueOf(127));
      KEYWORDS.put("global", Integer.valueOf(666));
      KEYWORDS.put("static", Integer.valueOf(667));
      KEYWORDS.put("instanceof", Integer.valueOf(668));
      KEYWORDS.put("while", Integer.valueOf(128));
      KEYWORDS.put("break", Integer.valueOf(129));
      KEYWORDS.put("continue", Integer.valueOf(130));
      KEYWORDS.put("null", Integer.valueOf(140));
      KEYWORDS.put("true", Integer.valueOf(141));
      KEYWORDS.put("false", Integer.valueOf(142));
      KEYWORDS.put("import", Integer.valueOf(160));
      KEYWORDS.put("frigginClass", Integer.valueOf(123456789));
      KEYWORDS.put("frigginConstructor", Integer.valueOf(987654321));
      KEYWORDS.put("zenClass", Integer.valueOf(123456789));
      KEYWORDS.put("zenConstructor", Integer.valueOf(987654321));
   }
}
