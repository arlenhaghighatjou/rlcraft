package com.elenai.elenaidodge.capability.regen;

public class Regen implements IRegen {
   private int regen = 0;

   public Regen() {
   }

   public void increase(int regen) {
      this.regen += regen;
   }

   public void decrease(int regen) {
      this.regen -= regen;
   }

   public void set(int regen) {
      this.regen = regen;
   }

   public int getRegen() {
      return this.regen;
   }
}
