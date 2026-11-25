package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeHooks {
   public BiomeHooks() {
   }

   public static float getFloatTemperature(World world, Biome biome, BlockPos pos) {
      try {
         return ((Float)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("getFloatTemperature", World.class, Biome.class, BlockPos.class).invoke((Object)null, world, biome, pos)).floatValue();
      } catch (Exception var4) {
         throw new RuntimeException("An error occurred calling getFloatTemperature", var4);
      }
   }

   public static float getFloatTemperature(Season.SubSeason subSeason, Biome biome, BlockPos pos) {
      try {
         return ((Float)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("getFloatTemperature", Season.SubSeason.class, Biome.class, BlockPos.class).invoke((Object)null, subSeason, biome, pos)).floatValue();
      } catch (Exception var4) {
         throw new RuntimeException("An error occurred calling getFloatTemperature", var4);
      }
   }
}
