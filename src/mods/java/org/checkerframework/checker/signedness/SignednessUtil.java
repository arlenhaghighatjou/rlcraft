package org.checkerframework.checker.signedness;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.checkerframework.checker.signedness.qual.Unsigned;

public final class SignednessUtil {
   private SignednessUtil() {
      throw new Error("Do not instantiate");
   }

   public static ByteBuffer wrapUnsigned(byte[] array) {
      return ByteBuffer.wrap(array);
   }

   public static ByteBuffer wrapUnsigned(byte[] array, int offset, int length) {
      return ByteBuffer.wrap(array, offset, length);
   }

   @Unsigned
   public static int getUnsignedInt(ByteBuffer b) {
      return b.getInt();
   }

   @Unsigned
   public static short getUnsignedShort(ByteBuffer b) {
      return b.getShort();
   }

   @Unsigned
   public static byte getUnsigned(ByteBuffer b) {
      return b.get();
   }

   @Unsigned
   public static byte getUnsigned(ByteBuffer b, int i) {
      return b.get(i);
   }

   public static ByteBuffer getUnsigned(ByteBuffer b, byte[] bs, int i, int l) {
      return b.get(bs, i, l);
   }

   public static ByteBuffer putUnsigned(ByteBuffer b, @Unsigned byte ubyte) {
      return b.put(ubyte);
   }

   public static ByteBuffer putUnsigned(ByteBuffer b, int i, @Unsigned byte ubyte) {
      return b.put(i, ubyte);
   }

   public static IntBuffer putUnsigned(IntBuffer b, @Unsigned int uint) {
      return b.put(uint);
   }

   public static IntBuffer putUnsigned(IntBuffer b, int i, @Unsigned int uint) {
      return b.put(i, uint);
   }

   public static IntBuffer putUnsigned(IntBuffer b, int[] uints) {
      return b.put(uints);
   }

   public static IntBuffer putUnsigned(IntBuffer b, int[] uints, int i, int l) {
      return b.put(uints, i, l);
   }

   @Unsigned
   public static int getUnsigned(IntBuffer b, int i) {
      return b.get(i);
   }

   public static ByteBuffer putUnsignedShort(ByteBuffer b, @Unsigned short ushort) {
      return b.putShort(ushort);
   }

   public static ByteBuffer putUnsignedShort(ByteBuffer b, int i, @Unsigned short ushort) {
      return b.putShort(i, ushort);
   }

   public static ByteBuffer putUnsignedInt(ByteBuffer b, @Unsigned int uint) {
      return b.putInt(uint);
   }

   public static ByteBuffer putUnsignedInt(ByteBuffer b, int i, @Unsigned int uint) {
      return b.putInt(i, uint);
   }

   public static ByteBuffer putUnsignedLong(ByteBuffer b, int i, @Unsigned long ulong) {
      return b.putLong(i, ulong);
   }

   @Unsigned
   public static byte readUnsignedByte(RandomAccessFile f) throws IOException {
      return f.readByte();
   }

   @Unsigned
   public static char readUnsignedChar(RandomAccessFile f) throws IOException {
      return f.readChar();
   }

   @Unsigned
   public static short readUnsignedShort(RandomAccessFile f) throws IOException {
      return f.readShort();
   }

   @Unsigned
   public static int readUnsignedInt(RandomAccessFile f) throws IOException {
      return f.readInt();
   }

   @Unsigned
   public static long readUnsignedLong(RandomAccessFile f) throws IOException {
      return f.readLong();
   }

   public static int readUnsigned(RandomAccessFile f, byte[] b, int off, int len) throws IOException {
      return f.read(b, off, len);
   }

   public static void readFullyUnsigned(RandomAccessFile f, byte[] b) throws IOException {
      f.readFully(b);
   }

   public static void writeUnsigned(RandomAccessFile f, byte[] bs, int off, int len) throws IOException {
      f.write(bs, off, len);
   }

   public static void writeUnsignedByte(RandomAccessFile f, @Unsigned byte b) throws IOException {
      f.writeByte(toUnsignedInt(b));
   }

   public static void writeUnsignedChar(RandomAccessFile f, @Unsigned char c) throws IOException {
      f.writeChar(toUnsignedInt(c));
   }

   public static void writeUnsignedShort(RandomAccessFile f, @Unsigned short s) throws IOException {
      f.writeShort(toUnsignedInt(s));
   }

   public static void writeUnsignedInt(RandomAccessFile f, @Unsigned int i) throws IOException {
      f.writeInt(i);
   }

   public static void writeUnsignedLong(RandomAccessFile f, @Unsigned long l) throws IOException {
      f.writeLong(l);
   }

   public static void getUnsigned(ByteBuffer b, byte[] bs) {
      b.get(bs);
   }

   public static int compareUnsigned(@Unsigned long x, @Unsigned long y) {
      return Long.compare(x + Long.MIN_VALUE, y + Long.MIN_VALUE);
   }

