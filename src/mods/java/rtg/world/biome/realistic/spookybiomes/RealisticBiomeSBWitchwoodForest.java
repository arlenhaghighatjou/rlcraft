package rtg.world.biome.realistic.spookybiomes;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeSBWitchwoodForest extends RealisticBiomeSBBase {
   public RealisticBiomeSBWitchwoodForest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeSBWitchwoodForest.TerrainBOPOminousWoods(65.0F, 80.0F, 48.0F);
   }

   public static class TerrainBOPOminousWoods extends TerrainBase {
      private float minHeight;
      private float maxHeight;
      private float hillStrength;

      public TerrainBOPOminousWoods(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.maxHeight = maxHeight > this.rollingHillsMaxHeight ? this.rollingHillsMaxHeight : (maxHeight < this.minHeight ? this.rollingHillsMaxHeight : maxHeight);
         this.hillStrength = hillStrength;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainRollingHills(x, y, rtgWorld, river, this.hillStrength, this.maxHeight, this.groundNoiseAmplitudeHills, 0.0F);
      }
   }
}
