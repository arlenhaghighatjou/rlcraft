package com.endertech.minecraft.mods.adhooks.forces;

import net.minecraft.entity.Entity;

public abstract class ForceNode {
   public final Entity entity;

   public ForceNode(Entity entity) {
      this.entity = entity;
   }

   public abstract boolean exist();
}
