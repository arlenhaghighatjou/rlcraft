package com.fantasticsource.tools;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import javax.annotation.Nonnull;

public class NullPrintStream extends PrintStream {
   public NullPrintStream() {
      super((OutputStream)null);
   }

   public void flush() {
   }

   public void close() {
   }

   public boolean checkError() {
      return false;
   }

   public void write(int b) {
   }

   public void write(@Nonnull byte[] buf, int off, int len) {
   }

   public void print(boolean b) {
   }

   public void print(char c) {
   }

   public void print(int i) {
   }

   public void print(long l) {
   }

   public void print(float f) {
   }

   public void print(double d) {
   }

   public void print(@Nonnull char[] s) {
   }

   public void print(String s) {
   }

   public void print(Object obj) {
   }

   public void println() {
   }

   public void println(boolean x) {
   }

   public void println(char x) {
   }

   public void println(int x) {
   }

   public void println(long x) {
   }

   public void println(float x) {
   }

   public void println(double x) {
   }

   public void println(@Nonnull char[] x) {
   }

   public void println(String x) {
   }

   public void println(Object x) {
   }

   public PrintStream printf(@Nonnull String format, Object... args) {
      return this;
   }

   public PrintStream printf(Locale l, @Nonnull String format, Object... args) {
      return this;
   }

   public PrintStream format(@Nonnull String format, Object... args) {
      return this;
   }

   public PrintStream format(Locale l, @Nonnull String format, Object... args) {
      return this;
   }

   public PrintStream append(CharSequence csq) {
      return this;
   }

   public PrintStream append(CharSequence csq, int start, int end) {
      return this;
   }

   public PrintStream append(char c) {
      return this;
   }
}
