package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeENVEndShrubland extends RealisticBiomeENVBase {
   public RealisticBiomeENVEndShrubland(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVEndShrubland.TerrainBOPShrubland();
   }

   public static class TerrainBOPShrubland extends TerrainBase {
      public TerrainBOPShrubland() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 100.0F, 65.0F);
      }
   }
}
