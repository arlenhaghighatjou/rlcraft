package rtg.world.biome.realistic.biomesyougo;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGShatteredDesert extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGShatteredDesert(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGShatteredDesert.TerrainBOPBayou();
   }

   public static class TerrainBOPBayou extends TerrainBase {
      public TerrainBOPBayou() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 80.0F, 1.0F, 40.0F, 20.0F, 62.0F);
      }
   }
}
