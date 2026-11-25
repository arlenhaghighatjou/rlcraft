package meldexun.renderlib.util;

import meldexun.matrixutil.UnsafeUtil;

public interface MemoryAccess {
   long getAddress();

   default boolean getBoolean(long offset) {
      return UnsafeUtil.UNSAFE.getBoolean((Object)null, this.getAddress() + offset);
   }

   default void putBoolean(long offset, boolean data) {
      UnsafeUtil.UNSAFE.putBoolean((Object)null, this.getAddress() + offset, data);
   }

   default byte getByte(long offset) {
      return UnsafeUtil.UNSAFE.getByte(this.getAddress() + offset);
   }

   default void putByte(long offset, byte data) {
      UnsafeUtil.UNSAFE.putByte(this.getAddress() + offset, data);
   }

   default short getShort(long offset) {
      return UnsafeUtil.UNSAFE.getShort(this.getAddress() + offset);
   }

   default void putShort(long offset, short data) {
      UnsafeUtil.UNSAFE.putShort(this.getAddress() + offset, data);
   }

   default int getInt(long offset) {
      return UnsafeUtil.UNSAFE.getInt(this.getAddress() + offset);
   }

   default void putInt(long offset, int data) {
      UnsafeUtil.UNSAFE.putInt(this.getAddress() + offset, data);
   }

   default long getLong(long offset) {
      return UnsafeUtil.UNSAFE.getLong(this.getAddress() + offset);
   }

   default void putLong(long offset, long data) {
      UnsafeUtil.UNSAFE.putLong(this.getAddress() + offset, data);
   }

   default float getFloat(long offset) {
      return UnsafeUtil.UNSAFE.getFloat(this.getAddress() + offset);
   }

   default void putFloat(long offset, float data) {
      UnsafeUtil.UNSAFE.putFloat(this.getAddress() + offset, data);
   }

   default double getDouble(long offset) {
      return UnsafeUtil.UNSAFE.getDouble(this.getAddress() + offset);
   }

   default void putDouble(long offset, double data) {
      UnsafeUtil.UNSAFE.putDouble(this.getAddress() + offset, data);
   }

   default char getChar(long offset) {
      return UnsafeUtil.UNSAFE.getChar(this.getAddress() + offset);
   }

   default void putChar(long offset, char data) {
      UnsafeUtil.UNSAFE.putChar(this.getAddress() + offset, data);
   }

   static void copyMemory(MemoryAccess src, long srcOffset, MemoryAccess dest, long destOffset, long bytes) {
      UnsafeUtil.UNSAFE.copyMemory(src.getAddress() + srcOffset, dest.getAddress() + destOffset, bytes);
   }
}
