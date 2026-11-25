package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.season.SeasonASMHelper;

public class BiomeHooks {
   public BiomeHooks() {
   }

   public static float getFloatTemperature(World world, Biome biome, BlockPos pos) {
      return SeasonASMHelper.getFloatTemperature(world, biome, pos);
   }

   public static float getFloatTemperature(Season.SubSeason subSeason, Biome biome, BlockPos pos) {
      return SeasonASMHelper.getFloatTemperature(subSeason, biome, pos);
   }
}
