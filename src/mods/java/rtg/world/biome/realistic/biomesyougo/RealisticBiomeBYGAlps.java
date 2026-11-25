package rtg.world.biome.realistic.biomesyougo;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGAlps extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGAlps(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGAlps.TerrainBYGBiome();
   }

   public static class TerrainBYGBiome extends TerrainBase {
      private float start = 0.0F;
      private float height = 40.0F;
      private float width = 80.0F;

      public TerrainBYGBiome() {
         this.base = 120.0F;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, this.base - 62.0F);
      }
   }
}
