package rtg.api.world.terrain.heighteffect;

import rtg.api.util.noise.VoronoiResult;
import rtg.api.world.RTGWorld;

public class VoronoiBorderEffect extends HeightEffect {
   public float pointWavelength = 0.0F;
   public float floor = Float.MAX_VALUE;
   public float minimumDivisor = 0.0F;

   public VoronoiBorderEffect() {
   }

   public float added(RTGWorld rtgWorld, float x, float y) {
      VoronoiResult points = rtgWorld.cellularInstance(1).eval2D((double)(x / this.pointWavelength), (double)(y / this.pointWavelength));
      float raise = (float)points.interiorValue();
      raise = 1.0F - raise;
      return raise;
   }
}
