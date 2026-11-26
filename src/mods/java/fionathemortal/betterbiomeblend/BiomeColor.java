package fionathemortal.betterbiomeblend;

import java.util.Stack;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper.ColorResolver;

public final class BiomeColor {
   public static final Stack<ColorBlendCache> freeBlendCaches = new Stack();
   public static final byte[] neighbourOffsets = new byte[]{-1, -1, 0, -1, 1, -1, -1, 0, 0, 0, 1, 0, -1, 1, 0, 1, 1, 1};
   public static final byte[] neighbourRectParams = new byte[]{-1, -1, 0, 0, -16, -16, 0, 0, 0, -1, 0, 0, 0, -16, 0, 0, 0, -1, -1, 0, 16, -16, 0, 0, -1, 0, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 16, 0, 0, 0, -1, 0, 0, -1, -16, 16, 0, 0, 0, 0, 0, -1, 0, 16, 0, 0, 0, 0, -1, -1, 16, 16, 0, 0};

   public BiomeColor() {
   }

   public static int getNeighbourOffsetX(int chunkIndex) {
      int result = neighbourOffsets[2 * chunkIndex + 0];
      return result;
   }

   public static int getNeighbourOffsetZ(int chunkIndex) {
      int result = neighbourOffsets[2 * chunkIndex + 1];
      return result;
   }

   public static int getNeighbourRectMinX(int chunkIndex, int radius) {
      int offset = 8 * chunkIndex;
      int result = neighbourRectParams[offset + 0] & 16 - radius;
      return result;
   }

   public static int getNeighbourRectMinZ(int chunkIndex, int radius) {
      int offset = 8 * chunkIndex;
      int result = neighbourRectParams[offset + 1] & 16 - radius;
      return result;
   }

   public static int getNeighbourRectMaxX(int chunkIndex, int radius) {
      int offset = 8 * chunkIndex;
      int result = (neighbourRectParams[offset + 2] & radius - 16) + 16;
      return result;
   }

   public static int getNeighbourRectMaxZ(int chunkIndex, int radius) {
      int offset = 8 * chunkIndex;
      int result = (neighbourRectParams[offset + 3] & radius - 16) + 16;
      return result;
   }

   public static int getNeighbourRectBlendCacheMinX(int chunkIndex, int radius) {
      int offset = 8 * chunkIndex;
      int result = Math.max(neighbourRectParams[offset + 4] + radius, 0);
      return result;
   }

   public static int getNeighbourRectBlendCacheMinZ(int chunkIndex, int radius) {
      int offset = 8 * chunkIndex;
      int result = Math.max(neighbourRectParams[offset + 5] + radius, 0);
      return result;
   }

   public static void clearBlendCaches() {
      freeBlendCaches.clear();
   }

   public static ColorBlendCache acquireBlendCache(int blendRadius) {
      ColorBlendCache result = null;

      while(!freeBlendCaches.empty()) {
         ColorBlendCache cache = (ColorBlendCache)freeBlendCaches.pop();
         if (cache.blendRadius == blendRadius) {
            result = cache;
            break;
         }
      }

      if (result == null) {
         result = new ColorBlendCache(blendRadius);
      }

      return result;
   }

   public static void releaseBlendCache(ColorBlendCache cache) {
      int blendRadius = BetterBiomeBlendConfig.blendRadius;
      if (cache.blendRadius == blendRadius) {
         freeBlendCaches.push(cache);
      }

   }

   public static LocalColorChunk getThreadLocalGrassChunkWrapper(IBlockAccess blockAccess) {
      LocalColorChunk threadLocal = null;
      World world = getWorldFromBlockAccess(blockAccess);
      if (world instanceof ColorChunkCacheProvider) {
         threadLocal = ((ColorChunkCacheProvider)world).getTreadLocalGrassChunk();
      } else {
         threadLocal = StaticCompatibilityCache.getThreadLocalGrassChunkWrapper();
      }

      return threadLocal;
   }

   public static LocalColorChunk getThreadLocalWaterChunkWrapper(IBlockAccess blockAccess) {
      LocalColorChunk threadLocal = null;
      World world = getWorldFromBlockAccess(blockAccess);
      if (world instanceof ColorChunkCacheProvider) {
         threadLocal = ((ColorChunkCacheProvider)world).getTreadLocalWaterChunk();
      } else {
         threadLocal = StaticCompatibilityCache.getThreadLocalWaterChunkWrapper();
      }

      return threadLocal;
   }