   public static int compareUnsigned(@Unsigned int x, @Unsigned int y) {
      return Integer.compare(x + Integer.MIN_VALUE, y + Integer.MIN_VALUE);
   }

   public static int compareUnsigned(@Unsigned short x, @Unsigned short y) {
      return compareUnsigned(toUnsignedInt(x), toUnsignedInt(y));
   }

   public static int compareUnsigned(@Unsigned byte x, @Unsigned byte y) {
      return compareUnsigned(toUnsignedInt(x), toUnsignedInt(y));
   }

   public static String toUnsignedString(@Unsigned long l) {
      return toUnsignedBigInteger(l).toString();
   }

   public static String toUnsignedString(@Unsigned long l, int radix) {
      return toUnsignedBigInteger(l).toString(radix);
   }

   public static String toUnsignedString(@Unsigned int i) {
      return Long.toString(toUnsignedLong(i));
   }

   public static String toUnsignedString(@Unsigned int i, int radix) {
      return Long.toString(toUnsignedLong(i), radix);
   }

   public static String toUnsignedString(@Unsigned short s) {
      return Long.toString(toUnsignedLong(s));
   }

   public static String toUnsignedString(@Unsigned short s, int radix) {
      return Long.toString(toUnsignedLong(s), radix);
   }

   public static String toUnsignedString(@Unsigned byte b) {
      return Long.toString(toUnsignedLong(b));
   }

   public static String toUnsignedString(@Unsigned byte b, int radix) {
      return Long.toString(toUnsignedLong(b), radix);
   }

   @Unsigned
   private static BigInteger toUnsignedBigInteger(@Unsigned long l) {
      if (l >= 0L) {
         return BigInteger.valueOf(l);
      } else {
         int upper = (int)(l >>> 32);
         int lower = (int)l;
         return BigInteger.valueOf(toUnsignedLong(upper)).shiftLeft(32).add(BigInteger.valueOf(toUnsignedLong(lower)));
      }
   }

   @Unsigned
   public static long toUnsignedLong(@Unsigned int i) {
      return (long)i & 4294967295L;
   }

   @Unsigned
   public static long toUnsignedLong(@Unsigned short s) {
      return (long)s & 65535L;
   }

   @Unsigned
   public static int toUnsignedInt(@Unsigned short s) {
      return s & '\uffff';
   }

   @Unsigned
   public static long toUnsignedLong(@Unsigned byte b) {
      return (long)b & 255L;
   }

   @Unsigned
   public static int toUnsignedInt(@Unsigned byte b) {
      return b & 255;
   }

   @Unsigned
   public static short toUnsignedShort(@Unsigned byte b) {
      return (short)(b & 255);
   }

   @Unsigned
   public static long toUnsignedLong(@Unsigned char c) {
      return (long)c & 255L;
   }

   @Unsigned
   public static int toUnsignedInt(@Unsigned char c) {
      return c & 255;
   }

   @Unsigned
   public static short toUnsignedShort(@Unsigned char c) {
      return (short)(c & 255);
   }

   public static float toFloat(@Unsigned byte b) {
      return toUnsignedBigInteger(toUnsignedLong(b)).floatValue();
   }

   public static float toFloat(@Unsigned short s) {
      return toUnsignedBigInteger(toUnsignedLong(s)).floatValue();
   }

   public static float toFloat(@Unsigned int i) {
      return toUnsignedBigInteger(toUnsignedLong(i)).floatValue();
   }

   public static float toFloat(@Unsigned long l) {
      return toUnsignedBigInteger(l).floatValue();
   }

   public static double toDouble(@Unsigned byte b) {
      return toUnsignedBigInteger(toUnsignedLong(b)).doubleValue();
   }

   public static double toDouble(@Unsigned short s) {
      return toUnsignedBigInteger(toUnsignedLong(s)).doubleValue();
   }

   public static double toDouble(@Unsigned int i) {
      return toUnsignedBigInteger(toUnsignedLong(i)).doubleValue();
   }

   public static double toDouble(@Unsigned long l) {
      return toUnsignedBigInteger(l).doubleValue();
   }

   @Unsigned
   public static byte byteFromFloat(float f) {
      assert f >= 0.0F;

      return (byte)((int)f);
   }

   @Unsigned
   public static short shortFromFloat(float f) {
      assert f >= 0.0F;

      return (short)((int)f);
   }

   @Unsigned
   public static int intFromFloat(float f) {
      assert f >= 0.0F;

      return (int)f;
   }

   @Unsigned
   public static long longFromFloat(float f) {
      assert f >= 0.0F;

      return (long)f;
   }

   @Unsigned
   public static byte byteFromDouble(double d) {
      assert d >= 0.0D;

      return (byte)((int)d);
   }

   @Unsigned
   public static short shortFromDouble(double d) {
      assert d >= 0.0D;

      return (short)((int)d);
   }

   @Unsigned
   public static int intFromDouble(double d) {
      assert d >= 0.0D;

      return (int)d;
   }

   @Unsigned
   public static long longFromDouble(double d) {
      assert d >= 0.0D;

      return (long)d;
   }
}
