package net.ilexiconn.llibrary.client.model.tools;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ControlledAnimation {
   private int timer = 0;
   private int duration;
   private int timerChange;

   public ControlledAnimation(int d) {
      this.duration = d;
   }

   public void setDuration(int d) {
      this.timer = 0;
      this.duration = d;
   }

   public int getTimer() {
      return this.timer;
   }

   public void setTimer(int time) {
      this.timer = time;
      if (this.timer > this.duration) {
         this.timer = this.duration;
      } else if (this.timer < 0) {
         this.timer = 0;
      }

   }

   public void resetTimer() {
      this.timer = 0;
   }

   public void increaseTimer() {
      if (this.timer < this.duration) {
         ++this.timer;
         this.timerChange = 1;
      }

   }

   public boolean canIncreaseTimer() {
      return this.timer < this.duration;
   }

   public void increaseTimer(int time) {
      int newTime = this.timer + time;
      if (newTime <= this.duration && newTime >= 0) {
         this.timer = newTime;
      } else {
         this.timer = newTime < 0 ? 0 : this.duration;
      }

   }

   public void decreaseTimer() {
      if ((double)this.timer > 0.0D) {
         --this.timer;
         this.timerChange = -1;
      }

   }

   public boolean canDecreaseTimer() {
      return (double)this.timer > 0.0D;
   }

   public void decreaseTimer(int time) {
      if ((double)(this.timer - time) > 0.0D) {
         this.timer -= time;
      } else {
         this.timer = 0;
      }

   }

   public float getAnimationFraction() {
      return (float)this.timer / (float)this.duration;
   }

   public float getAnimationProgressSmooth() {
      if ((double)this.timer > 0.0D) {
         return this.timer < this.duration ? (float)(1.0D / (1.0D + Math.exp(4.0D - 8.0D * (double)this.getAnimationFraction()))) : 1.0F;
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
      return this.timerChange == -1 ? MathHelper.sin(1.5707964F * this.getAnimationFraction()) * MathHelper.sin(1.5707964F * this.getAnimationFraction()) : (float)Math.pow((double)MathHelper.sin(1.5707964F * this.getAnimationFraction()), 10.0D);
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
      double x = (double)this.getAnimationFraction();
      double xi = Math.pow(x, (double)n);
      return (float)(xi / (xi + Math.pow(1.0D - x, (double)n)));
   }

   public float getAnimationProgressArcTan() {
      return (float)(0.5D + 0.49806511402130127D * Math.atan(3.14159265359D * ((double)this.getAnimationFraction() - 0.5D)));
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
