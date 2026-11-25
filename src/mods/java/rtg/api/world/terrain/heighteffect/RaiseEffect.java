package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;

public class RaiseEffect extends HeightEffect {
   public final float height;

   public RaiseEffect(float height) {
      this.height = height;
   }

   public final float added(RTGWorld rtgWorld, float x, float y) {
      return this.height;
   }
}
