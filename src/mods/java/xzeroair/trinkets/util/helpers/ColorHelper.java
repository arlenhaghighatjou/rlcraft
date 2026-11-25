package xzeroair.trinkets.util.helpers;

public class ColorHelper {
   public ColorHelper() {
   }

   public static String getHexColorFromRGB(int r, int g, int b) {
      int decimal = getDecimalFromRGB(r, g, b);
      String hex = convertDecimalColorToHexadecimal(decimal);
      return hex;
   }

   public static String getHexColorFromRGB(float r, float g, float b) {
      int rR = (int)(r * 255.0F);
      int rG = (int)(g * 255.0F);
      int rB = (int)(b * 255.0F);
      return getHexColorFromRGB(rR, rG, rB);
   }

   public static int getDecimalFromRGB(int r, int g, int b) {
      return -16777216 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
   }

   /** @deprecated */
   @Deprecated
   public static int getDecimalFromRGBA(int r, int g, int b, int a) {
      return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
   }

   public static int getDecimalFromRGB(float r, float g, float b) {
      int rR = (int)(r * 255.0F);
      int rG = (int)(g * 255.0F);
      int rB = (int)(b * 255.0F);
      return getDecimalFromRGB(rR, rG, rB);
   }

   /** @deprecated */
   @Deprecated
   public static int getDecimalFromRGBA(float r, float g, float b, float a) {
      int rR = (int)(r * 255.0F);
      int rG = (int)(g * 255.0F);
      int rB = (int)(b * 255.0F);
      int rA = (int)(a * 255.0F);
      return getDecimalFromRGBA(rR, rG, rB, rA);
   }

   public static float[] getRGBColor(String hex) {
      try {
         String hexColor = hex.toLowerCase().replaceAll("[^#0-9a-f]", "");
         if (!hexColor.replace("#", "").isEmpty()) {
            int i = Integer.decode(hexColor).intValue();
            float r = (float)((i & 16711680) >> 16) / 255.0F;
            float g = (float)((i & '\uff00') >> 8) / 255.0F;
            float b = (float)((i & 255) >> 0) / 255.0F;
            return new float[]{r, g, b};
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return new float[]{0.0F, 0.0F, 0.0F};
   }

   public static float[] getRGBColor(int decimal) {
      return getRGBColor(decimal + "");
   }

   public static String convertDecimalColorToHexadecimal(int color) {
      String hex = Integer.toHexString(color & 16777215);
      if (hex.length() < 6) {
         if (hex.length() == 5) {
            hex = "0" + hex;
         }

         if (hex.length() == 4) {
            hex = "00" + hex;
         }

         if (hex.length() == 3) {
            hex = "000" + hex;
         }
      }

      hex = "#" + hex;
      return hex;
   }

   public static int convertHexadecimalToDecimal(String color) {
      try {
         String hexColor = color.toLowerCase().replaceAll("[^#0-9a-f]", "");
         if (!color.isEmpty()) {
            boolean CheckforHash = !color.startsWith("#");
            hexColor = CheckforHash ? "#" + color : color.toLowerCase();
            int length = hexColor.length();
            if (hexColor.length() >= 7) {
               length = 7;
            }

            int i = Integer.decode(hexColor.substring(0, length)).intValue();
            return i;
         }
      } catch (NumberFormatException var5) {
         ;
      }

      return 0;
   }

   public static class ColorObject {
      private String hexadecimal;
      private int decimal;
      private float r = 1.0F;
      private float g = 1.0F;
      private float b = 1.0F;

      public ColorObject(int decimal) {
         float[] rgb = ColorHelper.getRGBColor(decimal);
         this.decimal = decimal;
         this.hexadecimal = ColorHelper.convertDecimalColorToHexadecimal(decimal);
         this.r = rgb[0];
         this.g = rgb[1];
         this.b = rgb[2];
      }

      public ColorObject(String hexadecimal) {
         float[] rgb = ColorHelper.getRGBColor(hexadecimal);
         this.hexadecimal = hexadecimal;
         this.decimal = ColorHelper.convertHexadecimalToDecimal(hexadecimal);
         this.r = rgb[0];
         this.g = rgb[1];
         this.b = rgb[2];
      }

      public float getRed() {
         return this.r;
      }

      public float getGreen() {
         return this.g;
      }

      public float getBlue() {
         return this.b;
      }

      public int getDecimal() {
         return this.decimal;
      }

      public String getHexadecimal() {
         return this.hexadecimal;
      }
   }
}
