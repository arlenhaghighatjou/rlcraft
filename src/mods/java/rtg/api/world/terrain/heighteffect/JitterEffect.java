package rtg.api.world.terrain.heighteffect;

import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.world.RTGWorld;

public class JitterEffect extends HeightEffect {
   public float amplitude = 2.14748365E9F;
   public float wavelength = 0.0F;
   public HeightEffect jittered;

   public JitterEffect() {
   }

   public JitterEffect(float amplitude, float wavelength, HeightEffect toJitter) {
      this.amplitude = amplitude;
      this.wavelength = wavelength;
      this.jittered = toJitter;
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      ISimplexData2D jitterData = SimplexData2D.newDisk();
      rtgWorld.simplexInstance(1).multiEval2D((double)(x / this.wavelength), (double)(y / this.wavelength), jitterData);
      int pX = (int)Math.round((double)x + jitterData.getDeltaX() * (double)this.amplitude);
      int pY = (int)Math.round((double)y + jitterData.getDeltaY() * (double)this.amplitude);
      return this.jittered.added(rtgWorld, (float)pX, (float)pY);
   }
}
