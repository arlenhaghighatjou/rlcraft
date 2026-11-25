package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeTRAVTemperateRainforest extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVTemperateRainforest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVTemperateRainforest.TerrainBOPTemperateRainforest();
   }

   public static class TerrainBOPTemperateRainforest extends TerrainBase {
      public TerrainBOPTemperateRainforest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 100.0F, 65.0F);
      }
   }
}
