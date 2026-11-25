package me.ichun.mods.ichunutil.api.client.head;

import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.BooleanSupplier;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelPolarBear;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HeadBase<E extends EntityLivingBase> {
   public static BooleanSupplier acidEyesBooleanSupplier = () -> {
      // $FF: Couldn't be decompiled
   };
   public ModelRenderer[] headModel = null;
   public Object headInfo = null;
   public float[] headJoint = new float[3];
   public float[] eyeOffset = new float[]{0.0F, 0.25F, 0.25F};
   public float[] irisColour = new float[]{0.9F, 0.9F, 0.9F};
   public float[] pupilColour = new float[]{0.0F, 0.0F, 0.0F};
   public float halfInterpupillaryDistance = 0.125F;
   public float eyeScale = 0.75F;
   public float headTop = 0.5F;
   public float headFront = 0.25F;
   public EaglercraftRandom livingRand = new EaglercraftRandom();
   public int[] acidTime;

   public HeadBase() {
   }

   public HeadBase setHeadJoint(float jointX, float jointY, float jointZ) {
      this.headJoint = new float[]{jointX, jointY, jointZ};
      return this;
   }

   public HeadBase setEyeOffset(float offsetX, float offsetY, float offsetZ) {
      this.eyeOffset = new float[]{offsetX, offsetY, offsetZ};
      return this;
   }

   public HeadBase setHalfInterpupillaryDistance(float dist) {
      this.halfInterpupillaryDistance = dist;
      return this;
   }

   public HeadBase setEyeScale(float scale) {
      this.eyeScale = scale;
      return this;
   }

   public float[] getHeadJointOffset(E living, float partialTick, int eye) {
      this.headJoint[0] = -(this.headModel[0].rotationPointX / 16.0F);
      this.headJoint[1] = -(this.headModel[0].rotationPointY / 16.0F);
      this.headJoint[2] = -(this.headModel[0].rotationPointZ / 16.0F);
      return this.headJoint;
   }

   public float[] getEyeOffsetFromJoint(E living, float partialTick, int eye) {
      return this.eyeOffset;
   }

   public int getEyeCount(E living) {
      return 2;
   }

   public float maxEyeSizeGrowth(E living, int eye) {
      return 0.0F;
   }

   public float getEyeSideOffset(E living, float partialTick, int eye) {
      return eye == 0 ? this.halfInterpupillaryDistance : -this.halfInterpupillaryDistance;
   }

   public float getEyeScale(E living, float partialTick, int eye) {
      return this.eyeScale;
   }

   public float getEyeRotation(E living, float partialTick, int eye) {
      return 0.0F;
   }

   public float getPupilScale(E living, float partialTick, int eye) {
      if (!acidEyesBooleanSupplier.getAsBoolean() && ((Integer)living.getDataManager().get(EntityLivingBase.POTION_EFFECTS)).intValue() <= 0) {
         return 1.0F + 0.35F * ((float)living.deathTime + partialTick) / 20.0F;
      } else {
         this.livingRand.setSeed((long)(Math.abs(living.hashCode()) * 1000));
         int eyeCount = this.getEyeCount(living);
         if (this.acidTime == null || this.acidTime.length < eyeCount) {
            this.acidTime = new int[eyeCount];
         }

         for(int i = 0; i < eyeCount; ++i) {
            this.acidTime[i] = 20 + this.livingRand.nextInt(20);
         }

         return 0.3F + ((float)Math.sin(Math.toRadians((double)(((float)living.ticksExisted + partialTick) / (float)this.acidTime[eye] * 360.0F))) + 1.0F) / 2.0F;
      }
   }

   public float[] getIrisColours(E living, float partialTick, int eye) {
      return this.irisColour;
   }

   public float[] getPupilColours(E living, float partialTick, int eye) {
      return this.pupilColour;
   }

   public float getHeadTopOffsetFromJoint(E living, float partialTick, int eye) {
      return this.headTop;
   }

   public float getHeadFrontOffsetFromJoint(E living, float partialTick, int eye) {
      return this.headFront;
   }

   public float getHeadYaw(E living, float partialTick, int eye) {
      return (float)Math.toDegrees((double)this.headModel[0].rotateAngleY);
   }

   public float getHeadPitch(E living, float partialTick, int eye) {
      return (float)Math.toDegrees((double)this.headModel[0].rotateAngleX);
   }

   public float getHeadRoll(E living, float partialTick, int eye) {
      return (float)Math.toDegrees((double)this.headModel[0].rotateAngleZ);
   }

   public boolean affectedByInvisibility(E living, int eye) {
      return true;
   }

   public boolean doesEyeGlow(E living, int eye) {
      return false;
   }

   public void preChildEntHeadRenderCalls(E living, RenderLivingBase render) {
      if (living.isChild()) {
         float modelScale = 0.0625F;
         ModelBase model = render.getMainModel();
         if (model instanceof ModelBiped) {
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * modelScale, 0.0F);
         } else if (model instanceof ModelQuadruped) {
            if (model instanceof ModelPolarBear) {
               GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
            }

            GlStateManager.translate(0.0F, ((ModelQuadruped)model).childYOffset * modelScale, ((ModelQuadruped)model).childZOffset * modelScale);
         } else if (model instanceof ModelChicken) {
            GlStateManager.translate(0.0F, 5.0F * modelScale, 2.0F * modelScale);
         }
      }

   }

   public HeadBase clone() {
      try {
         HeadBase clone = (HeadBase)this.getClass().newInstance();

         for(int i = 0; i < 3; ++i) {
            clone.headJoint[i] = this.headJoint[i];
            clone.eyeOffset[i] = this.eyeOffset[i];
         }

         clone.halfInterpupillaryDistance = this.halfInterpupillaryDistance;
         clone.eyeScale = this.eyeScale;
         return clone;
      } catch (Exception var3) {
         return new HeadBase();
      }
   }

   public static void registerHeadHelper(Class<? extends EntityLivingBase> clz, HeadBase base) {
      try {
         Class clzz = Class.forName("me.ichun.mods.ichunutil.client.entity.head.HeadHandler");
         Map modelOffsetHelpers = (Map)ObfuscationReflectionHelper.getPrivateValue(clzz, (Object)null, "modelOffsetHelpers");
         modelOffsetHelpers.put(clz, base);
      } catch (Exception var4) {
         System.out.println("Error registering head helper class: " + clz.getName());
         var4.printStackTrace();
      }

   }
}
