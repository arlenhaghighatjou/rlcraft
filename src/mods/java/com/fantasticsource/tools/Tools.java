package com.fantasticsource.tools;

import com.fantasticsource.lwjgl.Quaternion;
import com.fantasticsource.tools.datastructures.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.regex.Matcher;
import sun.misc.Cleaner;

public class Tools {
   protected static PrintStream out = null;
   protected static PrintStream err = null;

   public Tools() {
   }

   public static String formatNicely(double d) {
      int i = (int)d;
      if ((int)(d + 1.0E-8D) != i) {
         ++i;
         d = (double)i;
      } else if ((int)(d - 1.0E-8D) != i) {
         d = (double)i;
      }

      double ad = Math.abs(d);
      if (ad == 0.0D) {
         return "0";
      } else if (ad >= 1000.0D) {
         int n;
         for(n = 0; Math.abs(i) > 10; ++n) {
            i /= 10;
         }

         return i + "x10^" + n;
      } else if (ad >= 100.0D) {
         return "" + (int)d;
      } else if (ad >= 0.1D) {
         String result = String.format("%.1f", d);
         return result.contains(".") && result.substring(result.length() - 2).equals(".0") ? "" + i : result;
      } else {
         int n;
         for(n = 0; Math.abs(d) < 1.0D; ++n) {
            d *= 10.0D;
         }

         String base = String.format("%.1f", d);
         return base.contains(".") && base.substring(base.length() - 2).equals(".0") ? i + "x10^-" + n : base + "x10^-" + n;
      }
   }

   public static Quaternion rotatedQuaternion(Quaternion v, Quaternion axis, double theta) {
      double sinThetaDiv2 = TrigLookupTable.TRIG_TABLE_1024.sin(theta * 0.5D);
      double cosThetaDiv2 = TrigLookupTable.TRIG_TABLE_1024.cos(theta * 0.5D);
      Quaternion q = new Quaternion((float)(sinThetaDiv2 * (double)axis.x), (float)(sinThetaDiv2 * (double)axis.y), (float)(sinThetaDiv2 * (double)axis.z), (float)cosThetaDiv2);
      Quaternion qConjugate = new Quaternion((float)(-(sinThetaDiv2 * (double)axis.x)), (float)(-(sinThetaDiv2 * (double)axis.y)), (float)(-(sinThetaDiv2 * (double)axis.z)), (float)cosThetaDiv2);
      return Quaternion.mul(Quaternion.mul(q, v, (Quaternion)null), qConjugate, (Quaternion)null);
   }

   public static void heapdump() {
      new Tools.Heapdump();
   }

