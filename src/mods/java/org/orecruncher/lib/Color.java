package org.orecruncher.lib;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class Color {
   public static final Color RED = new Color.ImmutableColor(255, 0, 0);
   public static final Color ORANGE = new Color.ImmutableColor(255, 127, 0);
   public static final Color YELLOW = new Color.ImmutableColor(255, 255, 0);
   public static final Color LGREEN = new Color.ImmutableColor(127, 255, 0);
   public static final Color GREEN = new Color.ImmutableColor(0, 255, 0);
   public static final Color TURQOISE = new Color.ImmutableColor(0, 255, 127);
   public static final Color CYAN = new Color.ImmutableColor(0, 255, 255);
   public static final Color AUQUAMARINE = new Color.ImmutableColor(0, 127, 255);
   public static final Color BLUE = new Color.ImmutableColor(0, 0, 255);
   public static final Color VIOLET = new Color.ImmutableColor(127, 0, 255);
   public static final Color MAGENTA = new Color.ImmutableColor(255, 0, 255);
   public static final Color RASPBERRY = new Color.ImmutableColor(255, 0, 127);
   public static final Color BLACK = new Color.ImmutableColor(0, 0, 0);
   public static final Color WHITE = new Color.ImmutableColor(255, 255, 255);
   public static final Color PURPLE = new Color.ImmutableColor(80, 0, 80);
   public static final Color INDIGO = new Color.ImmutableColor(75, 0, 130);
   public static final Color NAVY = new Color.ImmutableColor(0, 0, 128);
   public static final Color TAN = new Color.ImmutableColor(210, 180, 140);
   public static final Color GOLD = new Color.ImmutableColor(255, 215, 0);
   public static final Color GRAY = new Color.ImmutableColor(128, 128, 128);
   public static final Color LGRAY = new Color.ImmutableColor(192, 192, 192);
   public static final Color SLATEGRAY = new Color.ImmutableColor(112, 128, 144);
   public static final Color DARKSLATEGRAY = new Color.ImmutableColor(47, 79, 79);
   public static final Color MC_BLACK = new Color.ImmutableColor(0, 0, 0);
   public static final Color MC_DARKBLUE = new Color.ImmutableColor(0, 0, 170);
   public static final Color MC_DARKGREEN = new Color.ImmutableColor(0, 170, 0);
   public static final Color MC_DARKAQUA = new Color.ImmutableColor(0, 170, 170);
   public static final Color MC_DARKRED = new Color.ImmutableColor(170, 0, 0);
   public static final Color MC_DARKPURPLE = new Color.ImmutableColor(170, 0, 170);
   public static final Color MC_GOLD = new Color.ImmutableColor(255, 170, 0);
   public static final Color MC_GRAY = new Color.ImmutableColor(170, 170, 170);
   public static final Color MC_DARKGRAY = new Color.ImmutableColor(85, 85, 85);
   public static final Color MC_BLUE = new Color.ImmutableColor(85, 85, 255);
   public static final Color MC_GREEN = new Color.ImmutableColor(85, 255, 85);
   public static final Color MC_AQUA = new Color.ImmutableColor(85, 255, 255);
   public static final Color MC_RED = new Color.ImmutableColor(255, 85, 85);
   public static final Color MC_LIGHTPURPLE = new Color.ImmutableColor(255, 85, 255);
   public static final Color MC_YELLOW = new Color.ImmutableColor(255, 255, 85);
   public static final Color MC_WHITE = new Color.ImmutableColor(255, 255, 255);
   public static final Color AURORA_RED = new Color.ImmutableColor(1.0F, 0.0F, 0.0F);
   public static final Color AURORA_GREEN = new Color.ImmutableColor(0.5F, 1.0F, 0.0F);
   public static final Color AURORA_BLUE = new Color.ImmutableColor(0.0F, 0.8F, 1.0F);
   private static final Map<TextFormatting, Color> colorLookup = new EnumMap(TextFormatting.class);
   public float red;
   public float green;
   public float blue;

   public static Color getColor(TextFormatting format) {
      return (Color)colorLookup.get(format);
   }

   public Color(@Nonnull Color color) {
      this(color.red, color.green, color.blue);
   }

   public Color(int red, int green, int blue) {
      this((float)red / 255.0F, (float)green / 255.0F, (float)blue / 255.0F);
   }

   public Color(@Nonnull Vec3d vec) {
      this((float)vec.x, (float)vec.y, (float)vec.z);
   }

   public Color(int rgb) {
      this(rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255);
   }

   public Color(float red, float green, float blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public Color(double red, double green, double blue) {
      this((float)red, (float)green, (float)blue);
   }

   @Nonnull
   public Vec3d toVec3d() {
      return new Vec3d((double)this.red, (double)this.green, (double)this.blue);
   }

   @Nonnull
   public Vec3d transitionTo(@Nonnull Color target, int iterations) {
      double deltaRed = (double)((target.red - this.red) / (float)iterations);
      double deltaGreen = (double)((target.green - this.green) / (float)iterations);
      double deltaBlue = (double)((target.blue - this.blue) / (float)iterations);
      return new Vec3d(deltaRed, deltaGreen, deltaBlue);
   }

   @Nonnull
   public Color scale(float scaleFactor) {
      return this.scale(scaleFactor, scaleFactor, scaleFactor);
   }

   @Nonnull
   public Color scale(float scaleRed, float scaleGreen, float scaleBlue) {
      this.red *= scaleRed;
      this.green *= scaleGreen;
      this.blue *= scaleBlue;
      return this;
   }

   @Nonnull
   public static Color scale(@Nonnull Color color, float scaleFactor) {
      return (new Color(color)).scale(scaleFactor);
   }

   @Nonnull
   public Color add(@Nonnull Color color) {
      this.red += color.red;
      this.green += color.green;
      this.blue += color.blue;
      return this;
   }

   @Nonnull
   public Color add(float red, float green, float blue) {
      this.red += red;
      this.green += green;
      this.blue += blue;
      return this;
   }

   private static float blend(float c1, float c2, float factor) {
      return (float)Math.sqrt((double)((1.0F - factor) * c1 * c1 + factor * c2 * c2));
   }

   @Nonnull
   public Color blend(@Nonnull Color color, float factor) {
      this.red = blend(this.red, color.red, factor);
      this.green = blend(this.green, color.green, factor);
      this.blue = blend(this.blue, color.blue, factor);
      return this;
   }

   @Nonnull
   public Color mix(@Nonnull Color color) {
      return this.mix(color.red, color.green, color.blue);
   }

   @Nonnull
   public Color mix(float red, float green, float blue) {
      this.red = (this.red + red) / 2.0F;
      this.green = (this.green + green) / 2.0F;
      this.blue = (this.blue + blue) / 2.0F;
      return this;
   }

   @Nonnull
   public Color adjust(@Nonnull Vec3d adjust, @Nonnull Color target) {
      this.red = (float)((double)this.red + adjust.x);
      if (adjust.x < 0.0D && this.red < target.red || adjust.x > 0.0D && this.red > target.red) {
         this.red = target.red;
      }

      this.green = (float)((double)this.green + adjust.y);
      if (adjust.y < 0.0D && this.green < target.green || adjust.y > 0.0D && this.green > target.green) {
         this.green = target.green;
      }

      this.blue = (float)((double)this.blue + adjust.z);
      if (adjust.z < 0.0D && this.blue < target.blue || adjust.z > 0.0D && this.blue > target.blue) {
         this.blue = target.blue;
      }

      return this;
   }

   @Nonnull
   public Color luminance(float percent) {
      float r = Math.min(Math.max(0.0F, this.red + this.red * percent), 1.0F);
      float g = Math.min(Math.max(0.0F, this.green + this.green * percent), 1.0F);
      float b = Math.min(Math.max(0.0F, this.blue + this.blue * percent), 1.0F);
      return new Color(r, g, b);
   }

   public int rgb() {
      int iRed = (int)(this.red * 255.0F);
      int iGreen = (int)(this.green * 255.0F);
      int iBlue = (int)(this.blue * 255.0F);
      return iRed << 16 | iGreen << 8 | iBlue;
   }

   public int rgbWithAlpha(float alpha) {
      int iAlpha = (int)(alpha * 255.0F);
      return this.rgb() | iAlpha << 24;
   }

   public int hashCode() {
      int result = Float.hashCode(this.red);
      result = 31 * result + Float.hashCode(this.green);
      result = 31 * result + Float.hashCode(this.blue);
      return result;
   }

   public boolean equals(Object anObject) {
      if (anObject != null && anObject instanceof Color) {
         Color color = (Color)anObject;
         return this.red == color.red && this.green == color.green && this.blue == color.blue;
      } else {
         return false;
      }
   }

   @Nonnull
   public Color asImmutable() {
      return new Color.ImmutableColor(this);
   }

   @Nonnull
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[r:").append((int)(this.red * 255.0F));
      builder.append(",g:").append((int)(this.green * 255.0F));
      builder.append(",b:").append((int)(this.blue * 255.0F));
      builder.append(']');
      return builder.toString();
   }

   static {
      colorLookup.put(TextFormatting.BLACK, MC_BLACK);
      colorLookup.put(TextFormatting.DARK_BLUE, MC_DARKBLUE);
      colorLookup.put(TextFormatting.DARK_GREEN, MC_DARKGREEN);
      colorLookup.put(TextFormatting.DARK_AQUA, MC_DARKAQUA);
      colorLookup.put(TextFormatting.DARK_RED, MC_DARKRED);
      colorLookup.put(TextFormatting.DARK_PURPLE, MC_DARKPURPLE);
      colorLookup.put(TextFormatting.GOLD, MC_GOLD);
      colorLookup.put(TextFormatting.GRAY, MC_GRAY);
      colorLookup.put(TextFormatting.DARK_GRAY, MC_DARKGRAY);
      colorLookup.put(TextFormatting.BLUE, MC_BLUE);
      colorLookup.put(TextFormatting.GREEN, MC_GREEN);
      colorLookup.put(TextFormatting.AQUA, MC_AQUA);
      colorLookup.put(TextFormatting.RED, MC_RED);
      colorLookup.put(TextFormatting.LIGHT_PURPLE, MC_LIGHTPURPLE);
      colorLookup.put(TextFormatting.YELLOW, MC_YELLOW);
      colorLookup.put(TextFormatting.WHITE, MC_WHITE);
   }

   public static final class ImmutableColor extends Color {
      ImmutableColor(@Nonnull Color color) {
         super(color);
      }

      ImmutableColor(int red, int green, int blue) {
         super(red, green, blue);
      }

      public ImmutableColor(float red, float green, float blue) {
         super(red, green, blue);
      }

      public Color scale(float scaleFactor) {
         throw new UnsupportedOperationException();
      }

      public Color mix(float red, float green, float blue) {
         throw new UnsupportedOperationException();
      }

      public Color adjust(@Nonnull Vec3d adjust, @Nonnull Color target) {
         throw new UnsupportedOperationException();
      }
   }
}
