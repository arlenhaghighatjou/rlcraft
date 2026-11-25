package ivorius.ivtoolkit.rendering.textures;

import net.minecraft.util.math.MathHelper;

public class IvColorHelper {
   public IvColorHelper() {
   }

   public static int[] getARBGInts(int argb) {
      return new int[]{argb >>> 24, argb >>> 16 & 255, argb >>> 8 & 255, argb & 255};
   }

   public static int[] getARBGInts(float[] argb) {
      return new int[]{MathHelper.floor(argb[0] * 255.0F + 0.5F), MathHelper.floor(argb[1] * 255.0F + 0.5F), MathHelper.floor(argb[2] * 255.0F + 0.5F), MathHelper.floor(argb[3] * 255.0F + 0.5F)};
   }

   public static float[] getARBGFloats(int[] argb) {
      return new float[]{(float)argb[0] / 255.0F, (float)argb[1] / 255.0F, (float)argb[2] / 255.0F, (float)argb[3] / 255.0F};
   }

   public static float[] getARBGFloats(int argb) {
      int alpha = argb >>> 24;
      int red = argb >>> 16 & 255;
      int green = argb >>> 8 & 255;
      int blue = argb & 255;
      return new float[]{(float)alpha / 255.0F, (float)red / 255.0F, (float)green / 255.0F, (float)blue / 255.0F};
   }

   public static int getARBGInt(float[] argb) {
      int alpha = MathHelper.clamp(MathHelper.floor(argb[0] * 255.0F + 0.5F), 0, 255);
      int red = MathHelper.clamp(MathHelper.floor(argb[1] * 255.0F + 0.5F), 0, 255);
      int green = MathHelper.clamp(MathHelper.floor(argb[2] * 255.0F + 0.5F), 0, 255);
      int blue = MathHelper.clamp(MathHelper.floor(argb[3] * 255.0F + 0.5F), 0, 255);
      return alpha << 24 | red << 16 | green << 8 | blue;
   }
}
