package rtg.world.biome.realistic.mistbiomes;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeMBColdMistTaiga extends RealisticBiomeMBBase {
   public RealisticBiomeMBColdMistTaiga(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeMBColdMistTaiga.TerrainVanillaColdTaiga();
   }

   public static class TerrainVanillaColdTaiga extends TerrainBase {
      public TerrainVanillaColdTaiga() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainFlatLakes(x, y, rtgWorld, river, 66.0F);
      }
   }
}
