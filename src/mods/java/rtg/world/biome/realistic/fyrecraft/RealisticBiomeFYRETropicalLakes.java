package rtg.world.biome.realistic.fyrecraft;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.JitterEffect;

public class RealisticBiomeFYRETropicalLakes extends RealisticBiomeFYREBase {
   public RealisticBiomeFYRETropicalLakes(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeFYRETropicalLakes.TerrainBOPLandOfLakes();
   }

   public static class TerrainBOPLandOfLakes extends TerrainBase {
      private float minHeight;
      private float maxHeight;
      private float hillStrength;
      private HeightVariation small;
      private HeightVariation large;
      private JitterEffect largeJitter;
      private JitterEffect smallJitter;

      public TerrainBOPLandOfLakes() {
         super(63.0F);
         this.small = new HeightVariation();
         this.small.height = 2.5F;
         this.small.octave = 1;
         this.small.wavelength = 10.0F;
         this.large = new HeightVariation();
         this.large.height = 5.0F;
         this.large.octave = 2;
         this.large.wavelength = 20.0F;
         this.smallJitter = new JitterEffect();
         this.smallJitter.amplitude = 2.0F;
         this.smallJitter.wavelength = 9.0F;
         this.smallJitter.jittered = this.large.plus(this.small);
         this.largeJitter = new JitterEffect();
         this.largeJitter.amplitude = 4.0F;
         this.largeJitter.wavelength = 18.0F;
         this.largeJitter.jittered = this.smallJitter;
      }

      public TerrainBOPLandOfLakes(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.maxHeight = maxHeight > this.rollingHillsMaxHeight ? this.rollingHillsMaxHeight : (maxHeight < this.minHeight ? this.rollingHillsMaxHeight : maxHeight);
         this.hillStrength = hillStrength;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.largeJitter.added(rtgWorld, (float)x, (float)y) + this.base, river);
      }
   }
}