   public static LocalColorChunk getThreadLocalFoliageChunkWrapper(IBlockAccess blockAccess) {
      LocalColorChunk threadLocal = null;
      World world = getWorldFromBlockAccess(blockAccess);
      if (world instanceof ColorChunkCacheProvider) {
         threadLocal = ((ColorChunkCacheProvider)world).getTreadLocalFoliageChunk();
      } else {
         threadLocal = StaticCompatibilityCache.getThreadLocalFoliageChunkWrapper();
      }

      return threadLocal;
   }

   public static LocalColorChunk getThreadLocalGenericChunkWrapper(IBlockAccess blockAccess) {
      LocalColorChunk threadLocal = null;
      World world = getWorldFromBlockAccess(blockAccess);
      if (world instanceof ColorChunkCacheProvider) {
         threadLocal = ((ColorChunkCacheProvider)world).getTreadLocalGenericChunk();
      } else {
         threadLocal = StaticCompatibilityCache.getThreadLocalGenericChunkWrapper();
      }

      return threadLocal;
   }

   public static ColorChunk getThreadLocalChunk(LocalColorChunk threadLocal, int chunkX, int chunkZ, int colorType) {
      ColorChunk result = null;
      ColorChunk local = (ColorChunk)threadLocal.get();
      long key = ColorChunkCache.getChunkKey(chunkX, chunkZ, colorType);
      if (local.key == key) {
         result = local;
      }

      return result;
   }

   public static void setThreadLocalChunk(LocalColorChunk threadLocal, ColorChunk chunk, ColorChunkCache cache) {
      ColorChunk local = (ColorChunk)threadLocal.get();
      cache.releaseChunk(local);
      threadLocal.set(chunk);
   }

   public static void gatherRawColorsForChunk(IBlockAccess blockAccess, byte[] result, int chunkX, int chunkZ, ColorResolver colorResolver) {
      MutableBlockPos blockPos = new MutableBlockPos();
      int blockX = 16 * chunkX;
      int blockZ = 16 * chunkZ;
      int dstIndex = 0;

      for(int z = 0; z < 16; ++z) {
         for(int x = 0; x < 16; ++x) {
            blockPos.setPos(blockX + x, 0, blockZ + z);
            int color = colorResolver.getColorAtPos(blockAccess.getBiome(blockPos), blockPos);
            int colorR = Color.RGBAGetR(color);
            int colorG = Color.RGBAGetG(color);
            int colorB = Color.RGBAGetB(color);
            result[3 * dstIndex + 0] = (byte)colorR;
            result[3 * dstIndex + 1] = (byte)colorG;
            result[3 * dstIndex + 2] = (byte)colorB;
            ++dstIndex;
         }
      }

   }

   public static void gatherRawColorsToBlendCache(IBlockAccess blockAccess, int chunkX, int chunkZ, int blendRadius, byte[] result, int chunkIndex, ColorResolver colorResolver) {
      MutableBlockPos blockPos = new MutableBlockPos();
      int blockX = chunkX << 4;
      int blockZ = chunkZ << 4;
      int srcMinX = getNeighbourRectMinX(chunkIndex, blendRadius);
      int srcMinZ = getNeighbourRectMinZ(chunkIndex, blendRadius);
      int srcMaxX = getNeighbourRectMaxX(chunkIndex, blendRadius);
      int srcMaxZ = getNeighbourRectMaxZ(chunkIndex, blendRadius);
      int dstMinX = getNeighbourRectBlendCacheMinX(chunkIndex, blendRadius);
      int dstMinZ = getNeighbourRectBlendCacheMinZ(chunkIndex, blendRadius);
      int dstDim = 16 + 2 * blendRadius;
      int dstLine = 3 * (dstMinX + dstMinZ * dstDim);

      for(int z = srcMinZ; z < srcMaxZ; ++z) {
         int dstIndex = dstLine;

         for(int x = srcMinX; x < srcMaxX; ++x) {
            blockPos.setPos(blockX + x, 0, blockZ + z);
            int color = colorResolver.getColorAtPos(blockAccess.getBiome(blockPos), blockPos);
            int colorR = Color.RGBAGetR(color);
            int colorG = Color.RGBAGetG(color);
            int colorB = Color.RGBAGetB(color);
            result[dstIndex + 0] = (byte)colorR;
            result[dstIndex + 1] = (byte)colorG;
            result[dstIndex + 2] = (byte)colorB;
            dstIndex += 3;
         }

         dstLine += 3 * dstDim;
      }

   }

