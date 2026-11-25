package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.util.math.MathHelper;

public class ElytraAnimationBit extends AnimationBit<PlayerData> {
   private static final String[] ACTIONS = new String[]{"elytra"};

   public ElytraAnimationBit() {
   }

   public String[] getActions(PlayerData entityData) {
      return ACTIONS;
   }

   public void perform(PlayerData data) {
      double magnitude = data.getInterpolatedMotionMagnitude();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      float headYawAbs = MathHelper.abs(headYaw);
      float speedFactor = MathHelper.clamp((float)magnitude, 0.0F, 0.2F) / 0.2F;
      data.head.rotation.setSmoothness(1.0F).orientY(headYaw).rotateX(-90.0F);
      data.body.rotation.setSmoothness(0.7F).orientX(0.0F);
      data.leftArm.rotation.setSmoothness(0.7F).orientX(0.0F).rotateZ(-60.0F + 55.0F * speedFactor - headYawAbs * 0.5F);
      data.rightArm.rotation.setSmoothness(0.7F).orientX(0.0F).rotateZ(60.0F - 55.0F * speedFactor + headYawAbs * 0.5F);
      data.leftForeArm.rotation.setSmoothness(0.7F).orientZero();
      data.rightForeArm.rotation.setSmoothness(0.7F).orientZero();
      data.leftLeg.rotation.setSmoothness(0.7F).orientZ(-5.0F);
      data.rightLeg.rotation.setSmoothness(0.7F).orientZ(5.0F);
      data.leftForeLeg.rotation.setSmoothness(0.7F).orientX(0.0F);
      data.rightForeLeg.rotation.setSmoothness(0.7F).orientX(0.0F);
      data.centerRotation.setSmoothness(1.0F).orientZero();
      data.renderRotation.setSmoothness(0.7F).orientX(0.0F);
      data.globalOffset.slideToZero(0.7F);
   }
}
