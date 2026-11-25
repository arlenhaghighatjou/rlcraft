package me.ichun.mods.googlyeyes.common.tracker;

import java.util.Random;
import javax.annotation.Nonnull;
import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class GooglyTracker {
   public final EntityLivingBase parent;
   public final HeadBase helper;
   public final Random rand;
   public final float renderChance;
   public boolean shouldUpdate = true;
   public long lastUpdateRequest;
   public double motionX;
   public double motionY;
   public double motionZ;
   public GooglyTracker.EyeInfo[] eyes;

   public GooglyTracker(@Nonnull EntityLivingBase parent, @Nonnull HeadBase helper) {
      this.parent = parent;
      this.helper = helper;
      this.rand = new Random((long)(Math.abs(parent.hashCode()) * 8134));
      this.renderChance = this.rand.nextFloat();
      this.eyes = new GooglyTracker.EyeInfo[helper.getEyeCount(parent)];

      for(int i = 0; i < this.eyes.length; ++i) {
         this.eyes[i] = new GooglyTracker.EyeInfo();
      }

      this.update();
   }

   public void update() {
      if (this.shouldUpdate && this.shouldRender()) {
         this.shouldUpdate = false;
         this.motionX = this.parent.posX - this.parent.prevPosX;
         this.motionY = this.parent.posY - this.parent.prevPosY;
         this.motionZ = this.parent.posZ - this.parent.prevPosZ;

         for(int i = 0; i < this.eyes.length; ++i) {
            this.eyes[i].update(i, this, this.motionX, this.motionY, this.motionZ);
         }

      }
   }

   public void requireUpdate() {
      this.shouldUpdate = true;
      this.lastUpdateRequest = (long)iChunUtil.eventHandlerClient.ticks;
   }

   public boolean shouldRender() {
      return this.renderChance < (float)GooglyEyes.config.googlyEyeChance / 100.0F || GooglyEyes.config.allPlayersHaveGooglyEyes == 1 && this.parent instanceof EntityPlayer;
   }

   public class EyeInfo {
      public float prevRotationYaw;
      public float rotationYaw;
      public float prevRotationPitch;
      public float rotationPitch;
      public float prevRotationRoll;
      public float rotationRoll;
      public float prevDeltaX;
      public float prevDeltaY;
      public float deltaX;
      public float deltaY;
      public float momentumX;
      public float momentumY;

      public EyeInfo() {
         this.prevDeltaY = this.deltaY = -1.0F;
      }

      public void update(int eye, GooglyTracker parent, double motionX, double motionY, double motionZ) {
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
         this.prevRotationRoll = this.rotationRoll;
         this.rotationYaw = GooglyTracker.this.helper.getHeadYaw(parent.parent, 1.0F, eye);
         this.rotationPitch = GooglyTracker.this.helper.getHeadPitch(parent.parent, 1.0F, eye);
         this.rotationRoll = GooglyTracker.this.helper.getHeadRoll(parent.parent, 1.0F, eye);
         this.prevDeltaX = this.deltaX;
         this.prevDeltaY = this.deltaY;
         float yawDiff = this.rotationYaw - this.prevRotationYaw;
         float pitchDiff = this.rotationPitch - this.prevRotationPitch;
         float rollDiff = this.rotationRoll - this.prevRotationRoll;
         this.momentumY = (float)((double)this.momentumY + motionY * 1.5D + (motionX + motionZ) * GooglyTracker.this.rand.nextGaussian() * 0.75D + (double)(pitchDiff / 45.0F) + (double)(yawDiff / 180.0F) + (double)rollDiff * GooglyTracker.this.rand.nextGaussian() * 0.05000000074505806D);
         this.momentumX = (float)((double)this.momentumX - ((motionX + motionZ) * GooglyTracker.this.rand.nextGaussian() * 0.4000000059604645D + (double)(yawDiff / 45.0F) + (double)rollDiff * GooglyTracker.this.rand.nextGaussian() * 0.05000000074505806D));
         float momentumLoss = 0.9F;
         float newDeltaX = this.deltaX + this.momentumX;
         float newDeltaY = this.deltaY + this.momentumY;
         if (newDeltaX < -1.0F || newDeltaX > 1.0F) {
            float newMo = this.momentumX * -momentumLoss;
            float randFloat = 0.8F + GooglyTracker.this.rand.nextFloat() * 0.2F;
            this.momentumX = newMo * randFloat;
            this.momentumY += newMo * randFloat * (GooglyTracker.this.rand.nextFloat() > 0.5F ? 1.0F : -1.0F);
         }

         if (newDeltaY >= -1.0F && newDeltaY <= 1.0F) {
            this.momentumY -= MathHelper.clamp(1.0F + this.deltaY, 0.0F, 0.1999F);
         } else {
            float newMo = this.momentumY * -momentumLoss;
            float randFloat = 0.8F + GooglyTracker.this.rand.nextFloat() * 0.2F;
            this.momentumY = newMo * randFloat;
            this.momentumX += newMo * (1.0F - randFloat) * (GooglyTracker.this.rand.nextFloat() > 0.5F ? 1.0F : -1.0F);
         }

         this.momentumX *= 0.95F;
         this.deltaX *= 0.95F;
         if (Math.abs(this.momentumX) < 0.03F) {
            this.momentumX = 0.0F;
         }

         if (Math.abs(this.deltaX) < 0.03F) {
            this.deltaX = 0.0F;
         }

         float maxMomentum = 1.3F;
         this.momentumX = MathHelper.clamp(this.momentumX, -maxMomentum, maxMomentum);
         this.momentumY = MathHelper.clamp(this.momentumY, -maxMomentum, maxMomentum);
         this.deltaX += this.momentumX;
         this.deltaY += this.momentumY;
         this.deltaX = MathHelper.clamp(this.deltaX, -1.0F, 1.0F);
         this.deltaY = MathHelper.clamp(this.deltaY, -1.0F, 1.0F);
      }
   }
}
