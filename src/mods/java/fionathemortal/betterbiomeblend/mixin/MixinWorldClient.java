package fionathemortal.betterbiomeblend.mixin;

import fionathemortal.betterbiomeblend.ColorChunk;
import fionathemortal.betterbiomeblend.ColorChunkCache;
import fionathemortal.betterbiomeblend.ColorChunkCacheProvider;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({WorldClient.class})
public abstract class MixinWorldClient implements ColorChunkCacheProvider {
   @Unique
   public final ThreadLocal<ColorChunk> betterBiomeBlend$threadLocalGrassChunk = ThreadLocal.withInitial(() -> {
      ColorChunk chunk = new ColorChunk();
      chunk.acquire();
      return chunk;
   });
   @Unique
   public final ThreadLocal<ColorChunk> betterBiomeBlend$threadLocalWaterChunk = ThreadLocal.withInitial(() -> {
      ColorChunk chunk = new ColorChunk();
      chunk.acquire();
      return chunk;
   });
   @Unique
   public final ThreadLocal<ColorChunk> betterBiomeBlend$threadLocalFoliageChunk = ThreadLocal.withInitial(() -> {
      ColorChunk chunk = new ColorChunk();
      chunk.acquire();
      return chunk;
   });
   @Unique
   public final ThreadLocal<ColorChunk> betterBiomeBlend$threadLocalGenericChunk = ThreadLocal.withInitial(() -> {
      ColorChunk chunk = new ColorChunk();
      chunk.acquire();
      return chunk;
   });
   @Unique
   public final ColorChunkCache betterBiomeBlend$colorChunkCache = new ColorChunkCache(2048);

   public MixinWorldClient() {
   }

   public ColorChunkCache getColorChunkCache() {
      return this.betterBiomeBlend$colorChunkCache;
   }

   public ThreadLocal<ColorChunk> getTreadLocalGrassChunk() {
      return this.betterBiomeBlend$threadLocalGrassChunk;
   }

   public ThreadLocal<ColorChunk> getTreadLocalWaterChunk() {
      return this.betterBiomeBlend$threadLocalWaterChunk;
   }

   public ThreadLocal<ColorChunk> getTreadLocalFoliageChunk() {
      return this.betterBiomeBlend$threadLocalFoliageChunk;
   }

   public ThreadLocal<ColorChunk> getTreadLocalGenericChunk() {
      return this.betterBiomeBlend$threadLocalGenericChunk;
   }
}
