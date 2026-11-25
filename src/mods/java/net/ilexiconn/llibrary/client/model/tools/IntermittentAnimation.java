package net.ilexiconn.llibrary.client.model.tools;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.ilexiconn.llibrary.server.entity.IIntermittentEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IntermittentAnimation<T extends Entity & IIntermittentEntity> {
   private EaglercraftRandom rand = new EaglercraftRandom();
   private T entity;
   private int timeRunning;
   private int duration;
   private boolean isRunning;
   private int runDirection;
   private int timeIdle;
   private int minIdleTime;
   private int startProbability;
   private boolean isOperator;
   private byte id;

   public IntermittentAnimation(T entity, int duration, int intervalDuration, int startPropbability, boolean isOperator) {
      this.entity = entity;
      this.duration = duration;
      this.minIdleTime = intervalDuration;
      this.startProbability = startPropbability;
      this.isOperator = isOperator;
      this.timeRunning = 0;
      this.isRunning = false;
      this.runDirection = -1;
   }

   public void setID(byte id) {
      this.id = id;
   }

   public void setDuration(int duration) {
      this.timeRunning = 0;
      this.duration = duration;
   }

   public int getTimeRunning() {
      return this.timeRunning;
   }

   public void setTimeRunning(int timeRunning) {
      this.timeRunning = timeRunning;
      if (this.timeRunning > this.duration) {
         this.timeRunning = this.duration;
      } else if (this.timeRunning < 0) {
         this.timeRunning = 0;
      }

   }

   public void resetTimeRunning() {
      this.timeRunning = 0;
   }

   public void update() {
      if (this.isRunning) {
         if (this.timeRunning < this.duration && this.timeRunning > 0) {
            this.timeRunning += this.runDirection;
         } else {
            if (this.timeRunning >= this.duration) {
               this.timeRunning = this.duration;
            } else if (this.timeRunning <= 0) {
               this.timeRunning = 0;
            }

            this.timeIdle = 0;
            this.isRunning = false;
         }
      } else if (this.isOperator) {
         if (this.timeIdle < this.minIdleTime) {
            ++this.timeIdle;
         } else if (this.rand.nextInt(this.startProbability) == 0) {
            this.start();
            this.entity.world.setEntityState(this.entity, (byte)(((IIntermittentEntity)this.entity).getOffsetEntityState() + this.id));
         }
      }

   }

   public void start() {
      this.runDirection = -this.runDirection;
      this.timeRunning += this.runDirection;
      this.isRunning = true;
   }

   public void stop() {
      if (this.timeRunning > 0) {
         --this.timeRunning;
      } else {
         this.timeRunning = 0;
         this.isRunning = false;
         this.timeIdle = 0;
         this.runDirection = 1;
      }

   }

   public void stop(int timeDelta) {
      if (this.timeRunning - timeDelta > 0) {
         this.timeRunning -= timeDelta;
      } else {
         this.timeRunning = 0;
         this.isRunning = false;
         this.timeIdle = 0;
         this.runDirection = 1;
      }

   }

   public float getAnimationFraction() {
      return (float)this.timeRunning / (float)this.duration;
   }

   public float getAnimationProgressSmooth() {
      if (this.timeRunning > 0) {
         return this.timeRunning < this.duration ? (float)(1.0D / (1.0D + Math.exp(4.0D - 8.0D * (double)this.getAnimationFraction()))) : 1.0F;
      } else {
         return 0.0F;
      }
   }

   public float getAnimationProgressSteep() {
      return (float)(1.0D / (1.0D + Math.exp(6.0D - 12.0D * (double)this.getAnimationFraction())));
   }

   public float getAnimationProgressSin() {
      return MathHelper.sin(1.5707964F * this.getAnimationFraction());
   }

   public float getAnimationProgressSinSqrt() {
      float result = MathHelper.sin(1.5707964F * this.getAnimationFraction());
      return result * result;
   }

   public float getAnimationProgressSinToTen() {
      return (float)Math.pow((double)MathHelper.sin(1.5707964F * this.getAnimationFraction()), 10.0D);
   }

   public float getAnimationProgressSinToTenWithoutReturn() {
      return this.runDirection == -1 ? MathHelper.sin(1.5707964F * this.getAnimationFraction()) * MathHelper.sin(1.5707964F * this.getAnimationFraction()) : (float)Math.pow((double)MathHelper.sin(1.5707964F * this.getAnimationFraction()), 10.0D);
   }

   public float getAnimationProgressSinPowerOf(int i) {
      return (float)Math.pow((double)MathHelper.sin(1.5707964F * this.getAnimationFraction()), (double)i);
   }

   public float getAnimationProgressPoly2() {
      float x = this.getAnimationFraction();
      float x2 = x * x;
      return x2 / (x2 + (1.0F - x) * (1.0F - x));
   }

   public float getAnimationProgressPoly3() {
      float x = this.getAnimationFraction();
      float x3 = x * x * x;
      return x3 / (x3 + (1.0F - x) * (1.0F - x) * (1.0F - x));
   }

   public float getAnimationProgressPolyN(int n) {
      double x = (double)(this.timeRunning / this.duration);
      double xi = Math.pow(x, (double)n);
      return (float)(xi / (xi + Math.pow(1.0D - x, (double)n)));
   }

   public float getAnimationProgressArcTan() {
      return (float)(0.5D + 0.49806511402130127D * Math.atan(3.14159265359D * ((double)(this.timeRunning / this.duration) - 0.5D)));
   }

   public float getAnimationProgressTemporary() {
      float x = 6.2831855F * this.getAnimationFraction();
      return 0.5F - 0.5F * MathHelper.cos(x + MathHelper.sin(x));
   }

   public float getAnimationProgressTemporaryFS() {
      float x = 3.1415927F * this.getAnimationFraction();
      return MathHelper.sin(x + MathHelper.sin(x));
   }

   public float getAnimationProgressTemporaryInvesed() {
      float x = 6.2831855F * this.getAnimationFraction();
      return 0.5F + 0.5F * MathHelper.cos(x + MathHelper.sin(x));
   }
}
