package com.charles445.rltweaker.capability;

public class TweakerCapability implements ITweakerCapability {
   private float tanExhaustion = 0.0F;
   private int thirstPacketTicks = 0;

   public TweakerCapability() {
   }

   public void setTANExhaustion(float exhaustion) {
      this.tanExhaustion = exhaustion;
   }

   public float getTANExhaustion() {
      return this.tanExhaustion;
   }

   public void setThirstPacketTicks(int ticks) {
      this.thirstPacketTicks = ticks;
   }

   public void incrementThirstPacketTicks() {
      ++this.thirstPacketTicks;
   }

   public int getThirstPacketTicks() {
      return this.thirstPacketTicks;
   }
}
