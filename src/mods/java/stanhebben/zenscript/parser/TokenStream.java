package stanhebben.zenscript.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.util.ZenPosition;

public class TokenStream implements Iterator<Token> {
   private ZenParsedFile file;
   private String fileNameFallback;
   private TokenStream.CountingReader reader;
   private CompiledDFA dfa;
   private Token next;
   private int nextChar;
   private int line;
   private int lineOffset;
   private int tokenMemoryOffset;
   private LinkedList<Token> tokenMemory;
   private Stack<Integer> marks;
   private int tokenMemoryCurrent;

   public TokenStream(Reader reader, CompiledDFA dfa, String fileNameFallback) throws IOException {
      this.tokenMemoryOffset = 0;
      this.tokenMemoryCurrent = 0;
      this.tokenMemory = new LinkedList();
      this.marks = new Stack();
      this.fileNameFallback = fileNameFallback;
      this.reader = new TokenStream.CountingReader(reader);
      this.dfa = dfa;
      this.nextChar = this.reader.read();
      this.line = 1;
      this.lineOffset = 1;
      this.advance();
   }

   public TokenStream(String data, CompiledDFA dfa) throws IOException {
      this(new StringReader(data), dfa, "");
   }

   public ZenParsedFile getFile() {
      return this.file;
   }

   public void setFile(ZenParsedFile file) {
      this.file = file;
   }

   public int getLine() {
      return this.line;
   }

   public int getLineOffset() {
      return this.lineOffset;
   }

   public Token peek() {
      return this.tokenMemoryCurrent < this.tokenMemoryOffset + this.tokenMemory.size() ? (Token)this.tokenMemory.get(this.tokenMemoryCurrent - this.tokenMemoryOffset) : this.next;
   }

   public boolean isNext(int type) {
      return this.peek().getType() == type;
   }

   public Token optional(int... types) {
      for(int type : types) {
         Token token = this.optional(type);
         if (token != null) {
            return token;
         }
      }

      return null;
   }

   public Token optional(int type) {
      return this.peek() != null && this.peek().getType() == type ? this.next() : null;
   }

   public Token required(int type, String error) {
      Token t = this.peek();
      if (t != null && t.getType() == type) {
         return this.next();
      } else {
         throw new ParseException(this.file, this.line, this.lineOffset, error);
      }
   }

   public void pushMark() {
      this.marks.push(Integer.valueOf(this.tokenMemoryCurrent));
   }

   public void popMark() {
      this.marks.pop();
      if (this.marks.isEmpty()) {
         this.tokenMemoryOffset = this.tokenMemoryCurrent;
         this.tokenMemory.clear();
      }

   }

   public void reset() {
      this.tokenMemoryCurrent = ((Integer)this.marks.pop()).intValue();
   }

   public boolean hasNext() {
      return this.next != null;
   }

   public Token next() {
      if (this.tokenMemoryCurrent < this.tokenMemoryOffset + this.tokenMemory.size()) {
         return (Token)this.tokenMemory.get(this.tokenMemoryCurrent++ - this.tokenMemoryOffset);
      } else {
         Token result = this.next;
         if (this.marks.isEmpty()) {
            ++this.tokenMemoryOffset;
         } else {
            this.tokenMemory.add(result);
         }

         ++this.tokenMemoryCurrent;
         this.advance();
         return result;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("Not supported.");
   }

   protected Token process(Token token) {
      return token;
   }

   private void advance() {
      while(true) {
         this.advanceToken();
         if (this.next == null || this.next.getType() >= 0) {
            break;
         }
      }

   }

   private void advanceToken() {
      if (this.nextChar < 0) {
         this.next = null;
      } else {
         try {
            int state = 0;
            StringBuilder value = new StringBuilder();
            int tLine = this.line;
            int tLineOffset = this.lineOffset;

            for(int _fakeNextChar = this.nextChar > 127 ? 256 : this.nextChar; this.dfa.transitions[state].containsKey(_fakeNextChar); _fakeNextChar = this.nextChar > 127 ? 256 : this.nextChar) {
               if (this.nextChar < 0) {
                  value.append((char)this.nextChar);
               } else {
                  value.appendCodePoint(this.nextChar);
               }

               state = this.dfa.transitions[state].get(_fakeNextChar);
               this.line = this.reader.line;
               this.lineOffset = this.reader.lineOffset;
               this.nextChar = this.reader.read();
            }

            if (this.line < 0) {
               throw new IllegalStateException("Line cannot be negative");
            }

            if (this.dfa.finals[state] == Integer.MIN_VALUE) {
               if (this.nextChar < 0 && value.length() == 0) {
                  return;
               }

               throw new TokenException(this.file, this.line, this.lineOffset, (char)this.nextChar);
            }

            if (state == 0) {
               throw new TokenException(this.file, this.line, this.lineOffset, (char)this.nextChar);
            }

            this.next = this.process(new Token(value.toString(), this.dfa.finals[state], new ZenPosition(this.file, tLine, tLineOffset, this.fileNameFallback)));
         } catch (IOException var6) {
            var6.printStackTrace();
         }

      }
   }

   private class CountingReader {
      private int line;
      private int lineOffset;
      private Reader reader;
      private boolean eof;

      public CountingReader(Reader reader) {
         this.reader = reader;
         this.line = 1;
         this.lineOffset = 1;
      }

      public int read() throws IOException {
         int ch = this.reader.read();
         if (ch == -1) {
            this.eof = true;
            return ch;
         } else {
            if (ch == 10) {
               ++this.line;
               this.lineOffset = 1;
            } else {
               ++this.lineOffset;
            }

            return ch;
         }
      }
   }
}
