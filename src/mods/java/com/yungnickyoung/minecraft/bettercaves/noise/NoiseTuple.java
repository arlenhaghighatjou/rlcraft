package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.ArrayList;
import java.util.List;

public class NoiseTuple {
   private List<Double> noiseValues = new ArrayList();
   private int length = 0;

   public NoiseTuple(double... vals) {
      for(double val : vals) {
         this.noiseValues.add(Double.valueOf(val));
         ++this.length;
      }

   }

   public void put(double val) {
      this.noiseValues.add(Double.valueOf(val));
      ++this.length;
   }

   public double get(int index) throws IndexOutOfBoundsException {
      if (index >= 0 && index < this.length) {
         return ((Double)this.noiseValues.get(index)).doubleValue();
      } else {
         throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);
      }
   }

   public void set(int index, double newValue) throws IndexOutOfBoundsException {
      if (index >= 0 && index < this.length) {
         this.noiseValues.set(index, Double.valueOf(newValue));
      } else {
         throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);
      }
   }

   public NoiseTuple times(float magnitude) {
      NoiseTuple result = new NoiseTuple(new double[0]);

      for(int i = 0; i < this.length; ++i) {
         result.put(((Double)this.noiseValues.get(i)).doubleValue() * (double)magnitude);
      }

      return result;
   }

   public NoiseTuple plus(NoiseTuple other) {
      NoiseTuple result = new NoiseTuple(new double[0]);

      for(int i = 0; i < this.length; ++i) {
         result.put(((Double)this.noiseValues.get(i)).doubleValue() + other.get(i));
      }

      return result;
   }

   public List<Double> getNoiseValues() {
      return this.noiseValues;
   }

   public int size() {
      return this.length;
   }
}
