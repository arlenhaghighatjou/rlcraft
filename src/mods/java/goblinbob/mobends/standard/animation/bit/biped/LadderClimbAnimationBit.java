package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.util.GUtil;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class LadderClimbAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"ladder_climb"};

   public LadderClimbAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> entityData) {
      return ACTIONS;
   }

   public void perform(BipedEntityData<?> data) {
      EntityLivingBase living = data.getEntity();
      data.centerRotation.setSmoothness(0.3F).orientZero();
      float legAnimationOffset = 3.1415927F;
      float progress = data.getClimbingCycle();
      float armSwingRight = (float)Math.sin((double)progress) * 0.5F + 0.5F;
      float armSwingLeft = (float)Math.sin((double)progress + 3.141592653589793D) * 0.5F + 0.5F;
      float armSwingRight2 = (float)Math.sin((double)(progress - 0.3F)) * 0.5F + 0.5F;
      float armSwingLeft2 = (float)Math.sin((double)progress + 3.141592653589793D - 0.30000001192092896D) * 0.5F + 0.5F;
      float armSwingDouble = (float)Math.sin((double)(progress * 2.0F)) * 0.5F + 0.5F;
      float armSwingDouble2 = (float)Math.sin((double)(progress * 2.0F - 1.8F)) * 0.5F + 0.5F;
      float legSwingRight = (float)Math.sin((double)(progress + 3.1415927F)) * 0.5F + 0.5F;
      float legSwingLeft = (float)Math.sin((double)(progress + 3.1415927F) + 3.141592653589793D) * 0.5F + 0.5F;
      float legSwingRight2 = (float)Math.sin((double)(progress + 3.1415927F + 0.3F)) * 0.5F + 0.5F;
      float legSwingLeft2 = (float)Math.sin((double)(progress + 3.1415927F) + 3.141592653589793D + 0.30000001192092896D) * 0.5F + 0.5F;
      float armOrientX = -45.0F;
      float climbingRotation = data.getClimbingRotation();
      float renderRotationY = MathHelper.wrapDegrees(living.rotationYaw - ((Float)data.headYaw.get()).floatValue() - climbingRotation);
      data.renderRotation.setSmoothness(0.6F).orientY(renderRotationY);
      data.localOffset.slideZ(armSwingDouble2, 0.6F);
      data.body.rotation.setSmoothness(0.5F).orientX(armSwingDouble * 10.0F);
      data.rightArm.rotation.setSmoothness(0.5F).orientX(-135.0F + armSwingRight * 70.0F);
      data.leftArm.rotation.setSmoothness(0.5F).orientX(-135.0F + armSwingLeft * 70.0F);
      data.rightForeArm.rotation.setSmoothness(0.5F).orientX(armSwingRight2 * -80.0F);
      data.leftForeArm.rotation.setSmoothness(0.5F).orientX(armSwingLeft2 * -80.0F);
      data.rightLeg.rotation.setSmoothness(0.5F).orientX(-45.0F - legSwingRight * 50.0F);
      data.leftLeg.rotation.setSmoothness(0.5F).orientX(-45.0F - legSwingLeft * 50.0F);
      data.rightForeLeg.rotation.setSmoothness(0.5F).orientX(20.0F + legSwingRight2 * 90.0F);
      data.leftForeLeg.rotation.setSmoothness(0.5F).orientX(20.0F + legSwingLeft2 * 90.0F);
      data.head.rotation.orientX(((Float)data.headPitch.get()).floatValue()).rotateY(GUtil.clamp(MathHelper.wrapDegrees(((Float)data.headYaw.get()).floatValue() + renderRotationY), -90.0F, 90.0F));
      float ledgeClimbStart = 0.6F;
      if (data.getLedgeHeight() >= 0.6F) {
         float armRotX = data.getLedgeHeight() - 0.6F;
         data.body.rotation.setSmoothness(0.5F).orientX(armRotX * 50.0F);
         data.rightArm.rotation.setSmoothness(0.5F).orientX(-100.0F + armRotX * 40.0F);
         data.leftArm.rotation.setSmoothness(0.5F).orientX(-100.0F + armRotX * 40.0F);
         data.rightForeArm.rotation.setSmoothness(0.5F).orientX(-10.0F);
         data.leftForeArm.rotation.setSmoothness(0.5F).orientX(-10.0F);
      }

   }
}