   public static double calc(final String str) {
      return ((<undefinedtype>)(new Object() {
         int pos = -1;
         int ch;

         void nextChar() {
            this.ch = ++this.pos < str.length() ? str.charAt(this.pos) : -1;
         }

         boolean eat(int charToEat) {
            while(this.ch == 32) {
               this.nextChar();
            }

            if (this.ch == charToEat) {
               this.nextChar();
               return true;
            } else {
               return false;
            }
         }

         double parse() {
            this.nextChar();
            double x = this.parseExpression();
            if (this.pos < str.length()) {
               throw new RuntimeException("Unexpected: " + (char)this.ch);
            } else {
               return x;
            }
         }

         double parseExpression() {
            double x = this.parseTerm();

            while(true) {
               while(!this.eat(43)) {
                  if (!this.eat(45)) {
                     return x;
                  }

                  x -= this.parseTerm();
               }

               x += this.parseTerm();
            }
         }

         double parseTerm() {
            double x = this.parseFactor();

            while(true) {
               while(!this.eat(42)) {
                  if (!this.eat(47)) {
                     return x;
                  }

                  x /= this.parseFactor();
               }

               x *= this.parseFactor();
            }
         }

         double parseFactor() {
            if (this.eat(43)) {
               return this.parseFactor();
            } else if (this.eat(45)) {
               return -this.parseFactor();
            } else {
               int startPos = this.pos;
               double x;
               if (this.eat(40)) {
                  x = this.parseExpression();
                  this.eat(41);
               } else if ((this.ch < 48 || this.ch > 57) && this.ch != 46) {
                  if (this.ch < 97 || this.ch > 122) {
                     throw new RuntimeException("Unexpected: " + (char)this.ch);
                  }

                  while(this.ch >= 97 && this.ch <= 122) {
                     this.nextChar();
                  }

                  String func = str.substring(startPos, this.pos);
                  x = this.parseFactor();
                  byte var6 = -1;
                  switch(func.hashCode()) {
                  case 98695:
                     if (func.equals("cos")) {
                        var6 = 2;
                     }
                     break;
                  case 113880:
                     if (func.equals("sin")) {
                        var6 = 1;
                     }
                     break;
                  case 114593:
                     if (func.equals("tan")) {
                        var6 = 3;
                     }
                     break;
                  case 3538208:
                     if (func.equals("sqrt")) {
                        var6 = 0;
                     }
                  }

                  switch(var6) {
                  case 0:
                     x = Math.sqrt(x);
                     break;
                  case 1:
                     x = TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad(x));
                     break;
                  case 2:
                     x = TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad(x));
                     break;
                  case 3:
                     x = TrigLookupTable.TRIG_TABLE_1024.tan(Tools.degtorad(x));
                     break;
                  default:
                     throw new RuntimeException("Unknown function: " + func);
                  }
               } else {
                  while(this.ch >= 48 && this.ch <= 57 || this.ch == 46) {
                     this.nextChar();
                  }

                  x = Double.parseDouble(str.substring(startPos, this.pos));
               }

               if (this.eat(94)) {
                  x = Math.pow(x, this.parseFactor());
               }

               return x;
            }
         }
      })).parse();
   }

   public static BufferedReader getJarResourceReader(Class classInJar, String resourcePathAndName) {
      return new BufferedReader(new InputStreamReader(getJarResourceStream(classInJar, resourcePathAndName)));
   }

   public static InputStream getJarResourceStream(Class classInJar, String resourcePathAndName) {
      return classInJar.getClassLoader().getResourceAsStream(resourcePathAndName);
   }

   public static void copyFile(File source, File destination) throws IOException {
      BufferedReader reader = new BufferedReader(new FileReader(source));
      BufferedWriter writer = new BufferedWriter(new FileWriter(destination));

      for(String line = reader.readLine(); line != null; line = reader.readLine()) {
         writer.write(line + "\r\n");
      }

      writer.close();
      reader.close();
   }

   public static String caselessRegex(String regex) {
      StringBuilder result = new StringBuilder();

      for(char c : regex.toLowerCase().toCharArray()) {
         if (c >= 'a' && c <= 'z') {
            result.append("[").append(c).append(c - 32).append("]");
         } else {
            result.append(c);
         }
      }

      return result.toString();
   }

   public static boolean areRelated(Class c1, Class c2) {
      return isA(c1, c2) || isA(c2, c1);
   }

   public static boolean isA(Class c1, Class c2) {
      return c1 != null && c2 != null && c2.isAssignableFrom(c1);
   }

   public static Pair<Integer, Integer> getIntsFromLong(long l) {
      return new Pair<Integer, Integer>((int)(l & 4294967295L), (int)(l >>> 32 & 4294967295L));
   }

   public static long getLong(int upper, int lower) {
      return (long)upper << 32 | (long)lower & 4294967295L;
   }

   public static <T> boolean contains(T[] array, T object) {
      for(T t : array) {
         if (t.equals(object)) {
            return true;
         }
      }

      return false;
   }

   public static <T> int indexOf(T[] array, T object) {
      int i = 0;

      for(T t : array) {
         if (t.equals(object)) {
            return i;
         }

         ++i;
      }

      return -1;
   }

   public static void disableSystemOut() {
      if (System.out != null && !(System.out instanceof NullPrintStream)) {
         out = System.out;
         ReflectionTool.set(System.class, "out", (Object)null, (Object)null);
      }
   }

   public static void enableSystemOut() {
      if (out != null) {
         ReflectionTool.set(System.class, "out", (Object)null, out);
      }
   }

   public static void disableSystemErr() {
      if (System.err != null && !(System.err instanceof NullPrintStream)) {
         err = System.err;
         ReflectionTool.set(System.class, "err", (Object)null, (Object)null);
      }
   }

   public static void enableSystemErr() {
      if (err != null) {
         ReflectionTool.set(System.class, "err", (Object)null, err);
      }
   }

   public static String fixFileSeparators(String input) {
      return input.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
   }

   public static ByteBuffer cloneByteBuffer(ByteBuffer original) {
      int pos = original.position();
      int limit = original.limit();
      int mark = -1;

      try {
         original.reset();
         mark = original.position();
      } catch (InvalidMarkException var5) {
         ;
      }

      ByteBuffer clone = original.isDirect() ? ByteBuffer.allocateDirect(original.capacity()) : ByteBuffer.allocate(original.capacity());
      clone.order(original.order());
      original.limit(original.capacity());
      original.position(0);
      clone.put(original);
      if (mark != -1) {
         original.position(mark);
         original.mark();
         clone.position(mark);
         clone.mark();
      }

      original.position(pos);
      original.limit(limit);
      clone.position(pos);
      clone.limit(limit);
      return clone;
   }

   public static char[] sort(Character... values) {
      char[] result = new char[values.length];
      System.arraycopy(values, 0, result, 0, result.length);

      for(int passesRemaining = result.length - 1; passesRemaining > 0; --passesRemaining) {
         for(int i = 0; i < passesRemaining; ++i) {
            if (!inOrder(Character.valueOf(result[i]), Character.valueOf(result[i + 1]))) {
               char c = result[i];
               result[i] = result[i + 1];
               result[i + 1] = c;
            }
         }
      }

      return result;
   }

   public static boolean inOrder(Character s1, Character s2) {
      return inOrder("" + s1, "" + s2);
   }

   public static String[] sort(String... values) {
      String[] result = new String[values.length];
      System.arraycopy(values, 0, result, 0, result.length);

      for(int passesRemaining = result.length - 1; passesRemaining > 0; --passesRemaining) {
         for(int i = 0; i < passesRemaining; ++i) {
            if (!inOrder(result[i], result[i + 1])) {
               String s = result[i];
               result[i] = result[i + 1];
               result[i + 1] = s;
            }
         }
      }

      return result;
   }

   public static boolean inOrder(String s1, String s2) {
      char[] chars1 = s1.toLowerCase().toCharArray();
      char[] chars2 = s2.toLowerCase().toCharArray();

      for(int i = 0; i < chars1.length; ++i) {
         if (i >= chars2.length || chars2[i] < chars1[i]) {
            return false;
         }

         if (chars1[i] < chars2[i]) {
            return true;
         }
      }

      return true;
   }

   public static int parseHexInt(String hex) {
      hex = hex.replaceAll("0x", "");
      if (hex.length() > 8) {
         throw new NumberFormatException("Hex string too long (max 8 characters): " + hex);
      } else {
         return hex.length() < 8 ? Integer.parseInt(hex, 16) : Integer.parseInt(hex.substring(0, 2), 16) << 24 | Integer.parseInt(hex.substring(2), 16);
      }
   }

   public static boolean isPowerOfTwo(int n) {
      return n != 0 && (n & n - 1) == 0;
   }

   public static ArrayList<String> allRecursiveRelativeFilenames(String directory) {
      File folder = new File(directory);
      if (folder.exists() && folder.isDirectory()) {
         ArrayList<String> result = new ArrayList();

         for(File file : folder.listFiles()) {
            if (!file.isDirectory()) {
               result.add(file.getName());
            } else {
               result.addAll(allRecursiveRelativeFilenames(directory, file.getName()));
            }
         }

         return result;
      } else {
         throw new IllegalStateException("Directory does not exist: " + directory);
      }
   }

   private static ArrayList<String> allRecursiveRelativeFilenames(String mainDirectory, String relativeSubDirectory) {
      String fullDirectory = mainDirectory + File.separator + relativeSubDirectory;
      File folder = new File(fullDirectory);
      if (folder.exists() && folder.isDirectory()) {
         ArrayList<String> result = new ArrayList();

         for(File file : folder.listFiles()) {
            String relativeName = relativeSubDirectory + File.separator + file.getName();
            if (!file.isDirectory()) {
               result.add(relativeName);
            } else {
               result.addAll(allRecursiveRelativeFilenames(mainDirectory, relativeName));
            }
         }

         return result;
      } else {
         throw new IllegalStateException("Directory does not exist: " + fullDirectory);
      }
   }

   public static boolean deleteFilesRecursively(File file) {
      if (file.isDirectory()) {
         File[] files = file.listFiles();
         if (files != null) {
            for(File f : files) {
               deleteFilesRecursively(f);
            }
         }
      }

      return file.delete();
   }

   public static String[] fixedSplit(String string, String regex) {
      if (string == null) {
         return null;
      } else {
         return regex == null ? new String[]{string} : preservedSplitSeparated(string, regex)[0];
      }
   }

   public static String[] preservedSplit(String string, String regex, boolean interpolateResult) {
      if (string == null) {
         return null;
      } else if (regex == null) {
         return new String[]{string};
      } else {
         String[][] separated = preservedSplitSeparated(string, regex);
         String[] tokens = separated[0];
         String[] delimiters = separated[1];
         String[] result = new String[tokens.length + delimiters.length];
         if (interpolateResult) {
            int delimiterCount = delimiters.length;

            for(int i = 0; i < delimiterCount; ++i) {
               result[i << 1] = tokens[i];
               result[(i << 1) + 1] = delimiters[i];
            }

            result[result.length - 1] = tokens[tokens.length - 1];
         } else {
            System.arraycopy(tokens, 0, result, 0, tokens.length);
            System.arraycopy(delimiters, 0, result, tokens.length, delimiters.length);
         }

         return result;
      }
   }

   public static String[][] preservedSplitSeparated(String string, String regex) {
      if (string == null) {
         return (String[][])null;
      } else if (regex == null) {
         return new String[][]{{string}, new String[0]};
      } else {
         ArrayList<String> tokens = new ArrayList();
         ArrayList<String> delimiters = new ArrayList();
         String prev = string;

         String current;
         for(current = string.replaceFirst(regex, ""); !prev.equals(current); current = prev.replaceFirst(regex, "")) {
            int lengthDif = prev.length() - current.length();
            int prevIndex = prev.length() - 1;

            for(int curIndex = current.length() - 1; curIndex >= 0 && prev.charAt(prevIndex) == current.charAt(curIndex); --curIndex) {
               --prevIndex;
            }

            int startIndex = prevIndex + 1 - lengthDif;
            tokens.add(prev.substring(0, startIndex));
            delimiters.add(prev.substring(startIndex, startIndex + lengthDif));
            prev = prev.substring(startIndex + lengthDif);
         }

         tokens.add(current);
         return new String[][]{(String[])tokens.toArray(new String[0]), (String[])delimiters.toArray(new String[0])};
      }
   }

   public static int angleDifDeg(int angle1, int angle2) {
      return posMod(angle2 - angle1, 360);
   }

   public static float angleDifDeg(float angle1, float angle2) {
      return posMod(angle2 - angle1, 360.0F);
   }

   public static double angleDifDeg(double angle1, double angle2) {
      return posMod(angle2 - angle1, 360.0D);
   }

   public static float angleDifRad(float angle1, float angle2) {
      return posMod(angle2 - angle1, 6.2831855F);
   }

   public static double angleDifRad(double angle1, double angle2) {
      return posMod(angle2 - angle1, 6.283185307179586D);
   }

   public static int average(int... values) {
      int sum = 0;

      for(int value : values) {
         sum += value;
      }

      return sum / values.length;
   }

   public static float average(float... values) {
      float sum = 0.0F;

      for(float value : values) {
         sum += value;
      }

      return sum / (float)values.length;
   }

   public static double average(double... values) {
      double sum = 0.0D;

      for(double value : values) {
         sum += value;
      }

      return sum / (double)values.length;
   }

   public static int min(int... values) {
      int result = Integer.MAX_VALUE;

      for(int value : values) {
         if (value < result) {
            result = value;
         }
      }

      return result;
   }

   public static long min(long... values) {
      long result = Long.MAX_VALUE;

      for(long value : values) {
         if (value < result) {
            result = value;
         }
      }

      return result;
   }

   public static float min(float... values) {
      float result = Float.POSITIVE_INFINITY;

      for(float value : values) {
         if (value < result) {
            result = value;
         }
      }

      return result;
   }

   public static double min(double... values) {
      double result = Double.POSITIVE_INFINITY;

      for(double value : values) {
         if (value < result) {
            result = value;
         }
      }

      return result;
   }

   public static int max(int... values) {
      int result = Integer.MIN_VALUE;

      for(int value : values) {
         if (value > result) {
            result = value;
         }
      }

      return result;
   }

   public static long max(long... values) {
      long result = Long.MIN_VALUE;

      for(long value : values) {
         if (value > result) {
            result = value;
         }
      }

      return result;
   }

   public static float max(float... values) {
      float result = Float.NEGATIVE_INFINITY;

      for(float value : values) {
         if (value > result) {
            result = value;
         }
      }

      return result;
   }

   public static double max(double... values) {
      double result = Double.NEGATIVE_INFINITY;

      for(double value : values) {
         if (value > result) {
            result = value;
         }
      }

      return result;
   }

   public static void printStackTrace(Thread thread, int maxNodes) {
      StackTraceElement[] stack = thread.getStackTrace();

      for(int i = 0; i < maxNodes && i < stack.length; ++i) {
         System.out.println(stack[i].toString());
      }

   }

   public static void printStackTrace(Thread thread) {
      printStackTrace(thread, Integer.MAX_VALUE);
   }

   public static void printStackTrace() {
      printStackTrace(Thread.currentThread());
   }

   public static boolean stackContainsSubstring(Thread thread, String subString) {
      subString = subString.toLowerCase();
      StackTraceElement[] stack = thread.getStackTrace();

      for(StackTraceElement element : stack) {
         if (element.getClassName().toLowerCase().contains(subString)) {
            return true;
         }
      }

      return false;
   }

   public static boolean stackContainsSubstring(String substring) {
      return stackContainsSubstring(Thread.currentThread(), substring);
   }

   public static List<Class> getClassTree(Class clss) {
      if (clss == null) {
         return null;
      } else {
         List<Class> classList;
         for(classList = new ArrayList(); clss != null; clss = clss.getSuperclass()) {
            classList.add(clss);
         }

         return classList;
      }
   }

   public static void printClassTree(Class clss) {
      if (clss == null) {
         System.out.println("Class given was null");
      } else {
         System.out.println("===================================");
         System.out.println(clss.getSimpleName() + " Classtree:");

         while(clss != null) {
            System.out.println(clss.getName());
            clss = clss.getSuperclass();
         }

         System.out.println("===================================");
      }

   }

   public static void printMethods(Class clss) {
      if (clss == null) {
         System.out.println("Class given was null");
      } else {
         System.out.println("===================================");
         System.out.println(clss.getSimpleName() + " Methods:");

         for(Method method : clss.getDeclaredMethods()) {
            System.out.println(method);
         }

         System.out.println("===================================");
      }

   }

   public static void printFields(Class clss) {
      if (clss == null) {
         System.out.println("Class given was null");
      } else {
         System.out.println("===================================");
         System.out.println(clss.getSimpleName() + " Fields:");

         for(Field field : clss.getDeclaredFields()) {
            System.out.println(field);
         }

         System.out.println("===================================");
      }

   }

   public static <T> T choose(T[] choices) {
      return (T)choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static <T> T choose(List<T> choices) {
      return (T)choices.get((int)Math.floor(Math.random() * (double)choices.size()));
   }

   public static byte choose(byte... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static short choose(short... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static int choose(int... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static long choose(long... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static float choose(float... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static double choose(double... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static boolean choose(boolean... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static char choose(char... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static String choose(String... choices) {
      return choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static <E> E chooseObj(E... choices) {
      return (E)choices[(int)Math.floor(Math.random() * (double)choices.length)];
   }

   public static void insertBytes(byte[] bytes, int index, short value) {
      bytes[index] = (byte)(value >>> 8);
      bytes[index + 1] = (byte)value;
   }

   public static void insertBytes(byte[] bytes, int index, char value) {
      bytes[index] = (byte)(value >>> 8);
      bytes[index + 1] = (byte)value;
   }

   public static void insertBytes(byte[] bytes, int index, int value) {
      bytes[index] = (byte)(value >>> 24);
      bytes[index + 1] = (byte)(value >>> 16);
      bytes[index + 2] = (byte)(value >>> 8);
      bytes[index + 3] = (byte)value;
   }

   public static void insertBytes(byte[] bytes, int index, float value) {
      insertBytes(bytes, index, Float.floatToRawIntBits(value));
   }

   public static void insertBytes(byte[] bytes, int index, long value) {
      bytes[index] = (byte)((int)(value >>> 56));
      bytes[index + 1] = (byte)((int)(value >>> 48));
      bytes[index + 2] = (byte)((int)(value >>> 40));
      bytes[index + 3] = (byte)((int)(value >>> 32));
      bytes[index + 4] = (byte)((int)(value >>> 24));
      bytes[index + 5] = (byte)((int)(value >>> 16));
      bytes[index + 6] = (byte)((int)(value >>> 8));
      bytes[index + 7] = (byte)((int)value);
   }

   public static void insertBytes(byte[] bytes, int index, double value) {
      insertBytes(bytes, index, Double.doubleToRawLongBits(value));
   }

   public static byte[] mergeByteArrays(byte[] dest, byte[] src) {
      byte[] temp = new byte[dest.length + src.length];

      int i;
      for(i = 0; i < dest.length; ++i) {
         temp[i] = dest[i];
      }

      for(int i2 = i; i < temp.length; ++i) {
         temp[i] = src[i - i2];
      }

      return temp;
   }

   public static byte[] subArray(byte[] bytes, int start) {
      return subArray(bytes, start, bytes.length - start);
   }

   public static byte[] subArray(byte[] bytes, int start, int length) {
      byte[] result = new byte[length];
      System.arraycopy(bytes, start, result, 0, length);
      return result;
   }

   public static char[] subArray(char[] chars, int start) {
      return subArray(chars, start, chars.length - start);
   }

   public static char[] subArray(char[] chars, int start, int length) {
      char[] result = new char[length];
      System.arraycopy(chars, start, result, 0, length);
      return result;
   }

   public static short bytesToShort(byte[] b, int index) {
      return (short)((b[index] & 255) << 8 | b[index + 1] & 255);
   }

   public static char bytesToChar(byte[] b, int index) {
      return (char)((b[index] & 255) << 8 | b[index + 1] & 255);
   }

   public static int bytesToInt(byte[] b, int index) {
      return (b[index] & 255) << 24 | (b[index + 1] & 255) << 16 | (b[index + 2] & 255) << 8 | b[index + 3] & 255;
   }

   public static float bytesToFloat(byte[] b, int index) {
      return Float.intBitsToFloat(bytesToInt(b, index));
   }

   public static String bytesToASCII(byte[] b, int index, int length) {
      String result = "";

      for(int i = 0; i < length; ++i) {
         result = result + (char)b[index + i];
      }

      return result;
   }

   public static double degtorad(double deg) {
      return deg * 3.141592653589793D / 180.0D;
   }

   public static double radtodeg(double rad) {
      return rad * 180.0D / 3.141592653589793D;
   }

   public static int posMod(int a, int b) {
      a = a % b;
      if (a < 0) {
         a += b;
      }

      return a;
   }

   public static float posMod(float a, float b) {
      a = a % b;
      if (a < 0.0F) {
         a += b;
      }

      return a;
   }

   public static double posMod(double a, double b) {
      a = a % b;
      if (a < 0.0D) {
         a += b;
      }

      return a;
   }

   public static byte random(byte maxvalue) {
      return (byte)((int)((double)maxvalue * Math.random()));
   }

   public static short random(short maxvalue) {
      return (short)((int)((double)maxvalue * Math.random()));
   }

   public static int random(int maxvalue) {
      return (int)((double)maxvalue * Math.random());
   }

   public static long random(long maxvalue) {
      return (long)((double)maxvalue * Math.random());
   }

   public static float random(float maxvalue) {
      return (float)((double)maxvalue * Math.random());
   }

   public static double random(double maxvalue) {
      return maxvalue * Math.random();
   }

   public static char random(char maxvalue) {
      return (char)((int)((double)maxvalue * Math.random()));
   }

   public static double randomGaussian(EaglercraftRandom random, double center, double range) {
      double result;
      for(result = random.nextGaussian() * 0.5D; result < -1.0D || result > 1.0D; result = random.nextGaussian() * 0.5D) {
         ;
      }

      return center + range * result;
   }

   public static double distance(double x1, double y1, double x2, double y2) {
      return Math.sqrt(distanceSquared(x1, y1, x2, y2));
   }

   public static double distanceSquared(double x1, double y1, double x2, double y2) {
      return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
   }

   public static void freeDirectByteBuffer(ByteBuffer directBuffer) throws NoSuchFieldException, IllegalAccessException {
      Field field = directBuffer.getClass().getDeclaredField("cleaner");
      field.setAccessible(true);
      Cleaner cleaner = (Cleaner)field.get(directBuffer);
      cleaner.clean();
   }

   public static String readTXT(String filename) throws IOException {
      return readTXT(filename, false, (Class)null);
   }

   public static String readTXT(String filename, boolean internal, Class calledFrom) throws IOException {
      InputStream in;
      if (internal) {
         in = calledFrom.getResourceAsStream(filename);
      } else {
         in = new FileInputStream(filename);
      }

      String result = "";

      BufferedReader reader;
      String line;
      for(reader = new BufferedReader(new InputStreamReader(in, "UTF-8")); (line = reader.readLine()) != null; result = result + line + "\r\n") {
         ;
      }

      reader.close();
      in.close();
      return result;
   }

   public static ByteBuffer allocateNative(int bytes) {
      return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder());
   }

   public static class Heapdump {
      protected byte[][] bytes;

      public Heapdump() {
         int size = 1073741824;
         this.bytes = new byte[size][];

         for(int i = 0; i < size; ++i) {
            this.bytes[i] = new byte[size];
            this.bytes[i][size - 1] = 1;
         }

      }
   }
}
