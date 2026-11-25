package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class GroundEffect extends HeightEffect {
   private final float amplitude;

   public GroundEffect(float amplitude) {
      this.amplitude = amplitude;
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      return TerrainBase.groundNoise(x, y, this.amplitude, rtgWorld);
   }
}
