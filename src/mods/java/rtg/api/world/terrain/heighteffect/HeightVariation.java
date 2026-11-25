package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;

public class HeightVariation extends HeightEffect {
   public float height = 2.14748365E9F;
   public float wavelength = 0.0F;
   public int octave = -1;

   public HeightVariation() {
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      return rtgWorld.simplexInstance(this.octave).noise2f(x / this.wavelength, y / this.wavelength) * this.height;
   }
}
