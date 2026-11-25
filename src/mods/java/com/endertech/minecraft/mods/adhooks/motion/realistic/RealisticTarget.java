package com.endertech.minecraft.mods.adhooks.motion.realistic;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.mods.adhooks.motion.MotionMsg;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.Entity;

/** @deprecated */
@Deprecated
public class RealisticTarget {
   public final Entity entity;
   protected final Set<Entity> sources;
   protected Vect3d motion;
   protected int jumpTicksLeft;
   protected boolean noFrictions;
   protected boolean noLimitations;

   public RealisticTarget(Entity entity) {
      this.entity = entity;
      this.sources = new HashSet();
      this.motion = ForgeEntity.getMotion(entity);
      this.jumpTicksLeft = 0;
      this.noFrictions = false;
      this.noLimitations = false;
   }

   public RealisticTarget addForceFrom(Entity source, Vect3d motion) {
      this.sources.add(source);
      return this.addMotion(motion);
   }

   public RealisticTarget reduceBounce(Vect3d tension, float reduction) {
      Vect3d bounce = this.motion.projectOn(tension);
      this.motion = this.motion.subtract(bounce.scale((double)reduction));
      return this;
   }

   public Vect3d getMotion() {
      return this.motion;
   }

   public RealisticTarget addMotion(Vect3d motion) {
      this.motion = this.motion.add(motion);
      return this;
   }

   public RealisticTarget noFrictions(boolean value) {
      this.noFrictions = value;
      return this;
   }

   public RealisticTarget noLimitations(boolean value) {
      this.noLimitations = value;
      return this;
   }

   protected RealisticTarget applyFriction(double factor) {
      double veloc = this.motion.length();
      double decel = veloc * veloc * factor;
      veloc = veloc - decel;
      if (veloc < 0.0D) {
         veloc = 0.0D;
      }

      this.motion = this.motion.resize(veloc);
      return this;
   }

   protected RealisticTarget applyFrictions() {
      if (this.noFrictions) {
         return this;
      } else {
         double factor = 0.05D;
         if (this.entity.isInWater() || this.entity.isInLava()) {
            factor += 0.25D;
         }

         if (this.entity.onGround) {
            factor += 0.5D;
         }

         return this.applyFriction(factor);
      }
   }

   protected RealisticTarget applyGravity() {
      return this.entity.hasNoGravity() ? this : this.addMotion(Vect3d.from(0.0D, -0.08D, 0.0D));
   }

   protected RealisticTarget applyLimitations() {
      if (this.noLimitations) {
         return this;
      } else {
         double maxLength = 1.0D;
         if (this.motion.length() > 1.0D) {
            this.motion = this.motion.resize(1.0D);
         }

         maxLength = 0.003D;
         double x = Math.abs(this.motion.x) < 0.003D ? 0.0D : this.motion.x;
         double y = Math.abs(this.motion.y) < 0.003D ? 0.0D : this.motion.y;
         double z = Math.abs(this.motion.z) < 0.003D ? 0.0D : this.motion.z;
         this.motion = Vect3d.from(x, y, z);
         return this;
      }
   }

   protected RealisticTarget applyJumping() {
      if (!this.entity.onGround && this.jumpTicksLeft > 0) {
         --this.jumpTicksLeft;
         this.motion = this.motion.withY(ForgeEntity.getMotion(this.entity).y);
      }

      return this;
   }

   protected RealisticTarget applyCollisions() {
      if (!this.entity.onGround) {
         if (this.entity.collidedHorizontally) {
            this.motion = ForgeEntity.getMotion(this.entity).withY(this.motion.y);
         }

         if (this.entity.collidedVertically) {
            this.motion = this.motion.withY(ForgeEntity.getMotion(this.entity).y);
         }
      }

      return this;
   }

   protected void removeDeadSources() {
      Iterator<Entity> iterator = this.sources.iterator();

      while(iterator.hasNext()) {
         Entity entity = (Entity)iterator.next();
         if (!entity.isEntityAlive()) {
            iterator.remove();
         }
      }

   }

   public boolean hasSources() {
      for(Entity entity : this.sources) {
         if (entity.isEntityAlive()) {
            return true;
         }
      }

      return false;
   }

   public void applyMotion() {
      this.removeDeadSources();
      this.applyFrictions();
      this.applyGravity();
      this.applyCollisions();
      this.applyLimitations();
      this.applyJumping();
      ForgeEntity.setMotion(this.entity, this.motion);
      MotionMsg message = MotionMsg.of(this.entity);
      if (ForgeEntity.isServerSide(this.entity)) {
         message.sendToAllTracking(this.entity);
      } else if (!this.entity.onGround) {
         message.sendToServer();
      }

   }
}
