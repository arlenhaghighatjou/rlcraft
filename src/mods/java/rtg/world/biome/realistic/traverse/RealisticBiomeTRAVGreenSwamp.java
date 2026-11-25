package rtg.world.biome.realistic.traverse;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightVariation;

public class RealisticBiomeTRAVGreenSwamp extends RealisticBiomeTRAVBase {
   public RealisticBiomeTRAVGreenSwamp(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeTRAVGreenSwamp.TerrainBOPMarsh();
   }

   public static class TerrainBOPMarsh extends TerrainBase {
      private float baseHeight = 62.0F;
      private HeightVariation variation = new HeightVariation();
      private HeightVariation smallVariation;

      public TerrainBOPMarsh() {
         this.variation.height = 1.5F;
         this.variation.wavelength = 20.0F;
         this.variation.octave = 0;
         this.smallVariation = new HeightVariation();
         this.smallVariation.height = 1.5F;
         this.smallVariation.wavelength = 10.0F;
         this.smallVariation.octave = 0;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return this.baseHeight + this.variation.added(rtgWorld, (float)x, (float)y) + this.smallVariation.added(rtgWorld, (float)x, (float)y);
      }
   }
}
