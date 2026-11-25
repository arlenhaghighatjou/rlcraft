package ivorius.ivtoolkit.math;

import net.minecraft.util.math.MathHelper;

public class IvBytePacker {
   public IvBytePacker() {
   }

   public static byte getRequiredBitLength(int maxValue) {
      byte bits;
      for(bits = 0; maxValue > 0; ++bits) {
         maxValue >>>= 1;
      }

      return bits;
   }

   public static byte[] packValues(int[] values, byte bitLength) {
      byte[] packed = new byte[MathHelper.ceil((float)(values.length * bitLength) / 8.0F)];
      int currentArrayIndex = 0;
      long currentVal = 0L;
      byte currentSavedBits = 0;

      for(int value : values) {
         currentVal = currentVal << bitLength | (long)value;

         for(currentSavedBits += bitLength; currentSavedBits >= 8; ++currentArrayIndex) {
            packed[currentArrayIndex] = (byte)((int)(currentVal >>> currentSavedBits - 8));
            currentSavedBits = (byte)(currentSavedBits - 8);
            currentVal = deleteLeftBits(currentVal, 64 - currentSavedBits);
         }
      }

      if (currentSavedBits > 0) {
         packed[currentArrayIndex] = (byte)((int)(currentVal << 8 - currentSavedBits));
      }

      return packed;
   }

   public static int[] unpackValues(byte[] packed, byte bitLength, int valueCount) {
      int[] values = new int[valueCount];
      int currentArrayIndex = 0;
      long currentVal = 0L;
      byte currentSavedBits = 0;

      for(byte value : packed) {
         currentVal = currentVal << 8 | moveMSBToBytePos((long)value);

         for(currentSavedBits = (byte)(currentSavedBits + 8); currentSavedBits >= bitLength && currentArrayIndex < valueCount; ++currentArrayIndex) {
            values[currentArrayIndex] = (int)(currentVal >>> currentSavedBits - bitLength);
            currentSavedBits -= bitLength;
            currentVal = deleteLeftBits(currentVal, 64 - currentSavedBits);
         }
      }

      return values;
   }

   private static long moveMSBToBytePos(long value) {
      return value >>> 56 & 128L | value & 127L;
   }

   private static long deleteLeftBits(long val, int bits) {
      return bits >= 64 ? 0L : val << bits >>> bits;
   }
}
