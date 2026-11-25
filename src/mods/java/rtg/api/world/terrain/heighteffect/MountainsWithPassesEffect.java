package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class MountainsWithPassesEffect extends HeightEffect {
   public float mountainHeight = 2.14748365E9F;
   public float mountainWavelength = 0.0F;
   public float spikeHeight = 2.14748365E9F;
   public float spikeWavelength = 0.0F;
   public int hillOctave = 0;
   public int spikeOctave = 2;
   private float adjustedBottom = TerrainBase.blendedHillHeight(0.0F, 0.2F);

   public MountainsWithPassesEffect() {
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      float noise = rtgWorld.simplexInstance(this.hillOctave).noise2f(x / this.mountainWavelength, y / this.mountainWavelength);
      noise = Math.abs(noise);
      noise = TerrainBase.blendedHillHeight(noise, 0.2F);
      noise = 1.0F - (1.0F - noise) / (1.0F - this.adjustedBottom);
      float spikeNoise = TerrainBase.blendedHillHeight(noise, 0.1F);
      spikeNoise = spikeNoise * spikeNoise;
      spikeNoise = TerrainBase.blendedHillHeight(spikeNoise * noise);
      if ((double)noise > 1.01D) {
         throw new RuntimeException();
      } else if ((double)spikeNoise > 1.01D) {
         throw new RuntimeException();
      } else {
         return noise * this.mountainHeight + spikeNoise * this.spikeHeight;
      }
   }
}
