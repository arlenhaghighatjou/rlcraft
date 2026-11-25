package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class JumpAnimationBit<T extends BipedEntityData<?>> extends AnimationBit<T> {
   private static final String[] ACTIONS = new String[]{"jump"};

   public JumpAnimationBit() {
   }

   public String[] getActions(T entityData) {
      return ACTIONS;
   }

   public void onPlay(T data) {
      data.renderRotation.identity();
      data.centerRotation.identity();
      data.body.rotation.orientInstantX(20.0F);
      data.rightLeg.rotation.orientInstantX(0.0F);
      data.leftLeg.rotation.orientInstantX(0.0F);
      data.rightForeLeg.rotation.orientInstantX(0.0F);
      data.leftForeLeg.rotation.orientInstantX(0.0F);
      data.rightArm.rotation.orientInstantZ(2.0F);
      data.leftArm.rotation.orientInstantZ(-2.0F);
      data.rightForeArm.rotation.orientInstantX(-20.0F);
      data.leftForeArm.rotation.orientInstantX(-20.0F);
   }

   public void perform(T data) {
      if (data.getPrevMotionY() < 0.0D && data.getMotionY() > 0.0D) {
         this.onPlay(data);
      }

      EntityLivingBase biped = data.getEntity();
      data.globalOffset.slideToZero(0.3F);
      data.renderRotation.setSmoothness(0.3F).orientZero();
      data.centerRotation.setSmoothness(0.7F).orientZero();
      data.renderRightItemRotation.setSmoothness(0.3F).orientZero();
      data.renderLeftItemRotation.setSmoothness(0.3F).orientZero();
      float bodyRotationX = Math.max(1.0F - data.getTicksInAir() * 0.1F, 0.0F);
      data.body.rotation.setSmoothness(0.2F).orientX(bodyRotationX);
      data.rightArm.rotation.setSmoothness(0.05F).orientZ(45.0F);
      data.leftArm.rotation.setSmoothness(0.05F).orientZ(-45.0F);
      data.rightForeArm.rotation.setSmoothness(0.3F).orientX(0.0F);
      data.leftForeArm.rotation.setSmoothness(0.3F).orientX(0.0F);
      data.head.rotation.orientX(((Float)data.headPitch.get()).floatValue() - bodyRotationX).rotateY(((Float)data.headYaw.get()).floatValue());
      if (!data.isStillHorizontally()) {
         float PI = 3.1415927F;
         float limbSwing = ((Float)data.limbSwing.get()).floatValue() * 0.6662F;
         float limbSwingAmount = 0.7F * ((Float)data.limbSwingAmount.get()).floatValue() / 3.1415927F * 180.0F;
         data.rightLeg.rotation.setSmoothness(1.0F).orientX(-5.0F + MathHelper.cos(limbSwing) * limbSwingAmount);
         data.leftLeg.rotation.setSmoothness(1.0F).orientX(-5.0F + MathHelper.cos(limbSwing + 3.1415927F) * limbSwingAmount);
         float var = limbSwing / 3.1415927F % 2.0F;
         data.leftForeLeg.rotation.setSmoothness(0.3F).orientX((float)(var > 1.0F ? 45 : 0));
         data.rightForeLeg.rotation.setSmoothness(0.3F).orientX((float)(var > 1.0F ? 0 : 45));
         data.leftForeArm.rotation.setSmoothness(0.3F).orientX((MathHelper.cos(limbSwing + 1.5707964F) / 2.0F + 0.5F) * -20.0F);
         data.rightForeArm.rotation.setSmoothness(0.3F).orientX((MathHelper.cos(limbSwing) / 2.0F + 0.5F) * -20.0F);
      } else {
         data.rightLeg.rotation.setSmoothness(0.1F).orientZ(10.0F);
         data.rightLeg.rotation.setSmoothness(0.3F).rotateX(-45.0F);
         data.leftLeg.rotation.setSmoothness(0.1F).orientZ(-10.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).rotateX(-17.0F);
         data.rightForeLeg.rotation.setSmoothness(0.3F).orientX(70.0F);
         data.leftForeLeg.rotation.setSmoothness(0.3F).orientX(17.0F);
      }

   }
}