   public static void gatherRawColorsToBlendCache(IBlockAccess blockAccess, int chunkX, int chunkZ, int blendRadius, byte[] result, ColorResolver colorResolver) {
      for(int chunkIndex = 0; chunkIndex < 9; ++chunkIndex) {
         int offsetX = getNeighbourOffsetX(chunkIndex);
         int offsetZ = getNeighbourOffsetZ(chunkIndex);
         int rawChunkX = chunkX + offsetX;
         int rawChunkZ = chunkZ + offsetZ;
         gatherRawColorsToBlendCache(blockAccess, rawChunkX, rawChunkZ, blendRadius, result, chunkIndex, colorResolver);
      }

   }

   public static void blendCachedColorsForChunk(IBlockAccess blockAccess, byte[] result, ColorBlendCache blendCache) {
      float[] R = blendCache.R;
      float[] G = blendCache.G;
      float[] B = blendCache.B;
      int blendRadius = blendCache.blendRadius;
      int blendDim = 2 * blendRadius + 1;
      int blendCacheDim = 16 + 2 * blendRadius;
      int blendCount = blendDim * blendDim;

      for(int x = 0; x < blendCacheDim; ++x) {
         R[x] = Color.sRGBByteToLinearFloat(255 & blendCache.color[3 * x + 0]);
         G[x] = Color.sRGBByteToLinearFloat(255 & blendCache.color[3 * x + 1]);
         B[x] = Color.sRGBByteToLinearFloat(255 & blendCache.color[3 * x + 2]);
      }

      for(int z = 1; z < blendDim; ++z) {
         for(int x = 0; x < blendCacheDim; ++x) {
            R[x] += Color.sRGBByteToLinearFloat(255 & blendCache.color[3 * (blendCacheDim * z + x) + 0]);
            G[x] += Color.sRGBByteToLinearFloat(255 & blendCache.color[3 * (blendCacheDim * z + x) + 1]);
            B[x] += Color.sRGBByteToLinearFloat(255 & blendCache.color[3 * (blendCacheDim * z + x) + 2]);
         }
      }

      for(int z = 0; z < 16; ++z) {
         float accumulatedR = 0.0F;
         float accumulatedG = 0.0F;
         float accumulatedB = 0.0F;

         for(int x = 0; x < blendDim; ++x) {
            accumulatedR += R[x];
            accumulatedG += G[x];
            accumulatedB += B[x];
         }

         for(int x = 0; x < 16; ++x) {
            float colorR = accumulatedR / (float)blendCount;
            float colorG = accumulatedG / (float)blendCount;
            float colorB = accumulatedB / (float)blendCount;
            result[3 * (16 * z + x) + 0] = Color.linearFloatTosRGBByte(colorR);
            result[3 * (16 * z + x) + 1] = Color.linearFloatTosRGBByte(colorG);
            result[3 * (16 * z + x) + 2] = Color.linearFloatTosRGBByte(colorB);
            if (x < 15) {
               accumulatedR += R[x + blendDim] - R[x];
               accumulatedG += G[x + blendDim] - G[x];
               accumulatedB += B[x + blendDim] - B[x];
            }
         }

         if (z < 15) {
            for(int x = 0; x < blendCacheDim; ++x) {
               int index1 = 3 * (blendCacheDim * z + x);
               int index2 = 3 * (blendCacheDim * (z + blendDim) + x);
               R[x] += Color.sRGBByteToLinearFloat(255 & blendCache.color[index2 + 0]) - Color.sRGBByteToLinearFloat(255 & blendCache.color[index1 + 0]);
               G[x] += Color.sRGBByteToLinearFloat(255 & blendCache.color[index2 + 1]) - Color.sRGBByteToLinearFloat(255 & blendCache.color[index1 + 1]);
               B[x] += Color.sRGBByteToLinearFloat(255 & blendCache.color[index2 + 2]) - Color.sRGBByteToLinearFloat(255 & blendCache.color[index1 + 2]);
            }
         }
      }

   }

   public static void generateBlendedColorChunk(IBlockAccess blockAccess, int chunkX, int chunkZ, byte[] result, int colorType, ColorResolver colorResolver) {
      int blendRadius = BetterBiomeBlendConfig.blendRadius;
      if (blendRadius > 0 && blendRadius <= 14) {
         ColorBlendCache blendCache = acquireBlendCache(blendRadius);
         gatherRawColorsToBlendCache(blockAccess, chunkX, chunkZ, blendCache.blendRadius, blendCache.color, colorResolver);
         blendCachedColorsForChunk(blockAccess, result, blendCache);
         releaseBlendCache(blendCache);
      } else {
         gatherRawColorsForChunk(blockAccess, result, chunkX, chunkZ, colorResolver);
      }

   }

