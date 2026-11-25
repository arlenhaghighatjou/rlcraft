package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;
import java.util.HashMap;
import java.util.Map.Entry;

public class WeightedPool<T> {
   public HashMap<T, Integer> pool = new HashMap();
   public int totalWeight = 0;

   public WeightedPool() {
   }

   public void addWeight(T object, int weight) {
      int old = ((Integer)this.pool.getOrDefault(object, Integer.valueOf(0))).intValue();
      weight = weight + old;
      if (weight <= 0) {
         this.totalWeight -= ((Integer)this.pool.remove(object)).intValue();
      } else {
         this.pool.put(object, Integer.valueOf(weight));
         this.totalWeight += weight - old;
      }

   }

   public void setWeight(T object, int weight) {
      if (weight < 0) {
         weight = 0;
      }

      this.totalWeight -= ((Integer)this.pool.getOrDefault(object, Integer.valueOf(0))).intValue();
      if (weight == 0) {
         this.pool.remove(object);
      } else {
         this.pool.put(object, Integer.valueOf(weight));
      }

      this.totalWeight += weight;
   }

   public int getWeight(T object) {
      return ((Integer)this.pool.getOrDefault(object, Integer.valueOf(0))).intValue();
   }

   public double getChance(T object) {
      return this.totalWeight == 0 ? 0.0D : (double)((Integer)this.pool.getOrDefault(object, Integer.valueOf(0))).intValue() / (double)this.totalWeight;
   }

   public T getRandom() {
      if (this.totalWeight == 0) {
         return (T)null;
      } else {
         int index = Tools.random(this.totalWeight);

         for(Entry<T, Integer> entry : this.pool.entrySet()) {
            if (index < ((Integer)entry.getValue()).intValue()) {
               return (T)entry.getKey();
            }

            index -= ((Integer)entry.getValue()).intValue();
         }

         throw new IllegalStateException();
      }
   }
}
