package com.elenai.elenaidodge.capability.ledgegrabs;

public class LedgeGrabs implements ILedgeGrabs {
   private int ledgeGrabs = 0;

   public LedgeGrabs() {
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
