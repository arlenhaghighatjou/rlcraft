package com.elenai.elenaidodge.capability.particles;

public class Particles implements IParticles {
   private int particles = 0;

   public Particles() {
   }

   public void increase(int particles) {
      this.particles += particles;
   }

   public void decrease(int particles) {
      this.particles -= particles;
   }

   public void set(int particles) {
      this.particles = particles;
   }

   public int getParticles() {
      return this.particles;
   }
}
