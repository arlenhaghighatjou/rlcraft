package goblinbob.mobends.core.util;

public class Color implements IColor {
   public static final ColorReadonly WHITE = new ColorReadonly(1.0F, 1.0F, 1.0F, 1.0F);
   public static final ColorReadonly RED = new ColorReadonly(1.0F, 0.0F, 0.0F, 1.0F);
   public static final ColorReadonly GREEN = new ColorReadonly(0.0F, 1.0F, 0.0F, 1.0F);
   public static final ColorReadonly BLUE = new ColorReadonly(0.0F, 0.0F, 1.0F, 1.0F);
   public static final ColorReadonly BLACK = new ColorReadonly(0.0F, 0.0F, 0.0F, 1.0F);
   public static final ColorReadonly ZERO = new ColorReadonly(0.0F, 0.0F, 0.0F, 0.0F);
   public float r;
   public float g;
   public float b;
   public float a;

   public Color(float r, float g, float b) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = 1.0F;
   }

   public Color(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }

   public Color(int hexValue) {
      this.hex(hexValue);
   }

   public Color(IColorRead other) {
      this.r = other.getR();
      this.g = other.getG();
      this.b = other.getB();
      this.a = other.getA();
   }

   public void hex(int hexValue) {
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

   public void set(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }

   public void setR(float r) {
      this.r = r;
   }

   public void setG(float g) {
      this.g = g;
   }

   public void setB(float b) {
      this.b = b;
   }

   public void setA(float a) {
      this.a = a;
   }

   public void add(float r, float g, float b, float a) {
      this.r += r;
      this.g += g;
      this.b += b;
      this.a += a;
   }

   public static int asHex(IColorRead color) {
      int valueA = (int)GUtil.clamp(color.getA() * 255.0F, 0.0F, 255.0F);
      int valueR = (int)GUtil.clamp(color.getR() * 255.0F, 0.0F, 255.0F);
      int valueG = (int)GUtil.clamp(color.getG() * 255.0F, 0.0F, 255.0F);
      int valueB = (int)GUtil.clamp(color.getB() * 255.0F, 0.0F, 255.0F);
      int value = valueA << 8;
      value = value | valueR & 255;
      value = value << 8;
      value = value | valueG & 255;
      value = value << 8;
      value = value | valueB & 255;
      return value;
   }

   public static Color fromHexRGB(int hexValue) {
      int valueB = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueG = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueR = hexValue & 255;
      hexValue = hexValue >> 8;
      return new Color((float)valueR / 255.0F, (float)valueG / 255.0F, (float)valueB / 255.0F, 1.0F);
   }

   public static Color fromHex(int hexValue) {
      int valueB = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueG = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueR = hexValue & 255;
      hexValue = hexValue >> 8;
      int valueA = hexValue & 255;
      hexValue = hexValue >> 8;
      return new Color((float)valueR / 255.0F, (float)valueG / 255.0F, (float)valueB / 255.0F, (float)valueA / 255.0F);
   }
}
