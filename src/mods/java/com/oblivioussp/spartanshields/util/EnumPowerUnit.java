package com.oblivioussp.spartanshields.util;

public enum EnumPowerUnit {
   RedstoneFlux("rfCapacity", "rfPerDamage", 1.0F),
   ForgeEnergy("feCapacity", "fePerDamage", 1.0F),
   MicroInfinity("uiCapacity", "uiPerDamage", 1.0F);

   private String capUnloc;
   private String enPerDamUnloc;
   private float scale;

   private EnumPowerUnit(String capacityUnloc, String energyPerDamageUnloc, float powerScale) {
      this.capUnloc = capacityUnloc;
      this.enPerDamUnloc = energyPerDamageUnloc;
      this.scale = powerScale;
   }

   public String getCapacityUnlocalised() {
      return this.capUnloc;
   }

   public String getEnergyPerDamageUnlocalised() {
      return this.enPerDamUnloc;
   }

   public float getEnergyScaleToRF() {
      return this.scale;
   }
}
