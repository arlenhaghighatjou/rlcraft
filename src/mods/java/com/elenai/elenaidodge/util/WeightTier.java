package com.elenai.elenaidodge.util;

public class WeightTier {
   private double entryWeight;
   private double force;
   private int cooldown;

   public double getEntryWeight() {
      return this.entryWeight;
   }

   public void setEntryWeight(double entryWeight) {
      this.entryWeight = entryWeight;
   }

   public double getForce() {
      return this.force;
   }

   public void setForce(double force) {
      this.force = force;
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void setCooldown(int cooldown) {
      this.cooldown = cooldown;
   }

   public WeightTier(double entryWeight, int cooldown, double force) {
      this.entryWeight = entryWeight;
      this.force = force;
      this.cooldown = cooldown;
   }
}
