package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.math.MathHelper;

public class StandAnimationBit<T extends BipedEntityData<?>> extends AnimationBit<T> {
   private static final String[] ACTIONS = new String[]{"stand"};
   protected final float kneelDuration = 0.15F;

   public StandAnimationBit() {
   }

   public String[] getActions(T entityData) {
      return ACTIONS;
   }

   public void onPlay(T data) {
      float touchdown = Math.min(data.getTicksAfterTouchdown() * 0.15F, 1.0F);
      if (touchdown < 0.5F) {
         data.body.rotation.orientInstant(20.0F, 1.0F, 0.0F, 0.0F);
         data.rightLeg.rotation.orient(-20.0F, 1.0F, 0.0F, 0.0F);
         data.leftLeg.rotation.orient(-45.0F, 1.0F, 0.0F, 0.0F);
         data.rightForeLeg.rotation.orient(60.0F, 1.0F, 0.0F, 0.0F);
         data.leftForeLeg.rotation.orient(60.0F, 1.0F, 0.0F, 0.0F);
      }

   }

   public void perform(T data) {
      data.localOffset.slideToZero(0.3F);
      data.globalOffset.slideToZero(0.3F);
      data.renderRotation.setSmoothness(0.3F).orientZero();
      data.centerRotation.setSmoothness(0.3F).orientZero();
      data.renderRightItemRotation.setSmoothness(0.3F).orientZero();
      data.renderLeftItemRotation.setSmoothness(0.3F).orientZero();
      data.rightLeg.rotation.orient(0.0F, 1.0F, 0.0F, 0.0F);
      data.rightLeg.rotation.rotate(2.0F, 0.0F, 0.0F, 1.0F);
      data.rightLeg.rotation.rotate(5.0F, 0.0F, 1.0F, 0.0F);
      data.leftLeg.rotation.orient(0.0F, 1.0F, 0.0F, 0.0F);
      data.leftLeg.rotation.rotate(-2.0F, 0.0F, 0.0F, 1.0F);
      data.leftLeg.rotation.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
      data.rightForeLeg.rotation.orient(4.0F, 1.0F, 0.0F, 0.0F);
      data.leftForeLeg.rotation.orient(4.0F, 1.0F, 0.0F, 0.0F);
      data.rightForeArm.rotation.orient(-4.0F, 1.0F, 0.0F, 0.0F);
      data.leftForeArm.rotation.orient(-4.0F, 1.0F, 0.0F, 0.0F);
      data.head.rotation.orientX(((Float)data.headPitch.get()).floatValue()).rotateY(((Float)data.headYaw.get()).floatValue());
      float PI = 3.1415927F;
      float phase = DataUpdateHandler.getTicks() / 10.0F;
      data.body.rotation.setSmoothness(1.0F).orientX((MathHelper.cos(phase) - 1.0F) / 2.0F * -3.0F);
      data.rightArm.rotation.setSmoothness(0.4F).orientX(0.0F).rotateZ(MathHelper.cos(phase + 1.5707964F) * -2.5F + 2.5F);
      data.leftArm.rotation.setSmoothness(0.4F).orientX(0.0F).rotateZ(MathHelper.cos(phase + 1.5707964F) * 2.5F - 2.5F);
      float touchdown = Math.min(data.getTicksAfterTouchdown() * 0.15F, 1.0F);
      if (touchdown < 1.0F) {
         data.body.rotation.setSmoothness(1.0F);
         data.body.rotation.orient(20.0F * (1.0F - touchdown), 1.0F, 0.0F, 0.0F);
         data.globalOffset.setY((float)(-Math.sin((double)touchdown * 3.141592653589793D)) * 2.0F);
      }

   }
}
