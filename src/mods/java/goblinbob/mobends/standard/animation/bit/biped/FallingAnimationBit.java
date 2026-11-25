package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.math.MathHelper;

public class FallingAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"falling"};
   public static final float TICKS_BEFORE_FALLING = 10.0F;
   public static final float FALLING_TRANSITION_TICKS = 80.0F;

   public FallingAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> entityData) {
      return ACTIONS;
   }

   public void perform(BipedEntityData<?> data) {
      data.centerRotation.setSmoothness(0.3F).orientZero();
      data.head.rotation.orientX(((Float)data.headPitch.get()).floatValue()).rotateY(((Float)data.headYaw.get()).floatValue());
      data.body.rotation.orientY(0.0F).setSmoothness(0.5F);
      float ticks = DataUpdateHandler.getTicks() * 0.5F;
      float rightArmDelay = 1.0F;
      float armSpan = 20.0F;
      float legSpan = 10.0F;
      float transition = (data.getTicksFalling() - 10.0F) / 80.0F;
      transition = MathHelper.clamp(transition, 0.0F, 1.0F);
      float s = 0.0F + transition * 0.9F;
      data.leftArm.rotation.setSmoothness(s).orientZ(-90.0F + MathHelper.sin(ticks) * armSpan).rotateY(MathHelper.cos(ticks) * armSpan);
      data.rightArm.rotation.setSmoothness(s).orientZ(90.0F + MathHelper.sin(ticks + rightArmDelay) * armSpan).rotateY(MathHelper.cos(ticks + rightArmDelay) * armSpan);
      data.leftForeArm.rotation.setSmoothness(s).orientX(-15.0F);
      data.rightForeArm.rotation.setSmoothness(s).orientX(-15.0F);
      data.leftLeg.rotation.setSmoothness(s).orientX(MathHelper.sin(ticks) * legSpan).rotateZ(-20.0F + MathHelper.cos(ticks) * legSpan);
      data.rightLeg.rotation.setSmoothness(s).orientX(MathHelper.sin(ticks + rightArmDelay) * legSpan).rotateZ(20.0F + MathHelper.cos(ticks + rightArmDelay) * legSpan);
      data.leftForeLeg.rotation.setSmoothness(s).orientX(20.0F);
      data.rightForeLeg.rotation.setSmoothness(s).orientX(20.0F);
      data.renderRotation.setSmoothness(s).orientX(20.0F);
      data.head.rotation.setSmoothness(s).rotateX(-20.0F);
   }
}