   public static World getWorldFromBlockAccess(IBlockAccess blockAccess) {
      World result = null;
      if (blockAccess instanceof World) {
         result = (World)blockAccess;
      } else if (blockAccess instanceof ChunkCache) {
         result = ((ChunkCache)blockAccess).world;
      }

      return result;
   }

   public static ColorChunkCache getColorChunkCacheForWorld(World world) {
      ColorChunkCache cache = null;
      if (world instanceof ColorChunkCacheProvider) {
         ColorChunkCacheProvider cacheProvider = (ColorChunkCacheProvider)world;
         cache = cacheProvider.getColorChunkCache();
      } else {
         cache = StaticCompatibilityCache.getColorChunkCache();
      }

      return cache;
   }

   public static ColorChunkCache getColorChunkCacheForIBlockAccess(IBlockAccess blockAccess) {
      World world = getWorldFromBlockAccess(blockAccess);
      ColorChunkCache cache = getColorChunkCacheForWorld(world);
      return cache;
   }

   public static ColorChunk getBlendedColorChunk(ColorChunkCache cache, IBlockAccess blockAccess, int colorID, int chunkX, int chunkZ, ColorResolver colorResolver) {
      ColorChunk chunk = cache.getChunk(chunkX, chunkZ, colorID);
      if (chunk == null) {
         chunk = cache.newChunk(chunkX, chunkZ, colorID);
         generateBlendedColorChunk(blockAccess, chunkX, chunkZ, chunk.data, colorID, colorResolver);
         cache.putChunk(chunk);
      }

      return chunk;
   }

   public static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, ColorResolver colorResolver) {
      int x = pos.getX();
      int z = pos.getZ();
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      int colorResolverID = ColorResolverCompatibility.getColorResolverID(colorResolver);
      LocalColorChunk threadLocal = getThreadLocalGenericChunkWrapper(blockAccess);
      ColorChunk chunk = getThreadLocalChunk(threadLocal, chunkX, chunkZ, colorResolverID);
      if (chunk == null) {
         ColorChunkCache cache = getColorChunkCacheForIBlockAccess(blockAccess);
         chunk = getBlendedColorChunk(cache, blockAccess, colorResolverID, chunkX, chunkZ, colorResolver);
         setThreadLocalChunk(threadLocal, chunk, cache);
      }

      int result = chunk.getColor(x, z);
      return result;
   }

   public static int getGrassColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
      int x = pos.getX();
      int z = pos.getZ();
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      LocalColorChunk threadLocal = getThreadLocalGrassChunkWrapper(blockAccess);
      ColorChunk chunk = getThreadLocalChunk(threadLocal, chunkX, chunkZ, 0);
      if (chunk == null) {
         ColorChunkCache cache = getColorChunkCacheForIBlockAccess(blockAccess);
         chunk = getBlendedColorChunk(cache, blockAccess, 0, chunkX, chunkZ, BiomeColorHelper.GRASS_COLOR);
         setThreadLocalChunk(threadLocal, chunk, cache);
      }

      int result = chunk.getColor(x, z);
      return result;
   }

   public static int getFoliageColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
      int x = pos.getX();
      int z = pos.getZ();
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      LocalColorChunk threadLocal = getThreadLocalFoliageChunkWrapper(blockAccess);
      ColorChunk chunk = getThreadLocalChunk(threadLocal, chunkX, chunkZ, 2);
      if (chunk == null) {
         ColorChunkCache cache = getColorChunkCacheForIBlockAccess(blockAccess);
         chunk = getBlendedColorChunk(cache, blockAccess, 2, chunkX, chunkZ, BiomeColorHelper.FOLIAGE_COLOR);
         setThreadLocalChunk(threadLocal, chunk, cache);
      }

      int result = chunk.getColor(x, z);
      return result;
   }

   public static int getWaterColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
      int x = pos.getX();
      int z = pos.getZ();
      int chunkX = x >> 4;
      int chunkZ = z >> 4;
      LocalColorChunk threadLocal = getThreadLocalWaterChunkWrapper(blockAccess);
      ColorChunk chunk = getThreadLocalChunk(threadLocal, chunkX, chunkZ, 1);
      if (chunk == null) {
         ColorChunkCache cache = getColorChunkCacheForIBlockAccess(blockAccess);
         chunk = getBlendedColorChunk(cache, blockAccess, 1, chunkX, chunkZ, BiomeColorHelper.WATER_COLOR);
         setThreadLocalChunk(threadLocal, chunk, cache);
      }

      int result = chunk.getColor(x, z);
      return result;
   }
}
