package com.elenai.elenaidodge.capability.enabled;

public class Enabled implements IEnabled {
   private boolean enabled = true;

   public Enabled() {
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
