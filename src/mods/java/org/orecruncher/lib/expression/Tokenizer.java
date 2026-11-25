package org.orecruncher.lib.expression;

import java.util.Iterator;
import java.util.Set;

public final class Tokenizer implements Iterator<String> {
   public static final char decimalSeparator = '.';
   public static final char minusSign = '-';
   public static final char quote = '\'';
   public static final char bang = '!';
   private int pos = 0;
   private final String input;
   private String previousToken;
   private final Set<String> operators;

   public Tokenizer(String input, Set<String> operators) {
      this.input = input.trim();
      this.operators = operators;
   }

   public boolean hasNext() {
      return this.pos < this.input.length();
   }

   private char peekNextChar() {
      return this.pos < this.input.length() - 1 ? this.input.charAt(this.pos + 1) : '\u0000';
   }

   public String next() {
      if (this.pos >= this.input.length()) {
         return this.previousToken = null;
      } else {
         StringBuilder token = new StringBuilder();

         char ch;
         for(ch = this.input.charAt(this.pos); Character.isWhitespace(ch) && this.pos < this.input.length(); ch = this.input.charAt(++this.pos)) {
            ;
         }

         if (!Character.isDigit(ch)) {
            if (ch != '-' || !Character.isDigit(this.peekNextChar()) || !"(".equals(this.previousToken) && !",".equals(this.previousToken) && this.previousToken != null && !this.operators.contains(this.previousToken)) {
               if (!Character.isLetter(ch) && ch != '_') {
                  if (ch != '(' && ch != ')' && ch != ',') {
                     if (ch == '\'') {
                        token.append(ch);
                        ++this.pos;

                        for(ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos); ch != '\'' && this.pos < this.input.length(); ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos)) {
                           token.append(this.input.charAt(this.pos++));
                        }

                        if (ch == 0) {
                           throw new ExpressionException("String not terminated '" + token + "'");
                        }

                        token.append(ch);
                        ++this.pos;
                     } else {
                        while(!Character.isLetter(ch) && !Character.isDigit(ch) && ch != '_' && !Character.isWhitespace(ch) && ch != '(' && ch != ')' && ch != ',' && ch != '\'' && this.pos < this.input.length()) {
                           token.append(this.input.charAt(this.pos));
                           ++this.pos;
                           ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
                           if (ch == '-' || ch == '!') {
                              break;
                           }
                        }

                        if (!this.operators.contains(token.toString())) {
                           throw new ExpressionException("Unknown operator '" + token + "' at position " + (this.pos - token.length() + 1));
                        }
                     }
                  } else {
                     token.append(ch);
                     ++this.pos;
                  }
               } else {
                  while((Character.isLetter(ch) || Character.isDigit(ch) || ch == '_' || ch == '.') && this.pos < this.input.length()) {
                     token.append(this.input.charAt(this.pos++));
                     ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
                  }
               }
            } else {
               token.append('-');
               ++this.pos;
               token.append(this.next());
            }
         } else {
            while((Character.isDigit(ch) || ch == '.' || ch == 'e' || ch == 'E' || ch == '-' && token.length() > 0 && ('e' == token.charAt(token.length() - 1) || 'E' == token.charAt(token.length() - 1)) || ch == '+' && token.length() > 0 && ('e' == token.charAt(token.length() - 1) || 'E' == token.charAt(token.length() - 1))) && this.pos < this.input.length()) {
               token.append(this.input.charAt(this.pos++));
               ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
            }
         }

         return this.previousToken = token.toString();
      }
   }

   public void remove() {
      throw new ExpressionException("remove() not supported");
   }

   public int getPos() {
      return this.pos;
   }
}
