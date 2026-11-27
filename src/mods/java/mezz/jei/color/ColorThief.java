package mezz.jei.color;

import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import java.util.Arrays;
import javax.annotation.Nullable;

public class ColorThief {
   private static final int DEFAULT_QUALITY = 2;
   private static final boolean DEFAULT_IGNORE_WHITE = false;

   public ColorThief() {
   }

   @Nullable
   public static int[] getColor(ImageData sourceImage) {
      int[][] palette = getPalette(sourceImage, 5);
      if (palette == null) {
         return null;
      } else {
         int[] dominantColor = palette[0];
         return dominantColor;
      }
   }

   @Nullable
   public static int[] getColor(ImageData sourceImage, int quality, boolean ignoreWhite) {
      int[][] palette = getPalette(sourceImage, 5, quality, ignoreWhite);
      if (palette == null) {
         return null;
      } else {
         int[] dominantColor = palette[0];
         return dominantColor;
      }
   }

   @Nullable
   public static int[][] getPalette(ImageData sourceImage, int colorCount) {
      MMCQ.CMap cmap = getColorMap(sourceImage, colorCount);
      return cmap == null ? (int[][])null : cmap.palette();
   }

   @Nullable
   public static int[][] getPalette(ImageData sourceImage, int colorCount, int quality, boolean ignoreWhite) {
      MMCQ.CMap cmap = getColorMap(sourceImage, colorCount, quality, ignoreWhite);
      return cmap == null ? (int[][])null : cmap.palette();
   }

   @Nullable
   public static MMCQ.CMap getColorMap(ImageData sourceImage, int colorCount) {
      return getColorMap(sourceImage, colorCount, 2, false);
   }

   @Nullable
   public static MMCQ.CMap getColorMap(ImageData sourceImage, int colorCount, int quality, boolean ignoreWhite) {
      int[][] pixelArray = getPixels(sourceImage, quality, ignoreWhite);
      MMCQ.CMap cmap = MMCQ.quantize(pixelArray, colorCount);
      return cmap;
   }

   private static int[][] getPixels(ImageData sourceImage, int quality, boolean ignoreWhite) {
      int[] pixels = sourceImage.pixels;
      int pixelCount = sourceImage.width * sourceImage.height;
      int numRegardedPixels = (pixelCount + quality - 1) / quality;
      int numUsedPixels = 0;
      int[][] pixelArray = new int[numRegardedPixels][];

      for(int i = 0; i < pixelCount; i += quality) {
         int pixel = pixels[i];
         int a = pixel >>> 24;
         int b = pixel >> 16 & 255;
         int g = pixel >> 8 & 255;
         int r = pixel & 255;
         if (a >= 125 && (!ignoreWhite || r <= 250 || g <= 250 || b <= 250)) {
            pixelArray[numUsedPixels] = new int[]{r, g, b};
            ++numUsedPixels;
         }
      }

      return (int[][])Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
   }
}
