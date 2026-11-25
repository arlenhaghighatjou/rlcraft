package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.math.MathHelper;

public class WalkAnimationBit<T extends BipedEntityData<?>> extends AnimationBit<T> {
   private static final String[] ACTIONS = new String[]{"walk"};
   protected final float KNEEL_DURATION = 0.15F;

   public WalkAnimationBit() {
   }

   public String[] getActions(T entityData) {
      return ACTIONS;
   }

   public void perform(T data) {
      data.localOffset.slideToZero(0.3F);
      data.globalOffset.slideToZero(0.3F);
      data.centerRotation.setSmoothness(0.3F).orientZero();
      data.renderRotation.setSmoothness(0.3F).orientZero();
      data.renderRightItemRotation.setSmoothness(0.3F).orientZero();
      data.renderLeftItemRotation.setSmoothness(0.3F).orientZero();
      float PI = 3.1415927F;
      float limbSwing = ((Float)data.limbSwing.get()).floatValue() * 0.6662F;
      float armSwingAmount = ((Float)data.limbSwingAmount.get()).floatValue() * 0.5F / 3.1415927F * 180.0F;
      data.rightArm.rotation.setSmoothness(0.8F).orientX(MathHelper.cos(limbSwing + 3.1415927F) * armSwingAmount).rotateZ(5.0F);
      data.leftArm.rotation.setSmoothness(0.8F).orientX(MathHelper.cos(limbSwing) * armSwingAmount).rotateZ(-5.0F);
      float legSwingAmount = 0.7F * ((Float)data.limbSwingAmount.get()).floatValue() / 3.1415927F * 180.0F;
      data.rightLeg.rotation.setSmoothness(1.0F).orientX(-5.0F + MathHelper.cos(limbSwing) * legSwingAmount).rotateZ(2.0F);
      data.leftLeg.rotation.setSmoothness(1.0F).orientX(-5.0F + MathHelper.cos(limbSwing + 3.1415927F) * legSwingAmount).rotateZ(-2.0F);
      float var = limbSwing / 3.1415927F % 2.0F;
      data.leftForeLeg.rotation.setSmoothness(0.5F).orientX(var > 1.0F ? 45.0F : 0.0F);
      data.rightForeLeg.rotation.setSmoothness(0.5F).orientX(var > 1.0F ? 0.0F : 45.0F);
      data.leftForeArm.rotation.setSmoothness(0.8F).orientX(MathHelper.cos(limbSwing + 1.5707964F) * -10.0F - 10.0F);
      data.rightForeArm.rotation.setSmoothness(0.8F).orientX(MathHelper.cos(limbSwing) * -10.0F - 10.0F);
      float bodyRotationY = MathHelper.cos(limbSwing) * -20.0F;
      float bodyRotationX = MathHelper.cos(limbSwing * 2.0F) * 5.0F + 3.0F;
      float var10 = ((Float)data.headYaw.get()).floatValue() * 0.1F;
      var10 = Math.max(-10.0F, Math.min(var10, 10.0F));
      data.body.rotation.setSmoothness(0.5F).orientY(bodyRotationY).rotateX(bodyRotationX).rotateZ(-var10);
      data.head.rotation.setSmoothness(0.5F).orientX(((Float)data.headPitch.get()).floatValue() - bodyRotationX).rotateY(((Float)data.headYaw.get()).floatValue() - bodyRotationY);
      data.globalOffset.slideY(MathHelper.cos(limbSwing * 2.0F) * 0.6F);
      float touchdown = Math.min(data.getTicksAfterTouchdown() * 0.15F, 1.0F);
      if (touchdown < 1.0F) {
         data.body.rotation.setSmoothness(1.0F);
         data.body.rotation.orient(20.0F * (1.0F - touchdown), 1.0F, 0.0F, 0.0F);
         data.globalOffset.setY((float)(-Math.sin((double)touchdown * 3.141592653589793D)) * 2.0F);
      }

   }
}
