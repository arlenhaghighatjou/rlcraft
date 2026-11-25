package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class SpikeEverywhereEffect extends HeightEffect {
   public float wavelength = 0.0F;
   public float minimumSimplex = 2.14748365E9F;
   public int octave;
   public float power = 1.6F;
   public HeightEffect spiked;

   public SpikeEverywhereEffect() {
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      float noise = rtgWorld.simplexInstance(this.octave).noise2f(x / this.wavelength, y / this.wavelength);
      noise = Math.abs(noise);
      noise = TerrainBase.blendedHillHeight(noise, this.minimumSimplex);
      noise = TerrainBase.unsignedPower(noise, this.power);
      return noise * this.spiked.added(rtgWorld, x, y);
   }
}
