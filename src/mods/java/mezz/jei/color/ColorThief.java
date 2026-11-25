package mezz.jei.color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import javax.annotation.Nullable;

public class ColorThief {
   private static final int DEFAULT_QUALITY = 2;
   private static final boolean DEFAULT_IGNORE_WHITE = false;

   public ColorThief() {
   }

   @Nullable
   public static int[] getColor(BufferedImage sourceImage) {
      int[][] palette = getPalette(sourceImage, 5);
      if (palette == null) {
         return null;
      } else {
         int[] dominantColor = palette[0];
         return dominantColor;
      }
   }

   @Nullable
   public static int[] getColor(BufferedImage sourceImage, int quality, boolean ignoreWhite) {
      int[][] palette = getPalette(sourceImage, 5, quality, ignoreWhite);
      if (palette == null) {
         return null;
      } else {
         int[] dominantColor = palette[0];
         return dominantColor;
      }
   }

   @Nullable
   public static int[][] getPalette(BufferedImage sourceImage, int colorCount) {
      MMCQ.CMap cmap = getColorMap(sourceImage, colorCount);
      return cmap == null ? (int[][])null : cmap.palette();
   }

   @Nullable
   public static int[][] getPalette(BufferedImage sourceImage, int colorCount, int quality, boolean ignoreWhite) {
      MMCQ.CMap cmap = getColorMap(sourceImage, colorCount, quality, ignoreWhite);
      return cmap == null ? (int[][])null : cmap.palette();
   }

   @Nullable
   public static MMCQ.CMap getColorMap(BufferedImage sourceImage, int colorCount) {
      return getColorMap(sourceImage, colorCount, 2, false);
   }

   @Nullable
   public static MMCQ.CMap getColorMap(BufferedImage sourceImage, int colorCount, int quality, boolean ignoreWhite) {
      int[][] pixelArray;
      switch(sourceImage.getType()) {
      case 5:
      case 6:
         pixelArray = getPixelsFast(sourceImage, quality, ignoreWhite);
         break;
      default:
         pixelArray = getPixelsSlow(sourceImage, quality, ignoreWhite);
      }

      MMCQ.CMap cmap = MMCQ.quantize(pixelArray, colorCount);
      return cmap;
   }

   private static int[][] getPixelsFast(BufferedImage sourceImage, int quality, boolean ignoreWhite) {
      DataBufferByte imageData = (DataBufferByte)sourceImage.getRaster().getDataBuffer();
      byte[] pixels = imageData.getData();
      int pixelCount = sourceImage.getWidth() * sourceImage.getHeight();
      int type = sourceImage.getType();
      int colorDepth;
      switch(type) {
      case 5:
         colorDepth = 3;
         break;
      case 6:
         colorDepth = 4;
         break;
      default:
         throw new IllegalArgumentException("Unhandled type: " + type);
      }

      int expectedDataLength = pixelCount * colorDepth;
      if (expectedDataLength != pixels.length) {
         throw new IllegalArgumentException("(expectedDataLength = " + expectedDataLength + ") != (pixels.length = " + pixels.length + ")");
      } else {
         int numRegardedPixels = (pixelCount + quality - 1) / quality;
         int numUsedPixels = 0;
         int[][] pixelArray = new int[numRegardedPixels][];
         switch(type) {
         case 5:
            for(int i = 0; i < pixelCount; i += quality) {
               int offset = i * 3;
               int b = pixels[offset] & 255;
               int g = pixels[offset + 1] & 255;
               int r = pixels[offset + 2] & 255;
               if (!ignoreWhite || r <= 250 || g <= 250 || b <= 250) {
                  pixelArray[numUsedPixels] = new int[]{r, g, b};
                  ++numUsedPixels;
               }
            }

            return (int[][])Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
         case 6:
            for(int i = 0; i < pixelCount; i += quality) {
               int offset = i * 4;
               int a = pixels[offset] & 255;
               int b = pixels[offset + 1] & 255;
               int g = pixels[offset + 2] & 255;
               int r = pixels[offset + 3] & 255;
               if (a >= 125 && (!ignoreWhite || r <= 250 || g <= 250 || b <= 250)) {
                  pixelArray[numUsedPixels] = new int[]{r, g, b};
                  ++numUsedPixels;
               }
            }

            return (int[][])Arrays.copyOfRange(pixelArray, 0, numUsedPixels);
         default:
            throw new IllegalArgumentException("Unhandled type: " + type);
         }
      }
   }

   private static int[][] getPixelsSlow(BufferedImage sourceImage, int quality, boolean ignoreWhite) {
      int width = sourceImage.getWidth();
      int height = sourceImage.getHeight();
      int pixelCount = width * height;
      int numRegardedPixels = (pixelCount + quality - 1) / quality;
      int numUsedPixels = 0;
      int[][] res = new int[numRegardedPixels][];

      for(int i = 0; i < pixelCount; i += quality) {
         int row = i / width;
         int col = i % width;
         int rgb = sourceImage.getRGB(col, row);
         int r = rgb >> 16 & 255;
         int g = rgb >> 8 & 255;
         int b = rgb & 255;
         if (!ignoreWhite || r <= 250 || r <= 250 || r <= 250) {
            res[numUsedPixels] = new int[]{r, g, b};
            ++numUsedPixels;
         }
      }

      return (int[][])Arrays.copyOfRange(res, 0, numUsedPixels);
   }
}
