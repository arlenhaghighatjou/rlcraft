package com.endertech.minecraft.forge.configs;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.ForgeMain;
import java.awt.Color;
import java.util.Objects;

public class ColorARGB {
   public static final int MAX_BYTE_VALUE = 255;
   public static final ColorARGB DEFAULT = from(0);
   public static final String HEX_FORMAT = "ARGB (Alpha, Red, Green, Blue) hex format (0xaarrggbb)";
   public static final String DESCRIPTION_COMMON = "Defines a color in ARGB (Alpha, Red, Green, Blue) hex format (0xaarrggbb)";
   public static final String DESCRIPTION_FOR_SMOKE = "Defines a color in ARGB (Alpha, Red, Green, Blue) hex format (0xaarrggbb) which will be applied to the smoke.\nThe alpha channel of this color (aa) determines probability of applying (ff - always, 7f - 1/2, 00 - never).\n";
   private final ColorARGB.Channel alpha;
   private final ColorARGB.Channel red;
   private final ColorARGB.Channel green;
   private final ColorARGB.Channel blue;

   protected ColorARGB(ColorARGB.Channel alpha, ColorARGB.Channel red, ColorARGB.Channel green, ColorARGB.Channel blue) {
      this.alpha = alpha;
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public static ColorARGB from(int intARGB) {
      return new ColorARGB(ColorARGB.Channel.from(intARGB >> 24 & 255), ColorARGB.Channel.from(intARGB >> 16 & 255), ColorARGB.Channel.from(intARGB >> 8 & 255), ColorARGB.Channel.from(intARGB >> 0 & 255));
   }

   public static ColorARGB from(String hexARGB) {
      return from(getARGB(hexARGB));
   }

   public static ColorARGB from(Color color) {
      return from(color.getRGB());
   }

   protected static int getARGB(String hexARGB) {
      hexARGB = hexARGB.toLowerCase().trim();
      int offset = 0;
      if (hexARGB.startsWith("#")) {
         offset = 1;
      }

      if (hexARGB.startsWith("0x")) {
         offset = 2;
      }

      int argb = 0;

      try {
         argb = Integer.parseUnsignedInt(hexARGB.substring(offset), 16);
      } catch (NumberFormatException var4) {
         ForgeMain.instance.getLogger().error("'{}' is not a valid hex ARGBcolor definition!", hexARGB);
      }

      return argb;
   }

   protected static String getHexARGB(int intARGB) {
      return String.format("%#010x", intARGB);
   }

   public ColorARGB.Channel getAlpha() {
      return this.alpha;
   }

   public ColorARGB.Channel getRed() {
      return this.red;
   }

   public ColorARGB.Channel getGreen() {
      return this.green;
   }

   public ColorARGB.Channel getBlue() {
      return this.blue;
   }

   public int getARGB() {
      return this.alpha.value << 24 | this.red.value << 16 | this.green.value << 8 | this.blue.value << 0;
   }

   public String toString() {
      return this.getClass().getSimpleName() + Args.group(Args.get("a", Integer.valueOf(this.getAlpha().value)), Args.get("r", Integer.valueOf(this.getRed().value)), Args.get("g", Integer.valueOf(this.getGreen().value)), Args.get("b", Integer.valueOf(this.getBlue().value)));
   }

   public String getHexARGB() {
      return getHexARGB(this.getARGB());
   }

   public boolean isOpaque() {
      return this.getAlpha().value == 255;
   }

   public boolean isCompletelyTransparent() {
      return this.getAlpha().value == 0;
   }

   public ColorARGB maxOpaque() {
      return new ColorARGB(ColorARGB.Channel.from(255), this.getRed(), this.getGreen(), this.getBlue());
   }

   public ColorARGB withAlpha(ColorARGB.Channel alpha) {
      return new ColorARGB(alpha, this.getRed(), this.getGreen(), this.getBlue());
   }

   public ColorARGB withRed(ColorARGB.Channel red) {
      return new ColorARGB(this.getAlpha(), red, this.getGreen(), this.getBlue());
   }

   public ColorARGB withGreen(ColorARGB.Channel green) {
      return new ColorARGB(this.getAlpha(), this.getRed(), green, this.getBlue());
   }

   public ColorARGB withBlue(ColorARGB.Channel blue) {
      return new ColorARGB(this.getAlpha(), this.getRed(), this.getGreen(), blue);
   }

   public boolean equals(Object obj) {
      if (obj instanceof ColorARGB) {
         ColorARGB argb = (ColorARGB)obj;
         return argb.getARGB() == this.getARGB();
      } else {
         return super.equals(obj);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getARGB()});
   }

   public static class Channel {
      public int value;

      protected Channel(int value) {
         this.value = value;
      }

      public static ColorARGB.Channel from(int value) {
         return new ColorARGB.Channel(value);
      }

      public static ColorARGB.Channel fromFloat(float value) {
         return from((int)(value * 255.0F + 0.5F));
      }

      public float toFloat() {
         return (float)this.value / 255.0F;
      }

      public boolean equals(Object obj) {
         if (obj instanceof ColorARGB.Channel) {
            return ((ColorARGB.Channel)obj).value == this.value;
         } else {
            return super.equals(obj);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.value});
      }
   }
}
