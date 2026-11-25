package hunternif.mc.atlas.util;

public class ByteUtil {
   public ByteUtil() {
   }

   private static int unsignedByteToInt(byte b) {
      return b & 255;
   }

   public static int[] unsignedByteToIntArray(Object array) {
      if (array instanceof int[]) {
         return (int[])array;
      } else {
         return array instanceof byte[] ? unsignedByteToIntArray((byte[])array) : new int[0];
      }
   }

   public static int[] unsignedByteToIntArray(byte[] bytes) {
      int[] ints = new int[bytes.length];

      for(int i = 0; i < bytes.length; ++i) {
         ints[i] = unsignedByteToInt(bytes[i]);
      }

      return ints;
   }
}
