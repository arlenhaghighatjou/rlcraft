package com.endertech.minecraft.forge.threads;

public abstract class ForgeThread extends Thread {
   public ForgeThread() {
      this.setPriority(1);
   }

   public abstract void run();
}
