package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;
import java.util.HashMap;
import java.util.Map.Entry;

public class DecimalWeightedPool<T> {
   public HashMap<T, Double> pool = new HashMap();
   public double totalWeight = 0.0D;

   public DecimalWeightedPool() {
   }

   public void addWeight(T object, double weight) {
      double old = ((Double)this.pool.getOrDefault(object, Double.valueOf(0.0D))).doubleValue();
      weight = weight + old;
      if (weight <= 0.0D) {
         this.totalWeight -= ((Double)this.pool.remove(object)).doubleValue();
      } else {
         this.pool.put(object, Double.valueOf(weight));
         this.totalWeight += weight - old;
      }

   }

   public void setWeight(T object, double weight) {
      if (weight < 0.0D) {
         weight = 0.0D;
      }

      this.totalWeight -= ((Double)this.pool.getOrDefault(object, Double.valueOf(0.0D))).doubleValue();
      if (weight == 0.0D) {
         this.pool.remove(object);
      } else {
         this.pool.put(object, Double.valueOf(weight));
      }

      this.totalWeight += weight;
   }

   public double getWeight(T object) {
      return ((Double)this.pool.getOrDefault(object, Double.valueOf(0.0D))).doubleValue();
   }

   public double getChance(T object) {
      return this.totalWeight == 0.0D ? 0.0D : ((Double)this.pool.getOrDefault(object, Double.valueOf(0.0D))).doubleValue() / this.totalWeight;
   }

   public T getRandom() {
      if (this.totalWeight == 0.0D) {
         return (T)null;
      } else {
         double index = Tools.random(this.totalWeight);

         for(Entry<T, Double> entry : this.pool.entrySet()) {
            if (index < ((Double)entry.getValue()).doubleValue()) {
               return (T)entry.getKey();
            }

            index -= ((Double)entry.getValue()).doubleValue();
         }

         throw new IllegalStateException();
      }
   }
}
