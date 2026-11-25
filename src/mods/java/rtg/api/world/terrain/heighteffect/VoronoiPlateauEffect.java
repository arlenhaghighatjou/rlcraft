package rtg.api.world.terrain.heighteffect;

import java.awt.geom.Point2D.Float;
import rtg.api.util.noise.VoronoiResult;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;

public class VoronoiPlateauEffect extends HeightEffect {
   public float pointWavelength = 200.0F;
   public float minimumDivisor = 0.0F;
   public float adjustmentRadius = 3.0F;

   public VoronoiPlateauEffect() {
   }

   public float added(RTGWorld rtgWorld, float x, float y) {
      Float evaluateAt = new Float(x / this.pointWavelength, y / this.pointWavelength);
      VoronoiResult points = rtgWorld.cellularInstance(1).eval2D((double)evaluateAt.x, (double)evaluateAt.y);
      float raise = (float)points.interiorValue();
      Float adjustAt = points.toLength(evaluateAt, this.adjustmentRadius);
      float multiplier = 1.3F;
      float noZeros = 0.1F;
      float adjustment = (float)rtgWorld.cellularInstance(2).eval2D((double)adjustAt.x, (double)adjustAt.y).interiorValue() * multiplier + noZeros;
      float reAdjustment = (float)rtgWorld.cellularInstance(3).eval2D((double)adjustAt.x, (double)adjustAt.y).interiorValue() * multiplier + noZeros;
      adjustment = TerrainBase.bayesianAdjustment(adjustment, reAdjustment);
      raise = TerrainBase.bayesianAdjustment(raise, adjustment);
      return raise;
   }
}
