package rtg.world.biome.realistic.fyrecraft;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeFYRERedDesertHills extends RealisticBiomeFYREBase {
   public RealisticBiomeFYRERedDesertHills(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeFYRERedDesertHills.TerrainVanillaDesertHills(10.0F, 80.0F, 68.0F, 200.0F);
   }

   public static class TerrainVanillaDesertHills extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainVanillaDesertHills(float hillStart, float landHeight, float baseHeight, float hillWidth) {
         this.start = hillStart;
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, this.base - 62.0F);
      }
   }
}
