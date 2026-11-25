package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityParticle extends EntityThrowable {
   public int particleAge = 0;
   public int particleAgeMax = 20;
   public double particleGravity = 0.0D;
   public String texture;
   public ModInfo group;

   public EntityParticle(World world, double x, double y, double z, String texture, ModInfo group) {
      super(world);
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.lastTickPosX = x;
      this.lastTickPosY = y;
      this.lastTickPosZ = z;
      this.texture = texture;
      this.group = group;
   }

   protected void entityInit() {
   }

   public void onUpdate() {
      System.out.println("Doing something!");
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleAgeMax) {
         this.setDead();
      }

      this.motionY -= 0.04D * this.particleGravity;
      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }

   public boolean canAttackWithItem() {
      return false;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void onImpact(RayTraceResult rayTraceResult) {
   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
   }

   public ResourceLocation getTexture() {
      if (AssetManager.getTexture(this.texture) == null) {
         AssetManager.addTexture(this.texture, this.group, "textures/particles/" + this.texture.toLowerCase() + ".png");
      }

      return AssetManager.getTexture(this.texture);
   }
}
