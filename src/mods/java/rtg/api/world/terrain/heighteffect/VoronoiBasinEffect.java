package rtg.api.world.terrain.heighteffect;

import java.awt.geom.Point2D.Float;
import rtg.api.util.noise.VoronoiResult;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class VoronoiBasinEffect extends HeightEffect {
   public float pointWavelength = 200.0F;
   public float minimumDivisor = 0.0F;
   public float adjustmentRadius = 1.0F;

   public VoronoiBasinEffect() {
   }

   public float added(RTGWorld rtgWorld, float x, float y) {
      Float evaluateAt = new Float(x / this.pointWavelength, y / this.pointWavelength);
      VoronoiResult points = rtgWorld.cellularInstance(1).eval2D((double)evaluateAt.x, (double)evaluateAt.y);
      float raise = (float)points.borderValue();
      Float adjustAt = points.toLength(evaluateAt, this.adjustmentRadius);
      float noZeros = 0.25F;
      float adjustment = (float)rtgWorld.cellularInstance(2).eval2D((double)adjustAt.x, (double)adjustAt.y).interiorValue() + noZeros;
      float reAdjustment = (float)rtgWorld.cellularInstance(3).eval2D((double)adjustAt.x, (double)adjustAt.y).interiorValue() + noZeros;
      adjustment = TerrainBase.bayesianAdjustment(adjustment, reAdjustment);
      raise = TerrainBase.bayesianAdjustment(raise, 1.0F / (adjustment * adjustment));
      return raise;
   }
}
