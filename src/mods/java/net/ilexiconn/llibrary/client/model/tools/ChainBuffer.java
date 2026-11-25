package net.ilexiconn.llibrary.client.model.tools;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChainBuffer {
   private int yawTimer;
   private float yawVariation;
   private int pitchTimer;
   private float pitchVariation;
   private float prevYawVariation;
   private float prevPitchVariation;

   public ChainBuffer() {
   }

   public void resetRotations() {
      this.yawVariation = 0.0F;
      this.pitchVariation = 0.0F;
      this.prevYawVariation = 0.0F;
      this.prevPitchVariation = 0.0F;
   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, float divisor, EntityLivingBase entity) {
      this.prevYawVariation = this.yawVariation;
      if (entity.renderYawOffset != entity.prevRenderYawOffset && MathHelper.abs(this.yawVariation) < maxAngle) {
         this.yawVariation += (entity.prevRenderYawOffset - entity.renderYawOffset) / divisor;
      }

      if (this.yawVariation > 0.7F * angleDecrement) {
         if (this.yawTimer > bufferTime) {
            this.yawVariation -= angleDecrement;
            if (MathHelper.abs(this.yawVariation) < angleDecrement) {
               this.yawVariation = 0.0F;
               this.yawTimer = 0;
            }
         } else {
            ++this.yawTimer;
         }
      } else if (this.yawVariation < -0.7F * angleDecrement) {
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
      if (entity.rotationPitch != entity.prevRotationPitch && MathHelper.abs(this.pitchVariation) < maxAngle) {
         this.pitchVariation += (entity.prevRotationPitch - entity.rotationPitch) / divisor;
      }

      if (this.pitchVariation > 0.7F * angleDecrement) {
         if (this.pitchTimer > bufferTime) {
            this.pitchVariation -= angleDecrement;
            if (MathHelper.abs(this.pitchVariation) < angleDecrement) {
               this.pitchVariation = 0.0F;
               this.pitchTimer = 0;
            }
         } else {
            ++this.pitchTimer;
         }
      } else if (this.pitchVariation < -0.7F * angleDecrement) {
         if (this.pitchTimer > bufferTime) {
            this.pitchVariation += angleDecrement;
            if (MathHelper.abs(this.pitchVariation) < angleDecrement) {
               this.pitchVariation = 0.0F;
               this.pitchTimer = 0;
            }
         } else {
            ++this.pitchTimer;
         }
      }

   }

   public void calculateChainSwingBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainSwingBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
   }

   public void calculateChainWaveBuffer(float maxAngle, int bufferTime, float angleDecrement, EntityLivingBase entity) {
      this.calculateChainWaveBuffer(maxAngle, bufferTime, angleDecrement, 1.0F, entity);
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
}
