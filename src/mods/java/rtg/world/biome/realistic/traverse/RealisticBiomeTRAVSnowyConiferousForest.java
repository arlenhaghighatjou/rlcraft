package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeTRAVSnowyConiferousForest extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVSnowyConiferousForest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVSnowyConiferousForest.TerrainBOPSnowyConiferousForest(65.0F, 70.0F, 40.0F);
   }

   public static class TerrainBOPSnowyConiferousForest extends TerrainBase {
      private float minHeight;
      private float maxHeight;
      private float hillStrength;

      public TerrainBOPSnowyConiferousForest(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.maxHeight = maxHeight > this.rollingHillsMaxHeight ? this.rollingHillsMaxHeight : (maxHeight < this.minHeight ? this.rollingHillsMaxHeight : maxHeight);
         this.hillStrength = hillStrength;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainRollingHills(x, y, rtgWorld, river, this.hillStrength, this.maxHeight, this.groundNoiseAmplitudeHills + 2.0F, 4.0F);
      }
   }
}
