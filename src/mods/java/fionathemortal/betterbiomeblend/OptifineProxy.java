package fionathemortal.betterbiomeblend;

import java.util.HashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper.ColorResolver;
import net.optifine.CustomColors.IColorizer;

public class OptifineProxy {
   private static final HashMap<IColorizer, Integer> knownColorizers = new HashMap();

   public OptifineProxy() {
   }

   private static int addNewColorizer(IColorizer colorizer) {
      ColorResolverCompatibility.lock.lock();
      int id = ColorResolverCompatibility.nextColorID++;
      knownColorizers.put(colorizer, Integer.valueOf(id));
      ColorResolverCompatibility.lock.unlock();
      return id;
   }

   public static int getColorizerID(IColorizer colorizer) {
      Integer id = (Integer)knownColorizers.get(colorizer);
      if (id == null) {
         id = addNewColorizer(colorizer);
      }

      return id.intValue();
   }

   public static ColorChunk getBlendedColorChunk(ColorChunkCache cache, final IBlockAccess blockAccess, int colorID, int chunkX, int chunkZ, final IColorizer colorizer, final IBlockState blockState) {
      ColorChunk chunk = cache.getChunk(chunkX, chunkZ, colorID);
      if (chunk == null) {
         chunk = cache.newChunk(chunkX, chunkZ, colorID);
         ColorResolver colorResolver = new ColorResolver() {
            public int getColorAtPos(Biome biome, BlockPos blockPosition) {
               return colorizer.getColor(blockState, blockAccess, blockPosition);
            }
         };
         BiomeColor.generateBlendedColorChunk(blockAccess, chunkX, chunkZ, chunk.data, colorID, colorResolver);
         cache.putChunk(chunk);
      }

      return chunk;
   }
}
