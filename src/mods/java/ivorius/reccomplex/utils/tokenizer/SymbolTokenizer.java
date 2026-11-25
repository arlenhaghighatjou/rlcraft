package ivorius.reccomplex.utils.tokenizer;

import gnu.trove.stack.TIntStack;
import gnu.trove.stack.array.TIntArrayStack;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

public class SymbolTokenizer<T extends SymbolTokenizer.Token> {
   @Nonnull
   protected SymbolTokenizer.CharacterRules characterRules;
   @Nonnull
   protected SymbolTokenizer.TokenFactory<T> tokenFactory;

   public SymbolTokenizer(@Nonnull SymbolTokenizer.CharacterRules characterRules, @Nonnull SymbolTokenizer.TokenFactory tokenFactory) {
      this.characterRules = characterRules;
      this.tokenFactory = tokenFactory;
   }

   @Nonnull
   public SymbolTokenizer.CharacterRules getCharacterRules() {
      return this.characterRules;
   }

   public void setCharacterRules(@Nonnull SymbolTokenizer.CharacterRules characterRules) {
      this.characterRules = characterRules;
   }

   @Nonnull
   public SymbolTokenizer.TokenFactory getTokenFactory() {
      return this.tokenFactory;
   }

   public void setTokenFactory(@Nonnull SymbolTokenizer.TokenFactory tokenFactory) {
      this.tokenFactory = tokenFactory;
   }

   public List<T> tokenize(String string) throws ParseException {
      Character escapeChar = this.characterRules.escapeChar();
      Character literalChar = this.characterRules.literalChar();
      int index = 0;
      int variableStart = -1;
      int literalStart = -1;
      boolean escape = false;
      TIntStack escapes = new TIntArrayStack();
      ArrayList<T> tokens = new ArrayList();

      while(index < string.length()) {
         char character = string.charAt(index);
         if (this.characterRules.isIllegal(character)) {
            throw new ParseException(String.format("Illegal character '%c'", character), index);
         }

         if (!escape && escapeChar != null && escapeChar.charValue() == character) {
            escape = true;
            escapes.push(index);
            if (variableStart < 0 && literalStart < 0) {
               variableStart = index;
            }
         } else if (literalStart >= 0) {
            if (!escape && literalChar != null && literalChar.charValue() == character) {
               tokens.add(this.constructStringToken(string, literalStart + 1, literalStart, index, escapes));
               literalStart = -1;
            }

            escape = false;
         } else if (!escape && literalChar != null && literalChar.charValue() == character) {
            if (variableStart >= 0) {
               tokens.add(this.constructStringToken(string, variableStart, variableStart, index, escapes));
            }

            literalStart = index;
         } else if (!escape && this.characterRules.isWhitespace(character)) {
            if (variableStart >= 0) {
               tokens.add(this.constructStringToken(string, variableStart, variableStart, index, escapes));
            }

            variableStart = -1;
         } else {
            T token;
            if (!escape && (token = this.tokenFactory.tryConstructSymbolTokenAt(index, string)) != null) {
               if (variableStart >= 0) {
                  tokens.add(this.constructStringToken(string, variableStart, variableStart, index, escapes));
               }

               variableStart = -1;
               tokens.add(token);
               index = token.endIndex;
               continue;
            }

            if (variableStart < 0) {
               variableStart = index;
            }

            escape = false;
         }

         ++index;
      }

      if (literalStart >= 0) {
         throw new ParseException(String.format("Unclosed literal '%c'", literalChar), index);
      } else if (escape) {
         throw new ParseException(String.format("Unclosed escape '%c'", escapeChar), index);
      } else {
         if (variableStart >= 0) {
            tokens.add(this.constructStringToken(string, variableStart, variableStart, index, escapes));
         }

         tokens.trimToSize();
         return tokens;
      }
   }

   public String escapeWhereNecessary(String string) {
      Character escapeChar = this.characterRules.escapeChar();
      if (escapeChar != null) {
         TIntStack escapes = new TIntArrayStack();

         for(int idx = 0; idx < string.length(); ++idx) {
            if (this.characterRules.isWhitespace(string.charAt(idx)) || this.tokenFactory.tryConstructSymbolTokenAt(idx, string) != null) {
               escapes.push(idx);
            }
         }

         if (escapes.size() > 0) {
            StringBuilder builder = new StringBuilder(string);

            while(escapes.size() > 0) {
               builder.insert(escapes.pop(), escapeChar);
            }

            return builder.toString();
         }
      }

      return string;
   }

   protected T constructStringToken(String string, int start, int varStart, int end, TIntStack escapes) {
      StringBuilder constant = new StringBuilder(string.substring(start, end));

      while(escapes.size() > 0) {
         constant.deleteCharAt(escapes.pop() - start);
      }

      escapes.clear();
      return this.tokenFactory.constructStringToken(varStart, constant.toString());
   }

   protected boolean hasAt(String string, String symbol, int index) {
      return string.regionMatches(index, symbol, 0, symbol.length());
   }

   public interface CharacterRules {
      @Nullable
      Character escapeChar();

      @Nullable
      Character literalChar();

      boolean isWhitespace(char var1);

      boolean isIllegal(char var1);
   }

   public static class SimpleCharacterRules implements SymbolTokenizer.CharacterRules {
      @Nullable
      private Character escapeChar;
      @Nullable
      private Character literalChar;
      @Nullable
      private char[] whitespace;
      @Nullable
      private char[] illegal;

      public SimpleCharacterRules() {
         this(Character.valueOf('\\'), Character.valueOf('"'), (char[])null, (char[])null);
      }

      public SimpleCharacterRules(Character escapeChar, Character literalChar, char[] whitespace, char[] illegal) {
         this.escapeChar = escapeChar;
         this.literalChar = literalChar;
         this.whitespace = whitespace;
         this.illegal = illegal;
      }

      @Nullable
      public Character getEscapeChar() {
         return this.escapeChar;
      }

      public void setEscapeChar(@Nullable Character escapeChar) {
         this.escapeChar = escapeChar;
      }

      @Nullable
      public Character getLiteralChar() {
         return this.literalChar;
      }

      public void setLiteralChar(@Nullable Character literalChar) {
         this.literalChar = literalChar;
      }

      @Nullable
      public char[] getWhitespace() {
         return this.whitespace;
      }

      public void setWhitespace(@Nullable char[] whitespace) {
         this.whitespace = whitespace;
      }

      @Nullable
      public char[] getIllegal() {
         return this.illegal;
      }

      public void setIllegal(@Nullable char[] illegal) {
         this.illegal = illegal;
      }

      public boolean isWhitespace(char character) {
         return this.whitespace != null ? ArrayUtils.contains(this.whitespace, character) : Character.isWhitespace(character);
      }

      public Character escapeChar() {
         return this.escapeChar;
      }

      public Character literalChar() {
         return this.literalChar;
      }

      public boolean isIllegal(char character) {
         return this.illegal != null && ArrayUtils.contains(this.illegal, character);
      }
   }

   public abstract static class Token {
      public final int startIndex;
      public final int endIndex;

      public Token(int startIndex, int endIndex) {
         this.startIndex = startIndex;
         this.endIndex = endIndex;
      }
   }

   public interface TokenFactory<T extends SymbolTokenizer.Token> {
      @Nullable
      T tryConstructSymbolTokenAt(int var1, @Nonnull String var2);

      @Nonnull
      T constructStringToken(int var1, @Nonnull String var2);
   }
}
