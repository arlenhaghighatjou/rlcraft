package com.elenai.elenaidodge.capability.ledgegrabcooldown;

public class LedgeGrabCooldown implements ILedgeGrabCooldown {
   private int ledgeGrabs = 0;

   public LedgeGrabCooldown() {
   }

   public void increase(int ledgeGrabs) {
      this.ledgeGrabs += ledgeGrabs;
   }

   public void decrease(int ledgeGrabs) {
      this.ledgeGrabs -= ledgeGrabs;
   }

   public void set(int ledgeGrabs) {
      this.ledgeGrabs = ledgeGrabs;
   }

   public int getLedgeGrabs() {
      return this.ledgeGrabs;
   }
}
