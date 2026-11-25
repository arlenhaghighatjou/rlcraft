package fionathemortal.betterbiomeblend;

public final class Color {
   public static final float[] sRGBLUT = new float[256];

   public Color() {
   }

   public static int makeRGBAWithFullAlpha(int R, int G, int B) {
      int result = (255 & R) << 0 | (255 & G) << 8 | (255 & B) << 16 | -16777216;
      return result;
   }

   public static int RGBAGetR(int color) {
      int result = color & 255;
      return result;
   }

   public static int RGBAGetG(int color) {
      int result = color >> 8 & 255;
      return result;
   }

   public static int RGBAGetB(int color) {
      int result = color >> 16 & 255;
      return result;
   }

   public static float byteToNormalizedFloat(int color) {
      float result = (float)color / 255.0F;
      return result;
   }

   public static byte normalizedFloatToByte(float color) {
      byte result = (byte)Math.round(color * 255.0F);
      return result;
   }

   public static float sRGBToLinear(float color) {
      float result;
      if (color <= 0.040448237F) {
         result = color / 12.92F;
      } else {
         result = (float)Math.pow((double)((color + 0.055F) / 1.055F), 2.4000000953674316D);
      }

      return result;
   }

   public static float linearTosRGB(float color) {
      float result;
      if (color <= 0.0031306685F) {
         result = color * 12.92F;
      } else {
         result = 1.055F * (float)Math.pow((double)color, 0.4166666567325592D) - 0.055F;
      }

      return result;
   }

   public static float sRGBByteToLinearFloat(int color) {
      float result = sRGBLUT[color];
      return result;
   }

   public static byte linearFloatTosRGBByte(float color) {
      float sRGB = linearTosRGB(color);
      byte result = normalizedFloatToByte(sRGB);
      return result;
   }

   static {
      for(int i = 0; i < 256; ++i) {
         float color = byteToNormalizedFloat(i);
         sRGBLUT[i] = sRGBToLinear(color);
      }

   }
}
