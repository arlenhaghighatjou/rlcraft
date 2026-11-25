package com.creativemd.creativecore.common.utils.mc;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.util.Color;

public class ColorUtils {
   public static final int WHITE = -1;
   public static final int RED = -65536;
   public static final int GREEN = -16711936;
   public static final int BLUE = -16776961;
   public static final int LIGHT_BLUE = -16740609;
   public static final int ORANGE = -23296;
   public static final int YELLOW = -256;
   public static final int CYAN = 16711681;
   public static final int MAGENTA = -65281;
   public static final int BLACK = -16777216;

   public ColorUtils() {
   }

   public static float getAlphaDecimal(int color) {
      return (float)(color >> 24 & 255) / 255.0F;
   }

   public static float getRedDecimal(int color) {
      return (float)(color >> 16 & 255) / 255.0F;
   }

   public static float getGreenDecimal(int color) {
      return (float)(color >> 8 & 255) / 255.0F;
   }

   public static float getBlueDecimal(int color) {
      return (float)(color & 255) / 255.0F;
   }

   public static int getAlpha(int color) {
      return color >> 24 & 255;
   }

   public static int getRed(int color) {
      return color >> 16 & 255;
   }

   public static int getGreen(int color) {
      return color >> 8 & 255;
   }

   public static int getBlue(int color) {
      return color & 255;
   }

   public static int setFullColor(int color) {
      return -16777216 | (getRed(color) & 255) << 16 | (getGreen(color) & 255) << 8 | getBlue(color) & 255;
   }

   public static int RGBAToInt(int red, int green, int blue, int alpha) {
      return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
   }

   public static int RGBAToInt(float red, float green, float blue, float alpha) {
      return RGBAToInt((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   public static int RGBAToInt(Color color) {
      return (color.getAlpha() & 255) << 24 | (color.getRed() & 255) << 16 | (color.getGreen() & 255) << 8 | color.getBlue() & 255;
   }

   public static Color IntToRGBA(int color) {
      int a = color >> 24 & 255;
      int r = color >> 16 & 255;
      int g = color >> 8 & 255;
      int b = color & 255;
      return new Color(r, g, b, a);
   }

   public static int RGBToInt(Vec3i color) {
      return -16777216 | (color.getX() & 255) << 16 | (color.getY() & 255) << 8 | color.getZ() & 255;
   }

   public static Vec3i IntToRGB(int color) {
      float r = (float)(color >> 16 & 255);
      float g = (float)(color >> 8 & 255);
      float b = (float)(color & 255);
      return new Vec3i((double)r, (double)g, (double)b);
   }

   public static Vec3d IntToVec(int color) {
      float r = (float)(color >> 16 & 255);
      float g = (float)(color >> 8 & 255);
      float b = (float)(color & 255);
      return new Vec3d((double)(r / 255.0F), (double)(g / 255.0F), (double)(b / 255.0F));
   }

   public static int VecToInt(Vec3d color) {
      return RGBToInt(new Vec3i(color.x * 255.0D, color.y * 255.0D, color.z * 255.0D));
   }

   public static Vec3i colorToVec(Color color) {
      return new Vec3i(color.getRed(), color.getGreen(), color.getBlue());
   }

   public static boolean isWhite(int color) {
      int r = color >> 16 & 255;
      int g = color >> 8 & 255;
      int b = color & 255;
      return r == 255 && g == 255 && b == 255;
   }

   public static boolean isTransparent(int color) {
      int a = color >> 24 & 255;
      return a < 255;
   }

   public static boolean isInvisible(int color) {
      int a = color >> 24 & 255;
      return a == 0;
   }

   public static int blend(int i1, int i2) {
      return blend(i1, i2, 0.5F);
   }

   public static int blend(int i1, int i2, float ratio) {
      if (ratio > 1.0F) {
         ratio = 1.0F;
      } else if (ratio < 0.0F) {
         ratio = 0.0F;
      }

      float iRatio = 1.0F - ratio;
      int a1 = i1 >> 24 & 255;
      int r1 = (i1 & 16711680) >> 16;
      int g1 = (i1 & '\uff00') >> 8;
      int b1 = i1 & 255;
      int a2 = i2 >> 24 & 255;
      int r2 = (i2 & 16711680) >> 16;
      int g2 = (i2 & '\uff00') >> 8;
      int b2 = i2 & 255;
      int a = (int)((float)a1 * iRatio + (float)a2 * ratio);
      int r = (int)((float)r1 * iRatio + (float)r2 * ratio);
      int g = (int)((float)g1 * iRatio + (float)g2 * ratio);
      int b = (int)((float)b1 * iRatio + (float)b2 * ratio);
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static enum ColorPart {
      RED {
         public int getColor(Color color) {
            return color.getRed();
         }

         public void setColor(Color color, int intenstiy) {
            color.setRed(intenstiy);
         }

         public int getBrightest() {
            return 16711680;
         }
      },
      GREEN {
         public int getColor(Color color) {
            return color.getGreen();
         }

         public void setColor(Color color, int intenstiy) {
            color.setGreen(intenstiy);
         }

         public int getBrightest() {
            return 65280;
         }
      },
      BLUE {
         public int getColor(Color color) {
            return color.getBlue();
         }

         public void setColor(Color color, int intenstiy) {
            color.setBlue(intenstiy);
         }

         public int getBrightest() {
            return 255;
         }
      },
      ALPHA {
         public int getColor(Color color) {
            return color.getAlpha();
         }

         public void setColor(Color color, int intenstiy) {
            color.setAlpha(intenstiy);
         }

         public int getBrightest() {
            return 255;
         }
      };

      private ColorPart() {
      }

      public abstract int getColor(Color var1);

      public abstract void setColor(Color var1, int var2);

      public abstract int getBrightest();
   }
}
