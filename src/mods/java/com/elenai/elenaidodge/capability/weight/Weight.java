package com.elenai.elenaidodge.capability.weight;

public class Weight implements IWeight {
   private double weight = 0.0D;

   public Weight() {
   }

   public void set(double weight) {
      this.weight = weight;
   }

   public double getWeight() {
      return this.weight;
   }
}
