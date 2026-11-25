package com.elenai.elenaidodge.capability.invincibility;

public class Invincibility implements IInvincibility {
   private int invincibility = 0;

   public Invincibility() {
   }

   public void increase(int invincibility) {
      this.invincibility += invincibility;
   }

   public void decrease(int invincibility) {
      this.invincibility -= invincibility;
   }

   public void set(int invincibility) {
      this.invincibility = invincibility;
   }

   public int getInvincibility() {
      return this.invincibility;
   }
}
