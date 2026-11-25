package rtg.world.biome.realistic.mistbiomes;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeMBMistTaiga extends RealisticBiomeMBBase {
   public RealisticBiomeMBMistTaiga(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeMBMistTaiga.TerrainVanillaTaiga();
   }

   public static class TerrainVanillaTaiga extends TerrainBase {
      public TerrainVanillaTaiga() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainFlatLakes(x, y, rtgWorld, river, 68.0F);
      }
   }
}
