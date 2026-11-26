package fionathemortal.betterbiomeblend;

public final class StaticCompatibilityCache {
   private static final LocalColorChunk threadLocalGrassChunk = new LocalColorChunk();
   private static final LocalColorChunk threadLocalWaterChunk = new LocalColorChunk();
   private static final LocalColorChunk threadLocalFoliageChunk = new LocalColorChunk();
   private static final LocalColorChunk threadLocalGenericChunk = new LocalColorChunk();
   private static ColorChunkCache colorChunkCache = null;

   public StaticCompatibilityCache() {
   }

   public static ColorChunkCache getColorChunkCache() {
      if (colorChunkCache == null) {
         colorChunkCache = new ColorChunkCache(2048);
      }

      return colorChunkCache;
   }

   public static LocalColorChunk getThreadLocalGrassChunkWrapper() {
      return threadLocalGrassChunk;
   }

   public static LocalColorChunk getThreadLocalWaterChunkWrapper() {
      return threadLocalWaterChunk;
   }

   public static LocalColorChunk getThreadLocalFoliageChunkWrapper() {
      return threadLocalFoliageChunk;
   }

   public static LocalColorChunk getThreadLocalGenericChunkWrapper() {
      return threadLocalGenericChunk;
   }
}
