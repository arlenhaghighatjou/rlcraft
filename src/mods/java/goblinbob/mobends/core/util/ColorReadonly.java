package goblinbob.mobends.core.util;

public class ColorReadonly implements IColorRead {
   private final float r;
   private final float g;
   private final float b;
   private final float a;

   public ColorReadonly(float r, float g, float b) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = 1.0F;
   }

   public ColorReadonly(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }

   public ColorReadonly(int hexValue) {
      int valueB = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueG = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueR = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueA = hexValue & 255;
      hexValue = hexValue >> 8;
      this.a = (float)valueA / 255.0F;
      this.r = (float)valueR / 255.0F;
      this.g = (float)valueG / 255.0F;
      this.b = (float)valueB / 255.0F;
   }

   public float getR() {
      return this.r;
   }

   public float getG() {
      return this.g;
   }

   public float getB() {
      return this.b;
   }

   public float getA() {
      return this.a;
   }
}
