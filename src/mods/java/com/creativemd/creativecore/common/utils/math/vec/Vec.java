package com.creativemd.creativecore.common.utils.math.vec;

import java.lang.reflect.InvocationTargetException;

public abstract class Vec<T extends Vec> {
   public Vec() {
   }

   public Vec(Vec vec) {
      for(int i = 0; i < vec.getDimensionCount(); ++i) {
         this.setValueByDim(i, vec.getValueByDim(i));
      }

   }

   public abstract void setValueByDim(int var1, double var2);

   public abstract double getValueByDim(int var1);

   public abstract int getDimensionCount();

   public T copy() {
      return (T)copyVec(this);
   }

   public T add(Vec vec) {
      T newVec = this.copy();

      for(int i = 0; i < vec.getDimensionCount(); ++i) {
         newVec.setValueByDim(i, newVec.getValueByDim(i) + vec.getValueByDim(i));
      }

      return newVec;
   }

   public T sub(Vec vec) {
      T newVec = this.copy();

      for(int i = 0; i < vec.getDimensionCount(); ++i) {
         newVec.setValueByDim(i, newVec.getValueByDim(i) - vec.getValueByDim(i));
      }

      return newVec;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[");

      for(int i = 0; i < this.getDimensionCount(); ++i) {
         if (i > 0) {
            builder.append(",");
         }

         builder.append(this.getValueByDim(i));
      }

      builder.append("]");
      return builder.toString();
   }

   public double distance(Vec vec) {
      if (vec.getDimensionCount() != this.getDimensionCount()) {
         return 0.0D;
      } else {
         double value = 0.0D;

         for(int i = 0; i < this.getDimensionCount(); ++i) {
            value += Math.pow(this.getValueByDim(i) - vec.getValueByDim(i), 2.0D);
         }

         return Math.sqrt(value);
      }
   }

   public static Vec copyVec(Vec vec) {
      switch(vec.getDimensionCount()) {
      case 1:
         return new Vec1(vec);
      case 2:
         return new Vec2(vec);
      case 3:
         return new Vec3(vec);
      default:
         return null;
      }
   }

   public static Vec createEmptyVec(Class className) {
      try {
         return (Vec)className.getConstructor(Vec.class).newInstance(new Vec1(0.0D));
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
