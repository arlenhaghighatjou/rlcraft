package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTransformationStack {
   private final Deque<EntityTransformationStack.EntityTransformation> stack = new ArrayDeque();
   private final Entity entity;

   public EntityTransformationStack(Entity ent) {
      this.entity = ent;
   }

   public void push() {
      this.stack.push(new EntityTransformationStack.EntityTransformationSeparator());
   }

   public void pop() {
      EntityTransformationStack.EntityTransformation last;
      while((last = (EntityTransformationStack.EntityTransformation)this.stack.poll()) != null && !(last instanceof EntityTransformationStack.EntityTransformationSeparator)) {
         last.revert();
      }

   }

   public void translate(double x, double y, double z) {
      EntityTransformationStack.EntityTransformation transformation = new EntityTransformationStack.EntityTransformation(x, y, z, 0.0F, 0.0F, 0.0F);
      this.stack.push(transformation);
      transformation.apply();
   }

   public void rotate(float yaw, float pitch, float roll) {
      EntityTransformationStack.EntityTransformation transformation = new EntityTransformationStack.EntityTransformation(0.0D, 0.0D, 0.0D, yaw, pitch, roll);
      this.stack.push(transformation);
      transformation.apply();
   }

   public EntityTransformationStack moveEntity(double destX, double destY, double destZ, float[] pos, float[] rot, float partialTicks) {
      this.push();
      double ePosX = this.entity.lastTickPosX + (this.entity.posX - this.entity.lastTickPosX) * (double)partialTicks;
      double ePosY = this.entity.lastTickPosY + (this.entity.posY - this.entity.lastTickPosY) * (double)partialTicks + (double)this.entity.getEyeHeight();
      double ePosZ = this.entity.lastTickPosZ + (this.entity.posZ - this.entity.lastTickPosZ) * (double)partialTicks;
      this.translate(destX - ePosX + (double)pos[0], destY - ePosY + (double)pos[1], destZ - ePosZ + (double)pos[2]);
      this.rotate(rot[0], rot[1], rot[2]);
      return this;
   }

   public void reset() {
      this.pop();
   }

   private class EntityTransformation {
      private double x;
      private double y;
      private double z;
      private float yaw;
      private float pitch;
      private float roll;

      public EntityTransformation(double x, double y, double z, float yaw, float pitch, float roll) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
         this.pitch = pitch;
         this.roll = roll;
      }

      public EntityTransformation() {
      }

      public void apply() {
         if (EntityTransformationStack.this.entity.world.isRemote) {
            this.applyClient();
         } else {
            this.applyMain();
         }

      }

      @SideOnly(Side.CLIENT)
      private void applyClient() {
         float prevYaw = 0.0F;
         float yaw = 0.0F;
         float prevPitch = 0.0F;
         float pitch = 0.0F;
         if (EntityTransformationStack.this.entity instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP)EntityTransformationStack.this.entity;
            prevYaw = player.prevRotationYaw - player.prevRenderArmYaw;
            yaw = player.rotationYaw - player.renderArmYaw;
            prevPitch = player.prevRotationPitch - player.prevRenderArmPitch;
            pitch = player.rotationPitch - player.renderArmPitch;
         }

         this.applyMain();
         if (EntityTransformationStack.this.entity instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP)EntityTransformationStack.this.entity;
            player.prevRenderArmYaw = player.prevRotationYaw;
            player.renderArmYaw = player.rotationYaw;
            player.prevRenderArmPitch = player.prevRotationPitch;
            player.renderArmPitch = player.rotationPitch;
            player.prevRenderArmYaw -= prevYaw;
            player.renderArmYaw -= yaw;
            player.prevRenderArmPitch -= prevPitch;
            player.renderArmPitch -= pitch;
         }

      }

      private void applyMain() {
         Entity var10000 = EntityTransformationStack.this.entity;
         var10000.posX += this.x;
         var10000 = EntityTransformationStack.this.entity;
         var10000.posY += this.y;
         var10000 = EntityTransformationStack.this.entity;
         var10000.posZ += this.z;
         var10000 = EntityTransformationStack.this.entity;
         var10000.prevPosX += this.x;
         var10000 = EntityTransformationStack.this.entity;
         var10000.prevPosY += this.y;
         var10000 = EntityTransformationStack.this.entity;
         var10000.prevPosZ += this.z;
         var10000 = EntityTransformationStack.this.entity;
         var10000.lastTickPosX += this.x;
         var10000 = EntityTransformationStack.this.entity;
         var10000.lastTickPosY += this.y;
         var10000 = EntityTransformationStack.this.entity;
         var10000.lastTickPosZ += this.z;
         EntityTransformationStack.this.entity.rotationPitch += this.pitch;
         EntityTransformationStack.this.entity.rotationYaw += this.yaw;
         EntityTransformationStack.this.entity.prevRotationPitch += this.pitch;
         EntityTransformationStack.this.entity.prevRotationYaw += this.yaw;
         if (EntityTransformationStack.this.entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase)EntityTransformationStack.this.entity;
            living.rotationYawHead += this.yaw;
            living.prevRotationYawHead += this.yaw;
         }

      }

      public void revert() {
         if (EntityTransformationStack.this.entity.world.isRemote) {
            this.revertClient();
         } else {
            this.revertMain();
         }

      }

      @SideOnly(Side.CLIENT)
      private void revertClient() {
         float prevYaw = 0.0F;
         float yaw = 0.0F;
         float prevPitch = 0.0F;
         float pitch = 0.0F;
         if (EntityTransformationStack.this.entity instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP)EntityTransformationStack.this.entity;
            prevYaw = player.prevRotationYaw - player.prevRenderArmYaw;
            yaw = player.rotationYaw - player.renderArmYaw;
            prevPitch = player.prevRotationPitch - player.prevRenderArmPitch;
            pitch = player.rotationPitch - player.renderArmPitch;
         }

         this.revertMain();
         if (EntityTransformationStack.this.entity instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP)EntityTransformationStack.this.entity;
            player.prevRenderArmYaw = player.prevRotationYaw;
            player.renderArmYaw = player.rotationYaw;
            player.prevRenderArmPitch = player.prevRotationPitch;
            player.renderArmPitch = player.rotationPitch;
            player.prevRenderArmYaw -= prevYaw;
            player.renderArmYaw -= yaw;
            player.prevRenderArmPitch -= prevPitch;
            player.renderArmPitch -= pitch;
         }

      }

      private void revertMain() {
         Entity var10000 = EntityTransformationStack.this.entity;
         var10000.posX -= this.x;
         var10000 = EntityTransformationStack.this.entity;
         var10000.posY -= this.y;
         var10000 = EntityTransformationStack.this.entity;
         var10000.posZ -= this.z;
         var10000 = EntityTransformationStack.this.entity;
         var10000.prevPosX -= this.x;
         var10000 = EntityTransformationStack.this.entity;
         var10000.prevPosY -= this.y;
         var10000 = EntityTransformationStack.this.entity;
         var10000.prevPosZ -= this.z;
         var10000 = EntityTransformationStack.this.entity;
         var10000.lastTickPosX -= this.x;
         var10000 = EntityTransformationStack.this.entity;
         var10000.lastTickPosY -= this.y;
         var10000 = EntityTransformationStack.this.entity;
         var10000.lastTickPosZ -= this.z;
         EntityTransformationStack.this.entity.rotationPitch -= this.pitch;
         EntityTransformationStack.this.entity.rotationYaw -= this.yaw;
         EntityTransformationStack.this.entity.prevRotationPitch -= this.pitch;
         EntityTransformationStack.this.entity.prevRotationYaw -= this.yaw;
         if (EntityTransformationStack.this.entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase)EntityTransformationStack.this.entity;
            living.rotationYawHead -= this.yaw;
            living.prevRotationYawHead -= this.yaw;
         }

      }
   }

   private class EntityTransformationSeparator extends EntityTransformationStack.EntityTransformation {
      private EntityTransformationSeparator() {
         super();
      }
   }
}
