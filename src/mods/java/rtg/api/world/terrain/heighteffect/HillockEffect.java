package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;

public class HillockEffect extends HeightEffect {
   public float height = 2.14748365E9F;
   public float wavelength = 0.0F;
   public float minimumSimplex = 2.14748365E9F;
   public int octave;

   public HillockEffect() {
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      float noise = rtgWorld.simplexInstance(this.octave).noise2f(x / this.wavelength, y / this.wavelength);
      if (noise < this.minimumSimplex) {
         noise = 0.0F;
      } else {
         noise = (noise - this.minimumSimplex) / (1.0F - this.minimumSimplex);
      }

      return noise * this.height;
   }
}
