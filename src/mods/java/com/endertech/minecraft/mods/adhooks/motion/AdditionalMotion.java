package com.endertech.minecraft.mods.adhooks.motion;

import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import net.minecraft.entity.Entity;

/** @deprecated */
@Deprecated
public class AdditionalMotion {
   protected static final FloatBounds VERT_PITCH_BOUNDS = FloatBounds.from(Float.valueOf(87.0F), Float.valueOf(93.0F));
   protected final Entity entity;
   protected final Vect3d tension;
   protected final boolean almostVertical;
   protected Vect3d motion;

   public static AdditionalMotion of(Entity entity, Vect3d tension) {
      return new AdditionalMotion(entity, tension);
   }

   public AdditionalMotion(Entity entity, Vect3d tension) {
      this.entity = entity;
      this.tension = tension;
      this.motion = tension;
      this.almostVertical = VERT_PITCH_BOUNDS.encloses(Float.valueOf(tension.pitch()));
   }

   public AdditionalMotion add(Vect3d motion) {
      this.motion = this.motion.add(motion);
      return this;
   }

   public AdditionalMotion apply() {
      ForgeEntity.addMotion(this.entity, this.motion);
      this.motion = Vect3d.ZERO;
      return this;
   }

   public AdditionalMotion swingAround(Vect3d pivotPosition, Vect3d targetPosition, Vect3d boostDirection) {
      if (this.tension.isZero()) {
         return this;
      } else {
         double radius = Vect3d.distance(pivotPosition, targetPosition);
         double instantSpeed = this.getInstantSpeed(radius);
         Vect3d entityMotion = this.getEntityMotion();
         Vect3d rotationCenter = pivotPosition.withY(targetPosition.y).subtract(targetPosition);
         Vect3d swing = entityMotion.subtract(entityMotion.projectOn(this.tension)).scale(instantSpeed);
         double centerForce = Vect3d.scalarMult(entityMotion, rotationCenter);
         if (centerForce < 0.0D && instantSpeed < 1.0D) {
            swing = swing.scale(instantSpeed);
         }

         if (swing.length() > 0.15D) {
            swing = swing.resize(0.15D);
         }

         if (this.entity.isSneaking()) {
            swing = swing.scale(0.6D);
         }

         if (boostDirection.notZero() && pivotPosition.y > targetPosition.y) {
            float pitch = pivotPosition.subtract(targetPosition).pitch();
            float pitchFactor = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(90.0F)).approxFactor(Float.valueOf(pitch));
            pitchFactor = ForgeBounds.FACTOR.getFloatBounds().enclose(Float.valueOf(pitchFactor)).floatValue();
            double motionLength = this.motion.length();
            double boost = motionLength * Math.pow(instantSpeed, 0.3846153846153846D) * (double)pitchFactor;
            if (boost > 1.0D) {
               boost = 1.0D;
            }

            this.add(boostDirection.scale(boost));
         }

         return this.add(swing);
      }
   }

   protected double getInstantSpeed(double radius) {
      return 0.02D * Math.sqrt(radius);
   }

   public AdditionalMotion reduceBounce(float reduction) {
      Vect3d bounce = this.getEntityMotion().projectOn(this.tension);
      return this.add(bounce.invert().scale((double)reduction));
   }

   protected Vect3d getEntityMotion() {
      return ForgeEntity.getMotion(this.entity);
   }
}
