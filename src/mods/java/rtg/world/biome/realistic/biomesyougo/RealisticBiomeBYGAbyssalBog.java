package rtg.world.biome.realistic.biomesyougo;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGAbyssalBog extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGAbyssalBog(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGAbyssalBog.TerrainBOPBayou();
   }

   public static class TerrainBOPBayou extends TerrainBase {
      public TerrainBOPBayou() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 80.0F, 1.0F, 40.0F, 20.0F, 62.0F);
      }
   }
}
