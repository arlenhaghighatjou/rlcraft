package com.endertech.minecraft.mods.adhooks.motion;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SinglePlayerTarget extends EntityTarget<EntityPlayerSP> {
   protected Vect3d durableMotion = Vect3d.ZERO;
   protected int duration;
   protected boolean swinging;

   public SinglePlayerTarget(EntityPlayerSP target, double damping) {
      super(target, damping);
   }

   public boolean exists() {
      return super.exists() || ((EntityPlayerSP)this.target).isEntityAlive() && this.durableMotion.notZero();
   }

   public void setDurableMotion(Vect3d motion, int duration) {
      this.durableMotion = motion;
      this.duration = duration;
   }

   public void setSwinging(boolean value) {
      this.swinging = value;
   }

   protected Vect3d getSwingMotion() {
      Vect3d motion = ForgeEntity.getMotion(this.target);
      Vect3d movement = Vect3d.from((double)((EntityPlayerSP)this.target).moveStrafing, 0.0D, (double)((EntityPlayerSP)this.target).moveForward).rotateAroundY(-((EntityPlayerSP)this.target).rotationYaw);
      return Vect3d.scalarMult(movement, motion) < 0.0D ? Vect3d.ZERO : movement.projectOn(motion).scale(0.1D);
   }

   public void applyMotions() {
      if (this.swinging) {
         this.addDampedMotion(this.getSwingMotion());
      }

      this.swinging = false;
      if (this.duration > 0) {
         --this.duration;
         this.addDampedMotion(this.durableMotion);
      } else {
         this.durableMotion = Vect3d.ZERO;
      }

      super.applyMotions();
   }
}
