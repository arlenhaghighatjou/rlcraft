package rtg.world.biome.realistic.mistbiomes;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeMBMistSwamp extends RealisticBiomeMBBase {
   public RealisticBiomeMBMistSwamp(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeMBMistSwamp.TerrainVanillaSwampland();
   }

   public static class TerrainVanillaSwampland extends TerrainBase {
      public TerrainVanillaSwampland() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainMarsh(x, y, rtgWorld, 61.5F, river);
      }
   }
}
