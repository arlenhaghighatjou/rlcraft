package rtg.world.biome.realistic.environs;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.BumpyHillsEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;

public class RealisticBiomeENVExtremeJungle extends RealisticBiomeENVBase {
   public RealisticBiomeENVExtremeJungle(Biome biome) {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeENVExtremeJungle.TerrainBOPHighland();
   }

   public static class TerrainBOPHighland extends TerrainBase {
      private float baseHeight = 90.0F;
      private BumpyHillsEffect onTop = new BumpyHillsEffect();
      private JitterEffect withJitter;

      public TerrainBOPHighland() {
         this.onTop.hillHeight = 30.0F;
         this.onTop.hillWavelength = 60.0F;
         this.onTop.spikeHeight = 20.0F;
         this.onTop.spikeWavelength = 10.0F;
         this.withJitter = new JitterEffect();
         this.withJitter.amplitude = 2.0F;
         this.withJitter.wavelength = 5.0F;
         this.withJitter.jittered = this.onTop;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.baseHeight + this.withJitter.added(rtgWorld, (float)x, (float)y) + groundNoise(x, y, 6.0F, rtgWorld), river);
      }
   }
}
