package org.orecruncher.dsurround.client.fx.particle;

import java.util.Random;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedField.FloatField;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class ParticleFrostBreath extends ParticleCloud {
   protected static final FloatField<ParticleCloud> sizeField = new FloatField(ParticleCloud.class, "oSize", "oSize");

   public ParticleFrostBreath(Entity entity) {
      super(entity.getEntityWorld(), 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      Random random = XorShiftRandom.current();
      boolean isChild = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isChild();
      Vec3d eyePosition = this.eyePosition(entity).subtract(0.0D, isChild ? 0.1D : 0.2D, 0.0D);
      Vec3d look = entity.getLook(1.0F);
      Vec3d origin = eyePosition.add(look.scale(isChild ? 0.25D : 0.5D));
      this.setPosition(origin.x, origin.y, origin.z);
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      Vec3d trajectory = look.rotateYaw(random.nextFloat() * 2.0F).rotatePitch(random.nextFloat() * 2.0F).normalize();
      this.motionX = trajectory.x * 0.01D;
      this.motionY = trajectory.y * 0.01D;
      this.motionZ = trajectory.z * 0.01D;
      this.particleAlpha = 0.2F;
      this.particleGravity = 0.0F;
      this.particleScale *= isChild ? 0.125F : 0.25F;
      sizeField.set(this, this.particleScale);
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

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9599999785423279D;
      this.motionY *= 0.9599999785423279D;
      this.motionZ *= 0.9599999785423279D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }
}
