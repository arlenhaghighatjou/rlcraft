package com.endertech.minecraft.mods.adhooks.motion;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import net.minecraft.entity.Entity;

public class EntityTarget<T extends Entity> {
   public final T target;
   protected final double damping;
   protected Vect3d prevMotion = Vect3d.ZERO;
   protected Vect3d currentMotion = Vect3d.ZERO;

   public EntityTarget(T target, double damping) {
      this.target = target;
      this.damping = damping;
   }

   public boolean exists() {
      return this.target.isEntityAlive() && (this.prevMotion.notZero() || this.currentMotion.notZero());
   }

   public void addDampedMotion(Vect3d motion) {
      this.currentMotion = this.currentMotion.add(motion);
   }

   public void applyMotions() {
      Vect3d dampedMotion = this.prevMotion.scale(this.damping);
      Vect3d newMotion = this.currentMotion.subtract(dampedMotion);
      ForgeEntity.addMotion(this.target, newMotion);
      this.prevMotion = this.currentMotion;
      this.currentMotion = Vect3d.ZERO;
   }
}
