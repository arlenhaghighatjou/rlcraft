package com.creativemd.creativecore.common.utils.math.graph;

import java.util.HashMap;

public abstract class Graph {
   public final HashMap<Float, Float> points;

   public Graph(HashMap<Float, Float> points) {
      this.points = points;
   }

   public Float getPreviousPointX(float below) {
      Float next = null;

      for(Float point : this.points.keySet()) {
         if (point.floatValue() <= below && (next == null || point.floatValue() > next.floatValue())) {
            next = point;
         }
      }

      return next;
   }

   public Float getNextPointX(float above) {
      Float next = null;

      for(Float point : this.points.keySet()) {
         if (point.floatValue() >= above && (next == null || point.floatValue() < next.floatValue())) {
            next = point;
         }
      }

      return next;
   }

   public float getLastPointX() {
      return this.getPreviousPointX(Float.MAX_VALUE).floatValue();
   }

   public float getFirstPointX() {
      return this.getNextPointX(Float.MIN_VALUE).floatValue();
   }

   public abstract float getY(float var1);
}
