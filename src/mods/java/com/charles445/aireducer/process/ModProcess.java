package com.charles445.aireducer.process;

import net.minecraft.entity.EntityLiving;

public abstract class ModProcess {
   public ModProcess() {
   }

   public abstract boolean canUse();

   public abstract void handle(EntityLiving var1, String var2, String var3);
}
