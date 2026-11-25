package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class BumpyHillsEffect extends HeightEffect {
   public float hillHeight = 2.14748365E9F;
   public float hillWavelength = 0.0F;
   public float spikeHeight = 2.14748365E9F;
   public float spikeWavelength = 0.0F;
   public int hillOctave = 0;
   public int spikeOctave = 2;

   public BumpyHillsEffect() {
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      float noise = rtgWorld.simplexInstance(this.hillOctave).noise2f(x / this.hillWavelength, y / this.hillWavelength);
      noise = TerrainBase.blendedHillHeight(noise);
      float spikeNoise = rtgWorld.simplexInstance(this.spikeOctave).noise2f(x / this.spikeWavelength, y / this.spikeWavelength);
      spikeNoise = TerrainBase.blendedHillHeight(spikeNoise * noise);
      return noise * this.hillHeight + spikeNoise * this.spikeHeight;
   }
}
