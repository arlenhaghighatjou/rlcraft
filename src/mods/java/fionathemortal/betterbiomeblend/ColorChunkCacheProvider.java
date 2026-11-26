package fionathemortal.betterbiomeblend;

public interface ColorChunkCacheProvider {
   ColorChunkCache getColorChunkCache();

   LocalColorChunk getTreadLocalGrassChunk();

   LocalColorChunk getTreadLocalWaterChunk();

   LocalColorChunk getTreadLocalFoliageChunk();

   LocalColorChunk getTreadLocalGenericChunk();
}
