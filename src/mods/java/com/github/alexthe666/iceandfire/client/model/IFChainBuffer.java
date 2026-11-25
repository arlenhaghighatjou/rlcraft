package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.entity.IFlapable;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IFChainBuffer {
   private int yawTimer;
   private float yawVariation;
   private int pitchTimer;
   private float pitchVariation;
   private float prevYawVariation;
   private float prevPitchVariation;

   public IFChainBuffer() {
   }

   public void resetRotations() {
      this.yawVariation = 0.0F;
      this.pitchVariation = 0.0F;
      this.prevYawVariation = 0.0F;
      this.prevPitchVariation = 0.0F;
   }

   private boolean compareDouble(double a, double b) {
      double c = a - b;
      return Math.abs(c - 1.0D) <= 0.01D;
   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevYawVariation = this.yawVariation;
      if (!this.compareDouble((double)entity.renderYawOffset, (double)entity.prevRenderYawOffset) && MathHelper.abs(this.yawVariation) < maxAngle) {
         this.yawVariation += MathHelper.clamp((entity.prevRenderYawOffset - entity.renderYawOffset) / divisor, -maxAngle, maxAngle);
      }

      if (this.yawVariation > 1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      } else if (this.yawVariation < -1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation += angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      }

   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevPitchVariation = this.pitchVariation;
      this.pitchVariation = entity.rotationPitch;
   }

   public void calculateChainFlapBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevYawVariation = this.yawVariation;
      if (!this.compareDouble((double)entity.renderYawOffset, (double)entity.prevRenderYawOffset) && MathHelper.abs(this.yawVariation) < maxAngle) {
         this.yawVariation += MathHelper.clamp((entity.prevRenderYawOffset - entity.renderYawOffset) / divisor, -maxAngle, maxAngle);
         if (entity instanceof IFlapable && (double)Math.abs(entity.prevRenderYawOffset - entity.renderYawOffset) > 15.0D) {
            ((IFlapable)entity).flapWings();
         }
      }

      if (this.yawVariation > 1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      } else if (this.yawVariation < -1.0F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation += angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      }

   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainSwingBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainWaveBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainFlapBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainFlapBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void applyChainSwingBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;

      for(ModelRenderer box : boxes) {
         box.rotateAngleY += rotateAmount;
      }

   }

   public void applyChainWaveBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevPitchVariation, this.pitchVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;

      for(ModelRenderer box : boxes) {
         box.rotateAngleX += rotateAmount;
      }

   }

   public void applyChainFlapBuffer(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;

      for(ModelRenderer box : boxes) {
         box.rotateAngleZ += rotateAmount;
      }

   }

   public void applyChainFlapBufferReverse(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;

      for(ModelRenderer box : boxes) {
         box.rotateAngleZ -= rotateAmount * 0.5F;
      }

   }

   public void applyChainSwingBufferReverse(ModelRenderer... boxes) {
      float rotateAmount = 0.017453292F * ClientUtils.interpolate(this.prevYawVariation, this.yawVariation, LLibrary.PROXY.getPartialTicks()) / (float)boxes.length;

      for(ModelRenderer box : boxes) {
         box.rotateAngleY -= rotateAmount;
      }

   }
}
