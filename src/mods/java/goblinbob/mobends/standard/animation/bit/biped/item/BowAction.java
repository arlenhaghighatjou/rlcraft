package goblinbob.mobends.standard.animation.bit.biped.item;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class BowAction extends AnimationBit<BipedEntityData<?>> {
   protected final EnumHandSide actionHand;

   public BowAction(EnumHandSide handSide) {
      this.actionHand = handSide;
   }

   public void perform(BipedEntityData<?> data) {
      data.localOffset.slideToZero(0.3F);
      EntityLivingBase living = data.getEntity();
      float headPitch = ((Float)data.headPitch.get()).floatValue();
      float headYaw = ((Float)data.headYaw.get()).floatValue();
      boolean mainHandSwitch = this.actionHand == EnumHandSide.RIGHT;
      float handDirMtp = mainHandSwitch ? 1.0F : -1.0F;
      ModelPartTransform mainArm = mainHandSwitch ? data.rightArm : data.leftArm;
      ModelPartTransform offArm = mainHandSwitch ? data.leftArm : data.rightArm;
      ModelPartTransform mainForeArm = mainHandSwitch ? data.rightForeArm : data.leftForeArm;
      ModelPartTransform offForeArm = mainHandSwitch ? data.leftForeArm : data.rightForeArm;
      int aimedBowDuration = living != null ? Math.min(living.getItemInUseMaxCount(), 15) : 0;
      float bodyTwistY = (float)(aimedBowDuration - 10) / 5.0F * -25.0F * handDirMtp;
      float var2 = (float)aimedBowDuration / 10.0F;
      float var5 = headPitch - 90.0F;
      var5 = Math.max(var5, -160.0F);
      float bodyRotationY = -bodyTwistY + headYaw;
      if (data.isClimbing()) {
         float climbingRotation = data.getClimbingRotation();
         float renderRotationY = MathHelper.wrapDegrees(living.rotationYaw - headYaw - climbingRotation);
         bodyRotationY = MathHelper.wrapDegrees(headYaw + renderRotationY);
         data.head.rotation.setSmoothness(0.5F).orientX(headPitch);
      } else {
         data.head.rotation.setSmoothness(0.5F).orientX(headPitch).rotateY(headYaw - bodyRotationY);
      }

      data.body.rotation.setSmoothness(0.8F).orientY(bodyRotationY);
      mainArm.rotation.setSmoothness(0.8F).orientX(headPitch - 90.0F).rotateY(bodyTwistY);
      offArm.rotation.setSmoothness(1.0F).orientY(80.0F * handDirMtp).rotateZ((-MathHelper.cos(headPitch / 180.0F * 3.1415927F) * 40.0F + 40.0F) * handDirMtp).rotateX(var5);
      mainForeArm.rotation.setSmoothness(1.0F).orientX(0.0F);
      offForeArm.rotation.orientX(var2 * -30.0F);
   }
}
