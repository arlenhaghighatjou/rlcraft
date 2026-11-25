package com.creativemd.creativecore.common.utils.math.graph;

import java.util.HashMap;

public class LinearGraph extends Graph {
   public LinearGraph(HashMap<Float, Float> points) {
      super(points);
   }

   public float getY(float x) {
      Float pointBefore = this.getPreviousPointX(x);
      Float pointAfter = this.getNextPointX(x);
      if (pointBefore != null && pointAfter != null) {
         if (pointBefore == pointAfter) {
            return ((Float)this.points.get(pointBefore)).floatValue();
         } else {
            float distance = pointAfter.floatValue() - pointBefore.floatValue();
            float relativePos = x - pointBefore.floatValue();
            float percentage = relativePos / distance;
            float pointBeforeY = ((Float)this.points.get(pointBefore)).floatValue();
            float distanceY = ((Float)this.points.get(pointAfter)).floatValue() - pointBeforeY;
            return pointBeforeY + distanceY * percentage;
         }
      } else if (pointBefore != null) {
         return ((Float)this.points.get(pointBefore)).floatValue();
      } else {
         return pointAfter != null ? ((Float)this.points.get(pointAfter)).floatValue() : 0.0F;
      }
   }
}
