package rtg.api.world.terrain.heighteffect;

import rtg.api.world.RTGWorld;

public abstract class HeightEffect {
   public HeightEffect() {
   }

   public abstract float added(RTGWorld var1, float var2, float var3);

   public HeightEffect plus(HeightEffect added) {
      return new SummedHeightEffect(this, added);
   }
}
