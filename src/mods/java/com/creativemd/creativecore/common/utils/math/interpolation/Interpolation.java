package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public abstract class Interpolation<T extends Vec> {
   protected LinkedHashMap<Double, T> points = new LinkedHashMap();
   protected ArrayList<T> pointVecs = new ArrayList();
   private final Class classOfT;

   public Interpolation(double[] times, T[] points) {
      if (points.length < 2) {
         throw new IllegalArgumentException("At least two points are needed!");
      } else if (times.length != points.length) {
         throw new IllegalArgumentException("Invalid times array!");
      } else {
         this.classOfT = points[0].getClass();

         for(int i = 0; i < points.length; ++i) {
            this.points.put(Double.valueOf(times[i]), points[i]);
         }

         this.pointVecs = new ArrayList(this.points.values());
      }
   }

   public Interpolation(T... points) {
      if (points.length < 2) {
         throw new IllegalArgumentException("At least two points are needed!");
      } else {
         this.classOfT = points[0].getClass();
         double time = 0.0D;
         double stepLength = 1.0D / (double)(points.length - 1);

         for(int i = 0; i < points.length; ++i) {
            this.points.put(Double.valueOf(time), points[i]);
            time += stepLength;
         }

         this.pointVecs = new ArrayList(this.points.values());
      }
   }

   protected double getValue(int index, int dim) {
      return ((Vec)this.pointVecs.get(index)).getValueByDim(dim);
   }

   public T valueAt(double t) {
      if (t >= 0.0D && t <= 1.0D) {
         Entry<Double, T> firstPoint = null;
         int indexFirst = -1;
         Entry<Double, T> secondPoint = null;
         int indexSecond = -1;
         int i = 0;

         for(Entry<Double, T> entry : this.points.entrySet()) {
            if (((Double)entry.getKey()).doubleValue() >= t) {
               if (firstPoint == null) {
                  firstPoint = entry;
                  indexFirst = i;
               } else {
                  secondPoint = entry;
                  indexSecond = i;
               }
               break;
            }

            firstPoint = entry;
            indexFirst = i++;
         }

         if (secondPoint == null) {
            return (T)(firstPoint.getValue()).copy();
         } else {
            T vec = Vec.copyVec((Vec)firstPoint.getValue());
            double pointDistance = ((Double)secondPoint.getKey()).doubleValue() - ((Double)firstPoint.getKey()).doubleValue();
            double mu = (t - ((Double)firstPoint.getKey()).doubleValue()) / pointDistance;

            for(int dim = 0; dim < vec.getDimensionCount(); ++dim) {
               vec.setValueByDim(dim, this.valueAt(mu, indexFirst, indexSecond, dim));
            }

            return vec;
         }
      } else {
         return (T)Vec.createEmptyVec(this.classOfT);
      }
   }

   public abstract double valueAt(double var1, int var3, int var4, int var5);
}
