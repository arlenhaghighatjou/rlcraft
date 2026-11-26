package hunternif.mc.atlas.util;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.SubTile;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.TileRenderIterator;
import hunternif.mc.atlas.client.gui.ExportUpdateListener;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExportImageUtil {
   public static final int TILE_SIZE = 16;
   public static final int MARKER_SIZE = 32;
   public static boolean isExporting = false;
   private static final int BG_TILE_SIZE = 22;

   public ExportImageUtil() {
   }

   private static ExportUpdateListener getListener() {
      return ExportUpdateListener.INSTANCE;
   }

   public static void exportPngImage(DimensionData biomeData, DimensionMarkersData localMarkers, String fileName, boolean showMarkers) throws AtlasTooLargeException {
      getListener().setHeaderString("gui.antiqueatlas.export.setup");
      int minX = (biomeData.getScope().minX - 1) * 16;
      int minY = (biomeData.getScope().minY - 1) * 16;
      int outWidth = (biomeData.getScope().maxX + 2) * 16 - minX;
      int outHeight = (biomeData.getScope().maxY + 2) * 16 - minY;
      if (outWidth < SettingsConfig.performance.exportSizeLimit && outHeight < SettingsConfig.performance.exportSizeLimit) {
         Log.info("Image size: %dx%d", outWidth, outHeight);
         getListener().setStatusString("gui.antiqueatlas.export.makingbuffer", outWidth, outHeight);
         ImageData outImage = new ImageData(outWidth, outHeight, true);
         ExportImageUtil.Canvas graphics = new ExportImageUtil.Canvas(outImage, 0);
         int scale = 2;
         int bgTilesX = Math.round((float)outWidth / 22.0F / (float)scale);
         int bgTilesY = Math.round((float)outHeight / 22.0F / (float)scale);
         getListener().setStatusString("gui.antiqueatlas.export.loadingtextures");
         getListener().setProgressMax(-1);
         ImageData bg = null;
         Map<ResourceLocation, ImageData> textureImageMap = new HashMap();

         try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(Textures.EXPORTED_BG).getInputStream();
            bg = TextureUtil.readBufferedImage(is);
            is.close();
            List<ResourceLocation> allTextures = new ArrayList(64);
            allTextures.addAll(BiomeTextureMap.instance().getAllTextures());
            if (showMarkers) {
               for(MarkerType type : MarkerRegistry.getValues()) {
                  allTextures.addAll(Arrays.asList(type.getAllIcons()));
               }
            }

            for(ResourceLocation texture : allTextures) {
               try {
                  is = Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream();
                  ImageData tileImage = TextureUtil.readBufferedImage(is);
                  is.close();
                  textureImageMap.put(texture, tileImage);
               } catch (FileNotFoundException var21) {
                  Log.warn("Texture %s not found!", texture.toString());
               }
            }
         } catch (IOException var22) {
            var22.printStackTrace();
         }

         getListener().setHeaderString("gui.antiqueatlas.export.rendering");
         drawMapToGraphics(graphics, bgTilesX, bgTilesY, outWidth, outHeight, biomeData, textureImageMap, localMarkers, showMarkers, minX, minY, scale, bg);

         try {
            getListener().setHeaderString("");
            getListener().setStatusString("gui.antiqueatlas.export.writing");
            PngWriter writer = new PngWriter(outWidth, outHeight);

            for(int y = 0; y < outHeight; ++y) {
               writer.writeRow(outImage.pixels, y * outWidth);
            }

            EagRuntime.downloadFileWithName(fileName, writer.finish());
            Log.info("Done writing image");
         } catch (IOException var20) {
            var20.printStackTrace();
         }

      } else {
         throw new AtlasTooLargeException();
      }
   }

   public static void exportPngImageTooLarge(DimensionData biomeData, DimensionMarkersData localMarkers, String fileName, boolean showMarkers) {
      getListener().setHeaderString("");
      int minX = (biomeData.getScope().minX - 1) * 16;
      int minY = (biomeData.getScope().minY - 1) * 16;
      int outWidth = (biomeData.getScope().maxX + 2) * 16 - minX;
      int outHeight = (biomeData.getScope().maxY + 2) * 16 - minY;
      Log.info("Image size: %dx%d", outWidth, outHeight);
      int scale = 2;
      int bgTilesX = Math.round((float)outWidth / 22.0F / 2.0F);
      int bgTilesY = Math.round((float)outHeight / 22.0F / 2.0F);
      getListener().setStatusString("gui.antiqueatlas.export.loadingtextures");
      getListener().setProgressMax(-1);
      ImageData bg = null;
      Map<ResourceLocation, ImageData> textureImageMap = new HashMap();

      try {
         InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(Textures.EXPORTED_BG).getInputStream();
         bg = TextureUtil.readBufferedImage(is);
         is.close();
         List<ResourceLocation> allTextures = new ArrayList(64);
         allTextures.addAll(BiomeTextureMap.instance().getAllTextures());
         if (showMarkers) {
            for(MarkerType type : MarkerRegistry.getValues()) {
               allTextures.addAll(Arrays.asList(type.getAllIcons()));
            }
         }

         for(ResourceLocation texture : allTextures) {
            try {
               is = Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream();
               ImageData tileImage = TextureUtil.readBufferedImage(is);
               is.close();
               textureImageMap.put(texture, tileImage);
            } catch (FileNotFoundException var26) {
               Log.warn("Texture %s not found!", texture.toString());
            }
         }
      } catch (IOException var27) {
         var27.printStackTrace();
      }

      long availableMem = getAvailableMemory();
      long usableMem = (long)((double)availableMem * 0.8D);
      int pixelSize = 4;
      int sliceHeight = 16;

      for(int i = bgTilesY; i > 0; --i) {
         long usedMem = (long)(i * 16) * (long)outWidth * (long)pixelSize;
         if (usedMem <= usableMem) {
            sliceHeight = i * 16;
            break;
         }

         Log.info("%d tiles tall is too big, %d > %d", i, usedMem, usableMem);
      }

      int slices = (int)Math.ceil((double)((float)outHeight / (float)sliceHeight));
      ImageData scanBuffer = new ImageData(outWidth, sliceHeight, true);
      getListener().setProgressMax(slices);

      try {
         getListener().setHeaderString("gui.antiqueatlas.export.renderstripe", Integer.valueOf(1), slices);
         PngWriter writer = new PngWriter(outWidth, outHeight);

         for(int slice = 0; slice < slices; ++slice) {
            int offsetY = slice * sliceHeight;
            Arrays.fill(scanBuffer.pixels, 0);
            getListener().setProgress(slice);
            getListener().setHeaderString("gui.antiqueatlas.export.renderstripe", slice + 1, slices);
            drawMapToGraphics(new ExportImageUtil.Canvas(scanBuffer, offsetY), bgTilesX, bgTilesY, outWidth, outHeight, biomeData, textureImageMap, localMarkers, showMarkers, minX, minY, 2, bg);
            getListener().setStatusString("gui.antiqueatlas.export.writestripe");
            int rows = Math.min(sliceHeight, outHeight - offsetY);
            getListener().setProgressMax(rows);

            for(int y = 0; y < rows; ++y) {
               writer.writeRow(scanBuffer.pixels, y * outWidth);
               getListener().setProgress(y);
            }
         }

         EagRuntime.downloadFileWithName(fileName, writer.finish());
         Log.info("Done writing image");
      } catch (IOException var25) {
         var25.printStackTrace();
      }

   }

   private static long getAvailableMemory() {
      long totalMemory = EagRuntime.totalMemory();
      long freeMemory = EagRuntime.freeMemory();
      long maxMemory = EagRuntime.maxMemory();
      long usedMemory = totalMemory - freeMemory;
      return maxMemory - usedMemory;
   }

   private static void drawMapToGraphics(ExportImageUtil.Canvas graphics, int bgTilesX, int bgTilesY, int outWidth, int outHeight, DimensionData biomeData, Map<ResourceLocation, ImageData> textureImageMap, DimensionMarkersData localMarkers, boolean showMarkers, int minX, int minY, int scale, ImageData bg) {
      getListener().setStatusString("gui.antiqueatlas.export.rendering.background");
      getListener().setProgressMax(bgTilesX * bgTilesY);
      graphics.drawImage(bg, 0, 0, 22 * scale, 22 * scale, 0, 0, 22, 22);
      getListener().addProgress(1);

      for(int x = 1; x < bgTilesX; ++x) {
         graphics.drawImage(bg, x * 22 * scale, 0, (x + 1) * 22 * scale, 22 * scale, 22, 0, 44, 22);
         getListener().addProgress(1);
      }

      for(int y = 1; y < bgTilesY; ++y) {
         graphics.drawImage(bg, 0, y * 22 * scale, 22 * scale, (y + 1) * 22 * scale, 0, 22, 22, 44);
         getListener().addProgress(1);
      }

      for(int x = 1; x < bgTilesX; ++x) {
         for(int y = 1; y < bgTilesY; ++y) {
            graphics.drawImage(bg, x * 22 * scale, y * 22 * scale, (x + 1) * 22 * scale, (y + 1) * 22 * scale, 22, 22, 44, 44);
            getListener().addProgress(1);
         }
      }

      graphics.drawImage(bg, outWidth - 22 * scale, 0, outWidth, 22 * scale, 44, 0, 66, 22);
      getListener().addProgress(1);

      for(int y = 1; y < bgTilesY; ++y) {
         graphics.drawImage(bg, outWidth - 22 * scale, y * 22 * scale, outWidth, (y + 1) * 22 * scale, 44, 22, 66, 44);
         getListener().addProgress(1);
      }

      graphics.drawImage(bg, 0, outHeight - 22 * scale, 22 * scale, outHeight, 0, 44, 22, 66);
      getListener().addProgress(1);

      for(int x = 1; x < bgTilesX; ++x) {
         graphics.drawImage(bg, x * 22 * scale, outHeight - 22 * scale, (x + 1) * 22 * scale, outHeight, 22, 44, 44, 66);
         getListener().addProgress(1);
      }

      graphics.drawImage(bg, outWidth - 22 * scale, outHeight - 22 * scale, outWidth, outHeight, 44, 44, 66, 66);
      getListener().addProgress(1);
      Rect scope = biomeData.getScope();
      getListener().setStatusString("gui.antiqueatlas.export.rendering.map");
      getListener().setProgressMax(scope.getHeight() * scope.getWidth());
      TileRenderIterator iter = new TileRenderIterator(biomeData);

      while(iter.hasNext()) {
         for(SubTile subtile : iter.next()) {
            if (subtile != null && subtile.tile != null) {
               ResourceLocation texture = BiomeTextureMap.instance().getTexture(subtile.tile);
               ImageData tileImage = (ImageData)textureImageMap.get(texture);
               if (tileImage != null) {
                  graphics.drawImage(tileImage, 16 + subtile.x * 16 / 2, 16 + subtile.y * 16 / 2, 16 + (subtile.x + 1) * 16 / 2, 16 + (subtile.y + 1) * 16 / 2, subtile.getTextureU() * 16 / 2, subtile.getTextureV() * 16 / 2, (subtile.getTextureU() + 1) * 16 / 2, (subtile.getTextureV() + 1) * 16 / 2);
               }
            }
         }

         getListener().addProgress(1);
      }

      getListener().setStatusString("gui.antiqueatlas.export.rendering.markers");
      getListener().setProgressMax(-1);
      List<Marker> markers = new ArrayList();

      for(int x = biomeData.getScope().minX / 8; x <= biomeData.getScope().maxX / 8; ++x) {
         for(int z = biomeData.getScope().minY / 8; z <= biomeData.getScope().maxY / 8; ++z) {
            markers.clear();
            if (localMarkers != null) {
               List<Marker> localMarkersAt = localMarkers.getMarkersAtChunk(x, z);
               if (localMarkersAt != null) {
                  markers.addAll(localMarkersAt);
               }
            }

            for(Marker marker : markers) {
               MarkerType type = MarkerRegistry.find(marker.getType());
               if (type == null) {
                  Log.warn("Could not find marker data for type: %s\n", marker.getType());
               } else if ((marker.isVisibleAhead() || biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) && !type.shouldHide(!showMarkers, 0)) {
                  type.calculateMip(1.0D, 1.0D, 1.0D);
                  MarkerRenderInfo info = type.getRenderInfo(1.0D, 1.0D, 1.0D);
                  type.resetMip();
                  ResourceLocation texture = info.tex;
                  ImageData markerImage = (ImageData)textureImageMap.get(texture);
                  if (markerImage != null) {
                     int markerX = marker.getX() - minX;
                     int markerY = marker.getZ() - minY;
                     graphics.drawImage(markerImage, (int)((double)markerX + info.x), (int)((double)markerY + info.y), info.width, info.height);
                  }
               }
            }
         }
      }

   }

   private static class Canvas {
      private final ImageData image;
      private final int offsetY;

      Canvas(ImageData image, int offsetY) {
         this.image = image;
         this.offsetY = offsetY;
      }

      void drawImage(ImageData src, int x, int y, int width, int height) {
         this.drawImage(src, x, y, x + width, y + height, 0, 0, src.width, src.height);
      }

      void drawImage(ImageData src, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
         int dw = dx2 - dx1;
         int dh = dy2 - dy1;
         if (dw > 0 && dh > 0) {
            int sw = sx2 - sx1;
            int sh = sy2 - sy1;
            ImageData dst = this.image;

            for(int dy = 0; dy < dh; ++dy) {
               int ty = dy1 + dy - this.offsetY;
               if (ty >= 0 && ty < dst.height) {
                  int sy = sy1 + (int)(((double)dy + 0.5D) * (double)sh / (double)dh);
                  if (sy >= 0 && sy < src.height) {
                     for(int dx = 0; dx < dw; ++dx) {
                        int tx = dx1 + dx;
                        if (tx >= 0 && tx < dst.width) {
                           int sx = sx1 + (int)(((double)dx + 0.5D) * (double)sw / (double)dw);
                           if (sx >= 0 && sx < src.width) {
                              int index = ty * dst.width + tx;
                              dst.pixels[index] = blend(src.pixels[sy * src.width + sx], dst.pixels[index]);
                           }
                        }
                     }
                  }
               }
            }

         }
      }

      private static int blend(int src, int dst) {
         int sa = src >>> 24;
         if (sa == 255) {
            return src;
         } else if (sa == 0) {
            return dst;
         } else {
            int da = dst >>> 24;
            int inv = da * (255 - sa) / 255;
            int a = sa + inv;
            int r = (((src >>> 16) & 255) * sa + ((dst >>> 16) & 255) * inv) / a;
            int g = (((src >>> 8) & 255) * sa + ((dst >>> 8) & 255) * inv) / a;
            int b = ((src & 255) * sa + (dst & 255) * inv) / a;
            return a << 24 | r << 16 | g << 8 | b;
         }
      }
   }
}
