package org.orecruncher.dsurround.client.fx.particle;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.client.particle.ParticleBubble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class ParticleBubbleBreath extends ParticleBubble {
   public ParticleBubbleBreath(@Nonnull Entity entity) {
      this(entity, false);
   }

   public ParticleBubbleBreath(@Nonnull Entity entity, boolean isDrowning) {
      super(entity.getEntityWorld(), 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      EaglercraftRandom random = XorShiftRandom.current();
      boolean isChild = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isChild();
      Vec3d eyePosition = this.eyePosition(entity).subtract(0.0D, isChild ? 0.05D : 0.1D, 0.0D);
      Vec3d look = entity.getLook(1.0F);
      Vec3d origin = eyePosition.add(look.scale(isChild ? 0.25D : 0.5D));
      this.setPosition(origin.x, origin.y, origin.z);
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      Vec3d trajectory = look.rotateYaw(random.nextFloat() * 2.0F).rotatePitch(random.nextFloat() * 2.0F).normalize();
      double factor = isDrowning ? 0.02D : 0.005D;
      this.motionX = trajectory.x * factor;
      this.motionZ = trajectory.z * factor;
      this.motionY = 0.06D;
      this.particleAlpha = 0.2F;
      this.particleGravity = 0.0F;
      this.particleScale *= isChild ? 0.125F : 0.25F;
      if (isDrowning) {
         this.particleScale *= 2.0F;
      }

   }

   protected Vec3d eyePosition(Entity e) {
      Vec3d t = e.getPositionEyes(1.0F);
      if (e.isSneaking()) {
         t = t.subtract(0.0D, 0.25D, 0.0D);
      }

      return t;
   }

   public boolean shouldDisableDepth() {
      return true;
   }
}
