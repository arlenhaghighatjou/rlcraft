package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeENVMoor extends RealisticBiomeENVBase {
   public RealisticBiomeENVMoor(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVMoor.TerrainBOPWoodland(10.0F, 25.0F, 72.0F, 120.0F);
   }

   public static class TerrainBOPWoodland extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainBOPWoodland(float hillStart, float landHeight, float baseHeight, float hillWidth) {
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
