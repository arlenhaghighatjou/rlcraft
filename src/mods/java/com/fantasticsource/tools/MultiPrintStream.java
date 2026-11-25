package com.fantasticsource.tools;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import javax.annotation.Nonnull;

public class MultiPrintStream extends PrintStream {
   public ArrayList<PrintStream> streams = new ArrayList();

   public MultiPrintStream(PrintStream... printStreams) {
      super((OutputStream)null);
      this.streams.addAll(Arrays.asList(printStreams));
   }

   public void flush() {
      for(PrintStream stream : this.streams) {
         stream.flush();
      }

   }

   public void close() {
      for(PrintStream stream : this.streams) {
         stream.close();
      }

   }

   public boolean checkError() {
      boolean error = super.checkError();

      for(PrintStream stream : this.streams) {
         error |= stream.checkError();
      }

      return error;
   }

   public void write(int b) {
      for(PrintStream stream : this.streams) {
         stream.write(b);
      }

   }

   public void write(@Nonnull byte[] buf, int off, int len) {
      for(PrintStream stream : this.streams) {
         stream.write(buf, off, len);
      }

   }

   public void print(boolean b) {
      for(PrintStream stream : this.streams) {
         stream.println(b);
      }

   }

   public void print(char c) {
      for(PrintStream stream : this.streams) {
         stream.println(c);
      }

   }

   public void print(int i) {
      for(PrintStream stream : this.streams) {
         stream.println(i);
      }

   }

   public void print(long l) {
      for(PrintStream stream : this.streams) {
         stream.println(l);
      }

   }

   public void print(float f) {
      for(PrintStream stream : this.streams) {
         stream.println(f);
      }

   }

   public void print(double d) {
      for(PrintStream stream : this.streams) {
         stream.println(d);
      }

   }

   public void print(@Nonnull char[] s) {
      for(PrintStream stream : this.streams) {
         stream.print(s);
      }

   }

   public void print(String s) {
      for(PrintStream stream : this.streams) {
         stream.print(s);
      }

   }

   public void print(Object obj) {
      for(PrintStream stream : this.streams) {
         stream.print(obj);
      }

   }

   public void println() {
      for(PrintStream stream : this.streams) {
         stream.println();
      }

   }

   public void println(boolean x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(char x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(int x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(long x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(float x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(double x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(@Nonnull char[] x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(String x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public void println(Object x) {
      for(PrintStream stream : this.streams) {
         stream.println(x);
      }

   }

   public PrintStream printf(@Nonnull String format, Object... args) {
      for(PrintStream stream : this.streams) {
         stream.printf(format, args);
      }

      return this;
   }

   public PrintStream printf(Locale l, @Nonnull String format, Object... args) {
      for(PrintStream stream : this.streams) {
         stream.printf(l, format, args);
      }

      return this;
   }

   public PrintStream format(@Nonnull String format, Object... args) {
      for(PrintStream stream : this.streams) {
         stream.format(format, args);
      }

      return this;
   }

   public PrintStream format(Locale l, @Nonnull String format, Object... args) {
      for(PrintStream stream : this.streams) {
         stream.format(l, format, args);
      }

      return this;
   }

   public PrintStream append(CharSequence csq) {
      for(PrintStream stream : this.streams) {
         stream.append(csq);
      }

      return this;
   }

   public PrintStream append(CharSequence csq, int start, int end) {
      for(PrintStream stream : this.streams) {
         stream.append(csq, start, end);
      }

      return this;
   }

   public PrintStream append(char c) {
      for(PrintStream stream : this.streams) {
         stream.append(c);
      }

      return this;
   }
}
