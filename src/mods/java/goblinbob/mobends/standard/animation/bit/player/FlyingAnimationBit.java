package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.MathHelper;

public class FlyingAnimationBit extends AnimationBit<PlayerData> {
   private static final String[] ACTIONS = new String[]{"flying"};
   private static final float PI = 3.1415927F;
   private static final float PI_2 = 6.2831855F;
   private static final double STILL_MOTION_THRESHOLD = 0.1D;
   private float transformTransition = 0.0F;

   public FlyingAnimationBit() {
   }

   public String[] getActions(PlayerData entityData) {
      return ACTIONS;
   }

   public void perform(PlayerData data) {
      AbstractClientPlayer player = (AbstractClientPlayer)data.getEntity();
      double magnitude = data.getInterpolatedMotionMagnitude();
      float ticks = DataUpdateHandler.getTicks();
      float forwardMomentum = MathHelper.clamp((float)data.getForwardMomentum(), -1.0F, 1.0F);
      float sideMomentum = MathHelper.clamp((float)data.getSidewaysMomentum(), -1.0F, 1.0F);
      double xzMomentum = data.getInterpolatedXZMotionMagnitude();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      float headYawAbs = MathHelper.abs(headYaw);
      float yMomentumAngle = (float)MathHelper.atan2(xzMomentum, data.getMotionY()) * 180.0F / 3.1415927F;
      if (player.isSprinting() && !data.isDrawingBow() && data.getTicksAfterAttack() >= 10.0F) {
         float speedFactor = MathHelper.clamp((float)magnitude, 0.0F, 0.2F) / 0.2F;
         data.centerRotation.setSmoothness(1.0F).orientX(yMomentumAngle * speedFactor).rotateZ(headYaw);
         float bodyRotationX = MathHelper.clamp(headPitch * 0.8F, -60.0F, 0.0F);
         data.head.rotation.setSmoothness(1.0F).orientY(headYaw).rotateX(headPitch - bodyRotationX - yMomentumAngle * speedFactor);
         data.body.rotation.setSmoothness(0.7F).orientX(bodyRotationX);
         data.leftArm.rotation.setSmoothness(0.7F).orientX(-bodyRotationX).rotateZ(-60.0F + 55.0F * speedFactor - headYawAbs * 0.5F);
         data.rightArm.rotation.setSmoothness(0.7F).orientX(-bodyRotationX).rotateZ(60.0F - 55.0F * speedFactor + headYawAbs * 0.5F);
         data.leftForeArm.rotation.setSmoothness(0.7F).orientZero();
         data.rightForeArm.rotation.setSmoothness(0.7F).orientZero();
         data.leftLeg.rotation.setSmoothness(0.7F).orientZ(-5.0F);
         data.rightLeg.rotation.setSmoothness(0.7F).orientZ(5.0F);
         data.leftForeLeg.rotation.setSmoothness(0.7F).orientX(0.0F);
         data.rightForeLeg.rotation.setSmoothness(0.7F).orientX(0.0F);
      } else if (magnitude < 0.1D) {
         float armSway = (MathHelper.cos(ticks * 0.0825F) + 1.0F) / 2.0F;
         float armSway2 = (-MathHelper.sin(ticks * 0.0825F) + 1.0F) / 2.0F;
         float legFlap = MathHelper.cos(ticks * 0.125F);
         float legFlap2 = MathHelper.sin(ticks * 0.125F);
         float foreArmSway = ticks * 0.1625F % 6.2831855F / 6.2831855F;
         float foreArmStretch = armSway * 2.0F;
         --foreArmStretch;
         foreArmStretch = Math.max(foreArmStretch, 0.0F);
         data.leftArm.rotation.setSmoothness(0.3F).orientX(armSway2 * 30.0F - 15.0F).rotateZ(-armSway * 30.0F);
         data.rightArm.rotation.setSmoothness(0.3F).orientX(armSway2 * 30.0F - 15.0F).rotateZ(armSway * 30.0F);
         data.leftForeArm.rotation.setSmoothness(0.3F).orientX(armSway2 * -40.0F);
         data.rightForeArm.rotation.setSmoothness(0.3F).orientX(armSway2 * -40.0F);
         data.leftLeg.rotation.setSmoothness(0.3F).orientZ(-5.0F + legFlap * 3.0F).rotateX(-25.0F + legFlap2 * 5.0F);
         data.rightLeg.rotation.setSmoothness(0.3F).orientZ(5.0F - legFlap * 3.0F).rotateX(-6.0F + legFlap2 * 5.0F);
         data.leftForeLeg.rotation.setSmoothness(0.4F).orientX(20.0F - legFlap2 * 15.0F);
         data.rightForeLeg.rotation.setSmoothness(0.4F).orientX(5.0F);
         data.body.rotation.orientX(armSway * 10.0F);
         data.centerRotation.orientZero();
         data.head.rotation.setSmoothness(1.0F).orientX(headPitch).rotateY(headYaw);
      } else {
         data.centerRotation.orientZero();
         data.centerRotation.rotateX(forwardMomentum * 50.0F);
         data.body.rotation.orientZero();
         data.leftArm.rotation.orientX(forwardMomentum * 90.0F).localRotateZ(sideMomentum * -80.0F - 20.0F);
         data.rightArm.rotation.orientX(forwardMomentum * 90.0F).localRotateZ(sideMomentum * -80.0F + 20.0F);
         data.leftForeArm.rotation.orientZero();
         data.rightForeArm.rotation.orientZero();
         data.leftLeg.rotation.orientX(-45.0F).localRotateZ(sideMomentum * -40.0F - 5.0F);
         data.rightLeg.rotation.orientX(-6.0F).localRotateZ(sideMomentum * -40.0F + 5.0F);
         data.leftForeLeg.rotation.orientX(30.0F);
         data.rightForeLeg.rotation.orientX(10.0F);
         data.head.rotation.setSmoothness(1.0F).orientX(headPitch).rotateX(-forwardMomentum * 50.0F);
         if (!data.isDrawingBow()) {
            data.centerRotation.localRotateY(-headYaw);
         }
      }

      data.renderRotation.setSmoothness(0.7F).orientX(0.0F);
      data.globalOffset.slideToZero(0.7F);
   }
}
