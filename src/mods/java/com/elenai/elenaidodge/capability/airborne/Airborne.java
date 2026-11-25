package com.elenai.elenaidodge.capability.airborne;

public class Airborne implements IAirborne {
   private boolean enabled = false;

   public Airborne() {
   }

   public void enable() {
      this.enabled = true;
   }

   public void disable() {
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void set(boolean bool) {
      this.enabled = bool;
   }
}
