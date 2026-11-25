package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.util.math.MathHelper;

public class SleepingAnimationBit extends AnimationBit<PlayerData> {
   private static final String[] ACTIONS = new String[]{"sleeping"};

   public SleepingAnimationBit() {
   }

   public String[] getActions(PlayerData entityData) {
      return ACTIONS;
   }

   public void perform(PlayerData data) {
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
      float PI = 3.1415927F;
      float phase = DataUpdateHandler.getTicks() / 10.0F;
      data.head.rotation.setSmoothness(1.0F).orientX((MathHelper.cos(phase) - 1.0F) / 2.0F * -3.0F);
      data.rightArm.rotation.setSmoothness(0.4F).orientX(0.0F).rotateZ(2.5F);
      data.leftArm.rotation.setSmoothness(0.4F).orientX(0.0F).rotateZ(-2.5F);
   }
}
