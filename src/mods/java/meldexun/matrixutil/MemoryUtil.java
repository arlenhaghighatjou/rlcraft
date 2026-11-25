package meldexun.matrixutil;

import java.lang.reflect.Field;
import java.nio.Buffer;

public class MemoryUtil {
   private static final long OFFSET_address;

   public MemoryUtil() {
   }

   public static long getAddress(Buffer buffer) {
      return UnsafeUtil.UNSAFE.getLong(buffer, OFFSET_address);
   }

   static {
      try {
         Field f = Buffer.class.getDeclaredField("address");
         OFFSET_address = UnsafeUtil.UNSAFE.objectFieldOffset(f);
      } catch (Exception var1) {
         throw new UnsupportedOperationException("Failed to find address field offset");
      }
   }
}
