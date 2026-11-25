package com.ferreusveritas.dynamictrees.client;

import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;

public class TextureUtils {
   public TextureUtils() {
   }

   public static int compose(int r, int g, int b, int a) {
      int rgb = (a << 8) + r;
      rgb = (rgb << 8) + g;
      rgb = (rgb << 8) + b;
      return rgb;
   }

   public static int compose(int[] c) {
      return c.length >= 4 ? compose(c[0], c[1], c[2], c[3]) : 0;
   }

   public static int[] decompose(int c) {
      return new int[]{red(c), green(c), blue(c), alpha(c)};
   }

   public static int alpha(int c) {
      return c >> 24 & 255;
   }

   public static int red(int c) {
      return c >> 16 & 255;
   }

   public static int green(int c) {
      return c >> 8 & 255;
   }

   public static int blue(int c) {
      return c & 255;
   }

   public static int avgColors(int[] pixels) {
      long rAccum = 0L;
      long gAccum = 0L;
      long bAccum = 0L;
      int count = 0;

      for(int i = 0; i < pixels.length; ++i) {
         int alpha = alpha(pixels[i]);
         if (alpha >= 128) {
            rAccum += (long)red(pixels[i]);
            gAccum += (long)green(pixels[i]);
            bAccum += (long)blue(pixels[i]);
            ++count;
         }
      }

      count = Math.max(1, count);
      int r = (int)(rAccum / (long)count);
      int g = (int)(gAccum / (long)count);
      int b = (int)(bAccum / (long)count);
      return compose(r, g, b, 255);
   }

   public static class PixelBuffer {
      public final int[] pixels;
      public final int w;
      public final int h;

      public PixelBuffer(int w, int h) {
         this.w = w;
         this.h = h;
         this.pixels = new int[w * h];
      }

      public PixelBuffer(EaglerTextureAtlasSprite sprite) {
         this.w = sprite.getIconWidth();
         this.h = sprite.getIconHeight();
         int[][] data = sprite.getFrameTextureData(0);
         this.pixels = data[0];
      }

      public PixelBuffer(EaglerTextureAtlasSprite sprite, boolean copy) {
         this.w = sprite.getIconWidth();
         this.h = sprite.getIconHeight();
         int[][] original = sprite.getFrameTextureData(0);
         this.pixels = Arrays.copyOf(original[0], original[0].length);
      }

      public PixelBuffer(TextureUtils.PixelBuffer other) {
         this.w = other.w;
         this.h = other.h;
         this.pixels = Arrays.copyOf(other.pixels, other.pixels.length);
      }

      public void apply(EaglerTextureAtlasSprite sprite) {
         if (this.w == sprite.getIconWidth() && this.h == sprite.getIconHeight()) {
            int[][] original = sprite.getFrameTextureData(0);
            int[][] data = new int[original.length][];
            data[0] = this.pixels;
            ArrayList<int[][]> ftd = new ArrayList();
            ftd.add(data);
            sprite.setFramesTextureData(ftd);
         }

      }

      public int calcPos(int offX, int offY) {
         return offY * this.w + offX;
      }

      public int getPixel(int offX, int offY) {
         return offX >= 0 && offX < this.w && offY >= 0 && offY < this.h ? this.pixels[this.calcPos(offX, offY)] : 0;
      }

      public void setPixel(int offX, int offY, int pixel) {
         if (offX >= 0 && offX < this.w && offY >= 0 && offY < this.h) {
            this.pixels[this.calcPos(offX, offY)] = pixel;
         }

      }

      public void blit(TextureUtils.PixelBuffer dst, int offX, int offY) {
         this.blit(dst, offX, offY, 0);
      }

      public void blit(TextureUtils.PixelBuffer dst, int offX, int offY, int rotCW90) {
         switch(rotCW90 & 3) {
         case 0:
            for(int y = 0; y < this.h; ++y) {
               for(int x = 0; x < this.w; ++x) {
                  dst.setPixel(x + offX, y + offY, this.getPixel(x, y));
               }
            }

            return;
         case 1:
            for(int y = 0; y < this.h; ++y) {
               for(int x = 0; x < this.w; ++x) {
                  int destX = this.h - y - 1;
                  dst.setPixel(destX + offX, x + offY, this.getPixel(x, y));
               }
            }

            return;
         case 2:
            for(int y = 0; y < this.h; ++y) {
               for(int x = 0; x < this.w; ++x) {
                  int destX = this.w - x - 1;
                  int destY = this.h - y - 1;
                  dst.setPixel(destX + offX, destY + offY, this.getPixel(x, y));
               }
            }

            return;
         case 3:
            for(int y = 0; y < this.h; ++y) {
               for(int x = 0; x < this.w; ++x) {
                  int destY = this.w - x - 1;
                  dst.setPixel(y + offX, destY + offY, this.getPixel(x, y));
               }
            }

            return;
         default:
         }
      }

      public int averageColor() {
         return TextureUtils.avgColors(this.pixels);
      }

      public void grayScale() {
         for(int i = 0; i < this.pixels.length; ++i) {
            int a = TextureUtils.alpha(this.pixels[i]);
            int r = TextureUtils.red(this.pixels[i]);
            int g = TextureUtils.green(this.pixels[i]);
            int b = TextureUtils.blue(this.pixels[i]);
            int gray = (r * 30 + g * 59 + b * 11) / 100;
            this.pixels[i] = TextureUtils.compose(gray, gray, gray, a);
         }

      }

      public void fill(int color) {
         for(int i = 0; i < this.pixels.length; ++i) {
            this.pixels[i] = color;
         }

      }
   }
}
